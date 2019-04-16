package com.hooneys.partyroom.DO;

public class User {
    private String nickName;
    private float markerColor;
    private float lat;
    private float lon;
    private String msg;

    public User() {
        this.nickName = null;
        this.markerColor = 0.0f;
        this.lat = 0.0f;
        this.lon = 0.0f;
        this.msg = null;
    }

    public User(String nickName, float markerColor, float lat, float lon, String msg) {
        this.nickName = nickName;
        this.markerColor = markerColor;
        this.lat = lat;
        this.lon = lon;
        this.msg = msg;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public float getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(float markerColor) {
        this.markerColor = markerColor;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
