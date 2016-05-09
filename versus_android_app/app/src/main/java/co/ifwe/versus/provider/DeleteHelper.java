package co.ifwe.versus.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

import co.ifwe.versus.utils.DatabaseUtils;
import co.ifwe.versus.utils.QueryBuilder;

public final class DeleteHelper {
    private static String sUsersCleanWhere = DatabaseUtils.DEFAULT_FALSE_WHERE; // WTF is this for?

    private DeleteHelper() { }

    public static int delete(Context ctx, SQLiteDatabase db, UriMatcher uriMatcher, Uri uri,
                             String selection, String[] selectionArgs) {
        final QueryBuilder builder = new QueryBuilder();
        final int match = uriMatcher.match(uri);
        final ArrayList<Uri> notifyUris = new ArrayList<>(5);

        switch (match) {
            case VersusUri.CATEGORIES:
                builder.table(Table.Categories.Name);
                break;
            case VersusUri.TOPICS:
                builder.table(Table.Topics.Name);
                break;
            case VersusUri.CATEGORY:
                builder.table(Table.Categories.Name);
                builder.where(
                        VersusContract.Categories.ID + " = ?",
                        uri.getLastPathSegment());
                break;
            case VersusUri.TOPIC:
                builder.table(Table.Topics.Name);
                builder.where(
                        VersusContract.Topics.ID + " = ?",
                        uri.getLastPathSegment());
                break;
            case VersusUri.CONVERSATIONS:
                builder.table(Table.Conversations.Name);
                break;
            case VersusUri.CONVERSATION:
                builder.table(Table.Conversations.Name);
                builder.where(VersusContract.Conversations.ROOM_NAME + " = ?", uri.getLastPathSegment());
                break;
            case VersusUri.MESSAGES:
                builder.table(Table.Messages.Name);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        int affected = builder.where(selection, selectionArgs).delete(db);
        if (affected > 0) {
            notifyUris.add(uri);
            DatabaseUtils.maybeNotifyChange(ctx, uriMatcher, notifyUris);
        }
        return affected;
    }
}
