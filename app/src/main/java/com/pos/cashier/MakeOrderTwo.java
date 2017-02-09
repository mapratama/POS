package com.pos.cashier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
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
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MakeOrderTwo extends AppCompatActivity implements OrderAdapter.OrderAdapterListener {
    private SharedPreferences appsPref;
    private Typeface fontDroidSans, fontDroidSansBold;
    private ProgressDialog pDialog;
    private PoshCashierDB poshCashierDB = null;
    private SQLiteDatabase db = null;
    private TextView lblDate, lblLocation;
    private EditText txtItemName;
    private ListView rcList;
    private Button btnAdd;
    private OrderAdapter adapter = null;
    private ArrayList<Stock> listStock = null;
    private ImageView imgBarcode;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_make_order_two);

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
        btnAdd            = (Button) findViewById(R.id.btnAdd);
        imgBarcode        = (ImageView) findViewById(R.id.imgBarcode);

        lblDate.setTypeface(fontDroidSans);
        txtItemName.setTypeface(fontDroidSans);
        lblLocation.setTypeface(fontDroidSans);
        btnAdd.setTypeface(fontDroidSansBold);

        lblLocation.setText(appsPref.getString(Constant.KEY_LOCATION_CODE, ""));
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        lblDate.setText(date);

        pDialog = new ProgressDialog(MakeOrderTwo.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        txtItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                initiateDataToAdapter(editable.toString());
            }
        });
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("BUAT ORDER");
        spanToolbar.setSpan(new TypeFaceSpan(MakeOrderTwo.this, "DroidSans-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        adapter = new OrderAdapter(this, this, true);
        db = poshCashierDB.getWritableDatabase();
        poshCashierDB.createTableStock(db, this);
    }

    // TODO We need to refresh adapter for every data update. Without it, ListView will never be refreshed
    protected void initiateDataToAdapter(String filterText) {
        try {
            initiateDB();
        } catch(Exception e) {
            Toast.makeText(MakeOrderTwo.this, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            listStock = poshCashierDB.getStock("", filterText);
            adapter.updateListStock(listStock);
            rcList.setAdapter(adapter);
        }
    }

    @Override
    public void onClicked(Stock stock) {
        Intent intent = getIntent();
        intent.putExtra("Stock", stock);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRemoved() {}

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
