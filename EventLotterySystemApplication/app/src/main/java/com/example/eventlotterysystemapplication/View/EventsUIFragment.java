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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Controller.EventAdapter;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentEventsUiBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private FragmentEventsUiBinding binding;

    // Adapter and filtered list for displaying events
    private EventAdapter eventAdapter;
    private final List<Event> filteredEventList = new ArrayList<>();

    // Master event list fetched from database
    private List<Event> eventList = new ArrayList<>();

    // Used for ADMIN control
    private String userId;
    private boolean isAdminMode;

    // Fragment parameters (not used heavily here)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public EventsUIFragment() {
        // Required empty public constructor
    }

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
        binding = FragmentEventsUiBinding.inflate(inflater, container, false);

        // Initialize EventAdapter with empty filtered list
        eventAdapter = new EventAdapter(requireContext(), filteredEventList);
        binding.eventsList.setAdapter(eventAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get global user ID and admin mode
        isAdminMode = AdminSession.getAdminMode();
        userId = AdminSession.getSelectedUserId();
        Log.d("EventsUIFragment", "userId = " + userId + "; isAdminMode = " + isAdminMode);

        if (isAdminMode) {
            binding.loadingEventUi.setVisibility(View.VISIBLE);
            binding.contentGroupEventsUi.setVisibility(View.GONE);
            // TODO: Admin-specific logic here

            binding.loadingEventUi.setVisibility(View.GONE);
            binding.contentGroupEventsUi.setVisibility(View.VISIBLE);
        } else {
            // Non-admin buttons
            binding.createEventButton.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("eventId", null);
                NavHostFragment.findNavController(EventsUIFragment.this)
                        .navigate(R.id.action_events_ui_fragment_to_create_or_edit_event_fragment, args);
            });

            binding.myEventsButton.setOnClickListener(v -> {
                NavHostFragment.findNavController(EventsUIFragment.this)
                        .navigate(R.id.action_events_ui_fragment_to_my_events_fragment);
            });

            binding.loadingEventUi.setVisibility(View.VISIBLE);
            binding.contentGroupEventsUi.setVisibility(View.GONE);
            binding.contentGroupAdminEventsUi.setVisibility(View.GONE);
        }

        // Fetch all events
        fetchAllEvents();

        // Older database call (kept if needed)
        Database.getDatabase().getAllEvents(task -> {
            if (!task.isSuccessful()) {
                binding.loadingEventUi.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                return;
            }
            eventList = task.getResult();
        });

        // Handle item clicks
        binding.eventsList.setOnItemClickListener((parent, v, position, id) -> {
            Event event = filteredEventList.get(position);
            Bundle args = new Bundle();
            args.putString("eventId", event.getEventID());
            args.putBoolean("isOwnedEvent", event.getOrganizerID().equals(userId));
            Log.d("EventsUIFragment", "isOwnedEvent = " + event.getOrganizerID().equals(userId));
            Log.d("EventsUIFragment", "eventId = " + event.getEventID());

            if (isAdminMode) {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_eventsUIFragment_to_eventDetailScreenFragment, args);
            } else {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.event_detail_screen, args);
            }
        });

        // Auto-navigate if eventID passed from MainActivity
        String eventID = null;
        if (getActivity() != null && getActivity().getIntent() != null) {
            eventID = getActivity().getIntent().getStringExtra("eventID");
        }

        if (eventID != null) {
            for (Event event : filteredEventList) {
                if (event.getEventID().equals(eventID)) {
                    Bundle args = new Bundle();
                    args.putString("eventId", eventID);
                    args.putBoolean("isOwnedEvent", event.getOrganizerID().equals(userId));
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.event_detail_screen, args);
                    break;
                }
            }
        }

        // Filter dialog
        binding.eventsScreenFilterButton.setOnClickListener(v -> showFilterEventDialog(binding));
    }

    private void showFilterEventDialog(FragmentEventsUiBinding binding) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogFilterEventsView = inflater.inflate(R.layout.dialog_filter_events, null);

        EditText keywordEditText = dialogFilterEventsView.findViewById(R.id.keywordSearchEditText);
        EditText availabilityEditText = dialogFilterEventsView.findViewById(R.id.availabilityEditText);

        Button searchButton = dialogFilterEventsView.findViewById(R.id.dialogSearchButton);
        Button backButton1 = dialogFilterEventsView.findViewById(R.id.dialogBackButton);

        AlertDialog dialog1 = new AlertDialog.Builder(requireContext())
                .setView(dialogFilterEventsView)
                .setCancelable(true)
                .create();

        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        backButton1.setOnClickListener(v -> dialog1.dismiss());

        searchButton.setOnClickListener(v -> {
            String keywordsStr = keywordEditText.getText().toString().trim();
            String availabilityStr = availabilityEditText.getText().toString().trim();

            ArrayList<String> keywordsList = new ArrayList<>();
            if (!keywordsStr.isEmpty()) {
                String[] keywordsArray = keywordsStr.split(",");
                for (String keyword : keywordsArray) {
                    String trimmedKeyword = keyword.trim();
                    if (!trimmedKeyword.isEmpty()) keywordsList.add(trimmedKeyword);
                }
            }

            LocalDateTime availability = null;
            if (!availabilityStr.isEmpty()) {
                availability = DateTimeFormatter(availabilityStr);
            }

            if (keywordsList.isEmpty() && availability == null) fetchAllEvents();
            else if (!keywordsList.isEmpty() && availability == null) filterEventsByKeyword(keywordsList);
            else if (keywordsList.isEmpty()) filterEventsByStartDate(availability);
            else filterEventsByKeywordAndStartDate(keywordsList, availability);

            dialog1.dismiss();
        });

        dialog1.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime DateTimeFormatter(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime eventDateTime = null;
        try {
            eventDateTime = LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            Toast.makeText(getContext(), "Invalid date/time format. Use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show();
            return null;
        }
        return eventDateTime;
    }

    private void fetchAllEventsAdmin() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        db.collection("Event")
                .get()
                .addOnSuccessListener(qs -> {
                    binding.loadingEventUi.setVisibility(View.GONE);
                    binding.contentGroupEventsUi.setVisibility(View.VISIBLE);
                    if (!isAdminMode) binding.contentGroupAdminEventsUi.setVisibility(View.VISIBLE);

                    eventList.clear();

                    // Use parseEvent instead of doc.toObject()
                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Database.getDatabase().parseEvent(doc, task -> {
                            if (task.isSuccessful()) {
                                Event event = task.getResult();
                                if (event != null) {
                                    eventList.add(event);

                                    // After adding all events, update filtered list and UI
                                    if (eventList.size() == qs.getDocuments().size()) {
                                        filteredEventList.clear();
                                        filteredEventList.addAll(eventList);
                                        eventAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                Log.e("EventsUIFragment", "Failed to parse event: " + task.getException());
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    binding.loadingEventUi.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchAllEvents() {
        if(isAdminMode) {
            fetchAllEventsAdmin();
            return;
        }
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.loadingEventUi.setVisibility(View.VISIBLE);
        binding.contentGroupEventsUi.setVisibility(View.GONE);

        User user = new User();
        user.setUserID(currentUser.getUid());

        Database.getDatabase().viewAvailableEvents(user, task -> {
            binding.loadingEventUi.setVisibility(View.GONE);
            binding.contentGroupEventsUi.setVisibility(View.VISIBLE);
            if (!isAdminMode) binding.contentGroupAdminEventsUi.setVisibility(View.VISIBLE);

            if (task.isSuccessful()) {
                List<Event> availableEvents = task.getResult();
                Log.d("Database", String.valueOf(availableEvents.size()));
                eventList.clear();
                if (availableEvents != null) {
                    eventList.addAll(availableEvents);
                }

                filteredEventList.clear();
                filteredEventList.addAll(eventList);
                eventAdapter.notifyDataSetChanged();
            } else {
                Log.e("EventsUIFragment", "Failed to load events: " + task.getException());
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
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
            filteredEventList.clear();
            filteredEventList.addAll(eventList);
            eventAdapter.notifyDataSetChanged();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        filteredEventList.clear();

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
                filteredEventList.add(event);
            }
        });

        // Update UI
        eventAdapter.notifyDataSetChanged();
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
            filteredEventList.clear();
            filteredEventList.addAll(eventList);
            eventAdapter.notifyDataSetChanged();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        filteredEventList.clear();

        // Filter events by start date being after the date provided
        eventList.forEach(event -> {
            if (event.getEventStartTime() != null && event.getEventStartTime().isAfter(date)) {
                filteredEventList.add(event);
            }
        });

        // Update UI
        eventAdapter.notifyDataSetChanged();
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
            filteredEventList.clear();
            filteredEventList.addAll(eventList);
            eventAdapter.notifyDataSetChanged();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        filteredEventList.clear();

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
                filteredEventList.add(event);
            }

        });

        // Update UI
        eventAdapter.notifyDataSetChanged();
    }
}
