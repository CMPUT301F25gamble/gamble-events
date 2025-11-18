package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.databinding.FragmentProfileUiBinding;
import com.google.firebase.installations.FirebaseInstallations;

/**
 * Displays the user's information and allows edits
 * Fetches the user's name, email, and phone number if it exists and displays it
 * Allows the user to view history by navigating to {@link UserEventHistoryFragment}, to confirm
 * updates to their information with the update profile button, and to delete their profile
 * by navigating to {@link DeleteProfileFragment}
 */
public class ProfileUIFragment extends Fragment {
    private FragmentProfileUiBinding binding;
    private Database database;
    private User currentUser;
    private String userId;
    private boolean isAdminMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            isAdminMode = getArguments().getBoolean("isAdminMode");
        } else {
            userId = null;
            isAdminMode = false;
        }

        Log.d("ProfileUIFragment",
                "userId arg = " + userId + " isAdminMode = " + isAdminMode);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileUiBinding.inflate(inflater, container, false);
        database = new Database();

        // Display the loading screen while the data is being fetched
        binding.loadingProfileUi.setVisibility(View.VISIBLE);
        binding.contentGroupProfileUi.setVisibility(View.GONE);

        if (userId != null && isAdminMode) {
            // Admin flow: load the selected user by ID
            database.getUser(userId, task -> {
                if (task.isSuccessful()) {
                    currentUser = task.getResult();
                    if (currentUser != null) {
                        binding.profileName.setText(currentUser.getName());
                        binding.profileEmail.setText(currentUser.getEmail());
                        binding.profilePhone.setText(currentUser.getPhoneNumber());

                        // Hide loading and show content
                        binding.loadingProfileUi.setVisibility(View.GONE);
                        binding.contentGroupProfileUi.setVisibility(View.VISIBLE);

                        // Admin Back button
                        binding.adminProfileBackButton.setOnClickListener(v -> {
                            NavHostFragment.findNavController(ProfileUIFragment.this).navigateUp();
                        });
                    }
                } else {
                    Log.e("ProfileUIFragment", "Failed to get user by id", task.getException());
                }
            });
        } else {
            // Normal flow: use existing device ID logic
            FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {
                database.getUserFromDeviceID(deviceId, task -> {
                    if (task.isSuccessful()) {
                        currentUser = task.getResult();
                        if (currentUser != null) {
                            // Hide loading and show content
                            binding.loadingProfileUi.setVisibility(View.GONE);
                            binding.contentGroupProfileUi.setVisibility(View.VISIBLE);

                            binding.adminProfileBackButton.setVisibility(View.GONE);
                            binding.userProfileEvents.setVisibility(View.GONE);
                            binding.profileName.setText(currentUser.getName());
                            binding.profileEmail.setText(currentUser.getEmail());
                            binding.profilePhone.setText(currentUser.getPhoneNumber());
                        }
                    } else {
                        Log.e("ProfileUIFragment", "Failed to get user", task.getException());
                    }
                });
            });
        }

        // When update profile button is clicked update user's data
        binding.updateProfileButton.setOnClickListener(v -> {
            if (currentUser != null) {
                // Fetch the values in the EditTexts
                String userName = binding.profileName.getText().toString().trim();
                String userEmail = binding.profileEmail.getText().toString().trim();
                String userPhone = binding.profilePhone.getText().toString().trim();

                // Warn users to not leave name and email empty
                if (userName.isEmpty()) {
                    binding.profileName.setError("Name is required");
                    return;
                }
                if (userEmail.isEmpty()) {
                    binding.profileEmail.setError("Email is required");
                    return;
                }

                // Update the user's params
                currentUser.setName(userName);
                currentUser.setEmail(userEmail);
                currentUser.setPhoneNumber(userPhone);

                // Update the database
                database.modifyUser(currentUser, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        Log.e("ProfileUIFragment", "Error updating user", task.getException());
                    }
                });
            } else {
                Toast.makeText(getContext(), "User not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });

        // Go to delete profile fragment when delete account button clicked
        binding.deleteProfileButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(ProfileUIFragment.this)
                    .navigate(R.id.action_profile_ui_fragment_to_delete_profile_fragment);
        });

        // Go to profile history fragment
        binding.profileHistoryButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(ProfileUIFragment.this)
                    .navigate(R.id.action_profile_ui_fragment_to_user_event_history_fragment);
        });

        return binding.getRoot();
    }
}