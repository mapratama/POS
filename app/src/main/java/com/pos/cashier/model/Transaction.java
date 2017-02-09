package com.pos.cashier.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {
    private String InvId      = "";
    private String StockId    = "";
    private String StockName  = "";
    private String SalesmanId = "";
    private String LocationId = "";
    private String Qty        = "";
    private String Price      = "";
    private String Amount     = "";
    private String Date       = "";
    private String Status     = "";

    public String getInvId() {
        return InvId;
    }

    public void setInvId(String invId) {
        InvId = invId;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStockId() {
        return StockId;
    }

    public void setStockId(String StockId) {
        this.StockId = StockId;
    }

    public String getSalesmanId() {
        return SalesmanId;
    }

    public void setSalesmanId(String SalesmanId) {
        this.SalesmanId = SalesmanId;
    }

    public String getLocationId() {
        return LocationId;
    }

    public void setLocationId(String locationId) {
        LocationId = locationId;
    }

    public String getStockName() {
        return StockName;
    }

    public void setStockName(String stockName) {
        StockName = stockName;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(InvId);
        dest.writeString(Amount);
        dest.writeString(Date);
        dest.writeString(Status);
        dest.writeString(StockId);
        dest.writeString(SalesmanId);
        dest.writeString(LocationId);
        dest.writeString(StockName);
        dest.writeString(Qty);
        dest.writeString(Price);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            Transaction transaction = new Transaction();
            transaction.InvId       = in.readString();
            transaction.Amount      = in.readString();
            transaction.Date        = in.readString();
            transaction.Status      = in.readString();
            transaction.StockId     = in.readString();
            transaction.SalesmanId  = in.readString();
            transaction.LocationId  = in.readString();
            transaction.StockName   = in.readString();
            transaction.Qty         = in.readString();
            transaction.Price       = in.readString();

            return transaction;
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
