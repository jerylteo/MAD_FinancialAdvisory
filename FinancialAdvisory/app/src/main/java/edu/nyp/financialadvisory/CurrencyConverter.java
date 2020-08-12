package edu.nyp.financialadvisory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CurrencyConverter extends AppCompatActivity {

    String TAG = "Currency Converter Page";
    TextView amount = null;
    ImageButton sgd = null, usd = null, euro = null;
    Button cancelBtn = null, convertBtn = null;
    String currency = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        amount = findViewById(R.id.amount);
        sgd = findViewById(R.id.sgd);
        usd = findViewById(R.id.usd);
        euro = findViewById(R.id.euro);
        cancelBtn = findViewById(R.id.cancel);
        convertBtn = findViewById(R.id.convert);
        sgd.setOnClickListener(clickListener);
        usd.setOnClickListener(clickListener);
        euro.setOnClickListener(clickListener);
        cancelBtn.setOnClickListener(clickListener);
        convertBtn.setOnClickListener(clickListener);

        retrieveDataFromPrefs();
    }

    public void retrieveDataFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("records", MODE_PRIVATE);
        SharedPreferences pPrefs = getSharedPreferences("profile", MODE_PRIVATE);

        amount.setText(Float.toString(prefs.getFloat("bal", 0.0f)) + " " + pPrefs.getString("currency", ""));
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == sgd) {
                currency = "SGD";
            }
            else if (v == usd) {
                currency = "USD";
            }
            else if (v == euro) {
                currency = "EURO";
            }
            else if (v == cancelBtn) {
                startActivity(new Intent(CurrencyConverter.this, MainActivity.class));
            }
            else if (v == convertBtn) {
                alertDialog();

            }
        }
    };

    public void alertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to convert your currency to " + currency)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        convertCurrency();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void convertCurrency() {
        if (currency != null) {
            SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
            String name = prefs.getString("name", "");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("currency", currency);
            editor.commit();

            prefs = getSharedPreferences("records", MODE_PRIVATE);

            float bal = prefs.getFloat("bal", 0.0f);

            editor = prefs.edit();

            AccountsDBAdapter dbAdapter = new AccountsDBAdapter(getApplicationContext());
            Cursor cursor = null;

            try {
                dbAdapter.open();
                cursor = dbAdapter.getAccount(name);

                String current = cursor.getString(3);
                dbAdapter.updateCurrency(name, currency);

                if (currency.endsWith("SGD")) {
                    if (current.endsWith("USD")) bal *= 1.37;       // USD to SGD
                    else if (current.endsWith("EURO")) bal *= 1.62;     // EURO to SGD
                }
                else if (currency.endsWith("USD")) {
                    if (current.endsWith("SGD")) bal *= 0.73;       // SGD to USD
                    else if (current.endsWith("EURO")) bal *= 1.18;     // EURO to USD
                }
                else if (currency.endsWith("EURO")) {
                    if (current.endsWith("SGD")) bal *= 0.62;       // SGD to EURO
                    else if (current.endsWith("USD")) bal *= 0.85;
                }
            }
            catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            finally {
                if (dbAdapter != null) {
                    dbAdapter.close();
                }

                editor.putFloat("bal", bal);
                editor.commit();


                startActivity(new Intent (CurrencyConverter.this, MainActivity.class));
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Please select a currency", Toast.LENGTH_SHORT).show();
        }

    }
}
