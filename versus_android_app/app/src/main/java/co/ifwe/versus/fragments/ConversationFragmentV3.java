package co.ifwe.versus.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.Profile;
import com.pubnub.api.Pubnub;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ifwe.versus.R;
import co.ifwe.versus.activities.VersusActivity;
import co.ifwe.versus.adapters.MessageAdapterV3;
import co.ifwe.versus.events.MessageEvent;
import co.ifwe.versus.models.ChatMessage;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.Status;
import co.ifwe.versus.provider.Projection;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.recycler.adapter.RecyclerMergeAdapter;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.callbacks.StubCallback;
import co.ifwe.versus.utils.FragmentState;
import co.ifwe.versus.utils.PubnubUtils;
import co.ifwe.versus.viewholders.MessageViewType;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ConversationFragmentV3 extends VersusFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ConversationFragmentV3.class.getCanonicalName();

    private static final int LOADER_CONVERSATION_ID = 1;
    private static final int LOADER_MESSAGES_ID = 2;

    private static final String ARG_CONVERSATION = "conversation";
    private static final String ARG_ROOM_NAME = "room_name";

    public static final int PAGE_SIZE = 30;

    private String mRoomName;
    private Conversation mConversation;
    private RecyclerMergeAdapter mMergeAdapter;
    private MessageAdapterV3 mMessageAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private View mLoadingView;

    @Bind(R.id.message_list)
    RecyclerView mListView;

    @Bind(R.id.message_input)
    EditText mMessageEditText;

    @Bind(R.id.progress_bar)
    MaterialProgressBar mProgressBar;

    @Bind(R.id.message_input_layout)
    View mMessageInputLayout;

    @Bind(R.id.matching_layout)
    View mMatchingLayout;

    @Inject
    ConversationsService mConversationsService;

    @Inject
    Pubnub mPubnub;

    private long mStartTime = -1L;
    private boolean mLoading = true;
    private boolean mRetrievedAll = false;

    private Timer mTimer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle args = getArguments();

        if (args.containsKey(ARG_CONVERSATION)) {
            mConversation = args.getParcelable(ARG_CONVERSATION);
            mRoomName = mConversation.getRoomName();
            updateTitle();
        } else if (args.containsKey(ARG_ROOM_NAME)){
            mRoomName = args.getString(ARG_ROOM_NAME);
            getActivity().setTitle(R.string.title_inbox);
        } else {
            throw new IllegalArgumentException("Conversation is required");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_v2, container, false);
        mLoadingView = inflater.inflate(R.layout.item_loading_header, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar.setMax(2*60*60);
        Profile profile = Profile.getCurrentProfile();
        mMergeAdapter = new RecyclerMergeAdapter();
        mMessageAdapter = new MessageAdapterV3(getActivity(), profile.getId());
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLinearLayoutManager);
        mMergeAdapter.setDataAdapter(mMessageAdapter);
        mListView.setAdapter(mMergeAdapter);
        mMergeAdapter.add(0, mLoadingView, MessageViewType.HEADER);
        mMergeAdapter.setActive(mLoadingView, true);

        mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (!mLoading) {
                    if (firstVisible <= 5 && !mRetrievedAll) {
                        mLoading = true;
                        mMergeAdapter.setActive(mLoadingView, true);
                        //retrieveHistory(mRoomName, PAGE_SIZE);
                    }
                }
            }
        });

        mMessageEditText.setSingleLine(true);
        mMessageEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mMessageEditText.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                onMessageSendClick();
                return true;
            }
            return false;
        });

        mMessageEditText.setOnEditorActionListener((v, actionId, event) -> {
            switch(actionId) {
                case EditorInfo.IME_ACTION_DONE:
                case EditorInfo.IME_ACTION_SEND:
                    onMessageSendClick();
                    return true;
                default:
                    return false;
            }
        });

        //retrieveHistory(mRoomName, PAGE_SIZE);

        if (mConversation != null) {
            getLoaderManager().initLoader(LOADER_CONVERSATION_ID, null, this);
        }
        getLoaderManager().initLoader(LOADER_MESSAGES_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
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

    private void init() {
        if (mConversation != null && mConversation.getStatus() == Status.ACTIVE) {
            mProgressBar.setVisibility(View.VISIBLE);
            long timeRemaining = (mConversation.getEndTime() - System.currentTimeMillis())/1000L;
            mProgressBar.setProgress((int) timeRemaining);
            mMessageInputLayout.setVisibility(View.VISIBLE);
            mMatchingLayout.setVisibility(View.GONE);
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    int progress = (int)((mConversation.getEndTime() - System.currentTimeMillis())/1000);
                    if (progress == 0) {
                        new MaterialDialog.Builder(getActivity())
                                .content(R.string.conversation_timeout_message)
                                .positiveText(R.string.conversation_timeout_ok)
                                .show();
                    }
                    mProgressBar.setProgress(progress);
                }
            }, 1000, 1000);
        } else if (mConversation != null && mConversation.getStatus() == Status.PENDING) {
            mProgressBar.setVisibility(View.GONE);
            mMessageInputLayout.setVisibility(View.GONE);
            mMatchingLayout.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mMessageInputLayout.setVisibility(View.GONE);
            mMatchingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMessageEvent(MessageEvent messageEvent) {
        if (TextUtils.equals(messageEvent.getRoomName(), mRoomName)
                && !TextUtils.equals(messageEvent.getUserId(), mUserId)) {
            //getActivity().runOnUiThread(() -> addMessage(messageEvent.getChatMessage()));
        } else {
            super.onMessageEvent(messageEvent);
        }
    }

    @OnClick(R.id.message_send)
    void onMessageSendClick() {
        if (!TextUtils.isEmpty(mMessageEditText.getText())) {
            PubnubUtils.publish(mPubnub, mRoomName,
                    new ChatMessage(mConversation.getRoomName(), mUserId, System.currentTimeMillis(),
                            mMessageEditText.getText().toString()),
                    getActivity(), new StubCallback<>());
            mMessageEditText.setText(null);
        }
    }

    private void retrieveHistory(String channelName, int amount) {
        PubnubUtils.history(mPubnub, channelName, mStartTime, false, amount,
                getActivity(), new StubCallback<>());
    }
//
//    private void addHistory(List<ChatMessage> chatMessages) {
//        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
//        View firstItemView = mLinearLayoutManager.findViewByPosition(firstItem);
//        float topOffset = 0f;
//        if (firstItemView != null) {
//            topOffset = firstItemView.getTop() - getResources().getDimension(R.dimen.progress_bar_height);
//        }
//
//        boolean empty = mMessageAdapter.getItemCount() == 0;
//        mMergeAdapter.setActive(mLoadingView, false);
//        mMessageAdapter.add(0, chatMessages);
//
//        if (empty) {
//            mLinearLayoutManager.scrollToPosition(chatMessages.size() - 1);
//        } else {
//            mLinearLayoutManager.scrollToPositionWithOffset(firstItem + chatMessages.size(), (int) topOffset);
//        }
//    }

//    private void addMessage(ChatMessage chatMessage) {
//        int bottom = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//        boolean atBottom = bottom == mMessageAdapter.getItemCount() - 1;
//        mMessageAdapter.add(chatMessage);
//        if (atBottom) {
//            mListView.scrollToPosition(mMessageAdapter.getItemCount() - 1);
//        }
//    }


    private void updateTitle() {
        VersusActivity activity = (VersusActivity) getActivity();
        ActionBar actionbar = activity.getSupportActionBar();
        String side = TextUtils.equals(mConversation.getUserAId(), mUserId) ?
                mConversation.getTopic().getSideA() : mConversation.getTopic().getSideB();
        actionbar.setTitle(mConversation.getTitle(getActivity()));
        actionbar.setSubtitle(getString(R.string.inbox_matched, side));
    }

    private void onBackPressed() {
        getActivity().finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_CONVERSATION_ID:
                return new CursorLoader(
                        getActivity(),
                        VersusContract.Conversations.buildConversationUri(mRoomName),
                        Projection.CONVERSATION,
                        null, null, null
                );
            case LOADER_MESSAGES_ID:
                return new CursorLoader(
                        getActivity(),
                        VersusContract.Messages.CONTENT_URI,
                        Projection.MESSAGE,
                        VersusContract.Messages.buildConversationSelection(),
                        new String[] {mRoomName},
                        VersusContract.Messages.getDefaultSortOrder()
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_CONVERSATION_ID:
                if (data != null && data.moveToFirst()) {
                    mConversation = Conversation.fromCursor(data);
                    updateTitle();
                    init();
                }
                break;
            case LOADER_MESSAGES_ID:
                mMessageAdapter.swapCursor(data);
                if (data.getCount() == 0) {
                    retrieveHistory(mRoomName, PAGE_SIZE);
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_MESSAGES_ID:
                mMessageAdapter.swapCursor(null);
                break;
        }
    }

    public static Bundle createState(String roomName) {
        Bundle args = new Bundle();
        args.putString(ARG_ROOM_NAME, roomName);
        return FragmentState.create(ConversationFragmentV3.class, args);
    }

    public static Bundle createState(Conversation conversation) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONVERSATION, conversation);
        return FragmentState.create(ConversationFragmentV3.class, args);
    }
}
