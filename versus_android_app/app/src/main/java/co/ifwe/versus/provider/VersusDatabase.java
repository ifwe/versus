package co.ifwe.versus.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VersusDatabase extends SQLiteOpenHelper {
    public final static String TAG = VersusDatabase.class.getSimpleName();

    public static final String DATABASE_NAME = "versus";
    public static final String FK_TRUE = "1";
    public static final String UNREFERENCED = "unreferenced";

    private static final int DATABASE_VERSION = 15;

    public VersusDatabase(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            //Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }

        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        reCreateTables(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        reCreateTables(db);
    }

    public void reCreateTables(SQLiteDatabase db) {
        dropTables(db);
        createTables(db);
    }

    public void createTables(SQLiteDatabase db) {
        db.execSQL(Table.Categories.CreateSql);
        db.execSQL(Table.Topics.CreateSql);
        db.execSQL(Table.Conversations.CreateSql);
        db.execSQL(Table.Messages.CreateSql);
    }

    public void dropTables(SQLiteDatabase db) {
        dropTable(db, Table.Categories.Name);
        dropTable(db, Table.Topics.Name);
        dropTable(db, Table.Conversations.Name);
        dropTable(db, Table.Messages.Name);
    }

    private void dropTable(SQLiteDatabase db, String table) {
        try {
            db.execSQL("DROP TABLE IF EXISTS '" + table + "'");
        } catch (SQLiteException e) {
            Log.e(TAG, "Failed to drop table " + table);
        }
    }

    private void dropView(SQLiteDatabase db, String view) {
        try {
            db.execSQL("DROP VIEW IF EXISTS '" + view + "'");
        } catch (SQLiteException e) {
            Log.e(TAG, "Failed to drop view " + view);
        }
    }

    @Override
    public String toString() {
        return "TaggedDatabase {" +
                "databaseName: " + getDatabaseName() +
                "}";
    }
}
