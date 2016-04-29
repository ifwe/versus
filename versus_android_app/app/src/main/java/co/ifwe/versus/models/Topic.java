package co.ifwe.versus.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

import co.ifwe.versus.VersusApplication;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.utils.CursorHelper;

public class Topic implements Parcelable, Content {

    @SerializedName("id")
    private int mTopicId;

    @SerializedName("side_a")
    private String mSideA;

    @SerializedName("side_b")
    private String mSideB;

    @SerializedName("side_a_fr")
    private String mSideAFrench;

    @SerializedName("side_b_fr")
    private String mSideBFrench;

    @SerializedName("side_a_img_url")
    private String mSideAUrl;

    @SerializedName("side_b_img_url")
    private String mSideBUrl;

    @SerializedName("category")
    private int mCategoryId;

    public Topic() {

    }

    public Topic(Parcel in) {
        mTopicId = in.readInt();
        mSideA = in.readString();
        mSideB = in.readString();
        mSideAFrench = in.readString();
        mSideBFrench = in.readString();
        mSideAUrl = in.readString();
        mSideBUrl = in.readString();
        mCategoryId = in.readInt();
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    public int getTopicId() {
        return mTopicId;
    }

    public String getSideA() {
        if (TextUtils.equals(Locale.getDefault().getDisplayLanguage(), "français")) {
            return mSideAFrench;
        } else {
            return mSideA;
        }
    }

    public String getSideB() {
        if (TextUtils.equals(Locale.getDefault().getDisplayLanguage(), "français")) {
            return mSideBFrench;
        } else {
            return mSideB;
        }
    }

    public String getSideAEnglish() {
        return mSideA;
    }

    public String getSideBEnglish() {
        return mSideB;
    }

    public String getSideAFrench() {
        return mSideAFrench;
    }

    public String getSideBFrench() {
        return mSideBFrench;
    }

    public String getSideAUrl() {
        return mSideAUrl;
    }

    public String getSideBUrl() {
        return mSideBUrl;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public String getTitle() {
        return mSideA + " vs " + mSideB;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mTopicId);
        dest.writeString(mSideA);
        dest.writeString(mSideB);
        dest.writeString(mSideAFrench);
        dest.writeString(mSideBFrench);
        dest.writeString(mSideAUrl);
        dest.writeString(mSideBUrl);
        dest.writeInt(mCategoryId);
    }

    public static Topic fromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        Topic topic = new Topic();
        try {
            CursorHelper c = new CursorHelper(cursor);

            topic.mTopicId = c.getInt(VersusContract.Topics.ID);
            topic.mSideA = c.getString(VersusContract.Topics.SIDE_A);
            topic.mSideB = c.getString(VersusContract.Topics.SIDE_B);
            topic.mSideAFrench = c.getString(VersusContract.Topics.SIDE_A_FR);
            topic.mSideBFrench = c.getString(VersusContract.Topics.SIDE_B_FR);
            topic.mSideAUrl = c.getString(VersusContract.Topics.SIDE_A_URL);
            topic.mSideBUrl = c.getString(VersusContract.Topics.SIDE_B_URL);
            topic.mCategoryId = c.getInt(VersusContract.Topics.CATEGORY_ID);
        } catch (CursorIndexOutOfBoundsException ignoreIt) {
            topic = null;
        }
        return topic;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(VersusContract.Topics.ID, getTopicId());
        values.put(VersusContract.Topics.SIDE_A, getSideAEnglish());
        values.put(VersusContract.Topics.SIDE_B, getSideBEnglish());
        values.put(VersusContract.Topics.SIDE_A_FR, getSideAFrench());
        values.put(VersusContract.Topics.SIDE_B_FR, getSideBFrench());
        values.put(VersusContract.Topics.SIDE_A_URL, getSideAUrl());
        values.put(VersusContract.Topics.SIDE_B_URL, getSideBUrl());
        values.put(VersusContract.Topics.CATEGORY_ID, getCategoryId());
        return values;
    }
}
