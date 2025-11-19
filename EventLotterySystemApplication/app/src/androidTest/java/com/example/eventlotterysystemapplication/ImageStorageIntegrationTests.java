package com.example.eventlotterysystemapplication;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.net.Uri;
import android.util.Log;

import com.example.eventlotterysystemapplication.Model.ImageStorage;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ImageStorageIntegrationTests {
    // Note: a lot of these tests are bad practices because they run additional functions from ImageStorage
    // that are not tested inside that test (for example testDeleteImage calls uploadEventImage when it should only test deleteEventPoster)
    // you would want to directly do upload image and fetch all images using FirebaseStorage methods
    // instead of calling potentially faulty functions but I'm too lazy so I just did it this way

    // Also note that we're running these operations on the actual poster_images folder, in the future
    // we could refactor the ImageStorage class to take in a directory path for the folder we want to get/upload/delete from
    // (e.g. passing in "test_poster_images/" so that its working directory is a testing directory)

    private ImageStorage imgStore;
    private final List<String> createdImgs = new ArrayList<>();
    private List<String> downloadUrls = new ArrayList<>();

    @Before
    public void setup() {
        imgStore = ImageStorage.getInstance();
    }

    public File getTestImage() {
        File testImage = null;
        try {
            // Obtain a temporary image from somewhere online
            URL url = new URL("https://i.imgur.com/b6pVsrn.jpeg");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            InputStream input = conn.getInputStream();
            testImage = File.createTempFile("test_image", ".jpg");
            OutputStream output = new FileOutputStream(testImage);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            output.close();
            input.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            Log.e("ImgStoreGetTestImage", "Malformed URL: " + e);
        } catch (IOException e) {
            Log.e("ImgStoreGetTestImage", "IOException opening connection: " + e);
        }
        return testImage;
    }

    @Test
    public void testUploadImg() throws FileNotFoundException, ExecutionException, InterruptedException {
        String eventId = "eventID123wowsooriginal";

        // Create a temp image file from an online url
        File testImage = getTestImage();

        if (testImage == null) {
            throw new FileNotFoundException("Test Image File was not created");
        }

        Tasks.await(imgStore.uploadEventPoster(eventId, testImage, task -> {
            Uri uri = task.getResult();
            assertTrue(uri.toString().contains(eventId));
            createdImgs.add(eventId + ".jpg");
            downloadUrls.add(uri.toString());
        }));
    }

    @Test
    public void testUploadNonImg() {
        assertThrows(IllegalArgumentException.class, () -> {
            imgStore.uploadEventPoster("123", new File("test.txt"), task -> {});
        });
    }

    @Test
    public void testFetchAllPosters() throws ExecutionException, InterruptedException, FileNotFoundException {
        // Upload an image to Firebase first
        String eventId = "coolEventId";
        File testImage = getTestImage();

        if (testImage == null) {
            throw new FileNotFoundException("Test Image File was not created");
        }

        Tasks.await(imgStore.uploadEventPoster(eventId, testImage, task -> {
            Uri uri = task.getResult();
            downloadUrls.add(uri.toString());
            Log.d("ImgStorageTestFetchAllPosters", "download uri is: " + uri);
            createdImgs.add(eventId + ".jpg");
        }));

        // Empty listener because AWAIT doesn't want to wait for the listener to finish so we gotta
        // wait for the return value instead of using the listener :(
        ListResult listResult = Tasks.await(imgStore.fetchAllPosterImageUrls(task -> {}));

        List<String> fetchedDownloadUrls = new ArrayList<>();
        listResult.getItems().forEach(storageReference -> {
            try {
                fetchedDownloadUrls.add(Tasks.await(storageReference.getDownloadUrl()).toString());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e); // top ten bruh moments LOL
            }
        });

        // Logging to verify no false positives
        Log.d("ImgStorageTestFetchAllPosters", fetchedDownloadUrls.toString());
        Log.d("ImgStorageTestFetchAllPosters", "fetched download url list size: " + fetchedDownloadUrls.size());
        downloadUrls.forEach(downloadUrl -> assertTrue(fetchedDownloadUrls.contains(downloadUrl)));
    }

    @Test
    public void testDeleteEventPoster() throws ExecutionException, InterruptedException, FileNotFoundException {
        // Upload an image to Firebase first
        String eventId = "interestingEventId";
        String imgName = eventId + ".jpg";
        File testImage = getTestImage();

        if (testImage == null) {
            throw new FileNotFoundException("Test Image File was not created");
        }

        // Obtain the download uri so that we can delete the image pointed to by the download uri
        Uri imgUri = Tasks.await(imgStore.uploadEventPoster(eventId, testImage, task -> {
            Uri uri = task.getResult();
            downloadUrls.add(uri.toString());
            Log.d("ImgStorageTestDeleteEventPoster", "download uri is: " + uri);
            createdImgs.add(imgName);
        }));

        // Deleting event poster by download uri (recommended way)
        Tasks.await(imgStore.deleteEventPoster(imgUri.toString(), task -> {}));

        // Remove to make sure that we don't double delete during the takedown
        createdImgs.remove(imgName);
        // Check that the image was deleted (the actual exception is a StorageException, but since
        // we are using Tasks.await it will throw an ExecutionException instead)
        assertThrows("Object does not exist at location.", ExecutionException.class, () -> {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("poster_images");
            Tasks.await(storageReference.child(imgName).delete());
        });
    }

    @Test
    public void testDeleteEventPosterByEventId() throws ExecutionException, InterruptedException, FileNotFoundException {
        // Upload an image to Firebase first
        String eventId = "intriguingEventId";
        String imgName = eventId + ".jpg";
        File testImage = getTestImage();

        if (testImage == null) {
            throw new FileNotFoundException("Test Image File was not created");
        }

        Tasks.await(imgStore.uploadEventPoster(eventId, testImage, task -> {
            createdImgs.add(imgName);
        }));

        // Deleting poster by event id version
        Tasks.await(imgStore.deleteEventPoster(task -> {}, eventId));

        // Remove to make sure that we don't double delete during the takedown
        createdImgs.remove(imgName);
        // Check that the image was deleted (the actual exception is a StorageException, but since
        // we are using Tasks.await it will throw an ExecutionException instead)
        assertThrows("Object does not exist at location.", ExecutionException.class, () -> {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("poster_images");
            Tasks.await(storageReference.child(imgName).delete());
        });
    }

    @After
    public void teardown() throws ExecutionException, InterruptedException {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("poster_images");
        for (String imgName : createdImgs) {
            Log.d("ImgStoreTestsTeardown", "deleting: " + imgName);
            Tasks.await(storageReference.child(imgName).delete());
        }
    }
}
