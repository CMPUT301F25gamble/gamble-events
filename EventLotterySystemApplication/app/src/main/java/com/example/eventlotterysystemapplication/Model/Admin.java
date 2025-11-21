package com.example.eventlotterysystemapplication.Model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;

import java.util.List;

/**
 * An instance of this class represents an admin
 */
public class Admin extends User{
    private Database db = Database.getDatabase();

    /**
     * A constructor for creating an Admin object, assuming such user does not exist yet
     * @param name The name of the user
     * @param email The email of the user
     * @param phoneNumber The phone number of the user
     * @param deviceID The deviceID of the user
     */
    public Admin(String name, String email, String phoneNumber , String deviceID, String deviceToken) {
        super(name, email, phoneNumber, deviceID, deviceToken);
        super.setAdmin(true);
    }

    /**
     * Removes an event
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
     * Removes a user profile
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
     * Removes an organizer's profile and all their organized events due to violation of app policy
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
        // TODO: wait for implentation for removeImage
    }

    /**
     * Browses a list of events
     * @param listener An OnCompleteListener used to retrieve a list of events
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void browseEvents(OnCompleteListener<List<Event>> listener) {
        db.getAllEvents(task -> {
            if (task.isSuccessful()) {
                List<Event> events = task.getResult();
                listener.onComplete(Tasks.forResult(events));
            } else {
                Log.e("Admin", "Cannot browse events");
            }
        });
    }

    /**
     * Browses a list of user profiles
     * @param listener An OnCompleteListener used to retrieve a list of users
     */
    public void browseProfiles(OnCompleteListener<List<User>> listener) {
        db.getAllUsers(task -> {
            if (task.isSuccessful()) {
                List<User> users = task.getResult();
                listener.onComplete(Tasks.forResult(users));
            } else {
                Log.e("Admin", "Cannot browse profiles");
            }
        });
    }

    public void browseImages() {

    }

    public void reviewNotificationLogs() {

    }

}