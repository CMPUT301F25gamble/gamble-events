package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentLotteryGuidelinesBinding;
import com.google.firebase.installations.FirebaseInstallations;

public class LotteryGuidelinesFragment extends Fragment {
    private Database database;
    private FragmentLotteryGuidelinesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLotteryGuidelinesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = new Database();
        
        // Cancel button to go back to previous register screen
        binding.registerCancelButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(LotteryGuidelinesFragment.this)
                    .navigate(R.id.action_lotteryGuidelinesFragment_to_first_time_user_info_fragment);
        });

        // Confirm button to go to next screen
        binding.registerConfirmButton.setOnClickListener(v -> {
            // Get the current user by deviceID
            FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {
                database.getUserFromDeviceID(deviceId, task -> {
                    if (task.isSuccessful()) {
                        User currentUser = task.getResult();
                        if (currentUser != null) {
                            // Mark registration as complete
                            currentUser.setRegistrationComplete(true);

                            database.modifyUser(currentUser, modifyTask -> {
                                if (modifyTask.isSuccessful()) {
                                    // Create new intent
                                    Intent nextActivityIntent = new Intent(getActivity(), ContentActivity.class);
                                    startActivity(nextActivityIntent);
                                    requireActivity().finish();  // finish the activity to free memory
                                } else {
                                    Log.e("Database", "Failed to update user registration status", modifyTask.getException());
                                }
                            });
                        }
                    } else {
                        Log.e("Database", "Failed to get user by deviceID", task.getException());
                    }
                });
            });

        });
    }
}
