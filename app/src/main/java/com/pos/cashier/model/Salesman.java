package com.pos.cashier.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Salesman implements Parcelable {
    private String EmployeeId    = "";
    private String EmployeeName  = "";
    private String LocationCode  = "";
    private String UserName      = "";
    private String PasswordValue = "";

    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String employeeId) {
        EmployeeId = employeeId;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPasswordValue() {
        return PasswordValue;
    }

    public void setPasswordValue(String passwordValue) {
        PasswordValue = passwordValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(EmployeeId);
        dest.writeString(EmployeeName);
        dest.writeString(LocationCode);
        dest.writeString(UserName);
        dest.writeString(PasswordValue);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Salesman> CREATOR = new Parcelable.Creator<Salesman>() {
        @Override
        public Salesman createFromParcel(Parcel in) {
            Salesman salesman      = new Salesman();
            salesman.EmployeeId    = in.readString();
            salesman.EmployeeName  = in.readString();
            salesman.LocationCode  = in.readString();
            salesman.UserName      = in.readString();
            salesman.PasswordValue = in.readString();

            return salesman;
        }

        @Override
        public Salesman[] newArray(int size) {
            return new Salesman[size];
        }
    };
}
