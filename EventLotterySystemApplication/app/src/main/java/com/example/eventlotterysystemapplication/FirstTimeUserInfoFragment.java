package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentFirstTimeInputBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirstTimeUserInfoFragment extends Fragment {
    private static final String TAG = "FirstTimeUserInfo"; // For debugging

    private FragmentFirstTimeInputBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFirstTimeInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedUserViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedUserViewModel.class); // Used to "pass info" to lottery guidelines

        // Cancel button to go back to previous register screen
        binding.registerCancelButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstTimeUserInfoFragment.this)
                    .navigate(R.id.action_first_time_user_info_fragment_to_register_screen_fragment);
        });

        // Confirm button to get user inputs and move to content activity
        binding.registerConfirmButton.setOnClickListener(v -> {
            String userName = binding.nameEditText.getText().toString().trim();
            String userEmail = binding.emailEditText.getText().toString().trim();
            String userPhone = binding.phoneEditText.getText().toString().trim();

            if (userName.isEmpty()) {
                binding.nameEditText.setError("Name is required");
                return;
            }
            if (userEmail.isEmpty()) {
                binding.emailEditText.setError("Email is required");
                return;
            }

            // Get device's actual ID
            FirebaseInstallations.getInstance().getId()
                    .addOnSuccessListener(deviceId -> {
                        Log.d(TAG, "Firebase Device ID: " + deviceId);

                        // Build user object
                        User user = new User();
                        user.setDeviceID(deviceId);
                        user.setName(userName);
                        user.setEmail(userEmail);
                        if (!userPhone.isEmpty()) user.setPhoneNumber(userPhone);

                        // Save temporarily to ViewModel instead of committing just yet
                        viewModel.setUser(user);

                        // Go to lottery guidelines screen
                        NavHostFragment.findNavController(FirstTimeUserInfoFragment.this)
                                .navigate(R.id.action_first_time_user_info_fragment_to_lotteryGuidelinesFragment);
                    });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}