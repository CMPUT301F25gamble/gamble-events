package com.example.eventlotterysystemapplication.View;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.databinding.FragmentCreateOrEditEventBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.installations.FirebaseInstallations;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Allows the user to create and event or edit an event that they have created
 * Prompts user to input all necessary information for creating an event, then adds event
 * to the database
 */

public class CreateOrEditEventFragment extends Fragment {
    private static final String TAG = "CreateOrEditEvent"; // For debugging
    Database database;
    private FragmentCreateOrEditEventBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateOrEditEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = new Database();

        // Create Event
        // Back Button to return to Events page
        binding.createOrEditEventBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                    .navigate(R.id.action_create_or_edit_event_fragment_to_events_ui_fragment);
        });


        // Once done button pressed, update database
        binding.createOrEditEventDoneButton.setOnClickListener(v-> {
            // Get values from EditTexts
            // String userName = binding.nameEditText.getText().toString().trim();
            String eventName = binding.createOrEditEventEventNameEditText.getText().toString().trim();
            String eventDesc = binding.createOrEditEventEventDescEditText.getText().toString().trim();
            String tagsStr = binding.createOrEditEventTagsEditText.getText().toString().trim();
            String eventLocation = binding.createOrEditEventLocationEditText.getText().toString().trim();
            String eventStartTimeStr = binding.createOrEditEventEventStartDateAndTimeEditText.getText().toString().trim();
            String eventEndTimeStr = binding.createOrEditEventEventEndDateAndTimeEditText.getText().toString().trim();
            String regStartTimeStr = binding.createOrEditEventRegistrationStartEditText.getText().toString().trim();
            String regEndTimeStr = binding.createOrEditEventRegistrationEndEditText.getText().toString().trim();
            String invitationAcceptanceDeadlineStr = binding.createOrEditEventInvitationEditText.getText().toString().trim();
            // TODO: Event Poster
            String limitWaitlistStr = binding.createOrEditLimitWaitlistEditText.getText().toString().trim();
            String numOfSelectedEntrantsStr = binding.createOrEditEventSelectedEntrantsNumEditText.getText().toString().trim();
            // TODO: Handle notifs set

           // Check that mandatory fields are filled
            if (eventName.isEmpty()) {
                binding.createOrEditEventEventNameEditText.setError("Event Name is required");
                return;
            }
            if (eventDesc.isEmpty()) {
                binding.createOrEditEventEventDescEditText.setError("Event Description is required");
                return;
            }
            if (eventLocation.isEmpty()) {
                binding.createOrEditEventLocationEditText.setError("Location is required");
                return;
            }
            if (eventStartTimeStr.isEmpty()) {
                binding.createOrEditEventEventStartDateAndTimeEditText.setError("Event Start Date and Time is required");
                return;
            }
            if (eventEndTimeStr.isEmpty()) {
                binding.createOrEditEventEventEndDateAndTimeEditText.setError("Event End Date and Time is required");
                return;
            }
            if (regStartTimeStr.isEmpty()) {
                binding.createOrEditEventRegistrationStartEditText.setError("Registration Start Date and Time is required");
                return;
            }
            if (regEndTimeStr.isEmpty()) {
                binding.createOrEditEventRegistrationEndEditText.setError("Registration End Date and Time is required");
                return;
            }
            if (invitationAcceptanceDeadlineStr.isEmpty()) {
                binding.createOrEditEventInvitationEditText.setError("Invitation Acceptance Deadline is required");
                return;
            }
            if (numOfSelectedEntrantsStr.isEmpty()) {
                binding.createOrEditEventSelectedEntrantsNumEditText.setError("Number of Selected Entrants is required");
                return;
            }

            // Parse dateTime types
            LocalDateTime eventStartTime;
            LocalDateTime eventEndTime;
            LocalDateTime regStartTime;
            LocalDateTime regEndTime;
            LocalDateTime invitationAcceptanceDeadline;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                eventStartTime = DateTimeFormatter(eventStartTimeStr);
                eventEndTime = DateTimeFormatter(eventEndTimeStr);
                regStartTime = DateTimeFormatter(regStartTimeStr);
                regEndTime = DateTimeFormatter(regEndTimeStr);
                invitationAcceptanceDeadline = DateTimeFormatter(invitationAcceptanceDeadlineStr);
            } else {
                invitationAcceptanceDeadline = null;
                eventStartTime = null;
                eventEndTime = null;
                regStartTime = null;
                regEndTime = null;
            }

            // Check if any field failed parsing
            if (eventStartTime == null || eventEndTime == null || regStartTime == null || regEndTime == null || invitationAcceptanceDeadline == null) {
                return; // stop here, let user fix inputs
            }


            // Check fields that should be integers
            int limitWaitlistValue;
            if (!limitWaitlistStr.isEmpty()) {
                limitWaitlistValue = Integer.parseInt(limitWaitlistStr);
            } else {
                limitWaitlistValue = -1; // means no limit
            }
            int numOfSelectedEntrantsValue = Integer.parseInt(numOfSelectedEntrantsStr);

            // Parse tags; split by commas
            ArrayList<String> tagsList = new ArrayList<>();
            if (!tagsStr.isEmpty()) {
                String[] tagsArray = tagsStr.split(",");
                for (String tag : tagsArray) {
                    String trimmedTag = tag.trim();
                    if (!trimmedTag.isEmpty()) {
                        tagsList.add(trimmedTag);
                    }
                }
            }

            // Get device's actual id to get the user, then update database
            FirebaseInstallations.getInstance().getId()
                    .addOnSuccessListener(deviceId -> {

                        // Fetch user
                        database.getUserFromDeviceID(deviceId, task -> {
                            if (task.isSuccessful()) {
                                // Create event class
                                Event event = new Event();

                                // Set event values
                                event.setName(eventName);
                                event.setDescription(eventDesc);
                                event.setPlace(eventLocation);

                                // Set tags
                                event.setEventTags(tagsList);

                                // Set timestamps
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    Timestamp eventStartTimeTS = new Timestamp(eventStartTime.atZone(ZoneId.systemDefault()).toInstant());
                                    event.setEventStartTimeTS(eventStartTimeTS);

                                    Timestamp eventEndTimeTS = new Timestamp(eventEndTime.atZone(ZoneId.systemDefault()).toInstant());
                                    event.setEventEndTimeTS(eventEndTimeTS);

                                    Timestamp regStartTimeTS = new Timestamp(regStartTime.atZone(ZoneId.systemDefault()).toInstant());
                                    event.setRegistrationStartTimeTS(regStartTimeTS);

                                    Timestamp regEndTimeTS = new Timestamp(regEndTime.atZone(ZoneId.systemDefault()).toInstant());
                                    event.setRegistrationEndTimeTS(regEndTimeTS);

                                    Timestamp invitationAcceptanceDeadlineTS = new Timestamp(invitationAcceptanceDeadline.atZone(ZoneId.systemDefault()).toInstant());
                                    event.setInvitationAcceptanceDeadlineTS(invitationAcceptanceDeadlineTS);
                                }

                                // Set event int values
                                if (limitWaitlistValue > 0){
                                    event.setMaxWaitingListCapacity(limitWaitlistValue);
                                }
                                event.setMaxFinalListCapacity(numOfSelectedEntrantsValue);

                                // Set organizer ID
                                User currentUser = task.getResult();
                                event.setOrganizerID(currentUser.getUserID());

                                // add event
                                database.addEvent(event, addEventTask -> {
                                    if (addEventTask.isSuccessful()) {
                                        Log.d(TAG, "Added event!");
                                        // Return to events page
                                        NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                                                .navigate(R.id.action_create_or_edit_event_fragment_to_events_ui_fragment);
                                    } else {
                                        Log.d(TAG, "Failed to add event");
                                    }

                                });
                            }
                        });

                    });
        });
    }

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
}
