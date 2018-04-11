package com.example.andreas.iridiumflares;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class MainActivity extends Activity {

    // Declaring a Location Manager
    protected LocationManager mLocationManager;
    Context context = MainActivity.this;
    final List<Flares> flareList = new ArrayList<Flares>();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

        setContentView(R.layout.activity_main);

        final ListView FlareListView = findViewById(R.id.list);



        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        //TODO fix so the app works after the firsst start
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
        final double currentLatitude = currentLocation.getLatitude();
        final double currentLongitude = currentLocation.getLongitude();
        Log.i("i", "Longitude: " + currentLongitude + " Latitude: " + currentLatitude);


        final FlaresFetcher flareFetcher = new FlaresFetcher(currentLongitude, currentLatitude);

        new AsyncTask<Void, Void, ArrayList<Flares>>() {
            @Override
            protected ArrayList<Flares> doInBackground(Void... params) {
                ArrayList<Flares> response = new ArrayList<Flares>();
                try {
                    response  = flareFetcher.fetchData(getApplicationContext());
                    CloudFetcher cloudFetcher = new CloudFetcher(currentLongitude,currentLatitude);
                    cloudFetcher.cloudCheck(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(ArrayList<Flares> response) {
                int i = 1;
                List<String> StringList = new ArrayList<String>();
                for(Flares f : response){
                    flareList.add(f);
                    StringList.add(i+ ". " + f.toString());
                    Log.i("Fetcher", "Entry: " + f.toString());
                    i++;
                }
                String[] flareStrings = StringList.toArray(new String[StringList.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, flareStrings);
                FlareListView.setAdapter(adapter);
                Log.i("i", "Created menu item");
                FlareListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
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
                // Do whatever you want to do with the network response
            }
        }.execute();







    }

    public void Switch(String data){
        Intent intent = new Intent(this, CompassActivity.class);
        int index = Integer.parseInt(data.substring(0,1))-1;

        intent.putExtra("Azimuth", flareList.get(index).getAzimuth() );
        intent.putExtra("Pitch",flareList.get(index).getAltitude());
        Log.i("date",flareList.get(index).getDate().toString());
        String dateString = flareList.get(index).getDate().toString();
        String delims = "[ ]+";
        String[] parts = dateString.split(delims);
        String parseDate = parts[1] + " "+ parts[2] + " " + parts[5]+ " "+ parts[3];
        LocalDateTime time = DateTimeFormat.forPattern("MMM dd yyyy HH:mm:ss").parseLocalDateTime(parseDate);
        //format Tue Apr 10 03:26:19 GMT+00:00 2018
        DateTimeZone zone = DateTimeZone.forID("Europe/Madrid");
        LocalDateTime currTime = new LocalDateTime(zone);

        Period p = new Period(currTime, time, PeriodType.millis());


        Log.i("curr d",currTime.toString());

        Log.i("d",time.toString());

        Log.i("d","mili:"+ p.getValue(0));

        intent.putExtra("Time",p.getValue(0));

        // Apr 11 2018 03:20:15
        startActivity(intent);
    }

}
