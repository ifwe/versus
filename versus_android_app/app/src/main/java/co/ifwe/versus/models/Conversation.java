package co.ifwe.versus.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.utils.CursorHelper;

public class Conversation implements Parcelable, Content {

    @SerializedName("room_name")
    private String mRoomName;

    @SerializedName("topic")
    private Topic mTopic;

    @SerializedName("status")
    private Status mStatus;

    @SerializedName("user_a")
    private String mUserAId;

    @SerializedName("user_b")
    private String mUserBId;

    @SerializedName("result")
    private Result mResult;

    @SerializedName("score_a")
    private int mScoreA;

    @SerializedName("score_b")
    private int mScoreB;

    @SerializedName("end_time")
    private long mEndTime;

    public Conversation() {
    }

    public Conversation(String roomName) {
        mRoomName = roomName;
    }

    public Conversation(QueueResult result) {
        mRoomName = result.getRoomName();
    }

    public Conversation(Parcel in) {
        mRoomName = in.readString();
        mTopic = in.readParcelable(Topic.class.getClassLoader());
        mStatus = Status.fromCode(in.readString(), Status.PENDING);
        mUserAId = in.readString();
        mUserBId = in.readString();
        mResult = Result.fromCode(in.readString(), Result.NONE);
        mScoreA = in.readInt();
        mScoreB = in.readInt();
        mEndTime = in.readLong();
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    public String getRoomName() {
        return mRoomName;
    }

    public Topic getTopic() {
        return mTopic;
    }

    public Status getStatus() {
        if (mStatus == null) {
            mStatus = Status.PENDING;
        }
        return mStatus;
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

    public Result getResult() {
        if (mResult == null) {
            mResult = Result.NONE;
        }

        return mResult;
    }

    public int getScoreA() {
        return mScoreA;
    }

    public int getScoreB() {
        return mScoreB;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public boolean isMatched() {
        return !TextUtils.isEmpty(mUserAId) && !TextUtils.isEmpty(mUserBId);
    }

    public String getTitle(Context context) {
        return mTopic.getTitle(context);
    }

    public static Conversation fromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        Conversation conversation = new Conversation();
        try {
            CursorHelper c = new CursorHelper(cursor);

            conversation.mRoomName = c.getString(VersusContract.Conversations.ROOM_NAME);
            conversation.mTopic = Topic.fromCursor(cursor);
            String status = c.getString(VersusContract.Conversations.STATUS);
            conversation.mStatus = Status.fromCode(status);
            conversation.mUserAId = c.getString(VersusContract.Conversations.USER_A);
            conversation.mUserBId = c.getString(VersusContract.Conversations.USER_B);
            String result = c.getString(VersusContract.Conversations.RESULT);
            conversation.mResult = Result.fromCode(result);
            conversation.mScoreA = c.getInt(VersusContract.Conversations.SCORE_A);
            conversation.mScoreB = c.getInt(VersusContract.Conversations.SCORE_B);
            conversation.mEndTime = c.getLong(VersusContract.Conversations.END_TIME);
        } catch (CursorIndexOutOfBoundsException ignoreIt) {
            conversation = null;
        }
        return conversation;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(VersusContract.Conversations.ROOM_NAME, getRoomName());
        values.put(VersusContract.Conversations.TOPIC_ID, getTopic().getTopicId());
        values.put(VersusContract.Conversations.STATUS, getStatus().getCode());
        values.put(VersusContract.Conversations.USER_A, getUserAId());
        values.put(VersusContract.Conversations.USER_B, getUserBId());
        values.put(VersusContract.Conversations.RESULT, getResult().getCode());
        values.put(VersusContract.Conversations.SCORE_A, getScoreA());
        values.put(VersusContract.Conversations.SCORE_B, getScoreB());
        values.put(VersusContract.Conversations.END_TIME, getEndTime());
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getRoomName());
        dest.writeParcelable(getTopic(), 0);
        dest.writeString(getStatus().getCode());
        dest.writeString(getUserAId());
        dest.writeString(getUserBId());
        dest.writeString(getResult().getCode());
        dest.writeInt(getScoreA());
        dest.writeInt(getScoreB());
        dest.writeLong(getEndTime());
    }
}
