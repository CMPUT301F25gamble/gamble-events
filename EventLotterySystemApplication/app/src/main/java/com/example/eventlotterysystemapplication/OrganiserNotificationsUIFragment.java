package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentOrganiserNotificationUiBinding;

/**
 * OrganiserNotificationUIFragment
 * Displays the options for sending notifications to either Waitlist Entrants, Chose Entrants,
 * or Cancelled Entrants.
 * Navigates to {@link OrganiserSendNotificationUIFragment} when a button is clicked and sends the entrant
 * type selection as an argument
 */

public class OrganiserNotificationsUIFragment extends Fragment {

    private FragmentOrganiserNotificationUiBinding binding;

    public OrganiserNotificationsUIFragment() {
        // Required empty constructor
    }

    public static OrganiserNotificationsUIFragment newInstance() {
        OrganiserNotificationsUIFragment fragment = new OrganiserNotificationsUIFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganiserNotificationUiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get buttons
        Button waitlistEntrantsButton = binding.waitlistEntrantsButton;
        Button chosenEntrantsButton = binding.chosenEntrantsButton;
        Button cancelledEntrantsButton = binding.cancelledEntrantsButton;


        // Set click listeners for buttons
        waitlistEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(OrganiserNotificationsUIFragment.this)
                    .navigate(OrganiserNotificationsUIFragmentDirections
                            .actionOrganiserNotificationsUIFragmentToOrganiserSendNotificationUIFragment("waitlist"));
        });

        chosenEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(OrganiserNotificationsUIFragment.this)
                    .navigate(OrganiserNotificationsUIFragmentDirections
                            .actionOrganiserNotificationsUIFragmentToOrganiserSendNotificationUIFragment("chosen"));
        });

        cancelledEntrantsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(OrganiserNotificationsUIFragment.this)
                    .navigate(OrganiserNotificationsUIFragmentDirections
                            .actionOrganiserNotificationsUIFragmentToOrganiserSendNotificationUIFragment("chosen"));
        });

    }
}
