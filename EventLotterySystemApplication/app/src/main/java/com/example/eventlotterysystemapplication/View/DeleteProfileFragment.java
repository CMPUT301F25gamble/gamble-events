package com.example.eventlotterysystemapplication.View;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.Controller.RegisterActivity;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.databinding.FragmentDeleteProfileBinding;
import com.google.firebase.installations.FirebaseInstallations;

/**
 * Allows user to permanently remove their account from the database
 * Provides a textual warning to the user that deletion is permanent
 */

public class DeleteProfileFragment extends Fragment {
    private static final String TAG = "DeleteProfileFragment"; // For debugging
    private Database database;
    private FragmentDeleteProfileBinding binding;
    private String userId;
    private boolean isAdminMode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDeleteProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = new Database();

        // Fetch the global user ID and admin mode from the AdminSession class
        userId = AdminSession.getSelectedUserId();
        isAdminMode = AdminSession.getAdminMode();
        Log.d("DeleteProfileFragment",
                "userId arg = " + userId + "; isAdminMode = " + isAdminMode);


        // Back button
        binding.deleteProfileBackButton.setOnClickListener(v -> {
            if (isAdminMode) {
                // IS ADMIN
                NavHostFragment.findNavController(DeleteProfileFragment.this)
                        .navigate(R.id.action_deleteProfileFragment_to_profileUIFragment2);
            } else {
                // NOT ADMIN
                NavHostFragment.findNavController(DeleteProfileFragment.this)
                        .navigate(R.id.action_delete_profile_fragment_to_profile_ui_fragment);
            }
        });

        // Confirm Delete button
        binding.confirmDeleteProfileButton.setOnClickListener(v -> {
            // Prevent double-taps in case of accidental clicks or rapid navigation
            v.setEnabled(false);

            // ADMIN MODE
            if (isAdminMode && userId != null) {
                Log.d(TAG, "Admin deleting user: " + userId);

                database.getUser(userId, task -> {
                    if (task.isSuccessful()) {
                        User userToDelete = task.getResult();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            database.deleteUser(userToDelete, deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    Toast.makeText(requireContext(), "User deleted!", Toast.LENGTH_SHORT).show();

                                    // After admin deletes a user, go back to all profiles fragment
                                    AdminSession.setSelectedUserId(null);
                                    NavHostFragment.findNavController(DeleteProfileFragment.this)
                                            .navigate(R.id.action_deleteProfileFragment_to_allProfilesFragment);

                                } else {
                                    Toast.makeText(requireContext(), "Failed to delete user.", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Delete failed", deleteTask.getException());
                                }
                            });
                        }
                    }
                });
                return; // STOP HERE
            }
            // Clear session here (sign out, wipe prefs/db, etc.)
            // Get the device ID
            FirebaseInstallations.getInstance().getId()
                .addOnSuccessListener(deviceId -> {
                    Log.d(TAG, "Firebase Device ID: " + deviceId);

                    // Use the device ID to get the user profile
                    database.getUserFromDeviceID(deviceId, task -> {
                        if (task.isSuccessful()) {
                            User userToDelete = task.getResult();

                            // Delete the user using database method
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                database.deleteUser(userToDelete, deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Deletion successful!", Toast.LENGTH_SHORT).show();

                                        // Launch RegisterActivity as a fresh task and clear the old one
                                        Intent intent = new Intent(requireContext(), RegisterActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                        // End the current Activity explicitly (extra safety so we don't garbage collect)
                                        requireActivity().finish();
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to delete account.", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Failed to delete user", deleteTask.getException());
                                    }
                                });
                            }
                        } else {
                            Log.e("DeleteUser", "Failed to get user by deviceID: ", task.getException());
                        }
                    });
                });

        });
    }
}
