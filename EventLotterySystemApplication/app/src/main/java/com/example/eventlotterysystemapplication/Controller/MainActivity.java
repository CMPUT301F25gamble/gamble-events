package com.example.eventlotterysystemapplication.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.databinding.ActivityMainBinding;
import com.google.firebase.installations.FirebaseInstallations;

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
                                Log.d(TAG, "Device registered. Going to content activity.");
                                goToContentActivity();
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

}