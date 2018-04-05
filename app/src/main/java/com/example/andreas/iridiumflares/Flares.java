package com.example.andreas.iridiumflares;

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
}
