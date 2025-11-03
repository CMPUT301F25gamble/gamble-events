package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.os.Bundle;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.main.getId()), (v, insets) -> {
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
        else if (!hasRegistered) {
            // Create Intent
            Intent goToRegisterIntent = new Intent(this, RegisterActivity.class);
            startActivity(goToRegisterIntent);
            finish();
        }
    }
}