package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentMyEventsBinding;

public class MyEventsFragment extends Fragment {
    private FragmentMyEventsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button editMyEventButton = binding.editMyEvent;

        editMyEventButton.setOnClickListener(v -> {
            Intent nextActivityIntent = new Intent(getActivity(), EditEventActivity.class);
            startActivity(nextActivityIntent);
            requireActivity().finish();
        });

        // Back button navigates to events page
        binding.myEventsBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(MyEventsFragment.this)
                    .navigate(R.id.action_my_events_fragment_to_events_ui_fragment);
        });
    }
}
