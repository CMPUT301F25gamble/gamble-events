package com.example.eventlotterysystemapplication.Controller;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.NotificationChannelFactory;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * First activity upon app launch
 * Retrieves device Id and determines if user should go to {@link RegisterActivity} or
 * {@link ContentActivity} depending on existence of deviceId in the database
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity"; // For debugging
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    Database database = Database.getDatabase();

    /**
     * Checks if user is registered via device
     * Takes user to event screen if registered, otherwise, to registration screen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e("MyApp", "Uncaught exception", throwable);
        });

        super.onCreate(savedInstanceState);

        NotificationChannelFactory.createNotificationChannels(this);

        // Check if app was opened via QR code / deep link
        String eventID;

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        assert action != null;
        Log.d("Action", action);
        if (data != null) {
            eventID = data.getLastPathSegment(); // If this is not null, go to event details
            Log.d("QR", "Scanned QR eventID = " + eventID);
        } else {
            eventID = null;
        }

        NotificationChannelFactory.createNotificationChannels(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Turn off the decor fitting system windows, which allows us to handle insets)
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get device's unique ID
        FirebaseInstallations.getInstance().getId()
                .addOnSuccessListener(deviceId -> {
                    Log.d(TAG, "Firebase Device ID: " + deviceId);
                    // String test = "deviceID67"; // replace deviceId with test to test going to content activity

                    // Check to see if device ID is in database
                    database.getUserFromDeviceID(deviceId, task -> {
                        if (task.isSuccessful()) {
                            User currentUser = task.getResult();
                            if (currentUser != null) {
                                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        String token = task1.getResult();
                                        String storedToken = currentUser.getDeviceToken();
                                        if (storedToken == null || !storedToken.equals(token)) {
                                            currentUser.setDeviceToken(token);
                                            database.modifyUser(currentUser, task2 -> {
                                                if (!task2.isSuccessful()) {
                                                    Log.e("Database", "Cannot modify user while updating token");
                                                }
                                            });
                                        }
                                    }
                                });
                                if (eventID != null) {
                                    Log.d(TAG, "Device registered. Going to event detail fragment.");
                                    goToContentActivityWithEvent(eventID);
                                } else {
                                    Log.d(TAG, "Device registered. Going to content activity.");
                                    goToContentActivity();
                                }
                            } else {
                                Log.d(TAG, "Device not registered. Going to registration activity.");
                                goToRegisterActivity();
                            }

                        } else {
                            Log.e(TAG, "Error querying deviceID", task.getException());
                            goToRegisterActivity();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get device ID", e);
                    goToRegisterActivity();
                });
    }

    /**
     * Check Notification permission is allowed. If not, request to allow.
     */
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted — request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            } else {
                // Permission already granted
                // You can proceed with showing notifications
            }
        } else {
            // No need to check permission for Android < 13
        }
    }

    /**
     * On Request permission result - after allowing or rejecting the notification permission request.
     * @param requestCode The request code passed in {@link #requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }


    /**
     * DeviceID found, send user to content activity
     */
    private void goToContentActivity() {
        // Create intent
        Intent goToContentIntent = new Intent(this, ContentActivity.class);
        startActivity(goToContentIntent);
    }

    /**
     * If deviceID not found, send user to register activity
     * Used as fallback
     */
    private void goToRegisterActivity() {
        // Create Intent
        Intent goToRegisterIntent = new Intent(this, RegisterActivity.class);
        startActivity(goToRegisterIntent);
        finish();
    }

    private void goToContentActivityWithEvent(String eventID) {
        Intent goToContentIntentWithEvent = new Intent(this, ContentActivity.class);
        goToContentIntentWithEvent.putExtra("eventID", eventID); // pass the QR code event ID
        startActivity(goToContentIntentWithEvent);
        finish();
    }
}