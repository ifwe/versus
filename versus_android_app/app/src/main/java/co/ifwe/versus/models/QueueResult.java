package co.ifwe.versus.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import co.ifwe.versus.provider.VersusContract;

public class QueueResult implements Parcelable, Content {
    @SerializedName("room_name")
    private String mRoomName;

    @SerializedName("topic_id")
    private int mTopicId;

    @SerializedName("user_a")
    private String mUserAId;

    @SerializedName("user_b")
    private String mUserBId;

    @SerializedName("end_time")
    private long mEndTime;

    public QueueResult() {
    }

    public QueueResult(Parcel in) {
        mRoomName = in.readString();
        mTopicId = in.readInt();
        mUserAId = in.readString();
        mUserBId = in.readString();
        mEndTime = in.readLong();
    }

    public static final Creator<QueueResult> CREATOR = new Creator<QueueResult>() {
        @Override
        public QueueResult createFromParcel(Parcel in) {
            return new QueueResult(in);
        }

        @Override
        public QueueResult[] newArray(int size) {
            return new QueueResult[size];
        }
    };

    public String getRoomName() {
        return mRoomName;
    }

    public int getTopicId() {
        return mTopicId;
    }

    public String getUserAId() {
        if (mUserAId == null) {
            mUserAId = "";
        }
        return mUserAId;
    }

    public String getUserBId() {
        if (mUserBId == null) {
            mUserBId = "";
        }
        return mUserBId;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public boolean isMatched() {
        return !TextUtils.isEmpty(mUserAId) && !TextUtils.isEmpty(mUserBId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getRoomName());
        dest.writeInt(mTopicId);
        dest.writeString(mUserAId);
        dest.writeString(mUserBId);
        dest.writeLong(mEndTime);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(VersusContract.Conversations.ROOM_NAME, getRoomName());
        values.put(VersusContract.Conversations.TOPIC_ID, getTopicId());
        values.put(VersusContract.Conversations.USER_A, getUserAId());
        values.put(VersusContract.Conversations.USER_B, getUserBId());
        Status status = isMatched() ? Status.ACTIVE : Status.PENDING;
        values.put(VersusContract.Conversations.STATUS, status.getCode());
        values.put(VersusContract.Conversations.END_TIME, getEndTime());
        return values;
    }

}
