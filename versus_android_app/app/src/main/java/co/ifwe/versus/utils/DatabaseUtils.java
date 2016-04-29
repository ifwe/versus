package co.ifwe.versus.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;

import java.util.List;

import co.ifwe.versus.provider.VersusContract;

public final class DatabaseUtils {
    public static final String DEFAULT_FALSE_WHERE = "0";
    private static final int FK_PRIMARY_FIELD = 4;
    private static final int FK_PRIMARY_TABLE = 2;
    private static final int FR_FOREIGN_FIELD = 3;
    private static final String[] TABLE_NAME_FIELD = {"tbl_name"};
    private static final String[] TABLE_ONLY_WHERE = {"table"};

    private DatabaseUtils() { }

    public static boolean isRowPresent(long rowId) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && rowId <= 0) ||
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && rowId > 0);
    }

    public static String[] getPathSegments(Uri uri) {
        List<String> paths = uri.getPathSegments();
        List<String> segments = paths.subList(1, paths.size());
        return segments.toArray(new String[segments.size()]);
    }

    // Selections with a qualifier aren't supported by android, to get around this all qualifiers
    // are aliased with __ instead of a period.
    public static String qualifyProjectedColumn(String qualifier, String columnName) {
        return String.format("%1$s__%2$s", qualifier, columnName);
    }

    public static String subQualify(String qualifier, String subqualifier) {
        if (qualifier == null) return subqualifier;
        return qualifyProjectedColumn(qualifier, subqualifier);
    }

    public static String qualifyProjectionColumn(String qualifier, String columnName) {
        return String.format("%1$s.%2$s AS %1$s__%2$s", qualifier, columnName);
    }

    public static String[] insertOrUpdate(SQLiteDatabase db, String table, String[] idColumns, ContentValues values) {
        final int n = idColumns.length;
        String[] ids = new String[n];
        for (int i = 0; i < n; i++) {
            ids[i] = values.getAsString(idColumns[i]);
        }

        long rowId = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        if (DatabaseUtils.isRowPresent(rowId)) {
            for (String idColumn : idColumns) {
                values.remove(idColumn);
            }
            if (values.size() > 0) {
                StringBuilder whereClauseBuilder = new StringBuilder();
                for (int i = 0; i < n - 1; i++) {
                    whereClauseBuilder.append(idColumns[i]).append(" = ? AND ");
                }
                whereClauseBuilder.append(idColumns[n - 1]).append(" = ?");

                db.update(table, values, whereClauseBuilder.toString(), ids);
            }
        }
        return ids;
    }

    public static String insertOrUpdate(SQLiteDatabase db, String table, String idColumn, ContentValues values) {
        return insertOrUpdate(db, table, new String[]{idColumn}, values)[0];
    }

    public static void maybeNotifyChange(Context context, UriMatcher matcher, Uri uri) {
        if (uri.getQueryParameter(VersusContract.QUERY_SILENT) != null) {
            return;
        }

        int match = matcher.match(uri);
        context.getContentResolver().notifyChange(uri, null);
    }

    public static void maybeNotifyChange(Context context, UriMatcher matcher, List<Uri> uriList) {
        for (Uri uri : uriList) {
            if (uri != null) maybeNotifyChange(context, matcher, uri);
        }
    }

    //WTF is this method for??
    public static String getForeignKeysWhereStatement(SQLiteDatabase db, String primaryTable, String primaryKeyField) {
        StringBuilder foreignKeysSql = new StringBuilder();
        foreignKeysSql.append(primaryKeyField).append(" NOT IN (SELECT DISTINCT ").append(primaryKeyField).append(" FROM (");

        Cursor allTablesCursor = null;
        try {
            allTablesCursor = db.query("sqlite_master", TABLE_NAME_FIELD, "type = ?", TABLE_ONLY_WHERE, null, null, null);

            if (allTablesCursor != null && allTablesCursor.moveToFirst()) {
                do {
                    String depTableName = allTablesCursor.getString(0);
                    Cursor depTablesCursor = null;
                    try {
                        depTablesCursor = db.rawQuery(String.format("PRAGMA foreign_key_list(%s)", depTableName), null);
                        if (depTablesCursor != null && depTablesCursor.moveToFirst()) {
                            do {
                                String refTable = depTablesCursor.getString(FK_PRIMARY_TABLE);
                                String refField = depTablesCursor.getString(FR_FOREIGN_FIELD);
                                String baseDepField = depTablesCursor.getString(FK_PRIMARY_FIELD);

                                if (primaryTable.equalsIgnoreCase(refTable) && primaryKeyField.equalsIgnoreCase(baseDepField)) {
                                    foreignKeysSql.append("SELECT ");
                                    foreignKeysSql.append(refField).append(" AS ").append(primaryKeyField);
                                    foreignKeysSql.append(" FROM ");
                                    foreignKeysSql.append(depTableName);
                                    foreignKeysSql.append(" UNION ");
                                }
                            } while (depTablesCursor.moveToNext());
                        }
                    } finally {
                        CursorUtils.close(depTablesCursor);
                    }
                } while (allTablesCursor.moveToNext());
            }
        } catch (Exception e) {
            return DEFAULT_FALSE_WHERE;
        } finally {
            CursorUtils.close(allTablesCursor);
        }

        foreignKeysSql.append("SELECT -1 as ").append(primaryKeyField);
        foreignKeysSql.append(") WHERE ").append(primaryKeyField).append(" IS NOT NULL)");

        return foreignKeysSql.toString();
    }
}