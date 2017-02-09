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
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.cashier.adapter.ListOrderAdapter;
import com.pos.cashier.database.PoshCashierDB;
import com.pos.cashier.model.Transaction;
import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.FontCache;
import com.pos.cashier.referensi.TypeFaceSpan;
import com.pos.cashier.referensi.URLS;
import java.util.ArrayList;

public class ListOrder extends AppCompatActivity implements ListOrderAdapter.ListOrderAdapterListener {
    private SharedPreferences appsPref;
    private Typeface fontDroidSans, fontDroidSansBold;
    private ProgressDialog pDialog;
    private PoshCashierDB poshCashierDB = null;
    private SQLiteDatabase db = null;
    private TextView lblDate, lblLocation;
    private EditText txtItemName;
    private ListView rcList;
    private Toolbar toolbar;
    private ListOrderAdapter adapter = null;
    private ArrayList<Transaction> transactionArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_list_order);

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

        lblDate.setTypeface(fontDroidSans);
        txtItemName.setTypeface(fontDroidSans);
        lblLocation.setTypeface(fontDroidSans);

        pDialog = new ProgressDialog(ListOrder.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        txtItemName.setText(appsPref.getString(Constant.KEY_LOCATION_CODE, ""));
        initiateDataToAdapter(txtItemName.getText().toString());
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("LIST ORDER");
        spanToolbar.setSpan(new TypeFaceSpan(ListOrder.this, "DroidSans-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
            Toast.makeText(ListOrder.this, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            transactionArrayList = poshCashierDB.getTransaction(filterText, "");
            adapter = new ListOrderAdapter(this, transactionArrayList, ListOrder.this);
            rcList.setAdapter(adapter);
            pDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            initiateDataToAdapter(txtItemName.getText().toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onClicked(Transaction transaction, String strStatus) {
        Intent intent = new Intent(ListOrder.this, LihatOrderActivity.class);
        intent.putExtra(Constant.KEY_INTENT_TRANSACTION, transaction);
        intent.putExtra(Constant.KEY_INTENT_STATUS, strStatus);
        startActivityForResult(intent, 1);
    }
}
