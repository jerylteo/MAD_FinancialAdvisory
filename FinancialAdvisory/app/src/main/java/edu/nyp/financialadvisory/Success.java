package edu.nyp.financialadvisory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Success extends AppCompatActivity {

    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Toast.makeText(getApplicationContext(), "Here's a free $100 to kickstart your budgeting!", Toast.LENGTH_SHORT).show();

        startBtn = findViewById(R.id.start);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Success.this, MainActivity.class));
            }
        });
    }
}
