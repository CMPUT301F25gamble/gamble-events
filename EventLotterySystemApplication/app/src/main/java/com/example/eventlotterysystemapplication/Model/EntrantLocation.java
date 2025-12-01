package com.example.eventlotterysystemapplication.Model;

/**
 * A small dataclass that represents the high cohesion data of the latitude and longitude of the
 * entrant's joining to the event
 */
public class EntrantLocation {

    private Double longitude;
    private Double latitude;

    /**
     * The constructor for this class
     * @param latitude The latitude from which the user joined the event
     * @param longitude The longitude from which the user joined the event
     */
    public EntrantLocation(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Empty constructor for convenience of instantiating object without having to define the
     * parameters first
     */
    public EntrantLocation(){

    }

    /**
     * Gets the entrant's join longitude
     * @return The longitude the entrant joined the event from
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the entrant's join longitude
     * @param longitude The longitude the entrant joined the event from
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the entrant's join latitude
     * @return The latitude the entrant joined the event from
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the entrant's join latitude
     * @param latitude Gets the entrant's join latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
