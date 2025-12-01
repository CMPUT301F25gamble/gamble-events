package com.example.eventlotterysystemapplication.View;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentRegisterScreenBinding;

/**
 * Displays the first time app logo and register button and a button to navigate to
 * {@link FirstTimeUserInfoFragment}
 */

public class RegisterScreenFragment extends Fragment {

    private FragmentRegisterScreenBinding binding;

    /**
     * Required empty public constructor
     */
    public RegisterScreenFragment() {
    }

    public static RegisterScreenFragment newInstance() {
        RegisterScreenFragment fragment = new RegisterScreenFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // make the register button navigate to first time input
        binding.registerButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(RegisterScreenFragment.this)
                    .navigate(R.id.action_registerScreenFragment_to_firstTimeUserInfoFragment2);
        });
    }
}