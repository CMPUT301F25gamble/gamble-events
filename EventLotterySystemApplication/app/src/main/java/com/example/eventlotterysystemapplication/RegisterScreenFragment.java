package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentRegisterScreenBinding;

public class RegisterScreenFragment extends Fragment {

    private FragmentRegisterScreenBinding binding;

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