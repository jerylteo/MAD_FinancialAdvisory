package edu.nyp.financialadvisory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    String TAG = "History Page";

    TableLayout recordTable = null;
    Button backBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recordTable = findViewById(R.id.recordTable);
        backBtn = findViewById(R.id.back);

        retrieveRecordsFromDB();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(History.this, MainActivity.class));
            }
        });
    }

    public void retrieveRecordsFromDB() {
        SharedPreferences profilePrefs = getSharedPreferences("profile", MODE_PRIVATE);
        String name = profilePrefs.getString("name", "");

        ArrayList<Record> recordList = new ArrayList<>();
        Record record;

        RecordsDBAdapter dbAdapter = new RecordsDBAdapter(getApplicationContext());
        Cursor cursor = null;

        try {
            dbAdapter.open();
            cursor = dbAdapter.getAllRecordsByName(name);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    record = new Record(cursor.getString(2), cursor.getString(3), cursor.getFloat(4));
                    recordList.add(record);
                }
            }
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        finally {
            if (dbAdapter != null) {
                dbAdapter.close();
            }
            addRows(recordList);
        }
    }

    public void addRows(ArrayList<Record> recordList) {
        System.out.println("Adding rows..");
        if (recordList.size() <= 0) {
            // print nothing
            TableRow row = new TableRow(this);
            TextView v = new TextView(this);
            v.setText("No records found.");
            row.addView(v);
            recordTable.addView(row);
        }
        else {
            int size = 10;
            if (recordList.size() <= 10) {
                size = recordList.size();
            }
            for(int i=0; i<size; i++) {
                TableRow row = new TableRow(this);

                TextView v = new TextView(this);
                if (recordList.get(i).getType().endsWith("Expenses")) {
                    v.setText(recordList.get(i).getCategory());
                }
                else {
                    v.setText(recordList.get(i).getType());
                }

                TextView amtView = new TextView(this);
                amtView.setText(Float.toString(recordList.get(i).getAmt()));

                row.addView(v);
                row.addView(amtView);
                recordTable.addView(row);
            }
        }
    }
}
