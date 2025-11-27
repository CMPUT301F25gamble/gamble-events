package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Controller.EventAdapter;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.databinding.FragmentUserEventHistoryBinding;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the user's past events and information relating to their acceptance or rejection
 * Fetches events from the database and displays them in a list view
 */

public class UserEventHistoryFragment extends Fragment {

    private FragmentUserEventHistoryBinding binding;
    private Database database;
    private User currentUser;
    private EventAdapter eventAdapter;
    // Admin flow
    private String userId;
    private boolean isAdminMode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserEventHistoryBinding.inflate(inflater, container, false);

        // Fetch the global user ID and admin mode from the AdminSession class
        userId = AdminSession.getSelectedUserId();
        isAdminMode = AdminSession.getAdminMode();
        Log.d("ProfileUIFragment",
                "userId arg = " + userId + "; isAdminMode = " + isAdminMode);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show loading and hide content until it is fetched
        binding.loadingUserEventHistory.setVisibility(View.VISIBLE);
        binding.contentGroupEventsUi.setVisibility(View.GONE);

        // Back button navigates to user profile view
        binding.userEventHistoryBackButton.setOnClickListener(v -> {
            int hostId = isAdminMode
                    ? R.id.admin_nav_host_fragment
                    : R.id.content_nav_host_fragment;

            NavHostFragment navHostFragment =
                    (NavHostFragment) requireActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(hostId);

            if (navHostFragment == null) {
                Log.e("NAV", "NavHostFragment is NULL for hostId=" + hostId);
                return; // prevents crash
            }

            NavController navController = navHostFragment.getNavController();

            if (isAdminMode) {
                navController.navigate(R.id.action_userEventHistoryFragment_to_profileUIFragment);
            } else {
                navController.navigate(R.id.action_user_event_history_fragment_to_profile_ui_fragment);
            }
        });

        // Create new instance of DB
        database = Database.getDatabase();

        // Fetch user using Device ID
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {
            database.getUserFromDeviceID(deviceId, task0 -> {
                if (task0.isSuccessful()) {
                    currentUser = task0.getResult();

                    // String resource for [USERNAME]’s Profile
                    String profileTitle = currentUser.getName() + "'s Profile";
                    // Get user ID
                    String userID = currentUser.getUserID();
                    Log.d("UserEventHistoryFragment", "User ID: " + userID);

                    // Set the title
                    binding.userEventHistoryTitle.setText(profileTitle);

                    // Populate userEventHistoryTitle
                    binding.userEventHistoryTitle.setText(profileTitle);

                    // Fetch the user's event history
                    database.getUserEventsHistory(userID, task1 -> {
                        if (task1.isSuccessful()) {

                            // Create new Array list
                            List<Event> userHistory = new ArrayList<>(task1.getResult());
                            Log.d("UserEventHistoryFragment", "User has " +
                                    userHistory.size() + " events");

                            // Create + set new adapter
                            eventAdapter = new EventAdapter(requireContext(), userHistory);
                            binding.userEventHistoryListView.setAdapter(eventAdapter);

                            // Hide loading and show content after fetch completes
                            binding.loadingUserEventHistory.setVisibility(View.GONE);
                            binding.contentGroupEventsUi.setVisibility(View.VISIBLE);
                        } else {
                            Log.e("UserEventHistoryFragment",
                                    "Failed to fetch user event history",
                                    task1.getException());
                        }
                    });
                } else {
                    Log.e("UserEventHistoryFragment", "Failed to get user", task0.getException());
                }
            });
        });
    }
}
