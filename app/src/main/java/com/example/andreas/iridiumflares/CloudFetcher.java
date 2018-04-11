package com.example.andreas.iridiumflares;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

public class CloudFetcher {

    double longitude;
    double latitude;

    public CloudFetcher(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void cloudCheck(ArrayList<Flares> flareList) throws IOException, JSONException, ParseException {
        String URL = "https://opendata-download-metfcst.smhi.se/api";
        JSONObject parameterObject = readJsonFromUrl(URL + "/category/pmp3g/version/2/geotype/point/lon/"+ longitude+"/lat/"+latitude+"/data.json");
        JSONArray wheterArray = parameterObject.getJSONArray("timeSeries");
        JSONObject firstTime;
        JSONObject endTime;
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
        //TODO create parser to get the time from the jsobobject

        //loop though every flare
        for (Flares f: flareList) {
            //for every flare find the best time by looping though the times in the timesseries list
            //when we reach a timestamp where the first time is before and the next time is after stop
            //compare the time difference between the time before and the time after
            //Select time with the smallest difference
            //get the tcc_mean value for that time
            //if it is greater than 4 remove the flare from the array
            //otherwise keep it and contiune with the next flare
            Date flareDate =f.getDate();

            for (int i = 0;wheterArray.length()-1>i;i++){
                firstTime = wheterArray.getJSONObject(i);
                endTime = wheterArray.getJSONObject(i+1);

                LocalDateTime l1 = new LocalDateTime(firstTime.getString("validTime").split("Z")[0]);
                LocalDateTime l2 = new LocalDateTime(endTime.getString("validTime").split("Z")[0]);
                LocalDateTime l3 = new LocalDateTime(flareDate);

                Log.i("First",l1.toString());
                Log.i("Okdate",l3.toString());

                if(l1.isBefore(l3)){
                    Log.i("ok","good;");
                }

                if(l1.isBefore(l3) && l2.isAfter(l3)){

                    Period p1 = new Period(l1, l3);
                    Period p2 = new Period(l3, l2);

                    int hours1 = p1.getHours();
                    int hours2 = p2.getHours();

                    int cloudcover;
                    if(hours1<hours2){
                        cloudcover = firstTime.getJSONArray("parameters").getJSONObject(7).getJSONArray("values").getInt(0);

                    }
                    else {
                        cloudcover = endTime.getJSONArray("parameters").getJSONObject(7).getJSONArray("values").getInt(0);

                    }
                    Log.i("Cover","amount:" +cloudcover);
                    if(cloudcover>4){
                        flareList.remove(f);
                    }
                    break;
                }
            }
        }
    }

    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        String text = readStringFromUrl(url);
        return new JSONObject(text);
    }
    private String readStringFromUrl(String url) throws IOException {

        InputStream inputStream = new URL(url).openStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }
            return stringBuilder.toString();
        } finally {
            inputStream.close();
        }
    }

}
