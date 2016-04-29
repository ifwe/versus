package co.ifwe.versus.utils;

import android.database.Cursor;

public class CursorHelper {

    private Cursor mCursor;
    private String mQualifier;

    public CursorHelper(Cursor cursor) {
        this(cursor, null);
    }

    public CursorHelper(Cursor cursor, String qualifier) {
        mCursor = cursor;
        mQualifier = qualifier;
    }

    public String getString(String columnName) {
        return mCursor.getString(getIndex(columnName));
    }

    public String getString(String columnName, String defaultValue) {
        return hasColumn(columnName) ? getString(columnName) : defaultValue;
    }

    public int getInt(String columnName) {
        return mCursor.getInt(getIndex(columnName));
    }

    public int getInt(String columnName, int defaultValue) {
        return hasColumn(columnName) ? getInt(columnName) : defaultValue;
    }

    public long getLong(String columnName) {
        return mCursor.getLong(getIndex(columnName));
    }

    public long getLong(String columnName, long defaultValue) {
        return hasColumn(columnName) ? getLong(columnName) : defaultValue;
    }

    public float getFloat(String columnName) {
        return mCursor.getFloat(getIndex(columnName));
    }

    public float getFloat(String columnName, float defaultValue) {
        return hasColumn(columnName) ? getFloat(columnName) : defaultValue;
    }

    public double getDouble(String columnName) {
        return mCursor.getDouble(getIndex(columnName));
    }

    public double getDouble(String columnName, double defaultValue) {
        return hasColumn(columnName) ? getDouble(columnName) : defaultValue;
    }

    public short getShort(String columnName) {
        return mCursor.getShort(getIndex(columnName));
    }

    public short getShort(String columnName, short defaultValue) {
        return hasColumn(columnName) ? getShort(columnName) : defaultValue;
    }

    public boolean getBoolean(String columnName) {
        return getBoolean(columnName, false);
    }

    public boolean getBoolean(String columnName, boolean defaultValue) {
        return hasColumn(columnName) ? (getInt(columnName) > 0) : defaultValue;
    }

    /**
     * @param columnName   column name
     * @param defaultValue default value
     * @return Boolean value for column, if field null it will return defaultValue
     */
    public Boolean getBooleanObject(String columnName, Boolean defaultValue) {
        int index = getIndex(columnName);
        if (index > -1 && !mCursor.isNull(index)) {
            return getBoolean(columnName);
        } else {
            return defaultValue;
        }
    }

    private int getIndex(String columnName) {
        if (mCursor == null) {
            return -1;
        }

        String qualifiedColumnName = columnName;

        if (mQualifier != null) {
            qualifiedColumnName = DatabaseUtils.qualifyProjectedColumn(mQualifier, columnName);
        }
        return mCursor.getColumnIndex(qualifiedColumnName);
    }

    public Cursor moveTo(int position) {
        if (mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor;
        } else {
            return null;
        }
    }

    public boolean moveToFirst() {
        return mCursor != null && mCursor.moveToFirst();
    }

    public boolean hasColumn(String columnName) {
        return getIndex(columnName) != -1;
    }

    public boolean moveToNext() {
        return mCursor != null && mCursor.moveToNext();
    }

    public boolean moveToLast() {
        return mCursor != null && mCursor.moveToLast();
    }

    public int getCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swapCursor(Cursor cursor) {
        if (cursor == mCursor) return;
        mCursor = cursor;
    }

    public static void close(Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return;
        }

        cursor.close();
    }

    public void close() {
        CursorHelper.close(mCursor);
    }
}
