package co.ifwe.versus.models;

import com.google.gson.annotations.SerializedName;

public class QueueBody {
    @SerializedName("fbid")
    private String mFacebookId;

    @SerializedName("topic_id")
    private int mTopicId;

    @SerializedName("side")
    private String mSide;

    @SerializedName("token")
    private String mToken;

    public QueueBody(String facebookId, int topicId, String side, String token) {
        mFacebookId = facebookId;
        mTopicId = topicId;
        mSide = side;
        mToken = token;
    }

    public String getFacebookId() {
        return mFacebookId;
    }

    public int getTopicId() {
        return mTopicId;
    }

    public String getSide() {
        return mSide;
    }

    public String getToken() {
        return mToken;
    }
}
