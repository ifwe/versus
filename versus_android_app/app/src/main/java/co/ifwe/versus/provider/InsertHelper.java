package co.ifwe.versus.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import co.ifwe.versus.utils.DatabaseUtils;

public final class InsertHelper {
    private final static String TAG = InsertHelper.class.getSimpleName();

    private InsertHelper() { }

    public static Uri insert(Context context, SQLiteDatabase db, UriMatcher uriMatcher, Uri uri, ContentValues values) {
        final int match = uriMatcher.match(uri);
        Uri itemUri;
        String id;
        long rowId;
        final ArrayList<Uri> notifyUris = new ArrayList<>(5);

        switch (match) {
            case VersusUri.CATEGORIES:
            case VersusUri.CATEGORY:
                db.insertWithOnConflict(Table.Categories.Name, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                int categoryId = values.getAsInteger(VersusContract.Categories.ID);
                itemUri = VersusContract.Categories.buildCategoryUri(categoryId);
                break;
            case VersusUri.TOPICS:
            case VersusUri.TOPIC:
                db.insertWithOnConflict(Table.Topics.Name, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                int topicId = values.getAsInteger(VersusContract.Topics.ID);
                itemUri = VersusContract.Topics.buildTopicUri(topicId);
                break;
            case VersusUri.CONVERSATIONS:
            case VersusUri.CONVERSATION:
                db.insertWithOnConflict(Table.Conversations.Name, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                String roomName = values.getAsString(VersusContract.Conversations.ROOM_NAME);
                itemUri = VersusContract.Conversations.buildConversationUri(roomName);
                break;
            case VersusUri.MESSAGES:
                db.insertWithOnConflict(Table.Messages.Name, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                String room = values.getAsString(VersusContract.Messages.ROOM_NAME);
                itemUri = VersusContract.Messages.buildConversationUri(room);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        notifyUris.add(uri);
        DatabaseUtils.maybeNotifyChange(context, uriMatcher, notifyUris);
        return itemUri;
    }

    public static int bulkInsert(Context context, SQLiteDatabase db, UriMatcher uriMatcher, Uri uri, ContentValues[] bulkValues) {
        final int match = uriMatcher.match(uri);
        String table = UriType.getTable(match);
        int added = 0;

        try {
            db.beginTransaction();
            for (ContentValues values : bulkValues) {
                int numUpdated;
                switch (match) {
                    case VersusUri.CATEGORIES:
                    case VersusUri.TOPICS:
                    case VersusUri.CONVERSATIONS:
                        numUpdated = 0;
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported URI: " + uri);
                }

                if (numUpdated == 0 &&
                    db.insertWithOnConflict(table, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE) > 0) {

                    added++;
                }
            }

            db.setTransactionSuccessful();
            DatabaseUtils.maybeNotifyChange(context, uriMatcher, uri);
        } catch (Exception ex) {
            added = 0;
            Log.e(TAG, "Failed to bulk insert: " + ex.getMessage());
        } finally {
            db.endTransaction();
        }

        return added;
    }
}
