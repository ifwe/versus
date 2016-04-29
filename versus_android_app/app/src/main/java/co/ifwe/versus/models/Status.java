package co.ifwe.versus.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import co.ifwe.versus.utils.EnumUtils;

public enum Status {
    @SerializedName("active")
    ACTIVE ("active"),

    @SerializedName("review")
    REVIEW ("review"),

    @SerializedName("done")
    DONE ("done"),

    @SerializedName("pending")
    PENDING ("pending");

    private String mCode;
    Status (String code) {
        mCode = code;
    }

    public String getCode() {
        return mCode;
    }

    @Nullable
    public static Status fromCode(@NonNull String code) {
        return fromCode(code, PENDING);
    }

    public static Status fromCode(String code, Status defaultValue) {
        return EnumUtils.from(code, values(), defaultValue);
    }

    @Override
    public String toString() {
        return getCode();
    }
}
