package com.tyctak.zerowastescalestill;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class _ClientStatus implements Parcelable {
    public String ClientCode;
    public String ClientVersion;
    public Date ClientInstalled;
    public String CurrentIP;
    public String HostIP;
    public boolean Connection;
    public String DateFormat;
    public String Currency;
    public String CurrencyCode;

    @Override
    public int describeContents() {
        return 0;
    }

    public _ClientStatus() { }

    protected _ClientStatus(Parcel in) {
        ClientCode = in.readString();
        ClientVersion = in.readString();
        ClientInstalled = new Date(in.readLong());
        CurrentIP = in.readString();
        HostIP = in.readString();
        Connection = ((in.readByte() == 1 ? true : false));
        DateFormat = in.readString();
        Currency = in.readString();
        CurrencyCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ClientCode);
        dest.writeString(ClientVersion);
        dest.writeLong(ClientInstalled.getTime());
        dest.writeString(CurrentIP);
        dest.writeString(HostIP);
        dest.writeByte((byte) (Connection ? 1 : 0));
        dest.writeString(DateFormat);
        dest.writeString(Currency);
        dest.writeString(CurrencyCode);
    }

    public static final Parcelable.Creator<_ClientStatus> CREATOR = new Parcelable.Creator<_ClientStatus>() {
        @Override
        public _ClientStatus createFromParcel(Parcel in) {
            return new _ClientStatus(in);
        }

        @Override
        public _ClientStatus[] newArray(int size) {
            return new _ClientStatus[size];
        }
    };
}