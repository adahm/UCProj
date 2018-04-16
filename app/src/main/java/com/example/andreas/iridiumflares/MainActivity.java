package com.example.andreas.iridiumflares;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    //id for notification channel
    public static String channelID;

    protected LocationManager mLocationManager;
    Context context = MainActivity.this;
    //list for the Flares
    final List<Flares> flareList = new ArrayList<Flares>();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

        setContentView(R.layout.activity_main);

        final ListView FlareListView = findViewById(R.id.list);

        Location currentLocation = getLocation();

        //create notification channel
        creatNotificationChannel();

        final double currentLatitude = currentLocation.getLatitude();
        final double currentLongitude = currentLocation.getLongitude();
        Log.i("i", "Longitude: " + currentLongitude + " Latitude: " + currentLatitude);

        //create object that will get the flares at the current location
        final FlaresFetcher flareFetcher = new FlaresFetcher(currentLongitude, currentLatitude);

        //web connections are not allowed to be run in main thread:
        new AsyncTask<Void, Void, ArrayList<Flares>>() {
            @Override
            protected ArrayList<Flares> doInBackground(Void... params) {
                ArrayList<Flares> response = new ArrayList<Flares>();
                try {
                    //get the flares at the current postion
                    response  = flareFetcher.fetchData(getApplicationContext());
                    //get the Forceast from SMHI at the current position
                    CloudFetcher cloudFetcher = new CloudFetcher(currentLongitude,currentLatitude);
                    //remove flares from the list where the clouds will cover the sky
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
                //create a string list of the flares and add
                for(Flares f : response){
                    flareList.add(f);
                    StringList.add(i+ ". " + f.toString());
                    Log.i("Fetcher", "Entry: " + f.toString());
                    i++;
                }

                String[] flareStrings = StringList.toArray(new String[StringList.size()]);
                //create an adapter to fill the listview with the flares
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, flareStrings);
                FlareListView.setAdapter(adapter);
                Log.i("i", "Created menu item");

                //set a click listener for the listview
                FlareListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View v, int position,
                                            long arg3)
                    {
                        //get the value from the selected item
                        String value = (String)adapter.getItemAtPosition(position);
                        Log.i("i", "menu item selected: " + value);
                        //switch activity when clicked
                        Switch(value);

                    }
                });
            }
        }.execute();
    }

    private void creatNotificationChannel(){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        channelID = "Flare_Id";
        CharSequence channelName = "Flare_channel";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, importance);

        notificationManager.createNotificationChannel(notificationChannel);
    }

    // Method for retrieving the current location, will handle permissions and wait for a GPS location before returning
    @NonNull
    private Location getLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //get permissions to use the phones location
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        //TODO fix so the app works after the first start
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("i", "NO GPS PERMISSION");
        }

        //create a locationlistener to get location of the phone
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                return;
            }

            @Override
            public void onProviderDisabled(String s) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
        };
        //get updates of the phones current position
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        //get the current location of the phone
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
        return currentLocation;
    }

    //method called when item in the listview is selected and will switch from the MainActivity to the compassActivity
    public void Switch(String data){

        Intent intent = new Intent(this, CompassActivity.class);
        //get the index of the item selected to get the data from the list
        int index = Integer.parseInt(data.substring(0,1))-1;
        //add the potch and Azimuth to the intent so the compass can use it
        intent.putExtra("Azimuth", flareList.get(index).getAzimuth() );
        intent.putExtra("Pitch",flareList.get(index).getAltitude());

        Log.i("date",flareList.get(index).getDate().toString());

        //Parse the date for the flare and get the difference in millis from the current time for the countdown
        String dateString = flareList.get(index).getDate().toString();
        String delims = "[ ]+";
        String[] parts = dateString.split(delims);
        String parseDate = parts[1] + " "+ parts[2] + " " + parts[5]+ " "+ parts[3];
        LocalDateTime time = DateTimeFormat.forPattern("MMM dd yyyy HH:mm:ss").parseLocalDateTime(parseDate);
        //format Tue Apr 10 03:26:19 GMT+00:00 2018

        //set cest/cet timezone
        DateTimeZone zone = DateTimeZone.forID("Europe/Stockholm");
        LocalDateTime currTime = new LocalDateTime(zone);

        Period p = new Period(currTime, time, PeriodType.millis());
        //add the difference in millis to the intent
        intent.putExtra("Time",p.getValue(0));

        //start the compass activity
        startActivity(intent);
    }



}
