package co.ifwe.versus.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import co.ifwe.versus.utils.EnumUtils;

public enum Result {
    @SerializedName("win")
    WIN ("win"),

    @SerializedName("loss")
    LOSS ("loss"),

    @SerializedName("draw")
    DRAW ("draw"),

    NONE ("none");

    private String mCode;
    Result (String code) {
        mCode = code;
    }

    public String getCode() {
        return mCode;
    }

    @Nullable
    public static Result fromCode(@NonNull String code) {
        return fromCode(code, NONE);
    }

    public static Result fromCode(String code, Result defaultValue) {
        return EnumUtils.from(code, values(), defaultValue);
    }

    @Override
    public String toString() {
        return getCode();
    }
}
