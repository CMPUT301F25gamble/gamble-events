package com.example.eventlotterysystemapplication.Model;


import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An singleton instance of this class represents a image storage bucket (currently on Firebase)
 */
public class ImageStorage {
    private static ImageStorage imgStore;
    private StorageReference storageRef; // root reference
    private StorageReference posterImagesRef; // path: poster_images/
    private final String TAG = "ImageStorage";
    private final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    /**
     * Constructor for ImageStorage that initializes the root and poster_images folder
     */
    private ImageStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        posterImagesRef = storageRef.child("poster_images");
    }


    /**
     * Returns the ImageStorage singleton and initializes it if it does not exist
     * @return ImageStorage singleton
     */
    public static ImageStorage getInstance() {
        if (imgStore == null) {
            imgStore = new ImageStorage();
        }

        return imgStore;
    }

    /**
     * Uploads or replaces an event poster image (.png, .jpg, .jpeg) into Firebase Storage
     * @param eventId The event ID (e.g. a png file will be stored as {eventId}.png)
     * @param eventPosterFile The local image file of the file
     * @param imageUrlListener An OnCompleteListener that obtains the download url link of the image (to be stored in Event object)
     * @return An asynchronous task of Uri (this is ONLY used for testing purposes as the listeners did not work);
     * please use the listeners when using this function on the main Android thread
     */
    public Task<Uri> uploadEventPoster(
            String eventId,
            File eventPosterFile,
            OnCompleteListener<Uri> imageUrlListener
    ) throws OutOfMemoryError, IllegalArgumentException {
        if (eventPosterFile.length() > MAX_FILE_SIZE) {
            throw new OutOfMemoryError("File exceeds max file size");
        }

        String eventPosterName = eventPosterFile.getName().toLowerCase();
        if (!eventPosterName.endsWith(".png") &&
            !eventPosterName.endsWith(".jpg") &&
            !eventPosterName.endsWith(".jpeg")
        ) {
            Log.d(TAG, "event poster file name: " + eventPosterName);
            throw new IllegalArgumentException("File is not an accepted image type (.png, .jpg, or .jpeg)");
        }

        Uri eventPosterFileUri = Uri.fromFile(eventPosterFile);
        String fileExtension = Objects.requireNonNull(eventPosterFileUri.getLastPathSegment())
                                .split("\\.")[1];

        StorageReference imageRef = posterImagesRef.child(eventId + "." + fileExtension);
        UploadTask uploadTask = imageRef.putFile(eventPosterFileUri);

        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Upload task was unsuccessful: " + Objects.requireNonNull(task.getException()));
            }
            return imageRef.getDownloadUrl();
        }).addOnSuccessListener(uri -> {
            Log.d(TAG, "Event Poster Image " + imageRef.getName() + " Successfully Added");
            // Passes the download url to imageUrlListener
            imageUrlListener.onComplete(Tasks.forResult(uri));
        });
    }

    /**
     * (PREFERRED WAY) Given a poster image download url that is stored on the event object,
     * delete that poster image on the storage bucket
     * @param posterDownloadUrl The poster download url (formatted like
     *                          https://firebasestorage.googleapis.com/v0/b/cmput301-gamblers.firebasestorage.app/o/...jpg?alt=media&token=...)
     * @param listener A void OnCompleteListener that will be called upon delete task completion
     * @return An asynchronous void task used ONLY for integration testing purposes. Please do not
     * use the return result to access the task, use the OnCompleteListener instead :)
     */
    public Task<Void> deleteEventPoster(String posterDownloadUrl, OnCompleteListener<Void> listener) {
        StorageReference eventPoster = FirebaseStorage.getInstance().getReferenceFromUrl(posterDownloadUrl);

        return eventPoster.delete().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Delete task was unsuccessful: " + Objects.requireNonNull(task.getException()));
                listener.onComplete(Tasks.forException(task.getException()));
                return;
            }
            Log.d(TAG, "Successfully deleted " + posterDownloadUrl);
            listener.onComplete(task);
        }).addOnFailureListener(exception -> {
            // File could not be deleted so it might not have been found
            Log.e(TAG, Objects.requireNonNull(exception.getMessage()));
            listener.onComplete(Tasks.forException(exception));
        });
    }

    /**
     * Given an eventId, delete the associated event poster stored on the storage bucket
     * This function assumes that the eventId poster is stored as an .jpg file which may lead to errors.
     * Therefore, use the posterDownloadUrl deleteEventPoster function if possible.
     * @param listener A void OnCompleteListener that will be called upon delete task completion
     * @param eventId The eventId of the event image poster (may or may not have an associated image so beware)
     * @return An asynchronous void task used ONLY for integration testing purposes. Please do not
     * use the return result to access the task, use the OnCompleteListener instead :)
     */
    public Task<Void> deleteEventPoster(OnCompleteListener<Void> listener, String eventId) {
        StorageReference eventPoster = posterImagesRef.child(eventId + ".jpg");

       return eventPoster.delete().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Delete task was unsuccessful: " + Objects.requireNonNull(task.getException()));
                listener.onComplete(Tasks.forException(task.getException()));
                return;
            }
            Log.d(TAG, "Successfully deleted " + eventId + ".jpg");
            listener.onComplete(task);
        }).addOnFailureListener(exception -> {
            // File could not be deleted so it might not have been found
            Log.e(TAG, Objects.requireNonNull(exception.getMessage()));
            listener.onComplete(Tasks.forException(exception));
        });
    }

    /**
     * One caveat is that all the images will be stored in memory so hopefully we don't have that many posters
     * @param listener A OnCompleteListener that will be called upon fetch task completion, the task
     *                 object will contain the list of download urls for all posters
     * @return An asynchronous ListResult task used ONLY for integration testing purposes. Please do not
     * use the return result to access the task, use the OnCompleteListener instead :)
     */
    public Task<ListResult> fetchAllPosterImageUrls(OnCompleteListener<List<String>> listener) {
        return posterImagesRef.listAll().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Could not fetch all events");
                listener.onComplete(Tasks.forException(Objects.requireNonNull(task.getException())));
                return;
            }
            List<StorageReference> posterRefList = task.getResult().getItems();
            List<String> downloadUrls = new ArrayList<>();
            List<Task<Uri>> downloadTasks = new ArrayList<>();

            // Since download uris are fetched asynchronously... GRRR... we need to store list of tasks to "await for"
            for (StorageReference posterRef : posterRefList) {
                Task<Uri> downloadTask = posterRef.getDownloadUrl().addOnCompleteListener(downloadUriTask -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Unable to get poster image download uri");
                        return;
                    }
                    downloadUrls.add(downloadUriTask.getResult().toString());
                });
                downloadTasks.add(downloadTask);
            }

            Tasks.whenAllComplete(downloadTasks).addOnCompleteListener(urlListTask -> {
                if (!urlListTask.isSuccessful()) {
                    Log.e(TAG, "Fetching poster download URLs unsuccessful");
                    listener.onComplete(Tasks.forException(Objects.requireNonNull(urlListTask.getException())));
                    return;
                }

                Log.d(TAG, "Successfully fetched all event poster download urls");
                listener.onComplete(Tasks.forResult(downloadUrls));
            });
        });
    }
}
