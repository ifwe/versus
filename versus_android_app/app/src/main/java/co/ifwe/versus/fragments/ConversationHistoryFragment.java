package co.ifwe.versus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Profile;
import com.pubnub.api.Pubnub;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.ifwe.versus.R;
import co.ifwe.versus.adapters.MessageAdapterV2;
import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.MessageHistory;
import co.ifwe.versus.recycler.adapter.RecyclerMergeAdapter;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.callbacks.StubCallback;
import co.ifwe.versus.utils.FragmentState;
import co.ifwe.versus.utils.PubnubUtils;
import co.ifwe.versus.viewholders.MessageViewType;

public class ConversationHistoryFragment extends VersusFragment {

    private static final String TAG = ConversationHistoryFragment.class.getCanonicalName();

    private static final String ARG_CONVERSATION = "conversation";

    public static final int PAGE_SIZE = 30;

    private Conversation mConversation;
    private MessageAdapterV2 mMessageAdapter;
    private RecyclerMergeAdapter mMergeAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private View mLoadingView;

    @Bind(R.id.message_list)
    RecyclerView mListView;

    @Inject
    ConversationsService mConversationsService;

    @Inject
    Pubnub mPubnub;

    private long mEndTime = -1L;
    private boolean mLoading = true;
    private boolean mRetrievedAll = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle args = getArguments();

        if (args.containsKey(ARG_CONVERSATION)) {
            mConversation = args.getParcelable(ARG_CONVERSATION);
            getActivity().setTitle(mConversation.getTopic().getTitle());
        } else {
            throw new IllegalArgumentException("Conversation is required");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_history, container, false);
        mLoadingView = inflater.inflate(R.layout.item_loading_header, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Profile profile = Profile.getCurrentProfile();
        mMergeAdapter = new RecyclerMergeAdapter();
        mMessageAdapter = new MessageAdapterV2(getActivity(), mConversation, profile.getId());
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLinearLayoutManager);
        mMergeAdapter.setDataAdapter(mMessageAdapter);
        mListView.setAdapter(mMergeAdapter);
        mMergeAdapter.add(mLoadingView, MessageViewType.HEADER);
        mMergeAdapter.setActive(mLoadingView, true);

        mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisible = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!mLoading) {
                    if (mMessageAdapter.getItemCount() - lastVisible >= 5 && !mRetrievedAll) {
                        mLoading = true;
                        retrieveHistory(mConversation.getRoomName(), PAGE_SIZE);
                    }
                }
            }
        });
        retrieveHistory(mConversation.getRoomName(), PAGE_SIZE);
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
        mMessageAdapter.add(mMessageAdapter.getItemCount(), chatMessages);
    }

    private void onBackPressed() {
        getActivity().finish();
    }

    public static Bundle createState(Conversation conversation) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONVERSATION, conversation);
        return FragmentState.create(ConversationHistoryFragment.class, args);
    }
}
