package com.example.eventlotterysystemapplication.View;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Entrant;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentEventsUiBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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
    // Used for ADMIN control
    private String userId;
    private boolean isAdminMode;

    private List<Event> eventList;


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

        // Get the global user ID and admin mode from the AdminSession class
        isAdminMode = AdminSession.getAdminMode();
        userId = AdminSession.getSelectedUserId();
        // Log global user ID and admin mode from the AdminSession class for debugging
        Log.d("EventsUIFragment", "userId = " + userId + "; isAdminMode = " + isAdminMode);

        if (isAdminMode) {
            // Show loading and hide content until data is fetched from db
            binding.loadingEventUi.setVisibility(View.VISIBLE);
            binding.contentGroupEventsUi.setVisibility(View.GONE);
            /* Separate content group for admin related buttons so Create Event button and
             * My Events button are not visible to the admin
             */

            // TODO: Add logic for my events

            binding.loadingEventUi.setVisibility(View.GONE);
            binding.contentGroupEventsUi.setVisibility(View.VISIBLE);
        } else {
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
            binding.contentGroupAdminEventsUi.setVisibility(View.GONE);
        }

        fetchAllEvents();

        // This method is a lot slower so will keep the current method of fetching events as well
        Database.getDatabase().getAllEvents(task -> {
            if (!task.isSuccessful()) {
                binding.loadingEventUi.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                return;
            }

            eventList = task.getResult();
        });

        // Switched from NavHostFragment to a bundle to pass data between fragments
        binding.eventsList.setOnItemClickListener((parent, v, position, id) -> {
            Bundle args = new Bundle();
            args.putString("eventId", docIds.get(position));
            args.putBoolean("isOwnedEvent", ownedFlags.get(position)); // true/false per event
            Log.d("EventsUIFragmentsss", "isOwnedEvent = " + ownedFlags.get(position));
            Log.d("EventsUIFragment", "eventId = " + docIds.get(position));


            if (isAdminMode) {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_eventsUIFragment_to_eventDetailScreenFragment, args);
            } else {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.event_detail_screen, args);
            }
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


        // Show pop-up when filter button pressed
        binding.eventsScreenFilterButton.setOnClickListener(v->{

            showFilterEventDialog(binding);
        });




    }

    /**
     * Displays a dialog containing interests and availability fields
     * for the user to input optionally such that upon clicking the
     * confirm button, the events ui page will update based on what
     * the user inputted.
     */
    private void showFilterEventDialog(FragmentEventsUiBinding binding) {

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogFilterEventsView = inflater.inflate(R.layout.dialog_filter_events, null);

        EditText keywordEditText = dialogFilterEventsView.findViewById(R.id.keywordSearchEditText);
        EditText availabilityEditText = dialogFilterEventsView.findViewById(R.id.availabilityEditText);

        Button searchButton = dialogFilterEventsView.findViewById(R.id.dialogSearchButton);
        Button backButton1 = dialogFilterEventsView.findViewById(R.id.dialogBackButton);


        // Setup first dialog for displaying user info
        AlertDialog dialog1 = new AlertDialog.Builder(requireContext())
                .setView(dialogFilterEventsView)
                .setCancelable(true)
                .create();

        // Set the background to transparent so we can show the rounded corners
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        // Return to list view
        backButton1.setOnClickListener(v -> dialog1.dismiss());

        // Move to a new dialog
        searchButton.setOnClickListener(v -> {
            // fetch edit text fields
            String keywordsStr = keywordEditText.getText().toString().trim();
            String availabilityStr = availabilityEditText.getText().toString().trim();

            // TODO: split keywords
            // Parse tags; split by commas
            ArrayList<String> keywordsList = new ArrayList<>();
            if (!keywordsStr.isEmpty()) {
                String[] keywordsArray = keywordsStr.split(",");
                for (String keyword : keywordsArray) {
                    String trimmedKeyword = keyword.trim();
                    if (!trimmedKeyword.isEmpty()) {
                        keywordsList.add(trimmedKeyword);
                    }
                }
            }

            // Format availability input
            LocalDateTime availability = null;
            if (!availabilityStr.isEmpty()) {
                availability = DateTimeFormatter(availabilityStr);
            }


            if (keywordsList.isEmpty() && availability == null) {
                // Both empty, so reset list
                fetchAllEvents();
            }
            else if (!keywordsList.isEmpty() && availability == null) {
                // Only keyword
                filterEventsByKeyword(keywordsList);
            }
            else if (keywordsList.isEmpty()) {
                // Only date
                filterEventsByStartDate(availability);
            }
            else {
                // Both fields filled
                filterEventsByKeywordAndStartDate(keywordsList, availability);
            }
            dialog1.dismiss();

        });
        dialog1.show();
    }

    /**
     * Converts the user inputted string into a LocalDateTime object
     * @param dateTimeStr The user inputted datetime string
     * @return A LocalDateTime object
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime DateTimeFormatter(String dateTimeStr) {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime eventDateTime = null;
        try {
            eventDateTime = LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            Toast.makeText(getContext(), "Invalid date/time format. Use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show();
            return null;
        }
        return eventDateTime;
    }


    /**
     * Fetch all events from Firebase and set the event names, docIDs, and owned arraylists
     */
    private void fetchAllEvents() {
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
                    // If the user is not an admin, show the non-admin-specific content group
                    if (!isAdminMode) {
                        binding.contentGroupAdminEventsUi.setVisibility(View.VISIBLE);
                    }
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
    }


    /**
     * Filters by name, description, tags, and location case-insensitively by a keyword.
     * The keyword must be a substring within any of the 4 filtering categories as described before
     * for the event to be matched. After filtering is done, updates the UI.
     * @param keywordsList the list of keywords to filter events by
     */
    private void filterEventsByKeyword(ArrayList<String> keywordsList) {
        if (eventList == null) {
            Log.e("EventsUi", "THE EVENT LIST IS NULL AHHH");
            return;
        }

        // If keyword is empty then fetch all events again
        if (keywordsList == null || keywordsList.isEmpty()) {
            fetchAllEvents();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        eventNames.clear();
        docIds.clear();
        ownedFlags.clear();

        // Filter events by keyword (CASE-INSENSITIVE)
        eventList.forEach(event -> {
            String name = event.getName();
            String description = event.getDescription();
            String location = event.getPlace();
            String tags = String.join(" ", event.getEventTags());

            boolean matched = false;

            for (String keyword : keywordsList) {
                String lowercaseKeyword = keyword.toLowerCase();
                if (name.toLowerCase().contains(lowercaseKeyword) ||
                        description.toLowerCase().contains(lowercaseKeyword) ||
                        location.toLowerCase().contains(lowercaseKeyword) ||
                        tags.toLowerCase().contains(lowercaseKeyword)) {
                        matched = true;
                        break; // prevent duplicate matches with OR semantics
                }
            }

            if (matched) {
                eventNames.add(name);
                docIds.add(event.getEventID());
                ownedFlags.add(event.getOrganizerID().equals(uid));
            }
        });

        // Update UI
        eventNamesAdapter.notifyDataSetChanged();
    }

    /**
     * Filters events by the start date being after the date given
     * @param date date to filter event start dates after
     */
    private void filterEventsByStartDate(LocalDateTime date) {
        if (eventList == null) {
            Log.e("EventsUi", "THE EVENT LIST IS NULL AHHH");
            return;
        }

        // If date is empty then fetch all events again
        if (date == null) {
            fetchAllEvents();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        eventNames.clear();
        docIds.clear();
        ownedFlags.clear();

        // Filter events by start date being after the date provided
        eventList.forEach(event -> {
            if (event.getEventStartTime() != null && event.getEventStartTime().isAfter(date)) {
                eventNames.add(event.getName());
                docIds.add(event.getEventID());
                ownedFlags.add(event.getOrganizerID().equals(uid));
            }
        });

        // Update UI
        eventNamesAdapter.notifyDataSetChanged();
    }

    /**
     * Filters event by start date AND by a keyword within name, description, tags, and location case-insensitively.
     * The keyword must be a substring within any of the 4 filtering categories as described before
     * for the event to be matched. After filtering is done, updates the UI.
     * @param date date to filter event start dates after
     */
    private void filterEventsByKeywordAndStartDate(ArrayList<String> keywordsList, LocalDateTime date) {
        if (eventList == null) {
            Log.e("EventsUi", "THE EVENT LIST IS NULL AHHH");
            return;
        }

        // If date is empty then fetch all events again
        if (date == null || keywordsList == null || keywordsList.isEmpty()) {
            fetchAllEvents();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        eventNames.clear();
        docIds.clear();
        ownedFlags.clear();

        // Filter events by start date being after the date provided
        eventList.forEach(event -> {
            String name = event.getName();
            String description = event.getDescription();
            String location = event.getPlace();
            String tags = String.join(" ", event.getEventTags());

            boolean matched = false;

            for (String keyword : keywordsList){
                String lowercaseKeyword = keyword.toLowerCase();
                if (name.toLowerCase().contains(lowercaseKeyword) ||
                        description.toLowerCase().contains(lowercaseKeyword) ||
                        location.toLowerCase().contains(lowercaseKeyword) ||
                        tags.toLowerCase().contains(lowercaseKeyword)) {
                    if (event.getEventStartTime() != null && event.getEventStartTime().isAfter(date)) {
                        matched = true;
                        break; // prevent duplicates
                    }
                }
            }

            if (matched) {
                eventNames.add(event.getName());
                docIds.add(event.getEventID());
                ownedFlags.add(event.getOrganizerID().equals(uid));
            }

        });

        // Update UI
        eventNamesAdapter.notifyDataSetChanged();
    }
}