package com.pos.cashier;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pos.cashier.referensi.Constant;
import com.pos.cashier.referensi.URLS;

public class SetUrlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_url);

        final EditText baseURLEditText = (EditText) findViewById(R.id.base_url);

        final Activity activity = this;
        Button saveButton = (Button) findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String baseUrl = baseURLEditText.getText().toString();
                if (baseUrl.isEmpty())
                    Toast.makeText(activity, "Maaf, API URL tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                else {
                    SharedPreferences appsPref = getSharedPreferences(URLS.PREF_NAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = appsPref.edit();
                    editor.putString(Constant.KEY_BASE_URL, baseUrl);
                    editor.commit();

                    Toast.makeText(activity, "API URL berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
