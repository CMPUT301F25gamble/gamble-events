package com.example.eventlotterysystemapplication;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventlotterysystemapplication.databinding.FragmentEventDetailScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class EventDetailScreen extends Fragment {

    private FragmentEventDetailScreenBinding binding;
    private String eventId;
    private final String TAG = "EventDetailScreen";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button navigates to Events UI fragment
        binding.eventDetailScreenBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EventDetailScreen.this)
                    .navigate(R.id.action_event_detail_screen_to_events_ui_fragment);
        });

        // get the docId passed from the list screen
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(requireContext(), "Missing eventId", Toast.LENGTH_LONG).show();
        }

        // Show loading and hide content until it is fetched
        binding.loadingEventDetailScreen.setVisibility(View.VISIBLE);
        binding.contentGroupEventsDetailScreen.setVisibility(View.GONE);

        // Obtain deviceID
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceID -> {
            Log.d(TAG, "Device ID obtained is: " + deviceID);
            assert deviceID != null;

            // Fetch this event and bind
            getEvent(task -> {
                if (task.isSuccessful()) {
                    // Grab event and bind it
                    Event event = task.getResult();
                    bindEvent(event);

                    // Hide loading and show content
                    binding.loadingEventDetailScreen.setVisibility(View.GONE);
                    binding.contentGroupEventsDetailScreen.setVisibility(View.VISIBLE);


                    // Update the "looks" of the button based on if the user is in the event or not
                    getUserFromDeviceID(deviceID, taskUser -> {
                        if (taskUser.isSuccessful()) {
                            User user = taskUser.getResult();
                            Log.d(TAG, "Grabbed user is: " + user);
                            Log.d(TAG, "Initial Waiting list: " + event.getEntrantList().getWaiting());

                            changeWaitlistBtn(event.getEntrantList().getWaiting().contains(user));
                        } else {
                            // Failed to load user; hide loading and show error
                            binding.loadingEventDetailScreen.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Failed to fetch user from device ID",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Failed to load event; hide loading and show error
                    binding.loadingEventDetailScreen.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to load event",
                            Toast.LENGTH_LONG).show();
                }
            });


            // Add joining/leaving waitlist functionality to button
            binding.navigationBarButton.setOnClickListener(v -> {
               getEvent(taskEvent -> {
                   if (taskEvent.isSuccessful()) {
                       Event event = taskEvent.getResult();
                       getUserFromDeviceID(deviceID, taskUser -> {
                           if (taskUser.isSuccessful()) {
                               // Grab user and check if already in waiting list
                               User user = taskUser.getResult();
                               if (!event.getEntrantList().getWaiting().contains(user)) {
                                   // User is not in waiting list, so join the waitlist
                                   event.joinWaitingList(user);
                                   changeWaitlistBtn(true);
                               } else {
                                   // User is in waiting list, so leave the waitlist
                                   event.leaveWaitingList(user);
                                   changeWaitlistBtn(false);
                               }
                               Log.d(TAG, "After button press, Waiting list: " + event.getEntrantList().getWaiting());
                           } else {
                               // Failed to obtain user; hide loading and show error
                               binding.loadingEventDetailScreen.setVisibility(View.GONE);
                               Toast.makeText(requireContext(), "Failed to obtain user from device ID",
                                       Toast.LENGTH_LONG).show();
                           }
                       });

                   } else {
                       // Failed to load event; hide loading and show error
                       binding.loadingEventDetailScreen.setVisibility(View.GONE);
                       Toast.makeText(requireContext(), "Failed to load event",
                               Toast.LENGTH_LONG).show();
                   }
               });
            });

        });

    }

    /**
     * Updates the waitlist button colors and text based on if the user is in the waitlist
     * @param userInWaitlist Boolean whether user is in waitlist of event or not
     */
    private void changeWaitlistBtn(boolean userInWaitlist) {
        if (userInWaitlist) {
            // User is in waiting list already so change button to leave waitlist
            binding.navigationBarButton.setText(R.string.leave_waitlist_text);
            binding.navigationBarButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red));
        } else {
            // User is not in waiting list
            binding.navigationBarButton.setText(R.string.join_waitlist_text);
            binding.navigationBarButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green));
        }
    }

    /**
     * Wrapper function for calling getUserFromDeviceID on the database
     * @param deviceID
     * @param callback
     */
    private void getUserFromDeviceID(String deviceID, OnCompleteListener<User> callback) {
        Database db = new Database();

        db.getUserFromDeviceID(deviceID, callback);
    }

    private void getEvent(OnCompleteListener<Event> callback) {
        Database db = new Database();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API level must be 26 or above
            db.getEvent(eventId, callback);
        }
    }

    private void bindEvent(Event event) {
        // Event name & description
        String eventName = event.getName();
        String eventDesc = event.getDescription();
        // Error Checking for null name or desc. (Don't think we need, may remove later)
        if (eventName == null || eventDesc == null) {
            Toast.makeText(requireContext(), "Missing name or description", Toast.LENGTH_LONG).show();
            return;
        }
        binding.eventNameText.setText(eventName);
        binding.eventDetailsDescText.setText(eventDesc);

        // Fetch tags from event
        // Get tags
        List<String> tags = event.getEventTags();
        if (tags == null) tags = new ArrayList<>(); // prevent NullPointerException if there are no tags by making an empty arraylist

        // Debugging
        if (tags.isEmpty()) {
            Toast.makeText(requireContext(), "No tags found", Toast.LENGTH_SHORT).show();
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
