package com.example.eventlotterysystemapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Controller.AdminActivity;
import com.example.eventlotterysystemapplication.Controller.ContentActivity;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentSettingsUiBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * SettingsUIFragment
 * Menu to display buttons to navigate to sub-menus for notification settings and important
 * information
 * Non-admin users will have two buttons labelled as such that navigate to the fragments
 * Admin users will have the previous buttons and an addition button that swaps to admin view
 */

public class SettingsUIFragment extends Fragment {

    private FragmentSettingsUiBinding binding;

    Database database = new Database();

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
        Button adminViewButton = binding.adminViewButton;

        // set the admin view text
        if (getActivity() instanceof ContentActivity) {
            adminViewButton.setText("Switch to Admin View");
        } else {
            adminViewButton.setText("Switch to User View");
        }

        adminViewButton.setVisibility(View.GONE); // Hide button by default (check admin later)

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

        // get admin status of user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        database.getUser(uid, task -> {
            if (task.isSuccessful()) {
                User adminUser = task.getResult();
                if (adminUser.isAdmin()) { // if admin show the button
                    adminViewButton.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getContext(), "Error getting user", Toast.LENGTH_SHORT).show();
            }
        });

        adminViewButton.setOnClickListener(v -> {
//            // Small bug fix for bottom nav being buggy
//            BottomNavigationView bottomNav =
//                    requireActivity().findViewById(R.id.bottomNavMenu);
//
//            bottomNav.setSelectedItemId(R.id.events_ui_fragment);
            // Navigate to admin activity

            if (getActivity() instanceof ContentActivity) {
                Intent goToAdminViewIntent = new Intent(requireContext(), AdminActivity.class);
                startActivity(goToAdminViewIntent);
                getActivity().finish();
            } else {
                Intent goToEntrantViewIntent = new Intent(requireContext(), ContentActivity.class);
                startActivity(goToEntrantViewIntent);
                getActivity().finish();
            }
        });
    }
}
