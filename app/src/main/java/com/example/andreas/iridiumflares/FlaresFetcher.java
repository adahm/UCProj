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

        /* COMMENTED FOR DEMO
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
            azimuthEnd = inputLine.indexOf("");
            azimuth = inputLine.substring(0, azimuthEnd);

            // Trim to end of row
            inputLine = inputLine.substring(inputLine.indexOf("</tr>")+5, inputLine.length());

            // Adds flare to flareList, data entered as Strings, parsing done in Flares class.
            flareList.add(new Flares(date, azimuth, altitude));
        }
        return flareList;
        */

        String[] dateArray = {
                "May 08, 08:10:00",
                "May 08, 08:20:00",
                "May 08, 08:30:00",
                "May 08, 08:40:00",
                "May 08, 08:50:00",
        };

        String[] azArray = {
                "100",
                "102",
                "104",
                "106",
                "109",
        };
        String[] altArray = {
                "25",
                "27",
                "27",
                "29",
                "230",
        };
        for(int i = 0; i<5; i++){
            flareList.add(new Flares(dateArray[i], azArray[i], altArray[i]));
        }
        return flareList;
    }
}


/* 

Time:       Alt.:   Azimuth:
8:10:00	    25.45	99.53
8:20:00	    26.71	101.8
8:30:00	    27.95	104.09
8:40:00	    29.18	106.43
8:50:00	    30.39	108.8
9:00:00	    31.59	111.21
9:10:00	    32.77	113.67
9:20:00	    33.92	116.17
9:30:00	    35.05	118.73
9:40:00	    36.16	121.33
9:50:00	    37.23	124
10:00:00	38.27	126.72
10:10:00	39.27	129.5
10:20:00	40.24	132.34
10:30:00	41.16	135.25
10:40:00	42.03	138.23
10:50:00	42.85	141.27
11:00:00	43.63	144.37
11:10:00	44.34	147.54
11:20:00	44.99	150.77
11:30:00	45.59	154.06
11:40:00	46.11	157.41
11:50:00	46.57	160.8
*/