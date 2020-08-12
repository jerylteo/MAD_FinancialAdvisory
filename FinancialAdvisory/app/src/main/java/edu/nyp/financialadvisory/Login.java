package edu.nyp.financialadvisory;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    String TAG = "Login";

    EditText edit_name = null;
    ImageButton backBtn = null;
    Button startBtn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_name = findViewById(R.id.name);
        backBtn = findViewById(R.id.back);
        startBtn = findViewById(R.id.start);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, JustStarted.class));
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit_name.getText().toString().matches("")) {
                    Boolean exist = retrieveFromDB();
                    if (exist)
                        startActivity(new Intent(Login.this, Success.class));
                    else
                        Toast.makeText(getApplicationContext(), "Name not found. Please try again.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter your name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean retrieveFromDB() {
        String name = edit_name.getText().toString();
        AccountsDBAdapter dbAdapter = new AccountsDBAdapter(getApplicationContext());
        Cursor cursor = null;
        Boolean valid = false;

        try {
            dbAdapter.open();
            cursor = dbAdapter.getAccount(name);

            if (cursor.moveToFirst()) {
                String n = cursor.getString(1);
                String e = cursor.getString(2);

                SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("name", n);
                editor.putString("email", e);

                editor.commit();
                valid = true;
            }
            else {
                valid = false;
            }

        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        finally {
            if (dbAdapter != null) {
                dbAdapter.close();
            }
        }

        return valid;
    }
}
