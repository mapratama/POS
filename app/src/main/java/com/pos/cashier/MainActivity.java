package com.pos.cashier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pos.cashier.database.PoshCashierDB;
import com.pos.cashier.model.Stock;
import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.FontCache;
import com.pos.cashier.referensi.URLS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private SharedPreferences appsPref;
    private Typeface fontDroidSans, fontDroidSansBold;
    private Button btnSinkronHO, btnOrder, btnListOrder, btnKirimHO, btnLogOut;
    private ProgressDialog pDialog;
    private PoshCashierDB poshCashierDB = null;
    private SQLiteDatabase db = null;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        fontDroidSans     = FontCache.get(this, "DroidSans");
        fontDroidSansBold = FontCache.get(this, "DroidSans-Bold");
        queue             = Volley.newRequestQueue(this);
        appsPref          = getSharedPreferences(URLS.PREF_NAME, Activity.MODE_PRIVATE);
        btnSinkronHO      = (Button) findViewById(R.id.btnSinkronHO);
        btnOrder          = (Button) findViewById(R.id.btnOrder);
        btnListOrder      = (Button) findViewById(R.id.btnListOrder);
        btnKirimHO        = (Button) findViewById(R.id.btnKirimHO);
        btnLogOut         = (Button) findViewById(R.id.btnLogOut);

        btnSinkronHO.setTypeface(fontDroidSans);
        btnOrder.setTypeface(fontDroidSans);
        btnListOrder.setTypeface(fontDroidSans);
        btnKirimHO.setTypeface(fontDroidSans);
        btnLogOut.setTypeface(fontDroidSans);

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        btnSinkronHO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStock();
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MakeOrderOne.class));
            }
        });

        btnListOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ListOrder.class));
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = appsPref.edit();
                editor.putString(Constant.KEY_EMPLOYEE_ID, "");
                editor.putString(Constant.KEY_EMPLOYEE_NAME, "");
                editor.putString(Constant.KEY_LOCATION_CODE, "");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    public void getStock() {
        pDialog.show();
        poshCashierDB = new PoshCashierDB(getApplicationContext());
        db = poshCashierDB.getWritableDatabase();
        poshCashierDB.createTableSalesman(db, getBaseContext());
        poshCashierDB.createTableStock(db, getBaseContext());
        poshCashierDB.createTableTransaction(db, getBaseContext());

        String strUrl = URLS.getStockUrl(this) + "&loccode=" + appsPref.getString(Constant.KEY_LOCATION_CODE, "");
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(strUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i=0; i<response.length(); i++) {
                        JSONObject jObjData = response.getJSONObject(i);
                        Stock stock   = new Stock();
                        stock.setItemId(jObjData.getString(Constant.KEY_ITEM_ID));
                        stock.setBarcode(jObjData.getString(Constant.KEY_BARCODE));
                        stock.setItemName(jObjData.getString(Constant.KEY_ITEM_NAME));
                        stock.setDescription(jObjData.getString(Constant.KEY_DESCRIPTION));
                        stock.setItemPrice(jObjData.getString(Constant.KEY_ITEM_PRICE));
                        stock.setQty(jObjData.getString(Constant.KEY_QTY));

                        // Add data stock to sqlite
                        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.STOCK_NAME + " WHERE "
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                db.close();
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "DONE!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
