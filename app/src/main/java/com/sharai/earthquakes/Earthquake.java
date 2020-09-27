package com.sharai.earthquakes;

public class Earthquake {

    float magnitude;
    String place, url, id;
    long time;

    public Earthquake(float mag, String place, long time, String url, String id) {
        this.magnitude = mag;
        this.place = place;
        this.time = time;
        this.url = url;
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public String getUrl() {
        return url;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public long getTime() {
        return time;
    }

}
