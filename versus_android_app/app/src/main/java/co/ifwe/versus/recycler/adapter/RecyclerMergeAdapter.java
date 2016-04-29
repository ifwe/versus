package co.ifwe.versus.recycler.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.ifwe.versus.recycler.RecyclerUtils;
import co.ifwe.versus.recycler.layout.GridSpanSizeLookup;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.NO_ID;
import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static android.support.v7.widget.RecyclerView.ViewHolder;

public class RecyclerMergeAdapter extends Adapter implements GridSpanSizeLookup {
    // adapter -> (adapter_view_type -> merge_view_type)
    private Map<Adapter, Map<Integer, Integer>> mAdapterToViewTypesMap = new HashMap<>();
    // merga_view_type -> (adapter, adapter_view_type)
    private Map<Integer, Pair<Adapter, Integer>> mViewTypesAdapterMap = new HashMap<>();

    private Map<Integer, Adapter> mHolderAdapterMap = new HashMap<>();

    private RecyclerMergeAdapterItems mItems;
    private int mViewTypesCounter = 0;
    private RecyclerView mRecyclerView = null;
    private Adapter mDataAdapter;

    public RecyclerMergeAdapter() {
        mItems = new RecyclerMergeAdapterItems(this);
    }

    /**
     * Adds a new View to Merge adapter
     *
     * @param view     - View to add
     * @param enumType - Unique type id, make sure that type is unique across all added views and adapters
     */
    public void add(View view, Enum enumType) {
        add(view, enumType.ordinal());
    }

    /**
     * Adds a new View to Merge adapter
     *
     * @param view         - View to add
     * @param uniqueTypeId - Unique type id, make sure that type is unique across all added views and adapters
     */
    public void add(View view, @IdRes int uniqueTypeId) {
        Adapter adapter = new RecyclerViewAdapter(view, uniqueTypeId);
        add(adapter);
    }

    /**
     * Adds a new Adapter to Merge adapter
     *
     * @param adapter - Adapter to add
     */
    public void add(Adapter adapter) {
        mItems.add(adapter);
        addInternal(adapter);
    }

    // TODO: figure out better way
    public void setDataAdapter(Adapter adapter) {
        add(adapter);
        mDataAdapter = adapter;
    }

    public Adapter getDataAdapter() {
        return mDataAdapter;
    }

    /**
     * Adds a new View to Merge adapter at specified position
     *
     * @param position - Position where to add new View at
     * @param view     - View to add
     * @param enumType - Unique type id, make sure that type is unique across all added views and adapters
     */
    public void add(int position, View view, Enum enumType) {
        add(position, view, enumType.ordinal());
    }

    /**
     * Adds a new View to Merge adapter
     *
     * @param position     - Position where to add new View at
     * @param view         - View to add
     * @param uniqueTypeId - Unique type id, make sure that type is unique across all added views and adapters
     */
    public void add(int position, View view, @IdRes int uniqueTypeId) {
        Adapter adapter = new RecyclerViewAdapter(view, uniqueTypeId);
        add(position, adapter);
    }

    /**
     * Adds a new Adapter to Merge adapter at specified position
     *
     * @param position - Position where to add new Adapter at
     * @param adapter  - Adapter to add
     */
    public void add(int position, Adapter adapter) {
        mItems.add(position, adapter);
        addInternal(adapter);
    }

