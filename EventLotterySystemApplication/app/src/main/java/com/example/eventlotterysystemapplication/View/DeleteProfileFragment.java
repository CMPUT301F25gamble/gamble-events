package com.example.eventlotterysystemapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Controller.Database;
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

        // Back button navigates to profile fragment
        binding.deleteProfileBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(DeleteProfileFragment.this)
                    .navigate(R.id.action_delete_profile_fragment_to_profile_ui_fragment);
        });

        // Delete button navigates to the register screen
        binding.confirmDeleteProfileButton.setOnClickListener(v -> {
            // Prevent double-taps in case of accidental clicks or rapid navigation
            v.setEnabled(false);

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
                            } else {
                                Log.e("DeleteUser", "Failed to get user by deviceID: ", task.getException());
                            }
                        });
                    });
        });
    }
}
