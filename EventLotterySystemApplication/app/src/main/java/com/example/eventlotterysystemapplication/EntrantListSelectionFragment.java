package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentEntrantListSelectionBinding;

public class EntrantListSelectionFragment extends Fragment {

    private FragmentEntrantListSelectionBinding binding;

    public EntrantListSelectionFragment() {
        // required empty constructor
    }

    public static EntrantListSelectionFragment newInstance() {
        EntrantListSelectionFragment fragment = new EntrantListSelectionFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEntrantListSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View all entrants Button to access list of all entrants
        binding.viewAllEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_allEntrantsListFragment);
        });

        // View all chosen entrants Button to access list of all entrants
        binding.allChosenEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_chosenEntrantList);
        });

        // View all pending entrants Button to access list of all entrants
        binding.allPendingEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_pendingEntrantList);
        });

        // View all cancelled entrants Button to access list of all entrants
        binding.allCancelledEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_cancelledEntrantListFragment);
        });
    }
}
