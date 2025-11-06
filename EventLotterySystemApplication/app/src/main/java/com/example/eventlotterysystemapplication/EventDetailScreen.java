package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentEventDetailScreenBinding;


public class EventDetailScreen extends Fragment {

    private FragmentEventDetailScreenBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailScreenBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button navigates to Events UI fragment
        binding.eventDetailScreenBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EventDetailScreen.this)
                    .navigate(R.id.action_event_detail_screen_to_events_ui_fragment);
        });
    }
}
