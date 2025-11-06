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
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentFirstTimeInputBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirstTimeUserInfoFragment extends Fragment {
    private static final String TAG = "FirstTimeUserInfo"; // For debugging

    private FragmentFirstTimeInputBinding binding;
    private Database database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFirstTimeInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = new Database();

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

            // Build user object
            User user = new User();
            user.setName(userName);
            user.setEmail(userEmail);
            if (!userPhone.isEmpty()) user.setPhoneNumber(userPhone);

            // Add to database
            database.addUser(user, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(FirstTimeUserInfoFragment.this)
                            .navigate(R.id.action_first_time_user_info_fragment_to_lotteryGuidelinesFragment);
                } else {
                    Toast.makeText(requireContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                    Log.e("Database", "User registration failed", task.getException());
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
