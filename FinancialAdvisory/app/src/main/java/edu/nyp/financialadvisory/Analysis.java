package edu.nyp.financialadvisory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Analysis extends AppCompatActivity {

    String TAG = "Analysis Page";
    Button continueBtn = null;
    TextView analysis = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        continueBtn = findViewById(R.id.con);
        analysis = findViewById(R.id.analysis);

        retrieveBalance();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Analysis.this, MainActivity.class));
            }
        });
    }

    public void retrieveBalance() {
        SharedPreferences prefs = getSharedPreferences("records", MODE_PRIVATE);
        float bal = prefs.getFloat("bal", 0.0f);

        if (bal < 100) {
            analysis.setText("You successfully saved money! No matter how small the amount, a step taken is a step closer to financial freedom.");
        }
        else if (bal >= 100 && bal < 500) {
            analysis.setText("You have very little expenses, a sign that you are thrifty and have good finance management skills! Keep it up!");
        }
        else {
            analysis.setText("Excellent job! Financial freedom is nothing but reality. Maintain and succeed!");
        }

    }
}
