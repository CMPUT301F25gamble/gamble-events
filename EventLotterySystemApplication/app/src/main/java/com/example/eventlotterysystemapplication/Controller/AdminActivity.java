package com.example.eventlotterysystemapplication.Controller;

import android.os.Bundle;

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
import com.example.eventlotterysystemapplication.databinding.ActivityAdminBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 *  AdminActivity hosts the admin section
 *  Sets up the admin navigation bar
 * */
public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.adminNavHostFragment, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.adminNavHostFragment.getId());

        assert navHostFragment != null;

        // Get NavController
        NavController navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.admin_nav_graph);

        // start navigation on the notifications tab
        binding.adminBottomNavMenu.setSelectedItemId(R.id.eventsUIFragment);
        navController.navigate(R.id.eventsUIFragment);

        /*
         * Nav logic for AdminBottomNavigationView
         * -> Fixes the issue withe the back stack on the bottom menu
         */
        binding.adminBottomNavMenu.setOnItemSelectedListener(item -> {
            int destinationId = item.getItemId();

            NavOptions navOptions = new NavOptions.Builder()
                    // Main fix here
                    .setPopUpTo(destinationId, true)
                    .build();

            // Second arg is null because there is no start args
            navController.navigate(destinationId, null, navOptions);
            return true;
        });
    }

}