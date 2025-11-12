package com.example.eventlotterysystemapplication.Model;


import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
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
}
