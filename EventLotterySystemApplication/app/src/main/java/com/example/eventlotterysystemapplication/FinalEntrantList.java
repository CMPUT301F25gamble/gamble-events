package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentFinalEntrantListBinding;

/**
 * Displays a listview of all final entrants that have accepted an invitation to join the event
 * Will fetch a list of final entrants from the database to be displayed in the listview
 */

public class FinalEntrantList extends Fragment {
    private FragmentFinalEntrantListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFinalEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back Button to return to Event Lists page
        binding.finalEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(FinalEntrantList.this)
                    .navigate(R.id.action_finalEntrantList_to_entrantListSelectionFragment);
        });
    }
}
