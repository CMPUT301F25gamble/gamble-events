package com.example.eventlotterysystemapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.eventlotterysystemapplication.databinding.FragmentEventsUiBinding;
import com.google.firebase.installations.FirebaseInstallations;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsUIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsUIFragment extends Fragment {
    /* Don't change the char 'i' in the name, Android Studio never generated
    * a class named FragmentEventsUIBinding, therefore we cannot capitalize it
    */
    private FragmentEventsUiBinding binding;
    private Database database;


    public EventsUIFragment() {
        // Required empty public constructor
    }

    public static EventsUIFragment newInstance(String param1, String param2) {
        EventsUIFragment fragment = new EventsUIFragment();
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
        binding = FragmentEventsUiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create Event button navigates to event creation page
        binding.createEventButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EventsUIFragment.this)
                    .navigate(R.id.action_events_ui_fragment_to_create_or_edit_event_fragment);
        });

        // My Events button navigates to my events page
        binding.myEventsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EventsUIFragment.this)
                    .navigate(R.id.action_events_ui_fragment_to_my_events_fragment);
        });
    }
}