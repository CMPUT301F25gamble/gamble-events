package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
import com.example.eventlotterysystemapplication.Model.EntrantStatus;
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
        Log.d("UserEventHistoryFragment",
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

        if (isAdminMode) {
            database.getUser(userId, task ->{
                if (task.isSuccessful()) {
                    currentUser = task.getResult();
                    if (currentUser == null) {
                        Log.e("UserEventHistoryFragment", "Admin: Couldn't fetch user");
                    }

                    // Get a different user's profile
                    String profileTitle = currentUser.getName() + "'s Profile";
                    binding.userEventHistoryTitle.setText(profileTitle);

                    // Load event history of another user (ADMIN MODE)
                    loadEventHistoryForUser(currentUser.getUserID());
                }
            });
        } else {
            // Fetch user using Device ID
            FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {
                database.getUserFromDeviceID(deviceId, task -> {
                    if (task.isSuccessful()) {
                        currentUser = task.getResult();
                        if (currentUser == null) {
                            Log.e("UserEventHistoryFragment", "Couldn't fetch user");
                            return;
                        }
                        // String resource for [USERNAME]’s Profile
                        String profileTitle = currentUser.getName() + "'s Profile";
                        Log.d("UserEventHistoryFragment", "User ID: "
                                + currentUser.getUserID());

                        // Set the title
                        binding.userEventHistoryTitle.setText(profileTitle);

                        // Populate userEventHistoryTitle
                        binding.userEventHistoryTitle.setText(profileTitle);

                        // Load the event history for the user
                        loadEventHistoryForUser(currentUser.getUserID());
                    } else {
                        Log.e("UserEventHistoryFragment",
                                "Failed to get user", task.getException());
                    }
                });
            });
        }
    }

    /**
     * Loads the event history for a user
     * @param userID
     */
    private void loadEventHistoryForUser(String userID) {
        // Fetch the user's event history
        database.getUserEventsHistory(userID, task -> {
            if (task.isSuccessful()) {
                // Create new Array list
                Pair<List<Event>, List<EntrantStatus>> result = task.getResult();
                List<Event> events = result.first;
                List<EntrantStatus> statuses = result.second;

                Log.d("UserEventHistoryFragment", "User has "
                        + events.size() + " events");

                // Create + set new adapter
                eventAdapter = new EventAdapter(requireContext(), events, statuses);
                binding.userEventHistoryListView.setAdapter(eventAdapter);

                binding.userEventHistoryListView.setOnItemClickListener((parent, view, position, id) -> {
                    Event selectedEvent = events.get(position);

                    int hostId = isAdminMode
                            ? R.id.admin_nav_host_fragment
                            : R.id.content_nav_host_fragment;

                    NavHostFragment navHostFragment =
                            (NavHostFragment) requireActivity()
                                    .getSupportFragmentManager()
                                    .findFragmentById(hostId);

                    if (navHostFragment == null) {
                        Log.e("NAV", "NavHostFragment is NULL for hostId=" + hostId);
                        return;
                    }

                    NavController navController = navHostFragment.getNavController();

                    // ---- NAVIGATION ----
                    // Use the correct action depending on admin mode
                    Bundle args = new Bundle();
                    args.putString("eventId", selectedEvent.getEventID());

                    if (isAdminMode) {
                        navController.navigate(R.id.action_userEventHistoryFragment_to_eventDetailScreenFragment, args);
                    } else {
                        navController.navigate(R.id.action_user_event_history_fragment_to_event_detail_screen, args);
                    }
                });


                // Hide loading and show content after fetch completes
                binding.loadingUserEventHistory.setVisibility(View.GONE);
                binding.contentGroupEventsUi.setVisibility(View.VISIBLE);

            } else {
                Log.e("UserEventHistoryFragment",
                        "Failed to fetch user event history", task.getException());
            }
        });
    }

}
