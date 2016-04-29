package co.ifwe.versus.recycler;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import co.ifwe.versus.recycler.adapter.RecyclerMergeAdapter;

public final class RecyclerUtils {
    private RecyclerUtils() {
        throw new RuntimeException("You can't instantiate RecyclerUtils");
    }

    public static int getSpanSize(RecyclerView view) {
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return 1;
    }

    public static int getItemCount(RecyclerView.Adapter adapter) {
        if (adapter == null) return 0;

        if (adapter instanceof RecyclerMergeAdapter) {
            return ((RecyclerMergeAdapter) adapter).getDataItemCount();
        } else {
            return adapter.getItemCount();
        }
    }

    public static int getDataAdapterOffset(RecyclerView.Adapter adapter) {
        if (adapter instanceof RecyclerMergeAdapter) {
            return ((RecyclerMergeAdapter) adapter).getDataAdapterOffset();
        } else {
            return 0;
        }
    }

    public static int getSafeTargetPosition(int position, int itemCount) {
        if (position < 0 || itemCount == 0) {
            return 0;
        }

        if (position >= itemCount) {
            return itemCount - 1;
        } else {
            return position;
        }
    }
}
