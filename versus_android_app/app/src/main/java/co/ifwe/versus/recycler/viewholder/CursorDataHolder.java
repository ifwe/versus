package co.ifwe.versus.recycler.viewholder;

import android.database.Cursor;

public interface CursorDataHolder {
    CursorDataHolder EMPTY = new CursorDataHolder() {
        @Override
        public void from(Cursor cursor) {}
    };

    void from(Cursor cursor);
}
