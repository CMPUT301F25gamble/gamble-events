package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentDeleteProfileBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentProfileUiBinding;

public class DeleteProfileFragment extends Fragment {

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

        // Back button navigates to profile fragment
        binding.deleteProfileBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(DeleteProfileFragment.this)
                    .navigate(R.id.action_delete_profile_fragment_to_profile_ui_fragment);
        });
    }
}
