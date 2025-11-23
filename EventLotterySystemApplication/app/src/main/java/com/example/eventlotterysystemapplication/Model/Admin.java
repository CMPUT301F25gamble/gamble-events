package com.example.eventlotterysystemapplication.Model;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;

import java.util.List;

/**
 * This class represents all the methods an admin can execute
 */
public class Admin extends User{
    private static Database db = Database.getDatabase();
    private static ImageStorage imageStorage = ImageStorage.getInstance();

    /**
     * Removes an event
     * @param event The event to be removed
     */
    public static void removeEvent(Event event) {
        db.deleteEvent(event, task -> {
            if (!task.isSuccessful()) {
                Log.e("Admin", "Cannot remove event");
            }
        });
    }

    /**
     * Removes a user profile
     * @param user The user to be removed
     */
    public static void removeProfile(User user) {
        db.deleteUser(user, task -> {
            if (!task.isSuccessful()) {
                Log.e("Admin", "Cannot remove user profile");
            }
        });
    }

    /**
     * Removes an organizer's profile and all their organized events due to violation of app policy
     * @param organizer The organizer to be removed
     */
    public static void removeOrganizer(User organizer) {
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
     * @param imageUrl the URL of the image
     */
    public static void removeImage(String imageUrl) {
        imageStorage.deleteEventPoster(imageUrl, task -> {
            if (task.isSuccessful()) {
                Log.d("Admin", "Removed image");
            } else {
                Log.e("Admin", "Cannot remove image");
            }
        });
    }

    /**
     * Removes a user's admin privileges
     * @param admin The admin to have their privileges revoked
     * @param listener An OnCompleteListener that will be called when the update operation finishes
     */
    public static void removeAdmin(User admin, OnCompleteListener<Void> listener) {
        admin.setAdmin(false);
        db.modifyUserById(admin.getUserID(), admin, task -> {
            if (task.isSuccessful()) {
                listener.onComplete(Tasks.forResult(null));
                Log.d("Admin", "Successfully revoked user's admin privilege");
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
                Log.e("Admin", "Cannot revoke user's admin privilege");
            }
        });

    }

    /**
     * Grants admin privileges to a user
     * @param user The user to have admin privileges granted to
     * @param listener An OnCompleteListener that will be called when the update operation finishes
     */
    public static void grantAdmin(User user, OnCompleteListener<Void> listener) {
        user.setAdmin(true);
        db.modifyUserById(user.getUserID(), user, task -> {
            if (task.isSuccessful()) {
                listener.onComplete(Tasks.forResult(null));
                Log.d("Admin", "Successfully granted admin privilege to the user");
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
                Log.e("Admin", "Cannot grant admin privilege");
            }
        });

    }

    /**
     * Browses a list of events
     * @param listener An OnCompleteListener used to retrieve a list of events
     */
    public static void browseEvents(OnCompleteListener<List<Event>> listener) {
        db.getAllEvents(task -> {
            if (task.isSuccessful()) {
                List<Event> events = task.getResult();
                listener.onComplete(Tasks.forResult(events));
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
                Log.e("Admin", "Cannot browse events");
            }
        });
    }

    /**
     * Browses a list of user profiles
     * @param listener An OnCompleteListener used to retrieve a list of users
     */
    public static void browseProfiles(OnCompleteListener<List<User>> listener) {
        db.getAllUsers(task -> {
            if (task.isSuccessful()) {
                List<User> users = task.getResult();
                listener.onComplete(Tasks.forResult(users));
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
                Log.e("Admin", "Cannot browse profiles");
            }
        });
    }

    /**
     * Browses a list of images
     * @param listener An OnCompleteListener used to retrieve a list of image URLs
     */
    public static void browseImages(OnCompleteListener<List<String>> listener) {
        imageStorage.fetchAllPosterImageUrls(task -> {
            if (task.isSuccessful()) {
                List<String> imageUrls = task.getResult();
                listener.onComplete(Tasks.forResult(imageUrls));
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
                Log.e("Admin", "Cannot browse images");
            }
        });
    }

    /**
     * Browses logs of all notifications sent to entrants by organizers
     * @param listener An OnCompleteListener used to retrieve a list of notifications
     */
    public static void reviewNotificationLogs(OnCompleteListener<List<Notification>> listener) {
        db.getAllNotifications(task -> {
            if (task.isSuccessful()) {
                List<Notification> notifications = task.getResult();
                listener.onComplete(Tasks.forResult(notifications));
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
                Log.e("Admin", "Cannot browse notification logs");
            }
        });
    }

}