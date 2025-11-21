package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentEventsUiBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Displays a listview of all available events that the user can join, as well as an option to go to
 * {@link MyEventsFragment} to view the user's created events and to create a new event in
 * {@link CreateOrEditEventFragment}
 * Fetches an array of events from the database then displays them textually in a listview
 * Allows the user to click on any event in the list to open up a detailed view of the event in
 * {@link EventDetailScreenFragment}
 */

public class EventsUIFragment extends Fragment {
    /* Don't change the char 'i' in the name, Android Studio never generated
    * a class named FragmentEventsUIBinding, therefore we cannot capitalize it
    */
    private FragmentEventsUiBinding binding;
    // Holds event names to display in the ListView
    private ArrayAdapter<String> eventNamesAdapter;
    private final ArrayList<String> eventNames = new ArrayList<>();

    // Parallel list to keep Firestore document IDs (to then pass onto event details screen)
    private final ArrayList<String> docIds = new ArrayList<>();

    // For the owned vs not owned event (used to all events on events UI)
    private final ArrayList<Boolean> ownedFlags = new ArrayList<>();

    private String eventId;

    public EventsUIFragment() {
        // Required empty public constructor
    }

    public static EventsUIFragment newInstance() {
        EventsUIFragment fragment = new EventsUIFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventsUIFragmentArgs args = EventsUIFragmentArgs.fromBundle(getArguments());
        eventId = args.getEventId();

        Log.d("EventsUIFragment", "Event ID: " + eventId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventsUiBinding.inflate(inflater, container, false);

        // Simple built-in row layout
        eventNamesAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                eventNames
        );
        binding.eventsList.setAdapter(eventNamesAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!eventId.equals("nothing")) {
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_events_ui_fragment_to_event_detail_screen, args);
        }

        // Create Event button navigates to event creation page
        binding.createEventButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("eventId", null);
            NavHostFragment.findNavController(EventsUIFragment.this)
                    .navigate(R.id.action_events_ui_fragment_to_create_or_edit_event_fragment, args);
        });

        // My Events button navigates to my events page
        binding.myEventsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EventsUIFragment.this)
                    .navigate(R.id.action_events_ui_fragment_to_my_events_fragment);
        });

        // Show loading and hide content until it is fetched
        binding.loadingEventUi.setVisibility(View.VISIBLE);
        binding.contentGroupEventsUi.setVisibility(View.GONE);

        // Fetch all Event docs and display their "name" field in the listView
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        db.collection("Event")
            .get()
            .addOnSuccessListener(qs -> {
                // Hide loading and show content
                binding.loadingEventUi.setVisibility(View.GONE);
                binding.contentGroupEventsUi.setVisibility(View.VISIBLE);
                eventNames.clear();
                docIds.clear();
                ownedFlags.clear();

                for (DocumentSnapshot doc : qs.getDocuments()) {
                    String eventName = doc.getString("name");
                    String organizerId = doc.getString("organizerID");
                    boolean owned = (uid != null && organizerId != null && organizerId.equals(uid));

                    // Fallback on the doc ID if event name is missing
                    if (eventName == null) {
                        eventName = doc.getId();
                        eventNames.add(eventName);
                    } else {
                        eventNames.add(eventName);
                    }

                    // Add docId in parallel list
                    docIds.add(doc.getId());

                    // Add owned flag in parallel list
                    ownedFlags.add(owned);
                }
                // Notify the adapter that the data set has changed
                eventNamesAdapter.notifyDataSetChanged();
            })
            // Hide loading and add a listener to handle errors
            .addOnFailureListener(e -> {
                binding.loadingEventUi.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            });

        // Switched from NavHostFragment to a bundle to pass data between fragments
        binding.eventsList.setOnItemClickListener((parent, v, position, id) -> {
            Bundle args = new Bundle();
            args.putString("eventId", docIds.get(position));
            args.putBoolean("isOwnedEvent", ownedFlags.get(position)); // true/false per event
            NavHostFragment.findNavController(this)
                    .navigate(R.id.event_detail_screen, args);
        });

        // Auto-navigate if eventID was passed from MainActivity
        String eventID = null;
        if (getActivity() != null && getActivity().getIntent() != null) {
            eventID = getActivity().getIntent().getStringExtra("eventID");
        }

        if (eventID != null) {
            // Find the document index to set isOwnedEvent flag
            int index = docIds.indexOf(eventID);
            boolean isOwnedEvent = (index != -1) ? ownedFlags.get(index) : false;

            Bundle args = new Bundle();
            args.putString("eventId", eventID);
            args.putBoolean("isOwnedEvent", isOwnedEvent);

            NavHostFragment.findNavController(this)
                    .navigate(R.id.event_detail_screen, args);
        }
    }
}