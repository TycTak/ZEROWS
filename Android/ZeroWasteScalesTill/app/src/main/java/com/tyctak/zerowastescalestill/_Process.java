package com.tyctak.zerowastescalestill;

import android.os.Parcel;
import android.os.Parcelable;

public class _Process implements Parcelable {
    public String Message;
    public String Description;

    @Override
    public int describeContents() {
        return 0;
    }

    protected _Process() { }

    protected _Process(Parcel in) {
        Message = in.readString();
        Description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Message);
        dest.writeString(Description);
    }

    public static final Creator<_Process> CREATOR = new Creator<_Process>() {
        @Override
        public _Process createFromParcel(Parcel in) {
            return new _Process(in);
        }

        @Override
        public _Process[] newArray(int size) {
            return new _Process[size];
        }
    };
}