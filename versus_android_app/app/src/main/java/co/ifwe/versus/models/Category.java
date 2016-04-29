package co.ifwe.versus.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import co.ifwe.versus.adapters.ListItemModel;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.utils.CursorHelper;

/**
 * Created by schoi on 3/23/16.
 */
public class Category implements ListItemModel, Model, Parcelable, Content {
    @SerializedName("id")
    private int mId;

    @SerializedName("name")
    private String mName;

    public Category() {
    }

    public Category(String name) {
        mName = name;
    }

    protected Category(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String getListItemText() {
        return getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
    }

    public boolean equals(Object o) {
        return (o instanceof Category) && ((Category) o).getId() == getId();
    }

    public static Category fromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        Category category = new Category();
        try {
            CursorHelper c = new CursorHelper(cursor);

            category.mId = c.getInt(VersusContract.Categories.ID);
            category.mName = c.getString(VersusContract.Categories.NAME);
        } catch (CursorIndexOutOfBoundsException ignoreIt) {
            category = null;
        }
        return category;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(VersusContract.Categories.ID, getId());
        values.put(VersusContract.Categories.NAME, getName());
        return values;
    }
}
