package com.pos.cashier.database;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.pos.cashier.model.Salesman;
import com.pos.cashier.model.Stock;
import com.pos.cashier.model.Transaction;
import com.pos.cashier.referensi.Constant;

public class PoshCashierDB extends SQLiteOpenHelper {
    private Context context = null;
    private SQLiteDatabase database = null;
    private static final String DATABASE_NAME = "PoshCashierDB";

    public PoshCashierDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void createTableSalesman(SQLiteDatabase db, Context context) {
        this.context     = context;
        database         = this.context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + Constant.SALESMAN_NAME + " ("
                + Constant.KEY_EMPLOYEE_ID + " varchar(50) PRIMARY KEY,"
                + Constant.KEY_EMPLOYEE_NAME + " varchar(250),"
                + Constant.KEY_LOCATION_CODE + " varchar(50),"
                + Constant.KEY_USER_NAME + " varchar(50),"
                + Constant.KEY_PASSWORD_VALUE + " varchar(50)" + ");";
        DBUtil.createTable(database, Constant.SALESMAN_NAME, sqlCreate);
    }

    public void createTableStock(SQLiteDatabase db, Context context) {
        this.context     = context;
        database         = this.context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + Constant.STOCK_NAME + " ("
                + Constant.KEY_ITEM_ID + " varchar(50) PRIMARY KEY,"
                + Constant.KEY_BARCODE + " varchar(250),"
                + Constant.KEY_ITEM_NAME + " varchar(50),"
                + Constant.KEY_DESCRIPTION + " text,"
                + Constant.KEY_ITEM_PRICE + " varchar(250),"
                + Constant.KEY_QTY + " varchar(50)" + ");";
        DBUtil.createTable(database, Constant.STOCK_NAME, sqlCreate);
    }

    public void createTableTransaction(SQLiteDatabase db, Context context) {
        this.context     = context;
        database         = this.context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + Constant.TRANSACTION_NAME + " ("
                + Constant.KEY_INV_ID + " varchar(50),"
                + Constant.KEY_STOCK_ID + " varchar(250),"
                + Constant.KEY_ITEM_NAME + " varchar(250),"
                + Constant.KEY_SALESMAN_ID + " varchar(250),"
                + Constant.KEY_LOCATION_ID + " varchar(50),"
                + Constant.KEY_QTY + " varchar(50),"
                + Constant.KEY_ITEM_PRICE + " varchar(50),"
                + Constant.KEY_AMOUNT + " varchar(250),"
                + Constant.KEY_DATE + " varchar(50),"
                + Constant.KEY_STATUS + " varchar(50)" + ");";
        DBUtil.createTable(database, Constant.TRANSACTION_NAME, sqlCreate);
    }

    public boolean checkTableSalesman() {
        boolean hasTables = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor     = db.rawQuery("SELECT * FROM " + Constant.SALESMAN_NAME, null);

        if (cursor != null && cursor.getCount() > 0) {
            hasTables = true;
            cursor.close();
        }

        return hasTables;
    }

