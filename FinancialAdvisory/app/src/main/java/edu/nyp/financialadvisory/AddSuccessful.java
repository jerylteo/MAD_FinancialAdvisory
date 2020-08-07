package edu.nyp.financialadvisory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddSuccessful extends AppCompatActivity {

    TextView review = null;
    Button continueBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_successful);

        review = findViewById(R.id.review);
        continueBtn = findViewById(R.id.con);

        getPrefs();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddSuccessful.this, MainActivity.class));
            }
        });
    }

    public void getPrefs() {
        SharedPreferences prefs = getSharedPreferences("records", MODE_PRIVATE);
        float latest = prefs.getFloat("latest", 0.0f);
        String text = "$" + latest;

        review.setText(text);
    }

}
