package com.example.eventlotterysystemapplication;

/**
 * An instance of this class represents a single user
 */
public class User {
    private String name;
    private String email;
    private String phoneNumber;
    private String deviceID;
    private String userID;
    private boolean isAdmin;

    public User(String email, String phoneNumber, String name, String deviceID) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.deviceID = deviceID;
        isAdmin = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
