package co.ifwe.versus.recycler.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import co.ifwe.versus.BuildConfig;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

public class RecyclerMergeCascadeDataObserver extends RecyclerView.AdapterDataObserver {
    private final boolean mDebug = BuildConfig.DEBUG && true;
    private final String mTag;

    // Adapter where this updates are coming from
    private final RecyclerView.Adapter mAdapter;

    private final RecyclerMergeAdapter mMergeAdapter;

    public RecyclerMergeCascadeDataObserver(RecyclerMergeAdapter mergeAdapter, RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mMergeAdapter = mergeAdapter;
        mTag = getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
    }

    @Override
    public void onChanged() {
        if (mDebug) Log.d(mTag, mAdapter + " onChanged");

        final int adapterStartPos = mMergeAdapter.getStartPosition(mAdapter);
        if (adapterStartPos != NO_POSITION) {
            mMergeAdapter.notifyDataSetChanged();
        } else {
            if (mDebug) {
                Log.d(mTag, "Calling 'onChanged' on deactivated adapter, ignoring...");
            }
        }
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        if (mDebug) {
            Log.d(mTag, mAdapter + " onItemRangeChanged "
                + "positionStart: " + positionStart + ", "
                + "itemCount: " + itemCount);
        }

        final int adapterStartPos = mMergeAdapter.getStartPosition(mAdapter);
        if (adapterStartPos != NO_POSITION) {
            final int start = adapterStartPos + positionStart;
            mMergeAdapter.notifyItemRangeChanged(start, itemCount);
        } else {
            if (mDebug) {
                Log.d(mTag, "Calling 'onItemRangeChanged' on deactivated adapter, ignoring...");
            }
        }
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        if (mDebug) {
            Log.d(mTag, mAdapter + " onItemRangeInserted "
                + "positionStart: " + positionStart + ", "
                + "itemCount: " + itemCount);
        }

        final int adapterStartPos = mMergeAdapter.getStartPosition(mAdapter);
        if (adapterStartPos != NO_POSITION) {
            final int start = adapterStartPos + positionStart;
            mMergeAdapter.notifyItemRangeInserted(start, itemCount);
        } else {
            if (mDebug) {
                Log.d(mTag, "Calling 'onItemRangeInserted' on deactivated adapter, ignoring...");
            }
        }
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        if (mDebug) {
            Log.d(mTag, mAdapter + " onItemRangeRemoved "
                + "positionStart: " + positionStart + ", "
                + "itemCount: " + itemCount);
        }

        final int adapterStartPos = mMergeAdapter.getStartPosition(mAdapter);
        if (adapterStartPos != NO_POSITION) {
            final int start = adapterStartPos + positionStart;
            mMergeAdapter.notifyItemRangeRemoved(start, itemCount);
        } else {
            if (mDebug) {
                Log.d(mTag, "Calling 'notifyItemRangeRemoved' on deactivated adapter, ignoring...");
            }
        }
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        if (mDebug) {
            Log.d(mTag, mAdapter + " onItemRangeMoved "
                + "fromPosition: " + fromPosition + ", "
                + "toPosition: " + toPosition + ", "
                + "itemCount: " + itemCount);
        }

        final int adapterStartPos = mMergeAdapter.getStartPosition(mAdapter);
        if (adapterStartPos != NO_POSITION) {
            final int from = adapterStartPos + fromPosition;
            final int to = adapterStartPos + toPosition;

            // FIXME: BUG???
            onItemRangeMoved(from, to, itemCount);
        } else {
            if (mDebug) {
                Log.d(mTag, "Calling 'onItemRangeMoved' on deactivated adapter, ignoring...");
            }
        }
    }
}
