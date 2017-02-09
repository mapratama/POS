package com.pos.cashier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.cashier.database.PoshCashierDB;
import com.pos.cashier.model.Salesman;
import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.FontCache;
import com.pos.cashier.referensi.URLS;
import java.util.ArrayList;

public class LoginActivity extends Activity {
    private PoshCashierDB poshCashierDB = null;
    private SQLiteDatabase db = null;
    private SharedPreferences appsPref;
    private Typeface fontDroidSans, fontDroidSansBold;
    private TextView lblUsername, lblPassword;
    private EditText txtUsername, txtPassword;
    private Button btnLogIn;
    private ArrayList<Salesman> salesmanArrayList = null;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        fontDroidSans     = FontCache.get(this, "DroidSans");
        fontDroidSansBold = FontCache.get(this, "DroidSans-Bold");
        appsPref          = getSharedPreferences(URLS.PREF_NAME, Activity.MODE_PRIVATE);
        lblUsername       = (TextView) findViewById(R.id.lblUsername);
        txtUsername       = (EditText) findViewById(R.id.txtUsername);
        lblPassword       = (TextView) findViewById(R.id.lblPassword);
        txtPassword       = (EditText) findViewById(R.id.txtPassword);
        btnLogIn          = (Button) findViewById(R.id.btnLogIn);

        lblUsername.setTypeface(fontDroidSansBold);
        txtUsername.setTypeface(fontDroidSans);
        lblPassword.setTypeface(fontDroidSansBold);
        txtPassword.setTypeface(fontDroidSans);
        btnLogIn.setTypeface(fontDroidSansBold);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtUsername.getText().length()==0) {
                    txtUsername.setError("Username is Empty!");
                } else if (txtPassword.getText().length()==0) {
                    txtPassword.setError("Password is Empty!");
                } else {
                    new getLogin().execute();
                }
            }
        });

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);
    }

    public class getLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            poshCashierDB = new PoshCashierDB(getApplicationContext());
            db = poshCashierDB.getWritableDatabase();
            poshCashierDB.createTableSalesman(db, getBaseContext());
            poshCashierDB.createTableStock(db, getBaseContext());
            poshCashierDB.createTableTransaction(db, getBaseContext());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            salesmanArrayList = poshCashierDB.getLogin(txtUsername.getText().toString(), txtPassword.getText().toString());
            if (salesmanArrayList.size()>0) {
                // Set employee_id to pref
                Salesman salesman = salesmanArrayList.get(0);
                SharedPreferences.Editor editor = appsPref.edit();
                editor.putString(Constant.KEY_EMPLOYEE_ID, salesman.getEmployeeId());
                editor.putString(Constant.KEY_EMPLOYEE_NAME, salesman.getEmployeeName());
                editor.putString(Constant.KEY_LOCATION_CODE, salesman.getLocationCode());
                editor.commit();

                startActivity(new Intent(getApplicationContext(), ConfigActivity.class));
                // close this activity
                finish();
            } else {
                pDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Username and Password not match!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
