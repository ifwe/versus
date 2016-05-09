package co.ifwe.versus.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.ifwe.versus.R;
import co.ifwe.versus.adapters.CategoryAdapter;
import co.ifwe.versus.models.Category;
import co.ifwe.versus.provider.Projection;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.services.TopicsService;
import co.ifwe.versus.utils.FragmentState;
import co.ifwe.versus.utils.FragmentUtils;

public class CategoryListFragment extends VersusFragment
    implements CategoryAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = CategoryListFragment.class.getCanonicalName();

    private static final int LOADER_CATEORIES_ID = 1;

    @Bind(android.R.id.list)
    RecyclerView mCategoriesRecyclerView;

    private CategoryAdapter mCategoryAdapter;

    @Inject
    TopicsService mTopicsService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.title_categories);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mCategoryAdapter = new CategoryAdapter(getActivity(), R.layout.item_category);
        mCategoriesRecyclerView.setAdapter(mCategoryAdapter);
        mCategoryAdapter.setOnItemClickListener(this);

        getLoaderManager().initLoader(LOADER_CATEORIES_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(Category item) {

        Bundle fragmentState = TopicFragment.createState(item);
        FragmentUtils.replace(getActivity(), fragmentState, R.id.content_frame);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_CATEORIES_ID:
                return new CursorLoader(
                        getActivity(),
                        VersusContract.Categories.CONTENT_URI,
                        Projection.CATEGORY,
                        null, null, null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_CATEORIES_ID:
                mCategoryAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCategoryAdapter.swapCursor(null);
    }

    public static Bundle createState() {
        Bundle args = new Bundle();
        return FragmentState.create(CategoryListFragment.class, args);
    }
}
