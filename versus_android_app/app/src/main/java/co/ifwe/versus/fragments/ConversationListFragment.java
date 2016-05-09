package co.ifwe.versus.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ifwe.versus.R;
import co.ifwe.versus.activities.CategoryActivity;
import co.ifwe.versus.activities.ConversationActivity;
import co.ifwe.versus.adapters.ConversationAdapter;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.models.Status;
import co.ifwe.versus.provider.Projection;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.callbacks.StubCallback;
import co.ifwe.versus.utils.FragmentState;
import co.ifwe.versus.views.EmptyView;

public class ConversationListFragment extends VersusFragment implements
        ConversationAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ConversationListFragment.class.getCanonicalName();
    private static final int LOADER_CONVERSATIONS_ID = 1;

    public static final String ARG_ACTIVE = "active";

    private ConversationAdapter mConversationAdapter;
    private boolean mIsActive;

    @Bind(R.id.new_fab)
    FloatingActionButton mNewConvoFab;

    @Bind(R.id.conversations_list)
    RecyclerView mConversationsRecyclerView;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.conversation_empty_view)
    EmptyView mEmptyView;

    @Inject
    ConversationsService mConversationsService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mIsActive = args.getBoolean(ARG_ACTIVE, true);

        getActivity().setTitle(mIsActive ? R.string.title_inbox : R.string.title_archive);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mConversationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mConversationAdapter = new ConversationAdapter(getActivity());
        mConversationsRecyclerView.setAdapter(mConversationAdapter);
        mConversationAdapter.setOnItemClickListener(this);

        if (!mIsActive) {
            mNewConvoFab.setVisibility(View.GONE);
        }

        List<Status> statuses = new ArrayList<>();
        if (mIsActive) {
            statuses.add(Status.ACTIVE);
        } else {
            statuses.add(Status.REVIEW);
            statuses.add(Status.DONE);
        }
        mSwipeRefreshLayout.setColorSchemeResources(R.color.sea_blue);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mConversationsService.getConversationList(statuses, mIsActive,
                    new StubCallback<List<Conversation>>() {
                        @Override
                        public void onComplete() {
                            super.onComplete();
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                    });
        });
        mConversationsService.getConversationList(statuses, mIsActive,
                new StubCallback<List<Conversation>>() {
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

        getLoaderManager().initLoader(LOADER_CONVERSATIONS_ID, null, this);
        mSwipeRefreshLayout.setRefreshing(true);
        initEmptyView(mIsActive);
    }

    private void loadConversation(Conversation conversation) {
        ConversationActivity.start(getActivity(), conversation);
    }

    @OnClick(R.id.new_fab)
    void onNewConvoFabClick() {

        CategoryActivity.startForResult(getActivity());
    }

    @Override
    public void onItemClick(Conversation conversation) {
        loadConversation(conversation);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Status[] statuses = new Status[2];
        if (mIsActive) {
            statuses[0] = Status.ACTIVE;
            statuses[1] = Status.PENDING;
        } else {
            statuses[0] = Status.REVIEW;
            statuses[1] = Status.DONE;
        }
        switch (id) {
            case LOADER_CONVERSATIONS_ID:
                return new CursorLoader(
                        getActivity(),
                        VersusContract.Conversations.CONTENT_URI,
                        Projection.CONVERSATION,
                        VersusContract.Conversations.buildStatusSelection(statuses),
                        null, null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mConversationAdapter.swapCursor(data);
        mSwipeRefreshLayout.setRefreshing(false);
        if (data.getCount() > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            initEmptyView(mIsActive);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mConversationAdapter.swapCursor(null);
    }

    private void initEmptyView(boolean isActive) {
        mEmptyView.setIconVisiblity(View.GONE);
        mEmptyView.setButtonVisibility(isActive ? View.GONE : View.VISIBLE);
        mEmptyView.setFabTextVisibility(isActive ? View.VISIBLE : View.GONE);
        mEmptyView.setMessage(isActive
                ? R.string.empty_view_no_conversations_active : R.string.empty_view_no_conversations_past);
        mEmptyView.setButtonClickListener((v) -> onNewConvoFabClick());
    }

    public static Bundle createState(boolean activeConversations) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_ACTIVE, activeConversations);
        return FragmentState.create(ConversationListFragment.class, args);
    }
}
