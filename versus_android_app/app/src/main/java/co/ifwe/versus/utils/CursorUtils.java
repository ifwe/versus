package co.ifwe.versus.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import co.ifwe.versus.models.LoaderContent;

public final class CursorUtils {

    private CursorUtils() {
    }

    public static int getInt(Cursor cursor, String name) {
        return getInt(cursor, name, 0);
    }

    public static int getInt(Cursor cursor, String name, int defaultValue) {
        final int index = cursor.getColumnIndex(name);
        return index == -1 ? defaultValue : cursor.getInt(index);
    }

    public static long getLong(Cursor cursor, String name, long defaultValue) {
        final int index = cursor.getColumnIndex(name);
        return index == -1 ? defaultValue : cursor.getLong(index);
    }

    public static Long getLong(Cursor cursor, String name, Long defaultValue) {
        final int index = cursor.getColumnIndex(name);
        return index == -1 ? defaultValue : cursor.getLong(index);
    }

    public static float getFloat(Cursor cursor, String name, float defaultValue) {
        final int index = cursor.getColumnIndex(name);
        return index == -1 ? defaultValue : cursor.getFloat(index);
    }

    public static String getString(Cursor cursor, String name, String defaultValue) {
        final int index = cursor.getColumnIndex(name);
        return index == -1 ? defaultValue : cursor.getString(index);
    }

    public static boolean getBoolean(Cursor cursor, String name) {
        final int index = cursor.getColumnIndex(name);
        return index != -1 && cursor.getInt(index) == 1;
    }

    public static boolean moveToPosition(Cursor cursor, int position) {
        return cursor != null && cursor.moveToPosition(position);
    }

    public static boolean moveToFirst(Cursor cursor) {
        return cursor != null && cursor.moveToFirst();
    }

    public static boolean moveToLast(Cursor cursor) {
        return cursor != null && cursor.moveToLast();
    }

    public static void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static <T extends LoaderContent> List<T> toList(Cursor cursor, Class<T> clazz) {
        List<T> list = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                try {
                    T rowObject = clazz.newInstance();
                    rowObject.fromCursor(cursor, rowObject);
                    list.add(rowObject);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static int[] createIndexes(@NonNull Cursor cursor, @NonNull String... projection) {
        int[] indexes = new int[projection.length];
        for (int i = 0, n = projection.length; i < n; i++) {
            indexes[i] = cursor.getColumnIndex(projection[i]);
        }
        return indexes;
    }

    public static boolean equals(Cursor cursor1, Cursor cursor2, int... indexes) {
        for (int i = 0, n = indexes.length; i < n; i++) {
            if (!equals(cursor1, cursor2, indexes[i])) return false;
        }
        return true;
    }

    public static List<Object[]> toMatrix(Cursor cursor, int... cursorIndexes) {
        int[] indexes;
        if (cursorIndexes.length > 0) {
            indexes = cursorIndexes;
        } else {
            indexes = new int[cursor.getColumnCount()];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = i;
            }
        }

        int count = cursor.getCount();
        List<Object[]> result = new ArrayList<>(count);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                Object[] row = new Object[indexes.length];
                for (int j = 0; j < indexes.length; j++) {
                    row[j] = getField(cursor, indexes[j]);
                }
                result.add(row);
            }
        }
        return result;
    }

    private static Object getField(Cursor cursor, int index) {
        final int type = cursor.getType(index);
        if (type == Cursor.FIELD_TYPE_FLOAT) {
            return cursor.getFloat(index);
        } else if (type == Cursor.FIELD_TYPE_INTEGER) {
            return cursor.getInt(index);
        } else if (type == Cursor.FIELD_TYPE_STRING) {
            return cursor.getString(index);
        } else if (type == Cursor.FIELD_TYPE_BLOB) {
            return cursor.getBlob(index);
        }
        return null;
    }

    public static boolean equals(Cursor cursor1, Cursor cursor2, int index) {
        final int type = cursor1.getType(index);
        if (type != cursor2.getType(index)) {
            return false;
        }

        if (type == Cursor.FIELD_TYPE_NULL) {
            return true;
        } else if (type == Cursor.FIELD_TYPE_FLOAT) {
            return Float.compare(cursor1.getFloat(index), cursor2.getFloat(index)) == 0;
        } else if (type == Cursor.FIELD_TYPE_INTEGER) {
            return cursor1.getInt(index) == cursor2.getInt(index);
        } else if (type == Cursor.FIELD_TYPE_STRING) {
            return cursor1.getString(index).equals(cursor2.getString(index));
        } else if (type == Cursor.FIELD_TYPE_BLOB) {
            return Arrays.equals(cursor1.getBlob(index), cursor2.getBlob(index));
        } else {
            return false;
        }
    }

    public static Object[] toObjectValues(Cursor cursor) {
        int count = cursor.getColumnCount();
        Object[] values = new Object[count];
        for (int i = 0; i < count; i++) {
            int type = cursor.getType(i);
            if (type == Cursor.FIELD_TYPE_NULL) {
                values[i] = null;
            } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                values[i] = cursor.getFloat(i);
            } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                values[i] = cursor.getInt(i);
            } else if (type == Cursor.FIELD_TYPE_STRING) {
                values[i] = cursor.getString(i);
            } else if (type == Cursor.FIELD_TYPE_BLOB) {
                values[i] = cursor.getBlob(i);
            }
        }

        return values;
    }

    public static int getCount(Cursor cursor) {
        return cursor != null ? cursor.getCount() : 0;
    }

    public static Cursor emptyCursor() {
        return new MatrixCursor(new String[]{BaseColumns._ID});
    }

    /**
     * Converts ContentValues into object[], by correctly mapping cursor columns
     */
    public static Object[] contentValuesToCursorRow(ContentValues cv, Cursor cursor) {
        Object[] row = new Object[cursor.getColumnCount()];
        for (Map.Entry<String, Object> entry : cv.valueSet()) {
            int columnIndex = cursor.getColumnIndex(entry.getKey());
            if (columnIndex > -1) {
                row[columnIndex] = entry.getValue();
            }
        }

        return row;
    }
}

