package co.ifwe.versus.models;

import com.google.gson.annotations.SerializedName;

public class VersionResponse {
    @SerializedName("code")
    private int mCode;

    @SerializedName("name")
    private String mName;

    @SerializedName("upgrade_required")
    private boolean mUpdateRequired;

    public VersionResponse() {
    }

    public int getCode() {
        return mCode;
    }

    public String getName() {
        return mName;
    }

    public boolean isUpdateRequired() {
        return mUpdateRequired;
    }
}
