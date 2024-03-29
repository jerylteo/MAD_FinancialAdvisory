package edu.nyp.financialadvisory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Registration extends AppCompatActivity {

    String TAG = "Registration";

    EditText edit_name = null;
    EditText edit_email = null;
    ImageButton backBtn = null;
    Button startBtn = null;
    String currency = null;

    private boolean isGPS = false;
    private boolean isPermissionGranted = false;

    private boolean isContinue = false;
    private double wayLatitude = 0.0, wayLongtitude = 0.0;
    private StringBuilder stringBuilder;

    // txtlocation, txtcontinuelocation

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    String coordinates = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edit_name = findViewById(R.id.name);
        edit_email = findViewById(R.id.email);
        backBtn = findViewById(R.id.back);
        startBtn = findViewById(R.id.start);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(Registration.this, JustStarted.class));
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (!edit_name.getText().toString().matches("") || !edit_email.getText().toString().matches("")) {
                try {
                    saveToDB(edit_name.getText().toString(), edit_email.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveAsPreference();
                startActivity(new Intent(Registration.this, Success.class));
            }
            else {
                Toast.makeText(getApplicationContext(), "Please enter your name & email.", Toast.LENGTH_SHORT).show();
            }
            }
        });

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                isGPS = isGPSEnable;
            }

        });

        checkForPermission();

        setUpLocationListener();
        getLocation();

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(Registration.this, location -> {
            if (location != null) {
                wayLatitude = location.getLatitude();
                wayLongtitude = location.getLongitude();
                coordinates = String.format(Locale.getDefault(), "%s - %s", wayLatitude, wayLongtitude);
            }
            else {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        });
    }


    private void setUpLocationListener() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);     // 10 seconds
        locationRequest.setFastestInterval(5 * 1000);       // 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for(Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongtitude = location.getLongitude();

                        coordinates = String.format(Locale.getDefault(), "%s - %s", wayLatitude, wayLongtitude);

                        if (mFusedLocationClient != null) mFusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.GPS_REQUEST) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                isGPS = true;
            } else {
                Toast.makeText(getApplicationContext(), "Cannot retrieve GPS. Defaulting to Singapore.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case AppConstants.LOCATION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) isPermissionGranted = true;
                else {
                    Toast.makeText(getApplicationContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                    isPermissionGranted = false;
                }
                break;
            }
        }
    }


    private void checkForPermission() {
        if (ActivityCompat.checkSelfPermission(Registration.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Registration.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, AppConstants.LOCATION_REQUEST);
            isPermissionGranted = false;
        }
        else {
            isPermissionGranted = true;
        }
    }

    public void saveToDB(String name, String email) throws IOException {
        // do gps shit
        // insertAccount(name, email, currency)
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = gcd.getFromLocation(wayLatitude, wayLongtitude, 1);

        System.out.println(wayLatitude + " , " + wayLongtitude);

        if (addresses.size() > 0) {
            if (addresses.get(0).getCountryCode().endsWith("US")) currency = "USD";
            else if (addresses.get(0).getCountryCode().endsWith("UK")) currency = "EURO";
            else currency = "SGD";
        }

        AccountsDBAdapter dbAdapter = new AccountsDBAdapter(getApplicationContext());
        RecordsDBAdapter rDbAdapter = new RecordsDBAdapter(getApplicationContext());
        try {
            dbAdapter.open();
            rDbAdapter.open();
            dbAdapter.insertAccount(name, email, currency);
            rDbAdapter.insertRecord(name, "Deposits", "", 100.0f);
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        finally {
            if (dbAdapter != null) {
                dbAdapter.close();
            }
            if (rDbAdapter != null) {
                rDbAdapter.close();
            }
        }
    }

    public void saveAsPreference() {
        String name = edit_name.getText().toString();
        String email = edit_email.getText().toString();

        SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("currency", currency);

        editor.commit();
    }
}
