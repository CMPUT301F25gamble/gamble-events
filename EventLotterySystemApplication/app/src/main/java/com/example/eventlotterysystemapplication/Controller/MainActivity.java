package com.example.eventlotterysystemapplication.Controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.R;
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
    Database database = new Database();

    /**
     * Checks if user is registered via device
     * Takes user to event screen if registered, otherwise, to registration screen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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
                    database.queryDeviceID(deviceId, task -> {
                        if (task.isSuccessful()) {
                            Boolean exists = task.getResult();

                            if (exists != null && exists) {
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

        createNotificationChannel("lotteryNotification", "This notification channel is used to notify entrants for lottery selection");
        createNotificationChannel("waitingListNotification", "This notification channel is used to notify entrants in the waiting list");
        createNotificationChannel("chosenListNotification", "This notification channel is used to notify entrants in the chosen list");
        createNotificationChannel("cancelledListNotification", "This notification channel is used to notify entrants in the chosen list");

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

    private void createNotificationChannel(String channelName, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    channelName,
                    description,
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.enableVibration(true); // Allow vibration for notifications

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }


    private void goToContentActivityWithEvent(String eventID) {
        Intent goToContentIntentWithEvent = new Intent(this, ContentActivity.class);
        goToContentIntentWithEvent.putExtra("eventID", eventID); // pass the QR code event ID
        startActivity(goToContentIntentWithEvent);
        finish();
    }


}