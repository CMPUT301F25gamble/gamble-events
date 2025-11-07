package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventlotterysystemapplication.databinding.FragmentEventDetailScreenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * EventDetailScreenFragment
 * This fragment displays the details for a selected event
 * Gets the eventId from arguments and fetches the event from the database
 * Displays fetched data including the name, description, tags, and an uploaded image
 * Gets the current activity, if it is ContentActivity then treats the user like an entrant and
 * allows the user to join the waitlist for the event
 * If the current activity is EditEventActivity then treats the user like the organiser and displays
 * options for editing the event
 * Currently does not display options for editing the event
 */

public class EventDetailScreenFragment extends Fragment {

    private FragmentEventDetailScreenBinding binding;
    private String eventId;
    private boolean isOwnedEvent = false;

    public EventDetailScreenFragment() {
        // Required empty public constructor
    }

    public static EventDetailScreenFragment newInstance() {
        EventDetailScreenFragment fragment = new EventDetailScreenFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventDetailScreenFragmentArgs args = EventDetailScreenFragmentArgs.fromBundle(getArguments());
        eventId = args.getEventId();
        isOwnedEvent = args.toBundle().getBoolean("isOwnedEvent", false);

        Log.d("EventDetailScreen", "Event ID: " + eventId + ", isOwnedEvent=" + isOwnedEvent);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = binding.eventDetailScreenBackButton;

        if (getActivity() instanceof EditEventActivity) {
            backButton.setOnClickListener(v -> {
               getActivity().finish();
            });
        } else {
            backButton.setOnClickListener(v -> {
               NavHostFragment.findNavController(EventDetailScreenFragment.this)
                       .navigateUp();
            });
        }

        // Show loading and hide content until it is fetched
        binding.loadingEventDetailScreen.setVisibility(View.VISIBLE);
        binding.contentGroupEventsDetailScreen.setVisibility(View.GONE);

        // Fetch this event and bind
        FirebaseFirestore.getInstance()
            .collection("Event")
            .document(eventId)
            .get()
            .addOnSuccessListener(doc -> {
                bindEvent(doc);

                // Fetch organizerID from Firestore
                String organizerId = doc.getString("organizerID");
                String currentUid = FirebaseAuth.getInstance().getCurrentUser() != null
                        ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                        : null;

                // TESTING, KEEP COMMENTED OUT
                // Toast.makeText(requireContext(), "Organizer ID: " + organizerId, Toast.LENGTH_LONG).show();
                // Toast.makeText(requireContext(), "Current UID: " + currentUid, Toast.LENGTH_LONG).show();

                // Check ownership
                boolean isOwnedEvent = (organizerId != null && organizerId.equals(currentUid));

                if (isOwnedEvent) {
                    binding.navigationBarButton.setText("Edit Event");
                    binding.navigationBarButton.setBackgroundTintList(
                            ContextCompat.getColorStateList(requireContext(), R.color.app_beige)
                    );
                }
                // Hide loading and show content
                binding.loadingEventDetailScreen.setVisibility(View.GONE);
                binding.contentGroupEventsDetailScreen.setVisibility(View.VISIBLE);
            })
            .addOnFailureListener(e -> {
                // Hide loading and show error
                binding.loadingEventDetailScreen.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load event",
                        Toast.LENGTH_LONG).show();
            });
    }

    private void bindEvent(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_LONG).show();
            return;
        }

        // event name & description
        String eventName = doc.getString("name");
        String eventDesc = doc.getString("description");
        // Error Checking for null name or desc. (Don't think we need, may remove later)
        if (eventName == null || eventDesc == null) {
            Toast.makeText(requireContext(), "Missing name or description", Toast.LENGTH_LONG).show();
            return;
        }
        binding.eventNameText.setText(eventName);
        binding.eventDetailsDescText.setText(eventDesc);

        // Fetch tags in DB
        // Get tags
        List<String> tags = (List<String>) doc.get("eventTags");
        if (tags == null)
            tags = new ArrayList<>(); // prevent NullPointerException if there are no tags by making an empty arraylist

        // Debugging
        if (tags.isEmpty()) {
            Log.d("EventDetailScreen", "No tags found");
            return;
        } else {
            Log.d("EventDetailScreen", "Tags loaded: " + tags.toString());
        }

        // Setup RecyclerView
        EventTagsAdapter adapter = new EventTagsAdapter(tags);
        binding.tagsHorizontalRv.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false);
        binding.tagsHorizontalRv.setLayoutManager(layoutManager);
    }
}
