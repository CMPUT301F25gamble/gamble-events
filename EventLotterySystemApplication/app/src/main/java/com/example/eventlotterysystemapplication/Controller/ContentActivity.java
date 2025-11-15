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

        Bundle startArgs = new Bundle();
        if (intent != null && intent.hasExtra("eventId")) {
            startArgs.putString("eventId", intent.getStringExtra("eventId"));
            navController.getGraph().setStartDestination(R.id.event_detail_screen);
        }

        navController.setGraph(R.navigation.content_nav_graph, startArgs);

        NavigationUI.setupWithNavController(binding.bottomNavMenu, navController);
    }
}