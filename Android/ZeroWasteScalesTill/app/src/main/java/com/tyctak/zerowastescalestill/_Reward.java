package com.tyctak.zerowastescalestill;

import android.os.Parcel;
import android.os.Parcelable;

public class _Reward implements Parcelable {
    public Boolean IsReward;
    public String RewardMember;
    public String RewardDescription;
    public Integer RewardType;
    public Integer RewardValue;
    public Boolean IsDisplayReward;

    @Override
    public int describeContents() {
        return 0;
    }

    protected _Reward() { }

    protected _Reward(Parcel in) {
        IsReward = (in.readByte() == 1 ? true : false);
        RewardMember = in.readString();
        RewardDescription = in.readString();
        RewardType = in.readInt();
        RewardValue = in.readInt();
        IsDisplayReward = (in.readByte() == 1 ? true : false);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte)(IsReward ? 1 : 0));
        dest.writeString(RewardMember);
        dest.writeString(RewardDescription);
        dest.writeInt(RewardType);
        dest.writeInt(RewardValue);
        dest.writeByte((byte)(IsDisplayReward ? 1 : 0));
    }

    public static final Parcelable.Creator<_Reward> CREATOR = new Parcelable.Creator<_Reward>() {
        @Override
        public _Reward createFromParcel(Parcel in) {
            return new _Reward(in);
        }

        @Override
        public _Reward[] newArray(int size) {
            return new _Reward[size];
        }
    };
}