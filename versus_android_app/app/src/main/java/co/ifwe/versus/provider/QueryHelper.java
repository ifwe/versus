package co.ifwe.versus.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import co.ifwe.versus.utils.CursorUtils;
import co.ifwe.versus.utils.QueryBuilder;

public final class QueryHelper {
    private final static String TAG = QueryHelper.class.getSimpleName();

    private QueryHelper() { }

    public static Cursor query(Context ctx, @NonNull SQLiteDatabase db, UriMatcher uriMatcher, Uri uri,
                               String selection, String[] selectionArgs, String[] projection,
                               String sortOrder) {

        final int match = uriMatcher.match(uri);
        final QueryBuilder builder = new QueryBuilder();
        String groupBy = null;

        if (!TextUtils.isEmpty(selection)) {
            builder.where(selection, selectionArgs);
        }

        switch (match) {
            case VersusUri.CATEGORY:
                builder.table(Table.Categories.Name);
                builder.where(VersusContract.Categories.ID + " = ?", uri.getLastPathSegment());
                break;
            case VersusUri.CATEGORIES:
                builder.table(Table.Categories.Name);
                break;
            case VersusUri.TOPIC:
                builder.table(Table.Topics.Name);
                builder.where(VersusContract.Categories.ID + " = ?", uri.getLastPathSegment());
                break;
            case VersusUri.TOPICS:
                builder.table(Table.Topics.Name);
                break;
            case VersusUri.CONVERSATION:
                builder.table(Table.Conversations.Name);
                builder.innerJoin(Table.Topics.Name, Qualified.TOPIC_TOPIC_ID, Qualified.CONVERSATION_TOPIC_ID);
                builder.where(VersusContract.Conversations.ROOM_NAME + " = ?", uri.getLastPathSegment());
                break;
            case VersusUri.CONVERSATIONS:
                builder.table(Table.Conversations.Name);
                builder.innerJoin(Table.Topics.Name, Qualified.TOPIC_TOPIC_ID, Qualified.CONVERSATION_TOPIC_ID);
                break;
            case VersusUri.RESULTS:
                builder.table(Table.Conversations.Name);
                groupBy = VersusContract.Conversations.RESULT;
                break;
            case VersusUri.MESSAGES:
                builder.table(Table.Messages.Name);
                break;
            default:
                IllegalArgumentException exception = new IllegalArgumentException("Unsupported URI: " + uri);
                Crashlytics.logException(exception);
                throw exception;
        }

        String limit = uri.getQueryParameter(VersusContract.QUERY_LIMIT);
        try {
            Cursor cursor = builder.query(db, projection, groupBy, null, sortOrder, limit);
            cursor.setNotificationUri(ctx.getContentResolver(), uri);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Projection failed for URI: " + uri.toString() + ", sql: " + builder.toString());

            Crashlytics.setString("uri", uri.toString());
            Crashlytics.setString("sql", builder.toString());
            Crashlytics.logException(e);

            return CursorUtils.emptyCursor();
        }
    }
}
