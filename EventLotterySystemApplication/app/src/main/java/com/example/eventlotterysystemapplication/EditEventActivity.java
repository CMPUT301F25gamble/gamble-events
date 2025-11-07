package com.example.eventlotterysystemapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.eventlotterysystemapplication.databinding.ActivityEditEventBinding;

public class EditEventActivity extends AppCompatActivity {

    public String eventId;

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

        // Get NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.editEventNavHostFragment.getId());

        assert navHostFragment != null;

        // Get NavController
        NavController navController = navHostFragment.getNavController();
//        navController.setGraph(R.navigation.edit_event_nav_graph, startArgs);

        NavigationUI.setupWithNavController(binding.editEventBottomNavMenu, navController);
    }
}