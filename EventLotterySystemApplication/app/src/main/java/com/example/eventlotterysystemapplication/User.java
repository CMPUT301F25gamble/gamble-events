package com.example.eventlotterysystemapplication;

import com.google.firebase.firestore.Exclude;

import java.util.Objects;
import android.provider.Settings;
import android.util.Log;

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

    public User() {
        // Empty constructor used by Firebase to deserialize documents into User object
    }
    public User(String email, String phoneNumber, String name, String deviceID) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.deviceID = deviceID;
        isAdmin = false;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public void joinEventWaitingList(Event event){
        event.joinWaitingList(this);
    }

    public void leaveEventWaitingList(Event event){
        event.leaveWaitingList(this);
    }

    public void joinEventChosenList(Event event) {
        event.joinChosenList(this);
    }

    public void leaveEventChosenList(Event event) {
        event.leaveChosenList(this);
    }

    public void joinEventCancelledList(Event event) {
        event.joinCancelledList(this);
    }

    public void joinEventFinalizedList(Event event) {
        event.joinFinalizedList(this);
    }

    public void leaveEventFinalizedList(Event event) {
        event.leaveFinalizedList(this);
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
        Database db = new Database();
        db.modifyUser(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot modify user");
            }
        });
    }
}
