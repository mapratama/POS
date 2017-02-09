package com.pos.cashier.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Stock implements Parcelable {
    private String ItemId      = "";
    private String Barcode     = "";
    private String ItemName    = "";
    private String Description = "";
    private String ItemPrice   = "";
    private String Qty         = "";

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getItemPrice() {
        return ItemPrice;
    }

    public void setItemPrice(String itemPrice) {
        ItemPrice = itemPrice;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ItemId);
        dest.writeString(Barcode);
        dest.writeString(ItemName);
        dest.writeString(Description);
        dest.writeString(ItemPrice);
        dest.writeString(Qty);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Stock> CREATOR = new Parcelable.Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            Stock stock       = new Stock();
            stock.ItemId      = in.readString();
            stock.Barcode     = in.readString();
            stock.ItemName    = in.readString();
            stock.Description = in.readString();
            stock.ItemPrice   = in.readString();
            stock.Qty         = in.readString();

            return stock;
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
}
