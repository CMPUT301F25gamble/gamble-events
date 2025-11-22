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
    private ImageStorage imageStorage = ImageStorage.getInstance();

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

    /**
     * Removes an image based on its URL
     * @param admin The admin
     * @param imageUrl the URL of the image
     */
    public void removeImage(User admin, String imageUrl) {
        if (!admin.isAdmin()) {
            throw new IllegalStateException("User is not an admin.");
        }
        imageStorage.deleteEventPoster(imageUrl, task -> {
            if (task.isSuccessful()) {
                Log.d("Admin", "Removed image");
            } else {
                Log.e("Admin", "Cannot remove image");
            }
        });
    }

    /**
     * Browses a list of events
     * @param admin The admin
     * @param listener An OnCompleteListener used to retrieve a list of events
     */
    public void browseEvents(User admin, OnCompleteListener<List<Event>> listener) {
        if (!admin.isAdmin()) {
            throw new IllegalStateException("User is not an admin.");
        }
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
     * @param admin The admin
     * @param listener An OnCompleteListener used to retrieve a list of users
     */
    public void browseProfiles(User admin, OnCompleteListener<List<User>> listener) {
        if (!admin.isAdmin()) {
            throw new IllegalStateException("User is not an admin.");
        }
        db.getAllUsers(task -> {
            if (task.isSuccessful()) {
                List<User> users = task.getResult();
                listener.onComplete(Tasks.forResult(users));
            } else {
                Log.e("Admin", "Cannot browse profiles");
            }
        });
    }

    /**
     * Browses a list of images
     * @param admin The admin
     * @param listener An OnCompleteListener used to retrieve a list of image URLs
     */
    public void browseImages(User admin, OnCompleteListener<List<String>> listener) {
        if (!admin.isAdmin()) {
            throw new IllegalStateException("User is not an admin.");
        }
        imageStorage.fetchAllPosterImageUrls(task -> {
            if (task.isSuccessful()) {
                List<String> imageUrls = task.getResult();
                listener.onComplete(Tasks.forResult(imageUrls));
            } else {
                Log.e("Admin", "Cannot browse images");
            }
        });
    }

    public void reviewNotificationLogs(User admin) {
        // TODO
    }

}