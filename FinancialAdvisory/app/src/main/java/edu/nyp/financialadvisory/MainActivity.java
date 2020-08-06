package edu.nyp.financialadvisory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String TAG = "Home Page";
    TextView viewBal = null, foodBal = null, transportBal = null, entertainmentBal = null;
    Button addDepositBtn = null;
    Button addExpenseBtn = null;
    ImageButton addBtn = null;
    ImageButton analysisBtn = null;
    ImageButton historyBtn = null;
    ImageButton currencyBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!profileExist()) {
            startActivity(new Intent(MainActivity.this, JustStarted.class));
        }
        else {
            setContentView(R.layout.activity_main);
            viewBal = findViewById(R.id.bal);
            foodBal = findViewById(R.id.foodBal);
            transportBal = findViewById(R.id.transportBal);
            entertainmentBal = findViewById(R.id.entertainmentBal);

            addBtn = findViewById(R.id.add);
            addExpenseBtn = findViewById(R.id.addExpense);
            addDepositBtn = findViewById(R.id.addDeposit);
            analysisBtn = findViewById(R.id.analysis);
            historyBtn = findViewById(R.id.history);
            currencyBtn = findViewById(R.id.currency);

            addBtn.setOnClickListener(clickListener);
            addExpenseBtn.setOnClickListener(clickListener);
            addDepositBtn.setOnClickListener(clickListener);
            analysisBtn.setOnClickListener(clickListener);
            historyBtn.setOnClickListener(clickListener);
            currencyBtn.setOnClickListener(clickListener);

            if (retrieveRecordsFromDB()) {
                updateRecordsFromPrefs();
            }
        }
    }

    public boolean profileExist() {
        SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);

        if (prefs.contains("name")) {
            Toast.makeText(getApplicationContext(), "Welcome back, " + prefs.getString("name", ""), Toast.LENGTH_SHORT).show();

            return true;
        }
        else {
            return false;
        }
    }

    public boolean retrieveRecordsFromDB() {
        boolean success = false;
        float bal = 0;
        float[] catBal = {0, 0, 0, 0};

        SharedPreferences profilePrefs = getSharedPreferences("profile", MODE_PRIVATE);
        String name = profilePrefs.getString("name", "");

        RecordsDBAdapter dbAdapter = new RecordsDBAdapter(getApplicationContext());
        Cursor cursor = null;

        SharedPreferences recordPrefs = getSharedPreferences("records", MODE_PRIVATE);
        SharedPreferences.Editor editor = recordPrefs.edit();

        try {
            dbAdapter.open();
            cursor = dbAdapter.getAllRecordsByName(name);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    System.out.println(cursor.getString(2));
                    if (cursor.getString(2).endsWith("Deposits")) {
                        bal += cursor.getFloat(4);
                    }
                    else if (cursor.getString(2).endsWith("Expenses")) {
                        bal -= cursor.getFloat(4);
                    }
                }

                String[] catArray = {"Food", "Transport", "Entertainment"};

                for(int i=0; i < catArray.length; i++) {
                    cursor = dbAdapter.getAllRecordsByNameCategory(name, catArray[i]);

                    if (cursor != null && cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            if (cursor.getString(2) == "Expenses") {
                                catBal[i] += cursor.getFloat(4);
                            }
                        }
                    }

                }
            }

            success = true;
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
            success = false;
        }
        finally {
            if (dbAdapter != null) {
                dbAdapter.close();
            }

            editor.putFloat("bal", bal);
            editor.putFloat("foodBal", catBal[0]);
            editor.putFloat("transportBal", catBal[1]);
            editor.putFloat("entertainmentBal", catBal[2]);

            editor.commit();

            for (float f : catBal) {
                System.out.println(f);
            }

        }

        return success;
    }

    public void updateRecordsFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("records", MODE_PRIVATE);

        viewBal.setText(Float.toString(prefs.getFloat("bal", 0.0f)));
        foodBal.setText(Float.toString(prefs.getFloat("foodBal", 0.0f)));
        transportBal.setText(Float.toString(prefs.getFloat("transportBal", 0.0f)));
        entertainmentBal.setText(Float.toString(prefs.getFloat("entertainmentBal", 0.0f)));
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == addBtn || v == addExpenseBtn) {
                startActivity(new Intent(MainActivity.this, AddExpense.class));
            } else if (v == addDepositBtn){
                startActivity(new Intent(MainActivity.this, AddDeposit.class));
            } else if (v == analysisBtn) {
                startActivity(new Intent(MainActivity.this, Analysis.class));
            } else if (v == historyBtn) {
                startActivity(new Intent(MainActivity.this, History.class));
            } else if (v == currencyBtn) {
                startActivity(new Intent(MainActivity.this, CurrencyConverter.class));
            }
        }
    };

}
