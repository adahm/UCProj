package com.example.andreas.iridiumflares;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.MalformedURLException;

public class MainActivity extends Activity {

    // Declaring a Location Manager
    protected LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("i", "NO GPS PERMISSION");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                return;
            }

            @Override
            public void onProviderDisabled(String s) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
        };
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Location currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        while (currentLocation == null){
            Log.i("i", "No location found yet");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        mLocationManager.removeUpdates(locationListener);
        double currentLatitude = currentLocation.getLatitude();
        double currentLongitude = currentLocation.getLongitude();
        Log.i("i", "Longitude: " + currentLongitude + " Latitude: " + currentLatitude);


        FlaresFetcher flareFetcher = new FlaresFetcher(currentLongitude, currentLatitude);
        try {
            flareFetcher.fetchData();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] myStringArray = {"test"};
        ListView FlareList = findViewById(R.id.list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, myStringArray);

        FlareList.setAdapter(adapter);

        Log.i("i", "Created menu item");
        FlareList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
                Log.i("i", "menu item selected: " + value);
                Switch(value);
                /*
                Intent intent = new Intent(this, VÅRAN-EGEN-KLASS.class);
                ****KOORDINATER****
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                 */

            }
        });

    }

    public void Switch(String angle){
        Intent intent = new Intent(this, CompassActivity.class);
        intent.putExtra("Angle", angle );
        startActivity(intent);
    }

}
