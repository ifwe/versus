package co.ifwe.versus.models;

import com.google.gson.annotations.SerializedName;

public class DisplaySizes {
    @SerializedName("name")
    private String mName;

    @SerializedName("uri")
    private String mUri;

    public DisplaySizes() {
    }

    public String getName() {
        return mName;
    }

    public String getUri() {
        return mUri;
    }
}
