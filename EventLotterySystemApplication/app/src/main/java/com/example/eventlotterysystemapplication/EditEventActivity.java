package com.example.eventlotterysystemapplication;

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

import com.example.eventlotterysystemapplication.databinding.ActivityEditEventBinding;

/**
 * EditEventActivity
 * This activity provides options related to event editing, sending notifications to entrants, and
 * viewing the list of entrants
 * Contains a NavHostFragment to display fragments related to this activity and a sets up a
 * BottomNavigationView to quickly swap between EventDetailScreenFragment,
 * EntrantListSelectionFragment, and OrganiserNotificationsUIFragment
 * Retrieves the eventId from the intent and puts it in a bundle to be used by related fragments
 */

public class EditEventActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityEditEventBinding binding = ActivityEditEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.editEventNavHostFragment, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the eventId from the intent
        String eventId = getIntent().getStringExtra("eventId");
        boolean isOwnedEvent = getIntent().getBooleanExtra("isOwnedEvent", false);

        Log.d("EditEventActivity", "eventId=" + eventId + ", isOwnedEvent=" + isOwnedEvent);

        // Create a Bundle to pass the eventId to the EditEventFragment
        Bundle startArgs = new Bundle();
        startArgs.putString("eventId", eventId);
        startArgs.putBoolean("isOwnedEvent", isOwnedEvent);

        // Get NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.editEventNavHostFragment.getId());

        assert navHostFragment != null;

        // Get NavController
        NavController navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.edit_event_nav_graph, startArgs);

        NavigationUI.setupWithNavController(binding.editEventBottomNavMenu, navController);
    }
}