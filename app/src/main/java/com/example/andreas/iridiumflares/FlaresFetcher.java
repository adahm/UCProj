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
        /* Code for opening connection to website disabled:
        // Generate URL for fetching data:
        String latitudeString = String.valueOf(latitude);
        latitudeString = latitudeString.substring(0, 4);
        String longitudeString = String.valueOf(longitude);
        longitudeString = longitudeString.substring(0, 4);
        URL link = new URL("http://www.heavens-above.com/localhtml.aspx?lat=" + latitudeString + "&lng=" + longitudeString);
        Log.i("Fetcher", "URL: " + link.toString());

        BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream(), Charset.forName("UTF-8"))); // Uncomment for internet fetch
        */

        // Fetch from local localhtml.html file stored in project rather than online:
        File file = new File("localhtml.html");
        Log.i("Fetcher", "File trying to read from path: " + file.getAbsolutePath());

        BufferedReader in = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.localhtml)));

        // Scraping html file:
        String inputLine, azimuth, altitude, date;
        int dateEnd, azimuthEnd, altitudeEnd;
        inputLine = in.readLine();

        // Go forward to line containing the table:
        while (!inputLine.contains("flaredetails.aspx") && in.ready())
        {
            inputLine = in.readLine();
        }

        // Entire table on single line, we iterate over it by extracting data and removing extracted data
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

            // Adds flare to flareList, data entered as Strings, parsing done in Flares class.
            flareList.add(new Flares(date, azimuth, altitude));
        }
        return flareList;
    }
}
