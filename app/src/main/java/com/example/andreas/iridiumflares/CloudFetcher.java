package com.example.andreas.iridiumflares;

import android.util.Log;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
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

    //Iterates over the flares and checks SMHI for the cloudcover at the time.
    public void cloudCheck(ArrayList<Flares> flareList) throws IOException, JSONException, ParseException {
        String URL = "https://opendata-download-metfcst.smhi.se/api";
        //Get the JSON-file from smhi at the location
        JSONObject parameterObject = readJsonFromUrl(URL + "/category/pmp3g/version/2/geotype/point/lon/"+ longitude+"/lat/"+latitude+"/data.json");
        JSONArray wheterArray = parameterObject.getJSONArray("timeSeries");
        JSONObject firstTime;
        JSONObject endTime;
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
        for (Flares f: flareList) {
            Date flareDate =f.getDate();
            //find the forcast at the time that is closest to the time when the flare appeares.
            for (int i = 0;wheterArray.length()-1>i;i++){
                firstTime = wheterArray.getJSONObject(i);
                endTime = wheterArray.getJSONObject(i+1);

                LocalDateTime l1 = new LocalDateTime(firstTime.getString("validTime").split("Z")[0]);
                LocalDateTime l2 = new LocalDateTime(endTime.getString("validTime").split("Z")[0]);
                LocalDateTime l3 = new LocalDateTime(flareDate);

                //when the flares time is between two timestamps from SMHI check which is closest and use that forecast
                if(l1.isBefore(l3) && l2.isAfter(l3)){

                    Log.i("First",l1.toString());

                    Log.i("CurrentDate",l3.toString());

                    Log.i("End",l2.toString());


                    Period p1 = new Period(l1, l3, PeriodType.hours());
                    Period p2 = new Period(l3, l2,PeriodType.hours());

                    int hours1 = p1.getHours();
                    int hours2 = p2.getHours();

                    int cloudcover;

                    //select the time with the least amount of hours difference
                    if(hours1<hours2){

                        cloudcover = firstTime.getJSONArray("parameters").getJSONObject(7).getJSONArray("values").getInt(0);

                    }
                    else {
                        cloudcover = endTime.getJSONArray("parameters").getJSONObject(7).getJSONArray("values").getInt(0);
                    }

                    //if cloudcover is more than 4 ocatns remove it from the list
                    Log.i("Cover","amount:" +cloudcover);
                    if(cloudcover>4){
                        flareList.remove(f);
                    }
                    break;
                }
            }
        }
    }

    //Return the Json from the URl
    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        String jSonString = getJSon(url);
        return new JSONObject(jSonString);
    }

    //read the Json object from SMHI
    private String getJSon(String url) throws IOException {

        InputStream inputStream = new URL(url).openStream();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = bufferedReader.read()) != -1) {
                sb.append((char) i);
            }
            return sb.toString();
        } finally {
            inputStream.close();
        }
    }

}
