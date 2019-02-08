package com.example.shan.mylocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView latitude;
    private TextView longitude;

    private TextView provText;
    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;

    private Location bestLocation = null;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude = (TextView) findViewById(R.id.lat);
        longitude = (TextView) findViewById(R.id.lon);
        provText = (TextView) findViewById(R.id.prov);


        //ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // Get the location manager
        locationManager = (LocationManager)  getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default

        criteria.setCostAllowed(false);

        mylistener = new MyLocationListener();

        getLoc();

    }

    public void getLoc(){
        try {
            Location location = null;
            // get the best provider depending on the criteria
            provider = locationManager.getBestProvider(criteria, true);
            if (provider == null){
                Toast.makeText(this, "provider not found. Returning null!", Toast.LENGTH_SHORT);
                return;
            }
            // the last known location of this provider
            location = locationManager.getLastKnownLocation(provider);
            //location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            /*List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {
                location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }
                if (bestLocation == null || location.getAccuracy() > bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = location;
                }
            }*/

            if (location != null) {
                mylistener.onLocationChanged(location);
            }
            /*else {
                leads to the settings because there is no last known location
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }*/
            // location updates: at least 1 meter and 200millsecs change
            locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, mylistener);

        }catch (SecurityException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    public void getLocation(View v){
        getLoc();
    }

    public void openMaps(View v){
        Intent intent = new Intent(this, MapsActivity.class);

        if (bestLocation!=null) {
            intent.putExtra("bestLocation", "is known");
            intent.putExtra("longitude", bestLocation.getLongitude());
            intent.putExtra("latitude", bestLocation.getLatitude());
        }
        startActivity(intent);
    }



    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            bestLocation = location;
            // Initialize the location fields
            latitude.setText("Latitude: "+String.valueOf(location.getLatitude()));
            longitude.setText("Longitude: "+String.valueOf(location.getLongitude()));
            provText.setText(provider + " provider has been selected.");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(MainActivity.this, provider + "'s status changed to "+status +"!",   Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Toast.makeText(MainActivity.this, "Provider " + provider + " enabled!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(MainActivity.this, "Provider " + provider + " disabled!", Toast.LENGTH_SHORT).show();
        }
    }
}
