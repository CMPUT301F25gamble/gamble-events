package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventlotterysystemapplication.databinding.ActivityMainBinding;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity"; // For debugging
    // Get an instance of the Firestore Database
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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

                    // FOR TESTING - skip registration if it matches
                    String testDeviceId = "fNnBwGwhaYStDGG6S3vs8sB52PU2";

                    // Force test case uncomment:
                    // deviceId = testDeviceId;

                    // Reference the collection
                    CollectionReference usersRef = db.collection("User");

                    // Check Firestore for device ID
                    usersRef.document(deviceId).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "Device registered in Firestore. Going to content activity.");
                            goToContentActivity();
                        } else {
                            Log.d(TAG, "Device not registered. Going to registration activity");
                            goToRegisterActivity();
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error checking registration", e);
                        goToRegisterActivity();
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