package com.example.andreas.iridiumflares;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

public class CloudFetcher {
    double longitude;
    double latitude;

    public CloudFetcher(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void cloudCheck(ArrayList<Flares> flareList) throws IOException, JSONException {
        String metObsAPI = "https://opendata-download-metobs.smhi.se/api";
        JSONObject parameterObject = readJsonFromUrl(metObsAPI + "/version/latest/geotype/point/lon/{"+ longitude+"/lat/"+latitude+"/data.json");

        for (Flares f: flareList) {

        }
    }

    public Boolean checkFlare(Flares f){
        //TODO gör en jsonarry som går över timeseries
        //
        return true;

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
