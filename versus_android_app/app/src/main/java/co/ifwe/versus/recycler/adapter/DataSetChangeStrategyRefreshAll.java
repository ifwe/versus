package co.ifwe.versus.recycler.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

public class DataSetChangeStrategyRefreshAll extends DataSetChangeStrategy {

    public DataSetChangeStrategyRefreshAll(RecyclerView.Adapter recyclerAdapter) {
        super(recyclerAdapter);
    }

    @Override
    protected void onCalculateDataSetChanges(Cursor oldCursor, Cursor newCursor) {
        super.onCalculateDataSetChanges(oldCursor, newCursor);
        mRecyclerAdapter.notifyDataSetChanged();
    }
}
