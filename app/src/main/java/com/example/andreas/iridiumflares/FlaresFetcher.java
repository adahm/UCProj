package com.example.andreas.iridiumflares;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FlaresFetcher {

    double longitude;
    double latitude;
    ArrayList<Flares> flareList;

    /*
    public static void main(String[] args) {
        double lon = 18.0686;
        double lat = 59.3293;
        FlaresFetcher myFetcher = new FlaresFetcher(lon, lat);

        try {
            myFetcher.fetchData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */

    public FlaresFetcher(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        flareList = new ArrayList<Flares>();


    }

    public ArrayList fetchData(Context context) throws IOException {
        // Generate URL for fetching data:
        //TODO chnage the substrings to be dynamic
        String latitudeString = String.valueOf(latitude);
        latitudeString = latitudeString.substring(0, 4);
        String longitudeString = String.valueOf(longitude);
        longitudeString = longitudeString.substring(0, 4);

        URL link = new URL("http://www.heavens-above.com/localhtml.aspx?lat=" + latitudeString + "&lng=" + longitudeString);
        File file = new File("localhtml.html");
        Log.i("Fetcher", "URL: " + link.toString());


        Log.i("Fetcher", "File trying to read from path: " + file.getAbsolutePath());


        // Use local file to read from rather than fetch data online
//      BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream(), Charset.forName("UTF-8")));
        BufferedReader in = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.localhtml)));
        Log.i("Fetcher", "progress?");
        String inputLine, azimuth, altitude, date, response;
        int dateEnd, azimuthEnd, altitudeEnd;
        response = "Response: \n";
        inputLine = in.readLine();


        // Go fwd to table:
        while (!inputLine.contains("flaredetails.aspx") && in.ready())
        {
            inputLine = in.readLine();
//            Log.i("Scraper: ", inputLine);
        }

       // Flares tempFlare = new Flares(1, 1);
        // Iterate over line until empty
        while (inputLine.length() > 1) {

            // Trim data to date:
            inputLine = inputLine.substring(inputLine.indexOf("<td>")+4, inputLine.length());
            inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.length());

            // Read the date:
            dateEnd = inputLine.indexOf("<");
            date = inputLine.substring(0, dateEnd);

            // Trim to Date end
            inputLine = inputLine.substring(inputLine.indexOf("</td>")+5, inputLine.length());
            // Trim to Brightness End
            inputLine = inputLine.substring(inputLine.indexOf("</td>")+5, inputLine.length());
            // Trim to Altitude
            inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.length());

            // Read altitude
            altitudeEnd = inputLine.indexOf("<")-1;
            altitude = inputLine.substring(0, altitudeEnd);

            // Trim to Altitude end
            inputLine = inputLine.substring(inputLine.indexOf("</td>")+5, inputLine.length());
            // Trim to Azimuth
            inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.length());

            // Read Azimuth
            azimuthEnd = inputLine.indexOf("°");
            azimuth = inputLine.substring(0, azimuthEnd);

            // Trim to end of row
            inputLine = inputLine.substring(inputLine.indexOf("</tr>")+5, inputLine.length());

            response += "Date: " + date + "; Azimuth: " + azimuth + "; Altitude: " + altitude + "\n";

            flareList.add(new Flares(date, azimuth, altitude));
            // CREATE AND STORE FLARE INSTANCE HERE
        }
        return flareList;
    }
}
/*
<tr class="clickableRow" onclick="window.location='flaredetails.aspx?fid=0&lat=59.3293&lng=18.0686&loc=Stockholm&alt=19&tz=CET'">
<td><a href="flaredetails.aspx?fid=0&lat=59.3293&lng=18.0686&loc=Stockholm&alt=19&tz=CET">Apr 5, 23:59:24</a></td> TIME
<td align="center">-0.9</td>BRIGHTNESS
<td align="center">34°</td>ALTITUDE
<td align="center">237° (WSW)</td><td>Iridium 91</td>AZIMUTH
<td align="center">50 km (E)</td><td align="center">-7.6</td>
<td align="right">-24° <img src="images/moon-icon.png" width="16" height="16" />
</td></tr><tr class="clickableRow" onclick="window.location='flaredetails.aspx?fid=1&lat=59.3293&lng=18.0686&loc=Stockholm&alt=19&tz=CET'">
<td><a href="flaredetails.aspx?fid=1&lat=59.3293&lng=18.0686&loc=Stockholm&alt=19&tz=CET">Apr 6, 00:00:34</a></td><td align="center">-7.4</td>
<td align="center">32°</td><td align="center">238° (WSW)</td><td>Iridium 55</td><td align="center">8 km (E)</td><td align="center">-7.6</td>
<td align="right">-24° <img src="images/moon-icon.png" width="16" height="16" /></td></tr> */