    private void addInternal(Adapter adapter) {
        adapter.registerAdapterDataObserver(new RecyclerMergeCascadeDataObserver(this, adapter));

        // Notify adapter in case it was added after RecyclerMergeAdapter attached to RecyclerView
        if (mRecyclerView != null) {
            adapter.onAttachedToRecyclerView(mRecyclerView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int mergeViewType) {
        Adapter adapter = getAdapterForViewType(mergeViewType);
        int adapterViewType = getRealViewTypeForViewType(mergeViewType);
        ViewHolder holder = adapter.onCreateViewHolder(parent, adapterViewType);
        mHolderAdapterMap.put(holder.hashCode(), adapter);

        // Recycler itself sets view type for the holder as pass in here, so
        // this holder has mergeViewType set in it, not adapterViewType.
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos = 0;
        for (Adapter adapter : getActiveItems()) {
            final int itemCount = adapter.getItemCount();
            if (pos + itemCount > position) {
                adapter.onBindViewHolder(holder, position - pos);
                return;
            }
            pos += itemCount;
        }
    }

    @Override
    public int getItemCount() {
        int total = 0;
        for (Adapter adapter : getActiveItems()) {
            total += adapter.getItemCount();
        }
        return total;
    }

    public int getDataItemCount() {
        return RecyclerUtils.getItemCount(mDataAdapter);
    }

    public int getDataAdapterOffset() {
        return mItems.getDataAdapterOffset();
    }

    @Override
    public int getItemViewType(int position) {
        Pair<Adapter, Integer> viewInfo = getItemInfoForPosition(position);
        if (viewInfo != null) {
            Adapter adapter = viewInfo.first;
            Integer viewType = viewInfo.second;

            Map<Integer, Integer> viewTypesMap = mAdapterToViewTypesMap.get(adapter);
            if (viewTypesMap != null) {
                Integer mergeViewType = viewTypesMap.get(viewType);
                if (mergeViewType != null) return mergeViewType;
            } else {
                viewTypesMap = new HashMap<>();
                mAdapterToViewTypesMap.put(adapter, viewTypesMap);
            }

            Integer mergeViewType = mViewTypesCounter++;
            viewTypesMap.put(viewType, mergeViewType);
            mViewTypesAdapterMap.put(mergeViewType, new Pair<>(adapter, viewType));
            return mergeViewType;
        }
        return NO_POSITION;
    }

    private Pair<Adapter, Integer> getItemInfoForPosition(int position) {
        int pos = 0;
        for (Adapter adapter : getActiveItems()) {
            final int itemCount = adapter.getItemCount();
            if (pos + itemCount > position) {
                final int itemViewType = adapter.getItemViewType(position - pos);
                return new Pair<>(adapter, itemViewType);
            }
            pos += itemCount;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        int pos = 0;
        for (Adapter adapter : getActiveItems()) {
            final int itemCount = adapter.getItemCount();
            if (pos + itemCount > position) {
                return adapter.getItemId(position - pos);
            }
            pos += itemCount;
        }
        return NO_ID;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        mHolderAdapterMap.get(holder.hashCode()).onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(ViewHolder holder) {
        return mHolderAdapterMap.get(holder.hashCode()).onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        mHolderAdapterMap.get(holder.hashCode()).onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        mHolderAdapterMap.get(holder.hashCode()).onViewDetachedFromWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        for (Adapter adapter : getItems()) {
            adapter.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        for (Adapter adapter : getItems()) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }
        mRecyclerView = null;
    }

    private Adapter getAdapterForViewType(int mergeViewType) {
        return mViewTypesAdapterMap.get(mergeViewType).first;
    }

    private Integer getRealViewTypeForViewType(int mergeViewType) {
        return mViewTypesAdapterMap.get(mergeViewType).second;
    }

    public List<Adapter> getActiveItems() {
        return mItems.getActiveItems();
    }

    private List<Adapter> getItems() {
        return mItems.getItems();
    }

    public void setActive(Adapter adapter, boolean isActive) {
        mItems.setActive(adapter, isActive);
    }

    public void setActive(View view, boolean isActive) {
        mItems.setActive(view, isActive);
    }

    @Override
    public int getSpanSize(int position) {
        int pos = 0;
        for (Adapter adapter : getActiveItems()) {
            final int itemCount = adapter.getItemCount();
            if (pos + itemCount > position) {
                if (adapter instanceof GridSpanSizeLookup) {
                    return ((GridSpanSizeLookup) adapter).getSpanSize(position - pos);
                } else {
                    break;
                }
            }
            pos += itemCount;
        }
        return 0;
    }

    /**
     * Search for adapter position in Merge adapter
     *
     * @param adapter
     * @return merge adapter position or {@link RecyclerView#NO_POSITION} if not found
     */
    protected int getStartPosition(Adapter adapter) {
        final List<Adapter> adapters = getActiveItems();
        int position = 0;
        for (Adapter a : adapters) {
            if (a == adapter) return position;
            position += a.getItemCount();
        }

        return NO_POSITION;
    }
}
