package co.ifwe.versus.recycler.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import co.ifwe.versus.utils.CursorUtils;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.myers.Equalizer;

public class DataSetChangeStrategyMyers extends DataSetChangeStrategy {

    private static final String TAG = "DataSetMyers";

    static class RowEqualizer implements Equalizer<Object[]> {
        @Override
        public boolean equals(Object[] original, Object[] revised) {
            return Arrays.deepEquals(original, revised);
        }
    }

    private static final RowEqualizer sRowEqualizer = new RowEqualizer();

    public DataSetChangeStrategyMyers(RecyclerView.Adapter recyclerAdapter) {
        super(recyclerAdapter);
    }

    @Override
    protected void onCalculateDataSetChanges(Cursor oldCursor, Cursor newCursor) {
        super.onCalculateDataSetChanges(oldCursor, newCursor);

        List<Object[]> oldFieldList;
        List<Object[]> newFieldList;
        if (mChangeProjectionIndexes == null) {
            oldFieldList = CursorUtils.toMatrix(oldCursor);
            newFieldList = CursorUtils.toMatrix(newCursor);
        } else {
            oldFieldList = CursorUtils.toMatrix(oldCursor, mChangeProjectionIndexes);
            newFieldList = CursorUtils.toMatrix(newCursor, mChangeProjectionIndexes);
        }

        List<Delta<Object[]>> deltas = DiffUtils.diff(oldFieldList, newFieldList, sRowEqualizer).getDeltas();

        for (Delta<Object[]> delta : deltas) {
            switch (delta.getType()) {
                case CHANGE:
                    performNotifyChange(delta.getOriginal(), delta.getRevised());
                    break;
                case INSERT:
                    performNotify(Notify.INSERT, delta.getRevised().getPosition(), delta.getRevised().size());
                    break;
                case DELETE:
                    performNotify(Notify.REMOVE, delta.getRevised().getPosition(), delta.getOriginal().size());
                    break;
                default:
                    Log.e(TAG, "Unexpected change type");
            }
        }
    }

    private void performNotifyChange(Chunk<Object[]> original, Chunk<Object[]> revised) {
        int reallyChangedCount = Math.min(original.size(), revised.size());
        performNotify(Notify.CHANGE, revised.getPosition(), reallyChangedCount);

        int remainingChunkPosition = revised.getPosition() + reallyChangedCount;
        if (original.size() > revised.size()) {
            performNotify(Notify.REMOVE, remainingChunkPosition, original.size() - reallyChangedCount);
        } else if (original.size() < revised.size()) {
            performNotify(Notify.INSERT, remainingChunkPosition, revised.size() - reallyChangedCount);
        }
    }
}
