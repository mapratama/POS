package com.pos.cashier;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Window;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pos.cashier.database.PoshCashierDB;
import com.pos.cashier.model.Salesman;
import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.URLS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends Activity {
    private PoshCashierDB poshCashierDB = null;
    private SQLiteDatabase db = null;
    private RequestQueue queue;
    private SharedPreferences appsPref;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        appsPref = getSharedPreferences(URLS.PREF_NAME, Activity.MODE_PRIVATE);
        queue    = Volley.newRequestQueue(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!appsPref.getString(Constant.KEY_EMPLOYEE_ID, "").equals("")) {
//                    if (appsPref.getString(Constant.KEY_BASE_URL, "").equals("") ||
//                            appsPref.getString(Constant.KEY_USER_LOCATION_ID, "").equals("")) {
//                        startActivity(new Intent(getApplicationContext(), ConfigActivity.class));
//                        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
//                    }
//                    else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
//                    }
                    finish();
                } else {
                    new InsertDataSalesmanToSqlite().execute();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    public void getSalesman() {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(URLS.getSalesmanUrl(this), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i=0; i<response.length(); i++) {
                        JSONObject jObjData = response.getJSONObject(i);
                        Salesman salesman   = new Salesman();
                        salesman.setEmployeeId(jObjData.getString(Constant.KEY_EMPLOYEE_ID));
                        salesman.setEmployeeName(jObjData.getString(Constant.KEY_EMPLOYEE_NAME));
                        salesman.setLocationCode(jObjData.getString(Constant.KEY_LOCATION_CODE));
                        salesman.setUserName(jObjData.getString(Constant.KEY_USER_NAME));
                        salesman.setPasswordValue(jObjData.getString(Constant.KEY_PASSWORD_VALUE));

                        // Add data salesman to sqlite
                        poshCashierDB.addSalesman(salesman);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    public class InsertDataSalesmanToSqlite extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            poshCashierDB = new PoshCashierDB(getApplicationContext());
            db = poshCashierDB.getWritableDatabase();
            poshCashierDB.createTableSalesman(db, getBaseContext());
            poshCashierDB.createTableStock(db, getBaseContext());
            poshCashierDB.createTableTransaction(db, getBaseContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (!poshCashierDB.checkTableSalesman()) {
                getSalesman();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );

            // close this activity
            finish();
        }
    }
}
