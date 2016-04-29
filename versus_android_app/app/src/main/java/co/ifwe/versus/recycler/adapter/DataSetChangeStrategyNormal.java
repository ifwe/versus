package co.ifwe.versus.recycler.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import co.ifwe.versus.utils.CursorUtils;

public class DataSetChangeStrategyNormal extends DataSetChangeStrategy {

    public DataSetChangeStrategyNormal(RecyclerView.Adapter recyclerAdapter) {
        super(recyclerAdapter);
    }

    protected void onCalculateDataSetChanges(Cursor oldCursor, Cursor newCursor) {
        super.onCalculateDataSetChanges(oldCursor, newCursor);

        int notifyStart = 0;
        Notify notify = Notify.UNKNOWN;

        for (int i = 0; i < mMaxCount; i++) {
            String oldId;
            if (oldCursor.moveToPosition(i)) {
                oldId = oldCursor.getString(mOldRowIdColumnIndex);
            } else {
                if (Notify.INSERT != notify) {
                    if (Notify.UNKNOWN != notify) {
                        performNotify(notify, notifyStart, i - notifyStart);
                    }
                    notify = Notify.INSERT;
                    notifyStart = i;
                }
                continue;
            }

            String newId;
            if (newCursor.moveToPosition(i)) {
                newId = newCursor.getString(mNewRowIdColumnIndex);
            } else {
                if (Notify.REMOVE != notify) {
                    if (Notify.UNKNOWN != notify) {
                        performNotify(notify, notifyStart, i - notifyStart);
                    }
                    notify = Notify.REMOVE;
                    notifyStart = i;
                }
                continue;
            }

            if (!TextUtils.equals(oldId, newId) ||
                (mChangeProjection != null && !CursorUtils.equals(oldCursor, newCursor, mChangeProjectionIndexes))) {
                if (Notify.CHANGE != notify) {
                    if (Notify.UNKNOWN != notify) {
                        performNotify(notify, notifyStart, i - notifyStart);
                    }
                    notify = Notify.CHANGE;
                    notifyStart = i;
                }
            } else {
                if (Notify.UNKNOWN != notify) {
                    performNotify(notify, notifyStart, i - notifyStart);
                    notify = Notify.UNKNOWN;
                }
            }
        }

        performNotify(notify, notifyStart, mMaxCount - notifyStart);
    }
}
