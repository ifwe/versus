package co.ifwe.versus.utils;


import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.models.Content;

public class ContentOperationsBuilder {

    private final ArrayList<ContentProviderOperation> mBatch;
    private final ContentResolver mContentResolver;
    private final String mAuthority;
    private final Set<Uri> mUriSet;

    public enum Notification {SILENT, INDIVIDUAL, COLLAPSE}

    public ContentOperationsBuilder(ContentResolver contentResolver) {
        this(contentResolver, VersusContract.CONTENT_AUTHORITY);
    }

    public ContentOperationsBuilder(ContentResolver contentResolver, String authority) {
        mBatch = new ArrayList<>();
        mUriSet = new HashSet<>();
        mContentResolver = contentResolver;
        mAuthority = authority;
    }

    private void insertValues(Uri uri, ContentValues contentValues) {
        if (uri == null || contentValues == null || contentValues.size() == 0) return;
        mBatch.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());
    }

    public ContentOperationsBuilder insert(Uri uri, ContentValues contentValues) {
        uri = notify(uri, Notification.COLLAPSE);

        insertValues(uri, contentValues);
        return this;
    }

    public ContentOperationsBuilder insert(Uri uri, Content content) {
        return bulkInsert(uri, Collections.singletonList(content));
    }

    public ContentOperationsBuilder bulkInsert(Uri uri, List<? extends Content> contents) {
        if (contents.size() > 0) {
            uri = notify(uri, Notification.COLLAPSE);

            for (Content content : contents) {
                if (content != null) insertValues(uri, content.toContentValues());
            }
        }
        return this;
    }

    public ContentOperationsBuilder bulkInsert(Uri uri, List<ContentValues> contentValues, boolean ignoreMe) {
        if (contentValues.size() > 0) {
            uri = notify(uri, Notification.COLLAPSE);
            for (ContentValues cv : contentValues) {
                if (cv != null) insertValues(uri, cv);
            }
        }
        return this;
    }

    public ContentOperationsBuilder update(Uri uri, Content content, String selection, String[] selectionArgs) {
        return update(uri, content.toContentValues(), selection, selectionArgs, Notification.COLLAPSE);
    }

    public ContentOperationsBuilder update(Uri uri, ContentValues values, String selection,
                                           String[] selectionArgs, Notification notification) {
        uri = notify(uri, notification);

        mBatch.add(
                ContentProviderOperation
                        .newUpdate(suppressNotificationsIfNeeded(uri, notification))
                        .withValues(values)
                        .withSelection(selection, selectionArgs)
                        .build()
        );

        return this;
    }

    public ContentOperationsBuilder update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return update(uri, values, selection, selectionArgs, Notification.COLLAPSE);
    }

    public ContentOperationsBuilder delete(Uri uri, String selection, String[] selectionArgs) {
        return delete(uri, selection, selectionArgs, Notification.COLLAPSE);
    }

    public ContentOperationsBuilder delete(Uri uri, String selection, String[] selectionArgs, Notification notification) {
        uri = notify(uri, notification);

        mBatch.add(
                ContentProviderOperation
                        .newDelete(suppressNotificationsIfNeeded(uri, notification))
                        .withSelection(selection, selectionArgs)
                        .build());
        return this;
    }

    private Uri suppressNotificationsIfNeeded(Uri uri, Notification notification) {
        if (notification == Notification.SILENT) {
            return uri.buildUpon().appendQueryParameter(VersusContract.QUERY_SILENT, "1").build();
        } else {
            return uri;
        }
    }

    public void apply() {
        if (mBatch.size() == 0) {
            return;
        }
        int affected = 0;
        try {
            ContentProviderResult[] results = mContentResolver.applyBatch(mAuthority, mBatch);
            if (results == null || results.length == 0) return;

            for (int i = 0; i < results.length; i++) {
                if (results[i] != null) {
                    if (results[i].count != null) { // updates and deletes
                        affected += results[i].count;
                    } else if (results[i].uri != null) { // inserts
                        affected++;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (affected > 0) {
            for (Uri uri : mUriSet) {
                mContentResolver.notifyChange(uri, null);
            }
        }
    }

    // apply the notification method to the uri
    private Uri notify(Uri uri, Notification notify) {
        switch (notify) {
            case SILENT:
                return silence(uri);
            case COLLAPSE:
                return collapse(uri);
        }

        return uri;
    }

    // collapse multiple notifications for the uri into a single notification
    private Uri collapse(Uri uri) {
        mUriSet.add(uri);

        return silence(uri);
    }

    private Uri silence(Uri uri) {
        return silenceUri(uri);
    }

    public static Uri silenceUri(Uri uri) {
        return uri.buildUpon().appendQueryParameter(VersusContract.QUERY_SILENT, "true").build();
    }
}
