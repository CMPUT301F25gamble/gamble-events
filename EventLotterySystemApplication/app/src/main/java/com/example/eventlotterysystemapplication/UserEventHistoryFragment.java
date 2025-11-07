package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentUserEventHistoryBinding;
import com.google.firebase.installations.FirebaseInstallations;

/**
 * Displays the user's past events and information relating to their acceptance or rejection
 * Fetches events from the database and displays them in a list view
 */

public class UserEventHistoryFragment extends Fragment {

    private FragmentUserEventHistoryBinding binding;
    private Database database;
    private User currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserEventHistoryBinding.inflate(inflater, container, false);
        // Create new instance of DB
        database = new Database();

        // Fetch user using Device ID
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {
            database.getUserFromDeviceID(deviceId, task -> {
                if (task.isSuccessful()) {
                    currentUser = task.getResult();

                    // String resource for [USERNAME]’s Profile
                    String profileTitle = currentUser.getName() + "'s Profile";
                    binding.userEventHistoryTitle.setText(profileTitle);

                    // Populate userEventHistoryTitle
                    binding.userEventHistoryTitle.setText(profileTitle);

                } else {
                    Log.e("UserEventHistoryFragment", "Failed to get user", task.getException());
                }
            });
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button navigates to user profile view
        binding.userEventHistoryBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(UserEventHistoryFragment.this)
                    .navigate(R.id.action_user_event_history_fragment_to_profile_ui_fragment);
        });
    }


}
