package edu.nyp.financialadvisory;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

public class JustStarted extends AppCompatActivity {

    Button createBtn = null;
    Button loginBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_just_started);

        System.out.println("IN JUST STARTED");
        createBtn = findViewById(R.id.create);
        createBtn.setOnClickListener(clickListener);
        loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == createBtn) {
                System.out.println("CLICKED CREATE ACCOUNT");
                startActivity(new Intent(JustStarted.this, Registration.class));
            } else if (v == loginBtn) {
                startActivity(new Intent(JustStarted.this, Login.class));
            }
        }
    };

}