    public void addSalesman(Salesman salesman) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.KEY_EMPLOYEE_ID, salesman.getEmployeeId());
        values.put(Constant.KEY_EMPLOYEE_NAME, salesman.getEmployeeName());
        values.put(Constant.KEY_LOCATION_CODE, salesman.getLocationCode());
        values.put(Constant.KEY_USER_NAME, salesman.getUserName());
        values.put(Constant.KEY_PASSWORD_VALUE, salesman.getPasswordValue());

        // Inserting Row
        db.insert(Constant.SALESMAN_NAME, null, values);
        db.close(); // Closing database connection
    }

    public void addStock(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor     = db.rawQuery("SELECT * FROM " + Constant.STOCK_NAME + " WHERE "
                + Constant.KEY_ITEM_ID + " = '" + stock.getItemId() + "'", null);

        if (cursor.getCount()>0) {
            // no action
        } else {
            ContentValues values = new ContentValues();
            values.put(Constant.KEY_ITEM_ID, stock.getItemId());
            values.put(Constant.KEY_BARCODE, stock.getBarcode());
            values.put(Constant.KEY_ITEM_NAME, stock.getItemName());
            values.put(Constant.KEY_DESCRIPTION, stock.getDescription());
            values.put(Constant.KEY_ITEM_PRICE, stock.getItemPrice());
            values.put(Constant.KEY_QTY, stock.getQty());
            // Inserting Row
            db.insert(Constant.STOCK_NAME, null, values);
        }

        cursor.close();
        db.close(); // Closing database connection
    }

    public ArrayList<Salesman> getLogin() {
        return this.getLogin("","");
    }

    public ArrayList<Salesman> getLogin(String strUserName, String strPassword) {
        Cursor cursor = null;
        ArrayList<Salesman> salesmanArrayList = null;

        String filterQuery = "";
        if (!strUserName.equalsIgnoreCase("") && !strPassword.equalsIgnoreCase("")) {
            filterQuery = " WHERE " + Constant.KEY_USER_NAME + " = '" + strUserName +
                          "' AND " + Constant.KEY_PASSWORD_VALUE + " = '" + strPassword + "'";
        }

        try {
            salesmanArrayList = new ArrayList<Salesman>();

            cursor = database.rawQuery("SELECT * FROM " + Constant.SALESMAN_NAME + filterQuery, null);
            if (cursor.getCount() > 0) {
                int indexEmployeeId    = cursor.getColumnIndex(Constant.KEY_EMPLOYEE_ID);
                int indexEmployeeName  = cursor.getColumnIndex(Constant.KEY_EMPLOYEE_NAME);
                int indexLocationCode  = cursor.getColumnIndex(Constant.KEY_LOCATION_CODE);
                int indexUserName      = cursor.getColumnIndex(Constant.KEY_USER_NAME);
                int indexPasswordValue = cursor.getColumnIndex(Constant.KEY_PASSWORD_VALUE);

                cursor.moveToFirst();
                do {
                    Salesman salesman = new Salesman();
                    salesman.setEmployeeId(cursor.getString(indexEmployeeId));
                    salesman.setEmployeeName(cursor.getString(indexEmployeeName));
                    salesman.setLocationCode(cursor.getString(indexLocationCode));
                    salesman.setUserName(cursor.getString(indexUserName));
                    salesman.setPasswordValue(cursor.getString(indexPasswordValue));

                    salesmanArrayList.add(salesman);
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return salesmanArrayList;
    }

    public ArrayList<Stock> getStock() {
        return this.getStock("", "");
    }

    public ArrayList<Stock> getStock(String strBarcode, String strItemName) {
        Cursor cursor = null;
        ArrayList<Stock> stockArrayList = null;

        String filterQuery = "";
        if (!strBarcode.equalsIgnoreCase("")) {
            filterQuery = " WHERE " + Constant.KEY_BARCODE + " like '%" + strBarcode + "%'";
        } else if (!strItemName.equalsIgnoreCase("")) {
            filterQuery = " WHERE " + Constant.KEY_ITEM_NAME + " like '%" + strItemName + "%'";
        }

        try {
            stockArrayList = new ArrayList<Stock>();

            cursor = database.rawQuery("SELECT * FROM " + Constant.STOCK_NAME + filterQuery, null);
            if (cursor.getCount() > 0) {
                int indexItemId      = cursor.getColumnIndex(Constant.KEY_ITEM_ID);
                int indexBarcode     = cursor.getColumnIndex(Constant.KEY_BARCODE);
                int indexItemName    = cursor.getColumnIndex(Constant.KEY_ITEM_NAME);
                int indexDescription = cursor.getColumnIndex(Constant.KEY_DESCRIPTION);
                int indexItemPrice   = cursor.getColumnIndex(Constant.KEY_ITEM_PRICE);
                int indexQty         = cursor.getColumnIndex(Constant.KEY_QTY);

                cursor.moveToFirst();
                do {
                    Stock stock = new Stock();
                    stock.setItemId(cursor.getString(indexItemId));
                    stock.setBarcode(cursor.getString(indexBarcode));
                    stock.setItemName(cursor.getString(indexItemName));
                    stock.setDescription(cursor.getString(indexDescription));
                    stock.setItemPrice(cursor.getString(indexItemPrice));
                    stock.setQty(cursor.getString(indexQty));

                    stockArrayList.add(stock);
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return stockArrayList;
    }

    public ArrayList<Transaction> getTransaction() {
        return this.getTransaction("", "");
    }

    public ArrayList<Transaction> getTransaction(String strLocation, String strInvId) {
        Cursor cursor = null;
        ArrayList<Transaction> transactionArrayList = null;

        String filterQuery = "";
        if (!strLocation.equalsIgnoreCase("")) {
            filterQuery = " WHERE " + Constant.KEY_LOCATION_ID + " like '%" + strLocation + "%' GROUP BY " + Constant.KEY_INV_ID;
        } else if (!strInvId.equalsIgnoreCase("")) {
            filterQuery = " WHERE " + Constant.KEY_INV_ID + " like '%" + strInvId + "%'";
        }

        try {
            transactionArrayList = new ArrayList<Transaction>();

            cursor = database.rawQuery("SELECT * FROM " + Constant.TRANSACTION_NAME + filterQuery, null);
            if (cursor.getCount() > 0) {
                int indexInvId      = cursor.getColumnIndex(Constant.KEY_INV_ID);
                int indexStockId    = cursor.getColumnIndex(Constant.KEY_STOCK_ID);
                int indexStockName  = cursor.getColumnIndex(Constant.KEY_ITEM_NAME);
                int indexSalesmanId = cursor.getColumnIndex(Constant.KEY_SALESMAN_ID);
                int indexLocationId = cursor.getColumnIndex(Constant.KEY_LOCATION_ID);
                int indexQty        = cursor.getColumnIndex(Constant.KEY_QTY);
                int indexPrice      = cursor.getColumnIndex(Constant.KEY_ITEM_PRICE);
                int indexAmount     = cursor.getColumnIndex(Constant.KEY_AMOUNT);
                int indexDate       = cursor.getColumnIndex(Constant.KEY_DATE);
                int indexStatus     = cursor.getColumnIndex(Constant.KEY_STATUS);

                cursor.moveToFirst();
                do {
                    Transaction transaction = new Transaction();
                    transaction.setInvId(cursor.getString(indexInvId));
                    transaction.setStockId(cursor.getString(indexStockId));
                    transaction.setStockName(cursor.getString(indexStockName));
                    transaction.setSalesmanId(cursor.getString(indexSalesmanId));
                    transaction.setLocationId(cursor.getString(indexLocationId));
                    transaction.setQty(cursor.getString(indexQty));
                    transaction.setPrice(cursor.getString(indexPrice));
                    transaction.setAmount(cursor.getString(indexAmount));
                    transaction.setDate(cursor.getString(indexDate));
                    transaction.setStatus(cursor.getString(indexStatus));

                    transactionArrayList.add(transaction);
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return transactionArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
    }
}