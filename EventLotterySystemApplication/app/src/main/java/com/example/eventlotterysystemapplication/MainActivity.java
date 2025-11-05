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

public class MainActivity extends AppCompatActivity {

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

        // Todo: add code to determine if device has already registered
        // Temporary variable to determine if device has already registered
        boolean hasRegistered = false;

        if (hasRegistered)  {
            // Create intent
            Intent goToContentIntent = new Intent(this, ContentActivity.class);
            startActivity(goToContentIntent);
        }
        else {
            // Create Intent
            Intent goToRegisterIntent = new Intent(this, RegisterActivity.class);
            startActivity(goToRegisterIntent);
            finish();
        }

        /*
        // From Gaurang Branch
        Intent intent = getIntent();
        Uri data = intent.getData();
        String action = intent.getAction();
        assert action != null;
        if (data != null){
            String eventID = data.getLastPathSegment();


            // TODO First check that the deviceID and user are registered in the database, and only
            //  then do we open up events page with eventID
        } else {
            setContentView(R.layout.activity_main);
        }
        */
    }
}