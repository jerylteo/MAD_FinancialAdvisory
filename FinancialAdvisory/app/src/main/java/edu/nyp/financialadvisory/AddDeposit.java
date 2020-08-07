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
import android.widget.SeekBar;
import android.widget.Toast;

public class AddDeposit extends AppCompatActivity {

    String TAG = "Add Deposits Page";
    EditText amount = null;
    ImageButton minus = null, plus = null;
    SeekBar seekBar = null;
    Button cancelBtn = null, sendBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deposit);

        amount = (EditText) findViewById(R.id.amount);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);
        seekBar = findViewById(R.id.seekBar);
        cancelBtn = findViewById(R.id.cancelDep);
        sendBtn = findViewById(R.id.sendDeposits);

        minus.setOnClickListener(clickListener);
        plus.setOnClickListener(clickListener);
        cancelBtn.setOnClickListener(clickListener);
        sendBtn.setOnClickListener(clickListener);

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
            else if (v == cancelBtn) {
                startActivity(new Intent(AddDeposit.this, MainActivity.class));
            }
            else if (v == sendBtn) {
                if (Float.parseFloat(amount.getText().toString()) > 0) {
                    SharedPreferences profilePrefs = getSharedPreferences("profile", MODE_PRIVATE);
                    String name = profilePrefs.getString("name", "");

                    RecordsDBAdapter dbAdapter = new RecordsDBAdapter(getApplicationContext());

                    SharedPreferences recordPrefs = getSharedPreferences("records", MODE_PRIVATE);
                    SharedPreferences.Editor editor = recordPrefs.edit();

                    try {
                        dbAdapter.open();
                        dbAdapter.insertRecord(name, "Deposits", "", Float.parseFloat(amount.getText().toString()));
                    }
                    catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                    finally {
                        if (dbAdapter != null) {
                            dbAdapter.close();
                        }


                        float input = recordPrefs.getFloat("bal", 0.0f);
                        input += Float.parseFloat(amount.getText().toString());

                        editor.putFloat("bal", input);
                        editor.putFloat("latest", Float.parseFloat(amount.getText().toString()));

                        editor.commit();

                        startActivity(new Intent(AddDeposit.this, AddSuccessful.class));
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "The system highly doubts a 0 deposit. Please check again", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };
}
