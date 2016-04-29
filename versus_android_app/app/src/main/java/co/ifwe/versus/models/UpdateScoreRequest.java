package co.ifwe.versus.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by schoi on 4/20/16.
 */
public class UpdateScoreRequest {
    @SerializedName("fbid")
    private String mFacebookId;

    @SerializedName("token")
    private String mToken;

    @SerializedName("pubnub_room")
    private String mChannel;

    @SerializedName("score_a")
    private int mScoreA;

    @SerializedName("score_b")
    private int mScoreB;

    public UpdateScoreRequest(String facebookId, String token, String channel, int scoreA, int scoreB) {
        mFacebookId = facebookId;
        mToken = token;
        mChannel = channel;
        mScoreA = scoreA;
        mScoreB = scoreB;
    }
}
