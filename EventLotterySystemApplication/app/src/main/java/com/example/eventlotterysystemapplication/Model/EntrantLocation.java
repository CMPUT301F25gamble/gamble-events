package com.example.eventlotterysystemapplication.Model;

public class EntrantLocation {

    private Double longitude;
    private Double latitude;

    public EntrantLocation(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public EntrantLocation(){

    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
