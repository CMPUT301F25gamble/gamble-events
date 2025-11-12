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

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
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

    private ImageStorage imgStore;
    private final List<String> createdImgs = new ArrayList<>();

    @Before
    public void setup() {
        imgStore = ImageStorage.getInstance();
    }

    @Test
    public void testUploadImg() throws FileNotFoundException, ExecutionException, InterruptedException {
        String eventId = "eventID123wowsooriginal";

        // Create a temp image file from an online url
        File testImage = null;
        try {
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
            Log.e("ImgStoreTests", "Malformed URL");
        } catch (IOException e) {
            Log.e("ImgStoreTests", "IOException opening connection");
        }

        if (testImage == null) {
            throw new FileNotFoundException("Test Image File was not be created");
        }

        Uri imgUri = Tasks.await(imgStore.uploadEventPoster(eventId, testImage, task -> {
            Uri uri = task.getResult();
            Log.d("ImgStoreTestsInner", uri.toString());
            assertTrue(uri.toString().contains(eventId));
        }));

        createdImgs.add(eventId + ".jpg");
        Log.d("ImgStoreTests", imgUri.toString());
        assertTrue(imgUri.toString().contains(eventId));
    }

    @Test
    public void testUploadNonImg() {
        assertThrows(IllegalArgumentException.class, () -> {
            imgStore.uploadEventPoster("123", new File("test.txt"), task -> {});
        });
    }

    @After
    public void teardown() throws ExecutionException, InterruptedException {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("poster_images");
        for (String imgName : createdImgs) {
            Log.d("adasd", imgName);
            Tasks.await(storageReference.child(imgName).delete());
        }
    }
}
