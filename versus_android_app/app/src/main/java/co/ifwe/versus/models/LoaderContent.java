package co.ifwe.versus.models;

import android.database.Cursor;

public interface LoaderContent extends Content {
    <T extends LoaderContent> void fromCursor(Cursor cursor, T object);
}

