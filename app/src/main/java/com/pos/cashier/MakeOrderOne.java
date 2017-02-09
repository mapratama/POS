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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.cashier.adapter.OrderAdapter;
import com.pos.cashier.database.PoshCashierDB;
import com.pos.cashier.model.Stock;
import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.FontCache;
import com.pos.cashier.referensi.TypeFaceSpan;
import com.pos.cashier.referensi.URLS;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MakeOrderOne extends AppCompatActivity implements OrderAdapter.OrderAdapterListener {
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
    private ArrayList<Stock> listStock = new ArrayList<>();
    private OrderAdapter adapter = null;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private View view;

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

        // Hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Init toolbar
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

        lblDate.setTypeface(fontDroidSans);
        txtItemName.setTypeface(fontDroidSans);
        lblLocation.setTypeface(fontDroidSans);
        lblTotal.setTypeface(fontDroidSans);
        lblOrderName.setTypeface(fontDroidSans);
        txtTotal.setTypeface(fontDroidSans);
        btnHapus.setTypeface(fontDroidSansBold);
        btnSimpan.setTypeface(fontDroidSansBold);
        btnProses.setTypeface(fontDroidSansBold);
        rcList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        lblLocation.setText(appsPref.getString(Constant.KEY_LOCATION_CODE, ""));
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        lblDate.setText(date);

        DateFormat dfInv = new SimpleDateFormat("yyMM");
        String dateInv = dfInv.format(Calendar.getInstance().getTime());
        String randomString = UUID.randomUUID().toString().toUpperCase().substring(0,5);
        lblOrderName.setText("SPG-LOC/" + dateInv + "/" + randomString);

        pDialog = new ProgressDialog(MakeOrderOne.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        txtItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MakeOrderOne.this, MakeOrderTwo.class);
                startActivityForResult(intent, 1);
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFinishing()) {
                    new SweetAlertDialog(MakeOrderOne.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Anda yakin?")
                            .setContentText("Ingin menghapus order ini?")
                            .setCancelText("Tidak")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    listStock.clear();
                                    adapter.updateListStock(listStock);
                                    rcList.setAdapter(adapter);
                                    txtTotal.setText("");

                                    sDialog.setTitleText("HAPUS!")
                                            .setContentText("Order Anda telah dihapus!")
                                            .setConfirmText("Ok")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.cancel();
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
                if (txtTotal.getText().length()==0) {
                    Toast.makeText(MakeOrderOne.this, "No Data", Toast.LENGTH_SHORT).show();
                } else {
                    simpanTransation(false);
                }
            }
        });

        imgBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermission()) {
                    requestPermission();
                } else {
                    startActivityForResult(new Intent(MakeOrderOne.this, BarcodeScannerActivity.class), 2);
                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (adapter.getCount() == 0) {
                        Toast.makeText(MakeOrderOne.this, "Maaf, order Anda kosong!", Toast.LENGTH_SHORT).show();
                    } else {
                        simpanTransation(true);
                    }
                } catch (Exception e) {
                    Toast.makeText(MakeOrderOne.this, "Maaf, order Anda kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter = new OrderAdapter(this, this, false);
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("BUAT ORDER");
        spanToolbar.setSpan(new TypeFaceSpan(MakeOrderOne.this, "DroidSans-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Stock stock = data.getParcelableExtra("Stock");
                addDataToList(stock);
            } else if (requestCode == 2) {
                initiateDataToAdapter(data.getStringExtra("Content"));
            }
        }
    }

    public void addDataToList(Stock stock) {
        if (listStock.size()==0) {
            stock.setQty("1");
            listStock.add(stock);
        } else {
            if (contains(listStock, stock.getItemId())) {
                for (int i = 0; i < listStock.size(); i++) {
                    if (listStock.get(i).getItemId().equalsIgnoreCase(stock.getItemId())) {
                        Stock newStock = stock;
                        int intQty = Integer.parseInt(listStock.get(i).getQty()) + 1;
                        newStock.setQty(""+intQty);
                        listStock.set(i, newStock);
                    }
                }
            } else {
                stock.setQty("1");
                listStock.add(stock);
            }
        }
        adapter.updateListStock(listStock);
        rcList.setAdapter(adapter);
        txtTotal.setText(Constant.currencyFormater(adapter.totalPrice));
    }

    boolean contains(ArrayList<Stock> list, String name) {
        for (Stock item : list) {
            if (item.getItemId().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void simpanTransation(boolean blnProses) {
        pDialog.show();
        poshCashierDB = new PoshCashierDB(getApplicationContext());
        db = poshCashierDB.getWritableDatabase();
        poshCashierDB.createTableTransaction(db, getBaseContext());

        for (int i=0; i<listStock.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(Constant.KEY_INV_ID, lblOrderName.getText().toString());
            values.put(Constant.KEY_STOCK_ID, listStock.get(i).getItemId());
            values.put(Constant.KEY_ITEM_NAME, listStock.get(i).getItemName());
            values.put(Constant.KEY_SALESMAN_ID, appsPref.getString(Constant.KEY_EMPLOYEE_ID, ""));
            values.put(Constant.KEY_LOCATION_ID, lblLocation.getText().toString());
            // AMOUNT
            String[] splitPrice = listStock.get(i).getItemPrice().split("\\.");
            Double price  = Double.parseDouble(splitPrice[0].toString());
            Double count  = Double.parseDouble(listStock.get(i).getQty());
            Double amount = price * count;
            values.put(Constant.KEY_QTY, listStock.get(i).getQty());
            values.put(Constant.KEY_ITEM_PRICE, amount);
            values.put(Constant.KEY_AMOUNT, txtTotal.getText().toString());
            values.put(Constant.KEY_DATE, lblDate.getText().toString());
            if (blnProses) {
                values.put(Constant.KEY_STATUS, "PROSES");
            } else {
                values.put(Constant.KEY_STATUS, "TUNDA");
            }
            // Inserting Row
            db.insert(Constant.TRANSACTION_NAME, null, values);
        }

        db.close();
        pDialog.dismiss();
        Toast.makeText(MakeOrderOne.this, "Berhasil disimpan ke Order", Toast.LENGTH_SHORT).show();
        finish();
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
            Toast.makeText(MakeOrderOne.this, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            ArrayList<Stock> searchStock = poshCashierDB.getStock(filterText, "");
            if (searchStock.size()==0) {
                Toast.makeText(MakeOrderOne.this, "Sorry, not match with our data", Toast.LENGTH_SHORT).show();
            } else {
                Stock stock = searchStock.get(0);
                addDataToList(stock);
            }
            pDialog.dismiss();
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MakeOrderOne.this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MakeOrderOne.this, Manifest.permission.CAMERA)) {
            startActivityForResult(new Intent(MakeOrderOne.this, BarcodeScannerActivity.class), 2);
        } else {
            ActivityCompat.requestPermissions(MakeOrderOne.this, new String[]{ Manifest.permission.CAMERA }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(MakeOrderOne.this, BarcodeScannerActivity.class), 2);
                } else {
                    Snackbar.make(view, "Permission Denied, You cannot access barcode reader.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onClicked(Stock stock) {}

    @Override
    public void onRemoved() {
        adapter.updateListStock(listStock);
        rcList.setAdapter(adapter);
        if (adapter.totalPrice == 0) {
            txtTotal.setText("");
        } else {
            txtTotal.setText(Constant.currencyFormater(adapter.totalPrice));
        }
    }
}
