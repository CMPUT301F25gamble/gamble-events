package com.example.eventlotterysystemapplication;

import com.google.firebase.firestore.Exclude;

import java.util.Objects;
import android.provider.Settings;

/**
 * An instance of this class represents a single user
 */
public class User{
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

        Database db = new Database();
        db.modifyUser(this);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;

        Database db = new Database();
        db.modifyUser(this);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;

        Database db = new Database();
        db.modifyUser(this);
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

        Database db = new Database();
        db.modifyUser(this);
    }

    @Exclude
    public String getUserID() {
        return userID;
    }

    @Exclude
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void joinEventWaitingList(Event event){
        event.joinWaitingList(this);
    }

    public void leaveEventWaitingList(Event event){
        event.leaveWaitingList(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        } else {
            User user2 = (User) o;
            return Objects.equals(this.userID, user2.userID);
        }
    }

    /**
     * Modify one or more user profile info
     * @param user The user profile
     * @param name The user name
     * @param email The user email
     * @param phoneNumber The user phone number
     */
    public void updateUserInfo(User user, String name, String email, String phoneNumber) {
        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }
        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            user.setPhoneNumber(phoneNumber);
        }

        // Will need to comment these out when running UserUnitTest
        Database database = new Database();
        database.modifyUser(user);
    }
}
