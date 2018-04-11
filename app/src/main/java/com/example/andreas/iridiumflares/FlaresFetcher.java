package com.example.andreas.iridiumflares;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class FlaresFetcher {

    double longitude;
    double latitude;
    ArrayList<Flares> flareList;

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
//      BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream(), Charset.forName("UTF-8"))); // Uncomment for internet fetch
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
        }

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

            // Adds flare to flareList
            flareList.add(new Flares(date, azimuth, altitude));
        }
        return flareList;
    }
}
