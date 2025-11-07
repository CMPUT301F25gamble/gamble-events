package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentLotteryGuidelinesBinding;
import com.google.firebase.installations.FirebaseInstallations;

/**
 * Displays the lottery guidelines and important information with how the lottery works for first
 * time user registration
 * Allows the user to cancel to go back to {@link FirstTimeUserInfoFragment}
 * Allows the user to confirm to go to {@link ContentActivity}
 */

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
        SharedUserViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedUserViewModel.class);

        // Cancel button to go back to previous register screen
        binding.registerCancelButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(LotteryGuidelinesFragment.this)
                    .navigate(R.id.action_lotteryGuidelinesFragment_to_first_time_user_info_fragment);
        });

        // Confirm button to go to next screen
        binding.registerConfirmButton.setOnClickListener(v -> {
            User user = viewModel.getUser().getValue(); // directly fetch without observing
            if (user != null) {
                database.addUser(user, task -> {
                    if (task.isSuccessful()) {
                        database.modifyUser(user, updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(requireContext(), "Registration complete!", Toast.LENGTH_SHORT).show();

                                // go to next activity
                                Intent nextActivityIntent = new Intent(getActivity(), ContentActivity.class);
                                startActivity(nextActivityIntent);
                                requireActivity().finish();
                            } else {
                                Toast.makeText(requireContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(requireContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Error: No user data found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
