package com.example.eventlotterysystemapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.example.eventlotterysystemapplication.databinding.FragmentEventsUiBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;

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
    // Holds event names to display in the ListView
    private ArrayAdapter<String> eventNamesAdapter;
    private final ArrayList<String> eventNames = new ArrayList<>();

    // Parallel list to keep Firestore document IDs (to then pass onto event details screen)
    private final ArrayList<String> docIds = new ArrayList<>();

    // For the owned vs not owned event (used to all events on events UI)
    private final ArrayList<Boolean> ownedFlags = new ArrayList<>();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EventsUIFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsUIFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsUIFragment newInstance(String param1, String param2) {
        EventsUIFragment fragment = new EventsUIFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
    }
}