package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentChosenEntrantListBinding;

/**
 * Displays a listview of chosen entrants that includes pending entrants, cancelled entrants,
 * and entrants that accepted and invitation to the event
 * Will fetch a list of the above from the database to be displayed in the listview
 * Navigated to from {@link EntrantListSelectionFragment}
 */

public class ChosenEntrantListFragment extends Fragment {
    private FragmentChosenEntrantListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChosenEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back Button to return to Event Lists page
        binding.chosenEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(ChosenEntrantListFragment.this)
                    .navigate(R.id.action_chosenEntrantList_to_entrantListSelectionFragment);
        });
    }
}
