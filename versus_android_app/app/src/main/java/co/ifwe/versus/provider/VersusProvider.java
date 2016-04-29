package co.ifwe.versus.provider;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import co.ifwe.versus.BuildConfig;
import co.ifwe.versus.utils.CursorUtils;

public class VersusProvider extends ContentProvider {

    private static final String TAG = VersusProvider.class.getSimpleName();
    private static final VersusMatcher sUriMatcher = VersusMatcher.get();
    private VersusDatabase mDatabaseHelper;

    public static void reset(Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentProviderClient client = resolver.acquireContentProviderClient(BuildConfig.APPLICATION_ID);
        ((VersusProvider) client.getLocalContentProvider()).reset();
        client.release();
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        VersusDatabase databaseHelper = getDatabaseHelper();
        if (databaseHelper == null) {
            Crashlytics.logException(new RuntimeException("Database helper is null"));
            return CursorUtils.emptyCursor();
        }

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        if (database == null) {
            Crashlytics.logException(new RuntimeException("Database is null"));
            return CursorUtils.emptyCursor();
        }

        if (!database.isOpen()) {
            Crashlytics.logException(new RuntimeException("Database is closed"));
            return CursorUtils.emptyCursor();
        }

        return QueryHelper.query(getContext(), database, sUriMatcher,
                uri, selection, selectionArgs, projection, sortOrder);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return UriType.getType(uri, sUriMatcher);
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        VersusDatabase database = getDatabaseHelper();
        if (database == null) {
            return null;
        }

        return InsertHelper.insert(getContext(), database.getWritableDatabase(), sUriMatcher, uri, values);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] bulkValues) {
        VersusDatabase dbHelper = getDatabaseHelper();
        if (dbHelper == null) return 0;
        return InsertHelper.bulkInsert(getContext(), dbHelper.getWritableDatabase(), sUriMatcher, uri, bulkValues);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        VersusDatabase database = getDatabaseHelper();
        if (database == null) {
            return 0;
        }
        return DeleteHelper.delete(getContext(), database.getWritableDatabase(), sUriMatcher, uri,
                selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        VersusDatabase database = getDatabaseHelper();
        if (database == null) {
            return 0;
        }
        return UpdateHelper.update(getContext(), database.getWritableDatabase(), sUriMatcher, uri, values, selection, selectionArgs);
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        VersusDatabase dbHelper = getDatabaseHelper();
        if (dbHelper == null) {
            throw new OperationApplicationException("Database is not available");
        }

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Nullable
    private VersusDatabase getDatabaseHelper() {

        String dbName = VersusDatabase.DATABASE_NAME;
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new VersusDatabase(getContext(), dbName);

            Log.d(TAG, "!!! DATABASE OPEN !!! " + String.valueOf(mDatabaseHelper));
        }

        // We need more information to figure out:
        // https://fabric.io/taggedinc/android/apps/com.taggedapp/issues/556d15cff505b5ccf001fde7
        final String databaseName = mDatabaseHelper.getDatabaseName();
        if (TextUtils.isEmpty(databaseName)) {
            Crashlytics.log("Database name is empty!");
            return null;
        }

        if (!databaseName.equals(dbName)) {
            VersusDatabase oldDatabase = mDatabaseHelper;
            VersusDatabase newDatabase = new VersusDatabase(getContext(), dbName);

            oldDatabase.close();
            mDatabaseHelper = newDatabase;

            Log.d(TAG, "!!! DATABASE SWITCH !!! old: " + String.valueOf(oldDatabase) +
                    ", new: " + String.valueOf(newDatabase));
        }

        return mDatabaseHelper;
    }

    private void reset() {
        Log.d(TAG, "!!! DATABASE CLOSE !!! " + String.valueOf(mDatabaseHelper));

        if (mDatabaseHelper != null) {
            mDatabaseHelper.close();
            mDatabaseHelper = null;
        }
    }
}
