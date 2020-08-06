package edu.nyp.financialadvisory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Registration extends AppCompatActivity {

    String TAG = "Registration";

    EditText edit_name = null;
    EditText edit_email = null;
    ImageButton backBtn = null;
    Button startBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edit_name = findViewById(R.id.name);
        edit_email = findViewById(R.id.email);
        backBtn = findViewById(R.id.back);
        startBtn = findViewById(R.id.start);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(Registration.this, JustStarted.class));
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (!edit_name.getText().toString().matches("") || !edit_email.getText().toString().matches("")) {
                saveToDB(edit_name.getText().toString(), edit_email.getText().toString());
                saveAsPreference();
                startActivity(new Intent(Registration.this, Success.class));
            }
            else {
                Toast.makeText(getApplicationContext(), "Please enter your name & email.", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    public void saveToDB(String name, String email) {
        AccountsDBAdapter dbAdapter = new AccountsDBAdapter(getApplicationContext());
        RecordsDBAdapter rDbAdapter = new RecordsDBAdapter(getApplicationContext());
        try {
            dbAdapter.open();
            rDbAdapter.open();
            dbAdapter.insertAccount(name, email);
            rDbAdapter.insertRecord(name, "Deposits", "", 100.0f);
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        finally {
            if (dbAdapter != null) {
                dbAdapter.close();
            }
            if (rDbAdapter != null) {
                rDbAdapter.close();
            }
        }
    }

    public void saveAsPreference() {
        String name = edit_name.getText().toString();
        String email = edit_email.getText().toString();

        SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", name);
        editor.putString("email", email);

        editor.commit();
    }
}
