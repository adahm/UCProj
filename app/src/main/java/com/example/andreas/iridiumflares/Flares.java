package com.example.andreas.iridiumflares;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Flares {
    Date date;
    int azimuth;
    int altitude;

    public Flares(Date date, int azimuth, int altitude) {
        this.date = date;
        this.azimuth = azimuth;
        this.altitude = altitude;
    }

    public Flares(String date, String az, String alt){
        this.azimuth = Integer.parseInt(az);
        this.altitude = Integer.parseInt(alt);
        this.date = parseDate(date);
    }

    // Lazy solution to add current year to dates, would not work if used around New Years Eve.
    // Takes date in the string format provided from data source and returns date object.
    public Date parseDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy MMM d, H:m:s");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String year = sdf.format(new Date());

            Log.i("Parsing", year+ " " +dateString);
            return format.parse(year+ " "+dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public String toString(){
        return "Date: " + date.toString() + "; Altitude: " + altitude + "; Azimuth: " + azimuth;
    }
}
