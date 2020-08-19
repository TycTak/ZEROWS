package com.tyctak.zerowastescalestill;

import android.os.Parcel;
import android.os.Parcelable;

import com.tyctak.zerowslib._ProductModel;

import java.util.Date;

public class _Product implements Parcelable {
    public Integer Id;
    public String Category;
    public Date ActionDate;
    public String Unit;
    public String ProductName;
    public Double Price;
    public String Type;
    public String Barcode;
    public Double UnitGross;
    public Double UnitNet;
    public String CombinedSearchString;
    public Integer Quantity;
    public Boolean IsWeighed;
    public Boolean IsBasket;
    public Integer Tare;
    public Integer NetWeight;
    public Integer GrossWeight;
    public String DealCode;
    public Integer ProductId;

    public _Product() { }

    public _ProductModel getProductModel() {
        _ProductModel productModel = new _ProductModel();
        productModel.Id = Id;
        productModel.Category = Category;
        productModel.ActionDate = ActionDate;
        productModel.Unit = Unit;
        productModel.ProductName = ProductName;
        productModel.Price = Price;
        productModel.Type = Type;
        productModel.Barcode = Barcode;
        productModel.UnitGross = UnitGross;
        productModel.UnitNet = UnitNet;
        productModel.CombinedSearchString = CombinedSearchString;
        productModel.Quantity = Quantity;
        productModel.IsWeighed = IsWeighed;
        productModel.IsBasket = IsBasket;
        productModel.Tare = Tare;
        productModel.NetWeight = NetWeight;
        productModel.GrossWeight = GrossWeight;
        productModel.DealCode = DealCode;
        productModel.ProductId = ProductId;

        return productModel;
    }

    protected _Product(Parcel in) {
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
        DealCode = in.readString();
        ProductId = in.readInt();
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
        dest.writeString(DealCode);
        dest.writeInt(ProductId);
    }

    public static final Parcelable.Creator<_Product> CREATOR = new Parcelable.Creator<_Product>() {
        @Override
        public _Product createFromParcel(Parcel in) {
            return new _Product(in);
        }

        @Override
        public _Product[] newArray(int size) {
            return new _Product[size];
        }
    };
}
