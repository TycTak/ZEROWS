package com.tyctak.zerowastescalestill;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class _ProductDetail extends _Product implements Parcelable {
    public String ProductCode;
    public String Country;
    public String Description;
    public Boolean IsOrganic;
    public Boolean IsVegan;
    public Boolean AddedSalt;
    public Boolean AddedSugar;
    public String Allergen;
    public String Nutritional;

    public _ProductDetail(_Product product) {
        Id = product.Id;
        Category = product.Category;
        ActionDate = product.ActionDate;
        Unit = product.Unit;
        ProductName = product.ProductName;
        Price = product.Price;
        Type = product.Type;
        Barcode = product.Barcode;
        UnitGross = product.UnitGross;
        UnitNet = product.UnitNet;
        CombinedSearchString = product.CombinedSearchString;
        Quantity = product.Quantity;
        IsWeighed = product.IsWeighed;
        IsBasket = product.IsBasket;
        Tare = product.Tare;
        NetWeight = product.NetWeight;
        GrossWeight = product.GrossWeight;
        DealCode = product.DealCode;
        ProductId = product.ProductId;
    }

    protected _ProductDetail(Parcel in) {
        Id = in.readInt();
        Category = in.readString();
        ActionDate = new Date(in.readLong());
        Unit = in.readString();
        ProductName = in.readString();
        Price = in.readDouble();
        Type = in.readString();
        Barcode = in.readString();
        UnitGross = in.readDouble();
        UnitNet = in.readDouble();
        CombinedSearchString = in.readString();
        Quantity = in.readInt();
        IsWeighed = (in.readByte() == 1 ? true : false);
        IsBasket = (in.readByte() == 1 ? true : false);
        Tare = in.readInt();
        NetWeight = in.readInt();
        GrossWeight = in.readInt();
        ProductCode = in.readString();
        Country = in.readString();
        Description = in.readString();
        IsOrganic = (in.readByte() == 1 ? true : false);
        IsVegan = (in.readByte() == 1 ? true : false);
        AddedSalt = (in.readByte() == 1 ? true : false);
        AddedSugar = (in.readByte() == 1 ? true : false);
        Allergen = in.readString();
        Nutritional = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Category);
        dest.writeLong(ActionDate.getTime());
        dest.writeString(Unit);
        dest.writeString(ProductName);
        dest.writeDouble(Price);
        dest.writeString(Type);
        dest.writeString(Barcode);
        dest.writeDouble(UnitGross);
        dest.writeDouble(UnitNet);
        dest.writeString(CombinedSearchString);
        dest.writeInt(Quantity);
        dest.writeByte((byte)(IsWeighed ? 1 : 0));
        dest.writeByte((byte)(IsBasket ? 1 : 0));
        dest.writeInt(Tare);
        dest.writeInt(NetWeight);
        dest.writeInt(GrossWeight);
        dest.writeString(ProductCode);
        dest.writeString(Country);
        dest.writeString(Description);
        dest.writeByte((byte)(IsOrganic ? 1 : 0));
        dest.writeByte((byte)(IsVegan ? 1 : 0));
        dest.writeByte((byte)(AddedSalt ? 1 : 0));
        dest.writeByte((byte)(AddedSugar ? 1 : 0));
        dest.writeString(Allergen);
        dest.writeString(Nutritional);
    }

    public static final Creator<_ProductDetail> CREATOR = new Creator<_ProductDetail>() {
        @Override
        public _ProductDetail createFromParcel(Parcel in) {
            return new _ProductDetail(in);
        }

        @Override
        public _ProductDetail[] newArray(int size) {
            return new _ProductDetail[size];
        }
    };
}