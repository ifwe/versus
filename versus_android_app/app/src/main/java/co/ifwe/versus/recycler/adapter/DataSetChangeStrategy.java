package co.ifwe.versus.recycler.adapter;

import android.database.Cursor;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;

import co.ifwe.versus.utils.CursorUtils;

public abstract class DataSetChangeStrategy {
    protected enum Notify {
        INSERT,
        REMOVE,
        CHANGE,
        UNKNOWN,
    }

    protected final RecyclerView.Adapter mRecyclerAdapter;

    protected int mOldRowIdColumnIndex;
    protected int mNewRowIdColumnIndex;
    protected int mOldCount;
    protected int mNewCount;
    protected int mMaxCount;

    protected String[] mChangeProjection = null;
    protected int[] mChangeProjectionIndexes = null;

    public DataSetChangeStrategy(RecyclerView.Adapter recyclerAdapter) {
        mRecyclerAdapter = recyclerAdapter;
    }

    /**
     * Set field name that you want to be compared on item change
     *
     * @param projection list of fields
     */
    public void setChangeProjection(String... projection) {
        mChangeProjection = projection;
        mChangeProjectionIndexes = null;
    }

    protected void ensureChangeProjectionIndexes(Cursor cursor) {
        if (mChangeProjection != null && mChangeProjectionIndexes == null) {
            mChangeProjectionIndexes = CursorUtils.createIndexes(cursor, mChangeProjection);
        }
    }

    protected void getCursorMetaData(Cursor oldCursor, Cursor newCursor) {
        mOldRowIdColumnIndex = oldCursor.getColumnIndexOrThrow("_id");
        mNewRowIdColumnIndex = newCursor.getColumnIndexOrThrow("_id");

        mOldCount = oldCursor.getCount();
        mNewCount = newCursor.getCount();
        mMaxCount = Math.max(mOldCount, mNewCount);
    }

    protected void performNotify(Notify notify, int start, int count) {
        switch (notify) {
            case INSERT:
                mRecyclerAdapter.notifyItemRangeInserted(start, count);
                break;
            case REMOVE:
                mRecyclerAdapter.notifyItemRangeRemoved(start, count);
                break;
            case CHANGE:
                mRecyclerAdapter.notifyItemRangeChanged(start, count);
                break;
        }
    }

    @CallSuper
    protected void onCalculateDataSetChanges(Cursor oldCursor, Cursor newCursor) {
        ensureChangeProjectionIndexes(newCursor);
        getCursorMetaData(oldCursor, newCursor);
    }
}
