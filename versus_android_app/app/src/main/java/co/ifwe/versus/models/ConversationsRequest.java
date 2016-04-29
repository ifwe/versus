package co.ifwe.versus.models;

import com.google.gson.annotations.SerializedName;

public class ConversationsRequest {
    @SerializedName("fbid")
    private String mFacebookId;

    @SerializedName("name")
    private String mName;

    @SerializedName("token")
    private String mToken;

    public ConversationsRequest(String facebookId, String name, String token) {
        mFacebookId = facebookId;
        mName = name;
        mToken = token;
    }

    public String getFacebookId() {
        return mFacebookId;
    }

    public String getName() {
        return mName;
    }

    public String getToken() {
        return mToken;
    }
}
