package edu.nyp.financialadvisory;

import androidx.appcompat.app.AppCompatActivity;

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

public class AddExpense extends AppCompatActivity {

    String TAG = "Add Expenses Page";
    EditText amount = null;
    ImageButton minus = null, plus = null, food = null, transport = null, entertainment = null;
    SeekBar seekBar = null;
    Button cancelBtn = null, sendBtn = null;
    String category = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        amount = (EditText) findViewById(R.id.amount);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);
        seekBar = findViewById(R.id.seekBar);
        food = findViewById(R.id.foodCat);
        transport = findViewById(R.id.transportCat);
        entertainment = findViewById(R.id.entertainmentCat);
        cancelBtn = findViewById(R.id.cancelExp);
        sendBtn = findViewById(R.id.sendExpense);

        minus.setOnClickListener(clickListener);
        plus.setOnClickListener(clickListener);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float amt = (float) i;
                amount.setText(Float.toString(amt));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        food.setOnClickListener(clickListener);
        transport.setOnClickListener(clickListener);
        entertainment.setOnClickListener(clickListener);
        cancelBtn.setOnClickListener(clickListener);
        sendBtn.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == minus) {
                float amt = Float.parseFloat(amount.getText().toString());
                amt -= 100;
                amount.setText(Float.toString(amt));
            }
            else if (v == plus) {
                float amt = Float.parseFloat(amount.getText().toString());
                amt += 100;
                amount.setText(Float.toString(amt));
            }
            else if (v == food) {
                category = "Food";
            }
            else if (v == transport) {
                category = "Transport";
            }
            else if (v == entertainment) {
                category = "Entertainment";
            }
            else if (v == cancelBtn) {
                startActivity(new Intent(AddExpense.this, MainActivity.class));
            }
            else if (v == sendBtn) {
                if (category == null) {
                    Toast.makeText(getApplicationContext(), "Please select a category.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (Float.parseFloat(amount.getText().toString()) > 0) {
                        SharedPreferences profilePrefs = getSharedPreferences("profile", MODE_PRIVATE);
                        String name = profilePrefs.getString("name", "");

                        RecordsDBAdapter dbAdapter = new RecordsDBAdapter(getApplicationContext());

                        SharedPreferences recordPrefs = getSharedPreferences("records", MODE_PRIVATE);
                        SharedPreferences.Editor editor = recordPrefs.edit();

                        if (recordPrefs.getFloat("bal", 0.0f) <= Float.parseFloat(amount.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Insufficient Balance. Current balance: $" + recordPrefs.getFloat("bal", 0.0f), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            try {
                                dbAdapter.open();
                                dbAdapter.insertRecord(name, "Expenses", category, Float.parseFloat(amount.getText().toString()));
                            }
                            catch (Exception e) {
                                Log.d(TAG, e.getMessage());
                            }
                            finally {
                                if (dbAdapter != null) {
                                    dbAdapter.close();
                                }

                                // why is this not working?
//                        String input = category.toLowerCase().concat("Bal");
//                        editor.putFloat(input, Float.parseFloat(amount.getText().toString()));
//                        editor.commit();

                                editor.putFloat("latest", Float.parseFloat(amount.getText().toString()));
                                editor.commit();

                                startActivity(new Intent(AddExpense.this, AddSuccessful.class));
                            }
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "The system highly doubts a 0 expenses. Please check again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

}
