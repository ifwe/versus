package co.ifwe.versus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pubnub.api.Pubnub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.ifwe.versus.R;
import co.ifwe.versus.adapters.ArbitrationAdapter;
import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.MessageHistory;
import co.ifwe.versus.recycler.adapter.RecyclerMergeAdapter;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.callbacks.StubCallback;
import co.ifwe.versus.utils.FragmentState;
import co.ifwe.versus.utils.PubnubUtils;
import co.ifwe.versus.viewholders.ArbitrationViewHolder;
import co.ifwe.versus.views.EmptyView;

public class ArbitrationFragment extends VersusFragment {

    private static final String TAG = ArbitrationFragment.class.getCanonicalName();

    private static final String ARG_CONVERSATION = "conversation";

    public static final int PAGE_SIZE = 30;

    private Conversation mConversation;
    private ArbitrationAdapter mArbitrationAdapter;
    private RecyclerMergeAdapter mMergeAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private View mLoadingView;
    //private View mSubmitView;

    @Bind(R.id.message_list)
    RecyclerView mListView;

    @Bind(R.id.empty_view)
    EmptyView mEmptyView;

    @Inject
    ConversationsService mConversationsService;

    @Inject
    Pubnub mPubnub;

    private long mEndTime = -1L;
    private boolean mLoading = true;
    private Map<String, Integer> mPointsMap;
    private boolean mRetrievedAll = false;
    private boolean mScrolledToBottom = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle args = getArguments();

        if (args.containsKey(ARG_CONVERSATION)) {
            mConversation = args.getParcelable(ARG_CONVERSATION);
            getActivity().setTitle(mConversation.getTitle(getActivity()));
            mPointsMap = new HashMap<>();
            mPointsMap.put(mConversation.getUserAId(), 0);
            mPointsMap.put(mConversation.getUserBId(), 0);
        } else {
            throw new IllegalArgumentException("Conversation is required");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_arbitration, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.submit);
        if (mRetrievedAll && mScrolledToBottom) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.submit:
                onSubmit();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arbitration, container, false);
        mLoadingView = inflater.inflate(R.layout.item_loading_header, container, false);
        //mSubmitView = inflater.inflate(R.layout.item_submit, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmptyView.setVisibility(View.GONE);
        mEmptyView.setButtonClickListener((v) -> onSubmitEmpty());
        mArbitrationAdapter = new ArbitrationAdapter(getActivity(), mConversation,
                (userId, liked) -> {
                    int toAdd = liked ? 1 : -1;
                    if (mPointsMap.containsKey(userId)){
                        mPointsMap.put(userId, mPointsMap.get(userId) + toAdd);
                    }
                });
        mMergeAdapter = new RecyclerMergeAdapter();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLinearLayoutManager);
        mMergeAdapter.setDataAdapter(mArbitrationAdapter);
        mListView.setAdapter(mMergeAdapter);
        mMergeAdapter.add(mLoadingView, ArbitrationViewHolder.ViewType.FOOTER);
        //mMergeAdapter.add(mSubmitView, ArbitrationViewHolder.ViewType.FOOTER);
        mMergeAdapter.setActive(mLoadingView, true);
        //mMergeAdapter.setActive(mSubmitView, false);
        //mSubmitView.setOnClickListener((v) -> onSubmit());

        mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisible = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!mLoading) {
                    if (mArbitrationAdapter.getItemCount() - lastVisible >= 5 && !mRetrievedAll) {
                        mLoading = true;
                        retrieveHistory(mConversation.getRoomName(), PAGE_SIZE);
                    }
                }
                if (lastVisible == mArbitrationAdapter.getItemCount() - 1) {
                    mScrolledToBottom = true;
                    getActivity().invalidateOptionsMenu();
                }
            }
        });

        retrieveHistory(mConversation.getRoomName(), PAGE_SIZE);
    }

    void onSubmit() {
        mConversationsService.submitScore(mConversation,
                mPointsMap.get(mConversation.getUserAId()), mPointsMap.get(mConversation.getUserBId()),
                new StubCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                super.onSuccess(aVoid);
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.arbitration_submit_message)
                        .positiveText(R.string.arbitration_submit_ok)
                        .onAny((dialog, which) -> getActivity().finish())
                        .show();
            }
        });
    }

    void onSubmitEmpty() {
        mConversationsService.submitScore(mConversation, 25, 25,
            new StubCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    super.onSuccess(aVoid);
                    getActivity().finish();
                }
            });
    }

    private void retrieveHistory(String channelName, int amount) {
        PubnubUtils.history(mPubnub, channelName, mEndTime, true, amount, getActivity(),
                new StubCallback<MessageHistory>() {
                    @Override
                    public void onSuccess(MessageHistory messageHistory) {
                        super.onSuccess(messageHistory);
                        mEndTime = messageHistory.getEndTime();
                        List<ChatMessage> messageList = messageHistory.getChatMessages();
                        getActivity().runOnUiThread(() -> addHistory(messageList));
                        if (messageList.size() < PAGE_SIZE) {
                            mRetrievedAll = true;
                            getActivity().invalidateOptionsMenu();
                            getActivity().runOnUiThread(() -> {
                                if (mArbitrationAdapter.getItemCount() == 0) {
                                    mEmptyView.setVisibility(View.VISIBLE);
                                } else {
                                    int lastVisible = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                                    if (lastVisible >= mArbitrationAdapter.getItemCount() - 1) {
                                        mScrolledToBottom = true;
                                    }
                                    getActivity().invalidateOptionsMenu();
                                }
                            });
                        }
                        mLoading = false;
                    }

                    @Override
                    public void onError(int errorCode) {
                        super.onError(errorCode);
                        mLoading = false;
                    }
                });
    }

    private void addHistory(List<ChatMessage> chatMessages) {
        mMergeAdapter.setActive(mLoadingView, false);
        mArbitrationAdapter.addHistory(chatMessages);
    }

    private void onBackPressed() {
        getActivity().finish();
    }

    public static Bundle createState(Conversation conversation) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONVERSATION, conversation);
        return FragmentState.create(ArbitrationFragment.class, args);
    }
}
