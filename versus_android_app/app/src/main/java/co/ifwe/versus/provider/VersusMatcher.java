package co.ifwe.versus.provider;

import android.content.UriMatcher;

public final class VersusMatcher extends UriMatcher {

    private static VersusMatcher sInstance = null;

    private VersusMatcher() {
        super(UriMatcher.NO_MATCH);
    }

    public static VersusMatcher get() {
        if (sInstance == null) {
            sInstance = new VersusMatcher();
            sInstance.init();
        }
        return sInstance;
    }

    private void init() {
        String authority = VersusContract.CONTENT_AUTHORITY;

        addURI(authority, VersusContract.Categories.CONTENT_PATH, VersusUri.CATEGORIES);
        addURI(authority, VersusContract.Categories.CONTENT_PATH + "/#", VersusUri.CATEGORY);

        addURI(authority, VersusContract.Topics.CONTENT_PATH, VersusUri.TOPICS);
        addURI(authority, VersusContract.Topics.CONTENT_PATH + "/#", VersusUri.TOPIC);

        addURI(authority, VersusContract.Conversations.CONTENT_PATH, VersusUri.CONVERSATIONS);
        addURI(authority, VersusContract.Conversations.CONTENT_PATH + "/#", VersusUri.CONVERSATION);
        addURI(authority, VersusContract.Conversations.CONTENT_PATH + "/*", VersusUri.RESULTS);

        addURI(authority, VersusContract.Messages.CONTENT_PATH, VersusUri.MESSAGES);
        addURI(authority, VersusContract.Messages.CONTENT_PATH + "/#", VersusUri.MESSAGES);
    }
}
