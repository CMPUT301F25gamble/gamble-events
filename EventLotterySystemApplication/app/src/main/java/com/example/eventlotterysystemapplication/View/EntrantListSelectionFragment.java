package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.EntrantStatus;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentEntrantListSelectionBinding;

/**
 * EntrantListSelectionFragment
 * Displays the categories of entrants and allows the user to navigate to the respective fragment
 * for viewing the chosen list of entrants
 * User can choose to see all entrants, chosen entrants, pending entrants, cancelled entrants, and
 * finalised list of entrants, with buttons linking to {@link AllEntrantsListFragment},
 * {@link ChosenEntrantListFragment}, {@link WaitingEntrantListFragment},
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
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_allEntrantsListFragment, bundle);
        });

        // View all chosen entrants Button to access list of all entrants
        binding.allChosenEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_chosenEntrantList, bundle);
        });

        // View all pending entrants Button to access list of all entrants
        binding.allPendingEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_pendingEntrantList, bundle);
        });

        // View all cancelled entrants Button to access list of all entrants
        binding.allCancelledEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_cancelledEntrantListFragment, bundle);
        });

        // View final entrants Button to access list of all entrants
        binding.finalListOfEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EntrantListSelectionFragment.this)
                    .navigate(R.id.action_entrantListSelectionFragment_to_finalEntrantList, bundle);
        });

        binding.mapTextView.setVisibility(View.GONE);

        binding.pendingMapButton.setOnClickListener(v -> {
            Bundle bundle3 = new Bundle();
            bundle3.putString("eventID", eventId);
            bundle3.putString("entrantStatus", String.valueOf(EntrantStatus.WAITING));
            NavHostFragment.findNavController(EntrantListSelectionFragment.this).navigate(R.id.action_entrantListSelectionFragment_to_my_event_enterants_map, bundle3);
        });
        binding.pendingMapButton.setVisibility(View.GONE);

        // TODO figure out some way to check if geolocation requirement is enabled

        Database.getDatabase().getEvent(eventId, task -> {
            if (task.isSuccessful()){
                if (task.getResult().isGeolocationRequirement()) {
                    Log.d("Geolocation", "Enabled");
                    binding.pendingMapButton.setVisibility(View.VISIBLE);
                    binding.mapTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
