package co.ifwe.versus.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    @SerializedName("fbid")
    private String mFacebookId;

    @SerializedName("name")
    private String mName;

    @SerializedName("energy")
    private int mEnergy;

    @SerializedName("next_energy_update")
    private long mNextEnergyUpdate;

    public User() {
    }

    public User(String facebookId, String name, int energy, long nextEnergyUpdate) {
        mFacebookId = facebookId;
        mName = name;
        mEnergy = energy;
        mNextEnergyUpdate = nextEnergyUpdate;
    }

    protected User(Parcel in) {
        mFacebookId = in.readString();
        mName = in.readString();
        mEnergy = in.readInt();
        mNextEnergyUpdate = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getFacebookId() {
        return mFacebookId;
    }

    public String getName() {
        return mName;
    }

    public int getEnergy() {
        return mEnergy;
    }

    public long getNextEnergyUpdate() {
        return mNextEnergyUpdate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFacebookId);
        dest.writeString(mName);
        dest.writeInt(mEnergy);
        dest.writeLong(mNextEnergyUpdate);
    }
}
