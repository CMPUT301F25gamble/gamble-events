package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Todo: add code to determine if device has already registered
        // Temporary variable to determine if device has already registered
        boolean hasRegistered = true;

        if (hasRegistered)  {
            // Create intent
            Intent goToContentIntent = new Intent(this, ContentActivity.class);
            startActivity(goToContentIntent);
        }
//        else if (!hasRegistered) {
//            // Create Intent
//            Intent goToRegisterIntent = new Intent(this, RegisterActivity.class);
//            startActivity(goToRegisterIntent);
//        }


//        // Initialise the BottomNavigationView
//        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
//
//        NavHostFragment navHostFragment = (NavHostFragment)
//                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//        assert navHostFragment != null;
//        NavController navController = navHostFragment.getNavController();
//
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }
}