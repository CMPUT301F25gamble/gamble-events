package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentEntrantListSelectionBinding;

/**
 * EntrantListSelectionFragment
 * Displays the categories of entrants and allows the user to navigate to the respective fragment
 * for viewing the chosen list of entrants
 * User can choose to see all entrants, chosen entrants, pending entrants, cancelled entrants, and
 * finalised list of entrants, with buttons linking to {@link AllEntrantsListFragment},
 * {@link ChosenEntrantListFragment}, {@link PendingEntrantListFragment},
 * {@link CancelledEntrantListFragment}, and {@link FinalEntrantListFragment}
 */

public class EntrantListSelectionFragment extends Fragment {

    private FragmentEntrantListSelectionBinding binding;
    private String eventId;
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
        com.example.eventlotterysystemapplication.View.EventDetailScreenFragmentArgs args = com.example.eventlotterysystemapplication.View.EventDetailScreenFragmentArgs.fromBundle(getArguments());
        eventId = args.getEventId();
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

        // Get the eventID from the intent
        String eventId = requireActivity().getIntent().getStringExtra("eventId");
        Bundle bundle = new Bundle();
        bundle.putString("eventID", eventId);

        // View all entrants Button to access list of all entrants
        binding.viewAllEntrantsButton.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            bundle1.putString("eventID", eventId);
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_allEntrantsListFragment, bundle);
        });

        // View all chosen entrants Button to access list of all entrants
        binding.allChosenEntrantsButton.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putString("eventID", eventId);
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_chosenEntrantList, bundle);
        });

        // View all pending entrants Button to access list of all entrants
        binding.allPendingEntrantsButton.setOnClickListener(v -> {
            Bundle bundle3 = new Bundle();
            bundle3.putString("eventID", eventId);
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_pendingEntrantList, bundle);
        });

        // View all cancelled entrants Button to access list of all entrants
        binding.allCancelledEntrantsButton.setOnClickListener(v -> {
            Bundle bundle4 = new Bundle();
            bundle4.putString("eventID", eventId);
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_cancelledEntrantListFragment, bundle);
        });

        // View final entrants Button to access list of all entrants
        binding.finalListOfEntrantsButton.setOnClickListener(v -> {
            Bundle bundle5 = new Bundle();
            bundle5.putString("eventID", eventId);
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_finalEntrantList, bundle);
        });
    }
}
