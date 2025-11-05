package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentFirstTimeInputBinding;

import java.util.Objects;

public class FirstTimeUserInfoFragment extends Fragment {

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

        // Cancel button to go back to previous register screen
        binding.registerCancelButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstTimeUserInfoFragment.this)
                    .navigate(R.id.action_first_time_user_info_fragment_to_register_screen_fragment);
        });

        // Confirm button to get user inputs and move to content activity
        binding.registerConfirmButton.setOnClickListener(v -> {
            // Get user input from text fields
            // Todo: add functionality for using these values to create a new account
            String userName = binding.nameEditText.getText().toString();
            String userEmail = binding.emailEditText.getText().toString();
            String userPhone = binding.phoneEditText.getText().toString();

            // Todo: Add functionality to verify that the user has input every field
            NavHostFragment.findNavController(FirstTimeUserInfoFragment.this)
                    .navigate(R.id.action_first_time_user_info_fragment_to_lotteryGuidelinesFragment);
            /*
             * As of right now we will move this to the lottery guidelines fragment
             */
            // Create new intent
            // Intent nextActivityIntent = new Intent(getActivity(), ContentActivity.class);
            // startActivity(nextActivityIntent);
            // requireActivity().finish();  // finish the activity to free memory
        });
    }
}
