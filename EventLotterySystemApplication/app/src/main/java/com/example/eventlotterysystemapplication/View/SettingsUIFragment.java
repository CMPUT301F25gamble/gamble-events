package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentSettingsUiBinding;

/**
 * SettingsUIFragment
 * Menu to display buttons to navigate to sub-menus for notification settings and important
 * information
 * Non-admin users will have two buttons labelled as such that navigate to the fragments
 * Admin users will have the previous buttons and an addition button that swaps to admin view
 */

public class SettingsUIFragment extends Fragment {

    private FragmentSettingsUiBinding binding;

    //Database database = new Database();

    public SettingsUIFragment () {
        // Required empty public constructor
    }

    public static SettingsUIFragment newInstance() {
        SettingsUIFragment fragment = new SettingsUIFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsUiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get buttons
        Button notificationSettingsButton = binding.notificationSettingsButton;
        Button tosButton = binding.tosButton;
//        Button adminViewButton = binding.adminViewButton;

//        // Set default admin button visibility to gone
//        adminViewButton.setVisibility(View.GONE);
//
//        // Show loading and hide content until it is fetched
//        binding.loadingSettingsScreen.setVisibility(View.VISIBLE);
//        binding.contentGroupSettingsScreen.setVisibility(View.GONE);
//
//        // Fetch the User ID
//        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
//                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
//                : null;
//
//        // If no user ID, return
//        if (uid == null) {
//            return;
//        }

        // Fetch the user & check if admin
//        database.getUser(uid, task -> {
//            if (task.isSuccessful()) {
//                User adminUser = task.getResult();
//                if (adminUser.isAdmin()) {
//                    adminViewButton.setVisibility(View.VISIBLE);
//                    // Hide loading and show content
//                    binding.loadingSettingsScreen.setVisibility(View.GONE);
//                    binding.contentGroupSettingsScreen.setVisibility(View.VISIBLE);
//                }
//            } else {
//                Toast.makeText(getContext(), "Error getting user", Toast.LENGTH_SHORT).show();
//            }
//        });

        // Set click listeners
        notificationSettingsButton.setOnClickListener(v -> {
            // Navigate to notification settings fragment
            NavHostFragment.findNavController(SettingsUIFragment.this)
                    .navigate(R.id.action_settingsUIFragment_to_settings_notifications_fragment);
        });

        tosButton.setOnClickListener(v -> {
            // Navigate to TOS fragment
            NavHostFragment.findNavController(SettingsUIFragment.this)
                    .navigate(R.id.action_settingsUIFragment_to_settingsTOSFragment);
        });

        // DO NOT INCLUDE
//        adminViewButton.setOnClickListener(v -> {
//            // Small bug fix for bottom nav being buggy
//            BottomNavigationView bottomNav =
//                    requireActivity().findViewById(R.id.bottomNavMenu);
//
//            bottomNav.setSelectedItemId(R.id.events_ui_fragment);
//        });
    }
}
