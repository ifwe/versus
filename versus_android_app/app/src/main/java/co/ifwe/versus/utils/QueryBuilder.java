package co.ifwe.versus.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryBuilder {
    private static final String TAG = QueryBuilder.class.toString();

    private String mTable = null;
    private ArrayList<String> mJoins = new ArrayList<String>();
    private Map<String, String> mProjectionMap = new HashMap<String, String>();
    private StringBuilder mSelection = new StringBuilder();
    private ArrayList<String> mSelectionArgs = new ArrayList<String>();

    /**
     * Reset any internal state, allowing this builder to be recycled.
     */
    public QueryBuilder reset() {
        mTable = null;
        mJoins.clear();
        mSelection.setLength(0);
        mSelectionArgs.clear();
        return this;
    }

    /**
     * Append the given selection clause to the internal state. Each clause is
     * surrounded with parenthesis and combined using {@code AND}.
     */
    public QueryBuilder where(String selection, String... selectionArgs) {
        if (TextUtils.isEmpty(selection)) {
            if (selectionArgs != null && selectionArgs.length > 0) {
                throw new IllegalArgumentException(
                    "Valid selection required when including arguments=");
            }

            // Shortcut when clause is empty
            return this;
        }

        if (mSelection.length() > 0) {
            mSelection.append(" AND ");
        }

        mSelection.append("(").append(selection).append(")");
        if (selectionArgs != null) {
            Collections.addAll(mSelectionArgs, selectionArgs);
        }

        return this;
    }

    public QueryBuilder table(String table) {
        mTable = table;
        return this;
    }

    public QueryBuilder innerJoin(String table, String field1, String field2) {
        mJoins.add(" INNER JOIN " + table + " ON " + field1 + " = " + field2);
        return this;
    }

    public QueryBuilder leftOuterJoin(String table, String[] leftFields, String[] rightFields) {
        return join("LEFT OUTER JOIN", table, leftFields, rightFields);
    }

    public QueryBuilder innerJoin(String table, String[] leftFields, String[] rightFields) {
        return join("INNER JOIN", table, leftFields, rightFields);
    }

    private QueryBuilder join(String joinType, String table, String[] leftFields, String[] rightFields) {
        final int n = leftFields.length;
        if (n != rightFields.length) {
            throw new IllegalArgumentException(
                "leftFields and rightFields must have same number of elements!");
        }

        StringBuilder joinStringBuilder = new StringBuilder();
        joinStringBuilder.append(" ").append(joinType).append(" ").append(table).append(" ON ( ");
        for (int i = 0; i < n - 1; i++) {
            joinStringBuilder.append(leftFields[i]).append(" = ").append(rightFields[i]).append(" AND ");
        }
        joinStringBuilder.append(leftFields[n - 1]).append(" = ").append(rightFields[n - 1]).append(" )");
        mJoins.add(joinStringBuilder.toString());
        return this;
    }

    public QueryBuilder leftOuterJoin(String table, String field1, String field2) {
        mJoins.add(" LEFT OUTER JOIN " + table + " ON " + field1 + " = " + field2);
        return this;
    }

    public QueryBuilder leftOuterJoin(String table, String as, String field1, String field2) {
        mJoins.add(" LEFT OUTER JOIN " + table + " AS " + as + " ON " + field1 + " = " + field2);
        return this;
    }

    private void assertTable() {
        if (mTable == null) {
            throw new IllegalStateException("Table not specified");
        }
    }

    public QueryBuilder mapToTable(String column, String table) {
        mProjectionMap.put(column, table + "." + column);
        return this;
    }

    public QueryBuilder map(String fromColumn, String toClause) {
        mProjectionMap.put(fromColumn, toClause + " AS " + fromColumn);
        return this;
    }

    /**
     * Return selection string for current internal state.
     *
     * @see #getSelectionArgs()
     */
    public String getSelection() {
        return mSelection.toString();
    }

    /**
     * Return selection arguments for current internal state.
     *
     * @see #getSelection()
     */
    public String[] getSelectionArgs() {
        return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
    }

    private void mapColumns(String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            final String target = mProjectionMap.get(columns[i]);
            if (target != null) {
                columns[i] = target;
            }
        }
    }

    @Override
    public String toString() {
        return "SelectionBuilder[table=" + mTable + TextUtils.join(" ", mJoins) + ", selection=" + getSelection()
            + ", selectionArgs=" + Arrays.toString(getSelectionArgs()) + "]";
    }

    /**
     * Execute query using the current internal state as {@code WHERE} clause.
     */
    public Cursor query(SQLiteDatabase db, String[] columns, String orderBy) {
        return query(db, columns, null, null, orderBy, null);
    }

    /**
     * Execute query using the current internal state as {@code WHERE} clause.
     */
    public Cursor query(SQLiteDatabase db, String[] columns, String groupBy,
                        String having, String orderBy, String limit) {
        assertTable();
        if (columns != null) mapColumns(columns);
        return db.query(mTable + TextUtils.join(" ", mJoins), columns, getSelection(), getSelectionArgs(), groupBy, having,
            orderBy, limit);
    }

    /**
     * Execute update using the current internal state as {@code WHERE} clause.
     */
    public int update(SQLiteDatabase db, ContentValues values) {
        assertTable();
        return db.update(mTable, values, getSelection(), getSelectionArgs());
    }

    /**
     * Execute delete using the current internal state as {@code WHERE} clause.
     */
    public int delete(SQLiteDatabase db) {
        assertTable();
        return db.delete(mTable, getSelection(), getSelectionArgs());
    }
}
