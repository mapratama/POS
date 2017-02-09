package com.pos.cashier;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.URLS;

public class ConfigActivity extends AppCompatActivity {

    private SharedPreferences appsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        appsPref = getSharedPreferences(URLS.PREF_NAME, Activity.MODE_PRIVATE);

        Button buttonSetConfig = (Button) findViewById(R.id.btn_set_config);
        Button buttonLocation = (Button) findViewById(R.id.btn_location);
        Button buttonDashboard = (Button) findViewById(R.id.btn_dashboard);

        final Activity activity = this;
        buttonDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appsPref.getString(Constant.KEY_BASE_URL, "").equals(""))
                    Toast.makeText(activity, "Mohon isi dahulu API URL pada menu SET CONFIG", Toast.LENGTH_SHORT).show();
                else if (appsPref.getString(Constant.KEY_USER_LOCATION_ID, "").equals(""))
                    Toast.makeText(activity, "Mohon isi dahulu data location pada menu LOCATION", Toast.LENGTH_SHORT).show();
                else
                    startActivity(new Intent(activity, MainActivity.class));
            }
        });

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appsPref.getString(Constant.KEY_BASE_URL, "").equals(""))
                    Toast.makeText(activity, "Mohon isi dahulu API URL pada menu SET CONFIG", Toast.LENGTH_SHORT).show();
                else
                    startActivity(new Intent(activity, LocationActivity.class));
            }
        });

        buttonSetConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, SetUrlActivity.class));
            }
        });
    }
}
