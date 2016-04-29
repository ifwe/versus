package co.ifwe.versus.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.utils.CursorHelper;

/**
 * Created by schoi on 3/16/16.
 */
public class ChatMessage implements Parcelable, Content {

    @SerializedName("userId")
    private String mUserId;

    @SerializedName("roomName")
    private String mRoomName;

    @SerializedName("timestamp")
    private long mTimestamp;

    @SerializedName("message")
    private String mMessage;

    private ChatMessage() {

    }

    public ChatMessage(String roomName, String userId, long timestamp, String message) {
        mRoomName = roomName;
        mUserId = userId;
        mTimestamp = timestamp;
        mMessage = message;
    }

    protected ChatMessage(Parcel in) {
        mRoomName = in.readString();
        mUserId = in.readString();
        mTimestamp = in.readLong();
        mMessage = in.readString();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public String getUserId() {
        return mUserId;
    }

    public String getRoomName() {
        return mRoomName;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public String getMessage() {
        return mMessage;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return mMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRoomName);
        dest.writeString(mUserId);
        dest.writeLong(mTimestamp);
        dest.writeString(mMessage);
    }

    public static ChatMessage fromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ChatMessage message = new ChatMessage();
        try {
            CursorHelper c = new CursorHelper(cursor);

            message.mRoomName = c.getString(VersusContract.Messages.ROOM_NAME);
            message.mUserId = c.getString(VersusContract.Messages.USER_ID);
            message.mTimestamp = c.getLong(VersusContract.Messages.TIMESTAMP);
            message.mMessage = c.getString(VersusContract.Messages.MESSAGE);
        } catch (CursorIndexOutOfBoundsException ignoreIt) {
            message = null;
        }
        return message;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(VersusContract.Messages.ROOM_NAME, getRoomName());
        values.put(VersusContract.Messages.USER_ID, getUserId());
        values.put(VersusContract.Messages.TIMESTAMP, getTimestamp());
        values.put(VersusContract.Messages.MESSAGE, getMessage());
        return values;
    }

}
