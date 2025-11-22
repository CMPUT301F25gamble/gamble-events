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
     * A constructor for creating an Admin object
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
     * Updates isAdmin to true in the database
     * @param listener An OnCompleteListener that will be called when the update operation finishes
     */
    public void updateAdminRecordOnDB(OnCompleteListener<Void> listener) {
        db.modifyUser(this, task -> {
            if (task.isSuccessful()) {
                listener.onComplete(Tasks.forResult(null));
            } else {
                Log.e("Admin", "Cannot update record on database");
                listener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Removes an event
     * @param event The event to be removed
     */
    public void removeEvent(Event event) {
        db.deleteEvent(event, task -> {
            if (!task.isSuccessful()) {
                Log.e("Admin", "Cannot remove event");
            }
        });
    }

    /**
     * Removes a user profile
     * @param user  The user to be removed
     */
    public void removeProfile(User user) {
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
    public void removeOrganizer(User organizer) {
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
    public void removeImage(String imageUrl) {
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
    public void removeAdmin(Admin admin, OnCompleteListener<Void> listener) {
        User user = new User(admin.getName(), admin.getEmail(), admin.getPhoneNumber(), admin.getDeviceID(), admin.getDeviceToken());
        db.modifyUserById(admin.getUserID(), user, task -> {
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
    public void grantAdmin(User user, OnCompleteListener<Void> listener) {
        Admin admin = new Admin(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getDeviceID(), user.getDeviceToken());
        db.modifyUserById(user.getUserID(), admin, task -> {
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
    public void browseEvents(OnCompleteListener<List<Event>> listener) {
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
    public void browseProfiles(OnCompleteListener<List<User>> listener) {
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
    public void browseImages(OnCompleteListener<List<String>> listener) {
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

    public void reviewNotificationLogs() {
        // TODO
    }

}