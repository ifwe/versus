package co.ifwe.versus.provider;

import android.net.Uri;

public final class UriType {
    private UriType() { }

    public static String getType(Uri uri, VersusMatcher matcher) {
        final int match = matcher.match(uri);

        switch (match) {
            case VersusUri.CATEGORIES:
                return VersusContract.Categories.CONTENT_TYPE;
            case VersusUri.CATEGORY:
                return VersusContract.Categories.CONTENT_ITEM_TYPE;
            case VersusUri.TOPICS:
                return VersusContract.Topics.CONTENT_TYPE;
            case VersusUri.TOPIC:
                return VersusContract.Topics.CONTENT_ITEM_TYPE;
            case VersusUri.CONVERSATIONS:
                return VersusContract.Conversations.CONTENT_TYPE;
            case VersusUri.CONVERSATION:
                return VersusContract.Conversations.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public static String getTable(int uri) {
        switch (uri) {
            case VersusUri.CATEGORIES:
            case VersusUri.CATEGORY:
                return Table.Categories.Name;
            case VersusUri.TOPICS:
            case VersusUri.TOPIC:
                return Table.Topics.Name;
            case VersusUri.CONVERSATIONS:
            case VersusUri.CONVERSATION:
                return Table.Conversations.Name;
            default:
                return null;
        }
    }
}
