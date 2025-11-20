package com.example.eventlotterysystemapplication.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.ActivityContentBinding;

/**
 * Activity that displays the main
 */

public class ContentActivity extends AppCompatActivity {

    ActivityContentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentNavHostFragment,
                (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // Get NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.contentNavHostFragment.getId());

        assert navHostFragment != null;

        // Get NavController
        NavController navController = navHostFragment.getNavController();

        // Start navigation on second tab (events page) and display it
        binding.bottomNavMenu.setSelectedItemId(R.id.events_ui_fragment);
        navController.navigate(R.id.events_ui_fragment);

        /*
         * Nav logic for BottomNavigationView
         * -> Fixes the issue withe the back stack on the bottom menu
         */
        binding.bottomNavMenu.setOnItemSelectedListener(item -> {
            int destinationId = item.getItemId();

            NavOptions navOptions = new NavOptions.Builder()
                // Main fix here
                .setPopUpTo(R.id.content_nav_graph, true)
                .build();
        Bundle startArgs = new Bundle();
        if (intent != null && intent.hasExtra("eventId")) {
            startArgs.putString("eventId", intent.getStringExtra("eventId"));
            Log.d("ContentActivity", "eventId: " + intent.getStringExtra("eventId"));
        }

            // Second arg is null because there is no start args
            navController.navigate(destinationId, null, navOptions);
            return true;
        });
        // Test
//        startArgs.putString("eventId", "2jKXO77SjVanAOxAcdBd");

        navController.setGraph(R.navigation.content_nav_graph);

        NavigationUI.setupWithNavController(binding.bottomNavMenu, navController);
    }
}