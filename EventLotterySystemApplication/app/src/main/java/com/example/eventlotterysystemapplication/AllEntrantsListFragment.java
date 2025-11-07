package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentAllEntrantsListBinding;

/**
 * Fragment for displaying a ListView of all entrants for the selected event
 * Will fetch a list of all entrants from the database and display them in a ListView
 * Navigated to from {@link EntrantListSelectionFragment}
 */

public class AllEntrantsListFragment extends Fragment {
    private FragmentAllEntrantsListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllEntrantsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back Button to return to Event Lists page
        binding.allEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(AllEntrantsListFragment.this)
                    .navigate(R.id.action_allEntrantsListFragment_to_entrantListSelectionFragment);
        });
    }
}
