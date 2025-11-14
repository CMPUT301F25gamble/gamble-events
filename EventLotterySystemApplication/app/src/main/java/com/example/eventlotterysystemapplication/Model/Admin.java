package com.example.eventlotterysystemapplication.Model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * An instance of this class represents an admin
 */
public class Admin extends User{
    private Database db = new Database();

    /**
     * A constructor for creating an Admin object, assuming such user does not exist yet
     * @param name The name of the user
     * @param email The email of the user
     * @param phoneNumber The phone number of the user
     * @param deviceID The deviceID of the user
     */
    public Admin(String name, String email, String phoneNumber , String deviceID) {
        super(name, email, phoneNumber, deviceID);
        super.setAdmin(true);
    }

    /**
     * Remove an event
     * @param admin The admin
     * @param event The event to be removed
     */
    public void removeEvent(User admin, Event event) {
        if (!admin.isAdmin()) {
            throw new IllegalStateException("User is not an admin.");
        }
        db.deleteEvent(event, task -> {
            if (!task.isSuccessful()) {
                Log.e("Admin", "Cannot remove event");
            }
        });
    }

    /**
     * Remove a user profile
     * @param admin The admin
     * @param user  The user to be removed
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void removeProfile(User admin, User user) {
        if (!admin.isAdmin()) {
            throw new IllegalStateException("User is not an admin.");
        }
        db.deleteUser(user, task -> {
            if (!task.isSuccessful()) {
                Log.e("Admin", "Cannot remove user profile");
            }
        });
    }

    /**
     * Remove an organizer's profile and all their organized events due to violation of app policy
     * @param admin The admin
     * @param organizer The organizer to be removed
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void removeOrganizer(User admin, User organizer) {
        if (!admin.isAdmin()) {
            throw new IllegalStateException("User is not an admin.");
        }
        db.deleteUser(organizer, task -> {
            if (task.isSuccessful()) {
                Log.d("Admin", "Removed organizer due to violation of app policy");
            } else {
                Log.e("Admin", "Cannot remove organizer");
            }
        });
    }

    public void removeImage() {

    }

    public void browseEvents() {

    }

    public void browseProfiles() {

    }

    public void browseImages() {

    }

    public void reviewNotificationLogs() {

    }

}
