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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Settings extends AppCompatActivity {

    String TAG = "Settings Page";
    TextView name = null;
    EditText email = null;
    TextView currencySettings = null, country = null, coordinates = null;
    Button currencyBtn = null, cancelBtn = null, saveBtn = null, aboutBtn = null, syncBtn = null;

    private boolean isGPS = false;
    private boolean isPermissionGranted = false;

    private boolean isContinue = false;
    private double wayLatitude = 0.0, wayLongtitude = 0.0;
    private StringBuilder stringBuilder;

    // txtlocation, txtcontinuelocation

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    String coord = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        currencySettings = findViewById(R.id.currencySettings);
        country = findViewById(R.id.country);
        coordinates = findViewById(R.id.coordinates);
        currencyBtn = findViewById(R.id.currencyBtn);
        cancelBtn = findViewById(R.id.cancel);
        saveBtn = findViewById(R.id.save);
        aboutBtn = findViewById(R.id.about);
        syncBtn = findViewById(R.id.sync);

        currencyBtn.setOnClickListener(clickListener);
        cancelBtn.setOnClickListener(clickListener);
        saveBtn.setOnClickListener(clickListener);
        aboutBtn.setOnClickListener(clickListener);
        syncBtn.setOnClickListener(clickListener);

        retrieveDataFromDB();

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                isGPS = isGPSEnable;
            }

        });

        checkForPermission();

        setUpLocationListener();
        getLocation();

        try {
            updateCountry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(Settings.this, location -> {
            if (location != null) {
                wayLatitude = location.getLatitude();
                wayLongtitude = location.getLongitude();
                coord = String.format(Locale.getDefault(), "%s , %s", wayLatitude, wayLongtitude);
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

                        coord = String.format(Locale.getDefault(), "%s , %s", wayLatitude, wayLongtitude);

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
        if (ActivityCompat.checkSelfPermission(Settings.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Settings.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, AppConstants.LOCATION_REQUEST);
            isPermissionGranted = false;
        }
        else {
            isPermissionGranted = true;
        }
    }

    public void retrieveDataFromDB() {
        SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
        name.setText(prefs.getString("name", ""));
        email.setText(prefs.getString("email", ""));
        currencySettings.setText(prefs.getString("currency", ""));
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == currencyBtn) {
                startActivity(new Intent(Settings.this, CurrencyConverter.class));
            } else if (v == cancelBtn){
                startActivity(new Intent(Settings.this, MainActivity.class));
            } else if (v == saveBtn) {
                SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);

                AccountsDBAdapter dbAdapter = new AccountsDBAdapter(getApplicationContext());
                RecordsDBAdapter rDbAdapter = new RecordsDBAdapter(getApplicationContext());

                try {
                    dbAdapter.open();
                    dbAdapter.updateName(prefs.getString("name", ""), name.getText().toString());
                    rDbAdapter.open();
                    rDbAdapter.updateName(prefs.getString("name", ""), name.getText().toString());
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


                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("name", name.getText().toString());
                editor.putString("email", email.getText().toString());
                editor.commit();


                startActivity(new Intent(Settings.this, MainActivity.class));
            } else if (v == aboutBtn) {
                startActivity(new Intent(Settings.this, About.class));
            } else if (v == syncBtn) {
                try {
                    updateCountry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void updateCountry() throws IOException {
        coordinates.setText(coord);
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = gcd.getFromLocation(wayLatitude, wayLongtitude, 1);

        if (addresses.size() > 0) {
            country.setText(addresses.get(0).getCountryCode());
        }
    }
}
