package com.pos.cashier;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.cashier.adapter.LihatOrderAdapter;
import com.pos.cashier.adapter.OrderAdapter;
import com.pos.cashier.database.PoshCashierDB;
import com.pos.cashier.model.Stock;
import com.pos.cashier.model.Transaction;
import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.FontCache;
import com.pos.cashier.referensi.TypeFaceSpan;
import com.pos.cashier.referensi.URLS;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LihatOrderActivity extends AppCompatActivity implements OrderAdapter.OrderAdapterListener,
        LihatOrderAdapter.LihatOrderAdapterListener {
    private SharedPreferences appsPref;
    private Typeface fontDroidSans, fontDroidSansBold;
    private ProgressDialog pDialog;
    private PoshCashierDB poshCashierDB = null;
    private SQLiteDatabase db = null;
    private TextView lblDate, lblLocation, lblTotal, lblOrderName, txtTotal;
    private EditText txtItemName;
    private ListView rcList;
    private Button btnHapus, btnSimpan, btnProses;
    private ImageView imgBarcode;
    private Toolbar toolbar;
    private LihatOrderAdapter adapter = null;
    private Bundle bundle = null;
    private ArrayList<Transaction> transactionList;
    private Transaction transaction;
    private int intTotalTransactionBeforeAdd = 0;
    private String strStatus;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private View view;
    private ArrayList<String> listItemDeleted = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_make_order_one);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initToolbar();

        fontDroidSans     = FontCache.get(this, "DroidSans");
        fontDroidSansBold = FontCache.get(this, "DroidSans-Bold");
        appsPref          = getSharedPreferences(URLS.PREF_NAME, Activity.MODE_PRIVATE);
        lblDate           = (TextView) findViewById(R.id.lblDate);
        txtItemName       = (EditText) findViewById(R.id.txtItemName);
        lblLocation       = (TextView) findViewById(R.id.lblLocation);
        rcList            = (ListView) findViewById(R.id.rcList);
        lblTotal          = (TextView) findViewById(R.id.lblTotal);
        lblOrderName      = (TextView) findViewById(R.id.lblOrderName);
        txtTotal          = (TextView) findViewById(R.id.txtTotal);
        btnHapus          = (Button) findViewById(R.id.btnHapus);
        btnSimpan         = (Button) findViewById(R.id.btnSimpan);
        btnProses         = (Button) findViewById(R.id.btnProses);
        imgBarcode        = (ImageView) findViewById(R.id.imgBarcode);
        bundle            = getIntent().getExtras();
        transaction       = bundle.getParcelable(Constant.KEY_INTENT_TRANSACTION);
        strStatus         = bundle.getString(Constant.KEY_INTENT_STATUS);

        lblDate.setTypeface(fontDroidSans);
        txtItemName.setTypeface(fontDroidSans);
        lblLocation.setTypeface(fontDroidSans);
        lblTotal.setTypeface(fontDroidSans);
        lblOrderName.setTypeface(fontDroidSans);
        txtTotal.setTypeface(fontDroidSans);
        btnHapus.setTypeface(fontDroidSansBold);
        btnSimpan.setTypeface(fontDroidSansBold);
        btnProses.setTypeface(fontDroidSansBold);

        lblLocation.setText(transaction.getLocationId());
        lblDate.setText(transaction.getDate());
        lblOrderName.setText(transaction.getInvId());
        txtTotal.setText(transaction.getAmount());
        btnHapus.setText("CANCEL");

        pDialog = new ProgressDialog(LihatOrderActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        txtItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LihatOrderActivity.this, MakeOrderTwo.class);
                startActivityForResult(intent, 1);
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFinishing()) {
                    new SweetAlertDialog(LihatOrderActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Anda yakin?")
                            .setContentText("Ingin membatalkan order ini?")
                            .setCancelText("Tidak")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    initiateDB();
                                    ContentValues values = new ContentValues();
                                    values.put(Constant.KEY_STATUS, "BATAL");
                                    db.update(Constant.TRANSACTION_NAME, values, Constant.KEY_INV_ID + "= ?", new String[] { transaction.getInvId() });
                                    db.close();

                                    sDialog.setTitleText("BATAL!")
                                            .setContentText("Order Anda telah dibatalkan!")
                                            .setConfirmText("Ok")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                }
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intTotalTransactionBeforeAdd == transactionList.size()) {
                    if (adapter.blnChange) {
                        simpanTransationTwo();
                    } else {
                        Toast.makeText(LihatOrderActivity.this, "No change", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    simpanTransation();
                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (adapter.getCount() == 0) {
                        Toast.makeText(LihatOrderActivity.this, "Maaf, order Anda kosong!", Toast.LENGTH_SHORT).show();
                    } else {
                        initiateDB();
                        ContentValues values = new ContentValues();
                        values.put(Constant.KEY_STATUS, "PROSES");
                        db.update(Constant.TRANSACTION_NAME, values, Constant.KEY_INV_ID + "= ?", new String[] { transaction.getInvId() });
                        db.close();
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(LihatOrderActivity.this, "Maaf, order Anda kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermission()) {
                    requestPermission();
                } else {
                    startActivityForResult(new Intent(LihatOrderActivity.this, BarcodeScannerActivity.class), 2);
                }
            }
        });

        adapter = new LihatOrderAdapter(this, LihatOrderActivity.this, strStatus);
        initiateDataToAdapter(transaction.getInvId());

        if (strStatus.equalsIgnoreCase("BATAL") || strStatus.equalsIgnoreCase("PROSES")) {
            btnHapus.setVisibility(View.GONE);
            btnProses.setVisibility(View.GONE);
            btnSimpan.setVisibility(View.GONE);
            txtItemName.setEnabled(false);
            imgBarcode.setEnabled(false);
        }
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("LIHAT ORDER");
        spanToolbar.setSpan(new TypeFaceSpan(LihatOrderActivity.this, "DroidSans-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Initiate Toolbar/ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(spanToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // TODO Get data from database SQLite
    private void initiateDB() {
        poshCashierDB = new PoshCashierDB(getApplicationContext());
        db = poshCashierDB.getWritableDatabase();
        poshCashierDB.createTableTransaction(db, this);
    }

    // TODO We need to refresh adapter for every data update. Without it, ListView will never be refreshed
    protected void initiateDataToAdapter(String filterText) {
        try {
            pDialog.show();
            initiateDB();
        } catch(Exception e) {
            Toast.makeText(LihatOrderActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            transactionList = poshCashierDB.getTransaction("", filterText);
            intTotalTransactionBeforeAdd = transactionList.size();
            adapter.updateListTransaction(transactionList);
            rcList.setAdapter(adapter);
            pDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Stock stock = data.getParcelableExtra("Stock");
                addDataToList(stock);
            } else if (requestCode == 2) {
                try {
                    pDialog.show();
                    initiateDB();
                } catch(Exception e) {
                    Toast.makeText(LihatOrderActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    ArrayList<Stock> searchStock = poshCashierDB.getStock(data.getStringExtra("Content"), "");
                    if (searchStock.size()==0) {
                        Toast.makeText(LihatOrderActivity.this, "Sorry, not match with our data", Toast.LENGTH_SHORT).show();
                    } else {
                        Stock stock = searchStock.get(0);
                        addDataToList(stock);
                    }
                    pDialog.dismiss();
                }
            }
        }
    }

    public void addDataToList(Stock stock) {
        if (contains(transactionList, stock.getItemId())) {
            for (int i = 0; i < transactionList.size(); i++) {
                if (transactionList.get(i).getStockId().equalsIgnoreCase(stock.getItemId())) {
                    Transaction newtransaction = transactionList.get(i);
                    // Set Qty
                    int intQty = Integer.parseInt(transactionList.get(i).getQty()) + 1;
                    newtransaction.setQty(""+intQty);
                    // Set Amount
                    String[] splitPrice = stock.getItemPrice().split("\\.");
                    Double price     = Double.parseDouble(splitPrice[0].toString());
                    Double amount    = Double.parseDouble(transactionList.get(i).getAmount().replace(",", ""));
                    Double newAmount = amount + price;
                    Double newPrice  = price * intQty;
                    newtransaction.setAmount(""+newAmount);
                    newtransaction.setPrice(""+newPrice);
                    transactionList.set(i, newtransaction);
                }
            }
        } else {
            Transaction mTransaction = new Transaction();
            mTransaction.setInvId(transaction.getInvId());
            mTransaction.setStockId(stock.getItemId());
            mTransaction.setStockName(stock.getItemName());
            mTransaction.setSalesmanId(transaction.getSalesmanId());
            mTransaction.setLocationId(transaction.getLocationId());
            mTransaction.setQty("1");
            mTransaction.setPrice(stock.getItemPrice());
            // Set Amount
            String[] splitPrice = stock.getItemPrice().split("\\.");
            Double price     = Double.parseDouble(splitPrice[0].toString());
            Double amount    = Double.parseDouble(transaction.getAmount().replace(",", ""));
            Double newAmount = amount + price;
            mTransaction.setAmount(""+newAmount);
            mTransaction.setDate(transaction.getDate());
            mTransaction.setStatus(transaction.getStatus());
            transactionList.add(mTransaction);
        }

        adapter.updateListTransaction(transactionList);
        rcList.setAdapter(adapter);
        //Double lastAmount = Double.parseDouble(transactionList.get(transactionList.size()-1).getAmount());
        //txtTotal.setText(Constant.currencyFormater(lastAmount));
        txtTotal.setText(Constant.currencyFormater(adapter.totalPrice));
    }

    boolean contains(ArrayList<Transaction> list, String name) {
        for (Transaction item : list) {
            if (item.getStockId().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void simpanTransation() {
        pDialog.show();
        poshCashierDB = new PoshCashierDB(getApplicationContext());
        db = poshCashierDB.getWritableDatabase();
        poshCashierDB.createTableTransaction(db, getBaseContext());

        if (intTotalTransactionBeforeAdd < transactionList.size()) {
            for (int i = intTotalTransactionBeforeAdd; i < transactionList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(Constant.KEY_INV_ID, lblOrderName.getText().toString());
                values.put(Constant.KEY_STOCK_ID, transactionList.get(i).getStockId());
                values.put(Constant.KEY_ITEM_NAME, transactionList.get(i).getStockName());
                values.put(Constant.KEY_SALESMAN_ID, appsPref.getString(Constant.KEY_EMPLOYEE_ID, ""));
                values.put(Constant.KEY_LOCATION_ID, lblLocation.getText().toString());
                // AMOUNT
                String[] splitPrice = transactionList.get(i).getPrice().split("\\.");
                Double price = Double.parseDouble(splitPrice[0].toString());
                Double count = Double.parseDouble(transactionList.get(i).getQty());
                Double amount = price * count;
                values.put(Constant.KEY_QTY, transactionList.get(i).getQty());
                values.put(Constant.KEY_ITEM_PRICE, transactionList.get(i).getPrice());
                values.put(Constant.KEY_AMOUNT, txtTotal.getText().toString());
                values.put(Constant.KEY_DATE, lblDate.getText().toString());
                values.put(Constant.KEY_STATUS, "TUNDA");
                // Inserting Row
                db.insert(Constant.TRANSACTION_NAME, null, values);
            }
        } else {
            // Kalau total sebelumnya lebih banyak dari yg sekarang, berarti ada item yg di-delete
            // Oleh karena itu, item yang di delete tadi, harus di hapus dulu dari database
            for (int intDelete=0; intDelete < listItemDeleted.size(); intDelete++) {
                db.delete(Constant.TRANSACTION_NAME, Constant.KEY_ITEM_NAME + "='" + listItemDeleted.get(intDelete) + "'", null);
            }

            // Kemudian untuk update valuenya, delete dulu item yang sama di database dgn yg di list
            // Sehingga datanya jadi update
            for (int i = 0; i < transactionList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(Constant.KEY_INV_ID, lblOrderName.getText().toString());
                values.put(Constant.KEY_STOCK_ID, transactionList.get(i).getStockId());
                values.put(Constant.KEY_ITEM_NAME, transactionList.get(i).getStockName());
                values.put(Constant.KEY_SALESMAN_ID, appsPref.getString(Constant.KEY_EMPLOYEE_ID, ""));
                values.put(Constant.KEY_LOCATION_ID, lblLocation.getText().toString());
                // AMOUNT
                String[] splitPrice = transactionList.get(i).getPrice().split("\\.");
                Double price  = Double.parseDouble(splitPrice[0].toString());
                Double count  = Double.parseDouble(transactionList.get(i).getQty());
                Double amount = price * count;
                values.put(Constant.KEY_QTY, transactionList.get(i).getQty());
                values.put(Constant.KEY_ITEM_PRICE, transactionList.get(i).getPrice());
                values.put(Constant.KEY_AMOUNT, txtTotal.getText().toString());
                values.put(Constant.KEY_DATE, lblDate.getText().toString());
                values.put(Constant.KEY_STATUS, "TUNDA");
                db.delete(Constant.TRANSACTION_NAME, Constant.KEY_ITEM_NAME + "='" + transactionList.get(i).getStockName() + "'", null);
                // Inserting Row
                db.insert(Constant.TRANSACTION_NAME, null, values);
            }
        }

        db.close();
        pDialog.dismiss();
        Toast.makeText(LihatOrderActivity.this, "Berhasil disimpan ke Order", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    public void simpanTransationTwo() {
        pDialog.show();
        poshCashierDB = new PoshCashierDB(getApplicationContext());
        db = poshCashierDB.getWritableDatabase();
        poshCashierDB.createTableTransaction(db, getBaseContext());

        for (int i=0; i<adapter.listChange.size(); i++) {
            int intPosition = adapter.listChange.get(i);
            ContentValues values = new ContentValues();
            values.put(Constant.KEY_INV_ID, lblOrderName.getText().toString());
            values.put(Constant.KEY_STOCK_ID, transactionList.get(intPosition).getStockId());
            values.put(Constant.KEY_ITEM_NAME, transactionList.get(intPosition).getStockName());
            values.put(Constant.KEY_SALESMAN_ID, appsPref.getString(Constant.KEY_EMPLOYEE_ID, ""));
            values.put(Constant.KEY_LOCATION_ID, lblLocation.getText().toString());
            // AMOUNT
            String[] splitPrice = transactionList.get(intPosition).getPrice().split("\\.");
            Double price  = Double.parseDouble(splitPrice[0].toString());
            Double count  = Double.parseDouble(transactionList.get(intPosition).getQty());
            Double amount = price * count;
            values.put(Constant.KEY_QTY, transactionList.get(intPosition).getQty());
            values.put(Constant.KEY_ITEM_PRICE, transactionList.get(intPosition).getPrice());
            values.put(Constant.KEY_AMOUNT, txtTotal.getText().toString());
            values.put(Constant.KEY_DATE, lblDate.getText().toString());
            values.put(Constant.KEY_STATUS, "TUNDA");
            db.delete(Constant.TRANSACTION_NAME, Constant.KEY_ITEM_NAME + "='" + transactionList.get(intPosition).getStockName() + "'", null);
            // Inserting Row
            db.insert(Constant.TRANSACTION_NAME, null, values);
        }

        db.close();
        pDialog.dismiss();
        Toast.makeText(LihatOrderActivity.this, "Berhasil disimpan ke Order", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onClicked(Stock stock) {}

    @Override
    public void onRemoved() {}

    @Override
    public void onRemoved(boolean blnRemoved, String strName) {
        if (blnRemoved) {
            // Nama item yang di-delete, di simpan di array list untuk nantinya akan di-delete dari database
            listItemDeleted.add(strName);
            /*pDialog.show();
            poshCashierDB = new PoshCashierDB(getApplicationContext());
            db = poshCashierDB.getWritableDatabase();
            poshCashierDB.createTableTransaction(db, getBaseContext());
            db.delete(Constant.TRANSACTION_NAME, Constant.KEY_ITEM_NAME + "='" + strName + "'", null);
            db.close();
            pDialog.dismiss();*/
        }

        adapter.updateListTransaction(transactionList);
        rcList.setAdapter(adapter);
        if (adapter.totalPrice == 0) {
            txtTotal.setText("");
        } else {
            txtTotal.setText(Constant.currencyFormater(adapter.totalPrice));
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(LihatOrderActivity.this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(LihatOrderActivity.this, Manifest.permission.CAMERA)) {
            startActivityForResult(new Intent(LihatOrderActivity.this, BarcodeScannerActivity.class), 2);
        } else {
            ActivityCompat.requestPermissions(LihatOrderActivity.this, new String[]{ Manifest.permission.CAMERA }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(LihatOrderActivity.this, BarcodeScannerActivity.class), 2);
                } else {
                    Snackbar.make(view, "Permission Denied, You cannot access barcode reader.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
}
