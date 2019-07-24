package com.hooneys.partyroom.DO;

public class UMarker {
    private double lat;
    private double lon;
    private float marker;

    public UMarker() {
        this.lat = 0.0;
        this.lon = 0.0;
        this.marker = 0.0f;
    }

    public UMarker(double lat, double lon, float marker) {
        this.lat = lat;
        this.lon = lon;
        this.marker = marker;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public float getMarker() {
        return marker;
    }

    public void setMarker(float marker) {
        this.marker = marker;
    }
}
