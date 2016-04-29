package co.ifwe.versus.recycler.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RecyclerMergeAdapterItems {
    private static class ItemState {
        RecyclerView.Adapter adapter;
        boolean isActive;

        public ItemState(RecyclerView.Adapter adapter, boolean isActive) {
            this.adapter = adapter;
            this.isActive = isActive;
        }
    }

    private final ArrayList<ItemState> mItemStates = new ArrayList<>();
    private final RecyclerMergeAdapter mMergeAdapter;

    public RecyclerMergeAdapterItems(RecyclerMergeAdapter mergeAdapter) {
        mMergeAdapter = mergeAdapter;
    }

    public void add(RecyclerView.Adapter adapter) {
        mItemStates.add(new ItemState(adapter, true));
    }

    public void add(int position, RecyclerView.Adapter adapter) {
        mItemStates.add(position, new ItemState(adapter, true));
    }

    public void setActive(RecyclerView.Adapter adapter, boolean isActive) {
        for (ItemState state : mItemStates) {
            if (state.adapter == adapter) {
                setActive(state, isActive);
                break;
            }
        }
    }

    public void setActive(View view, boolean isActive) {
        for (ItemState state : mItemStates) {
            if (RecyclerViewAdapter.hasView(state.adapter, view)) {
                setActive(state, isActive);
                break;
            }
        }
    }

    private void setActive(ItemState state, boolean isActive) {
        if (state.isActive == isActive) {
            return;
        }

        final int count = state.adapter.getItemCount();
        if (count == 0) {
            state.isActive = isActive;
        } else if (state.isActive) {
            final int start = mMergeAdapter.getStartPosition(state.adapter);
            state.isActive = isActive;

            mMergeAdapter.notifyItemRangeRemoved(start, count);
        } else {
            state.isActive = isActive;
            final int start = mMergeAdapter.getStartPosition(state.adapter);

            mMergeAdapter.notifyItemRangeInserted(start, count);
        }
    }

    public List<RecyclerView.Adapter> getActiveItems() {
        ArrayList<RecyclerView.Adapter> items = new ArrayList<>(mItemStates.size());
        for (ItemState state : mItemStates) {
            if (state.isActive) {
                items.add(state.adapter);
            }
        }
        return items;
    }

    public List<RecyclerView.Adapter> getItems() {
        ArrayList<RecyclerView.Adapter> items = new ArrayList<>(mItemStates.size());

        for (ItemState state : mItemStates) {
            items.add(state.adapter);
        }
        return items;
    }

    public int getDataAdapterOffset() {
        List<RecyclerView.Adapter> items = getActiveItems();
        int offset = 0;

        for (RecyclerView.Adapter itemAdapter : items) {
            if (itemAdapter == mMergeAdapter.getDataAdapter()) {
                break;
            } else {
                offset += itemAdapter.getItemCount();
            }
        }
        return offset;
    }
}


