package com.example.eventlotterysystemapplication.View;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.EntrantList;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.ImageStorage;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.databinding.FragmentCreateOrEditEventBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.installations.FirebaseInstallations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    private File posterFile;
    private final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private String eventId = null;

    // Initialize registerForActivityResult before the fragment is created
    // Launcher that takes an image from the user
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            o -> {
                if (o.getResultCode() == Activity.RESULT_OK && o.getData() != null && o.getData().getData() != null) {
                    Uri posterUri = o.getData().getData();
                    posterFile = getFileFromUri(posterUri);

                    if (posterFile == null) {
                        Log.e(TAG, "Image could not be converted to file");
                        Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (posterFile.length() > MAX_FILE_SIZE) {
                        Toast.makeText(getContext(), "Event Poster Image is Too Large (> 5 MB)", Toast.LENGTH_SHORT).show();
                        posterFile = null;
                        return;
                    }

                    Log.d(TAG, "poster file size: " + (float) (posterFile.length()) / 1024.0 / 1024.0 + "MB");
                    Toast.makeText(getContext(), "Event Poster Image Successfully Uploaded", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get eventId if the organizer is editing the event
        if (getArguments() != null) {
            eventId = CreateOrEditEventFragmentArgs.fromBundle(getArguments()).getEventId();
        }
    }

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

        // Change title/button text depending on if the user is editing the event or creating one
        if (eventId != null) {
            binding.createOrEditEventTitle.setText(R.string.edit_event_title_text);
            binding.createOrEditEventDoneButton.setText(R.string.done_editing_event_text);
        } else {
            binding.createOrEditEventTitle.setText(R.string.create_event_title_text);
            binding.createOrEditEventDoneButton.setText(R.string.done_creating_event_text);
        }

        // Create Event
        // Back Button to return to Events page
        binding.createOrEditEventBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                    .navigateUp();
        });

        // Upload Poster Button Listener
        binding.uploadPhotoButton.setOnClickListener(v -> userUploadEventPoster());

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

                                // Set entrant list
                                event.setEntrantList(new EntrantList());

                                // add event
                                database.addEvent(event, addEventTask -> {
                                    if (addEventTask.isSuccessful()) {
                                        // Upload event poster to storage bucket
                                        if (posterFile != null) {
                                            Log.d(TAG, "Adding poster image to event...");
                                            uploadEventPosterToStorage(event);
                                        } else {
                                            // Return to events page if no poster was uploaded
                                            Log.d(TAG, "No poster after adding event, going straight to event page...");
                                            NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                                                    .navigate(R.id.action_create_or_edit_event_fragment_to_events_ui_fragment);
                                        }
                                    } else {
                                        Log.d(TAG, "Failed to add event");
                                    }

                                });
                            }
                        });

                    });
        });
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
     * Event listener helper that uploads the event poster image using intents
     */
    private void userUploadEventPoster() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        // Let Android OS allow user to pick an image file to upload
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /**
     * Uploads the event poster image file to the storage bucket
     * @param event Event object needed for eventId and setting downloadUrl
     */
    private void uploadEventPosterToStorage(Event event) {
        if (posterFile == null) {
            Toast.makeText(getContext(), "Failed to convert URI to image file for upload", Toast.LENGTH_SHORT).show();
            // Return to events page anyways as event is created
            NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                    .navigate(R.id.action_create_or_edit_event_fragment_to_events_ui_fragment);
            return;
        }

        String eventID = event.getEventID();
        ImageStorage imageStorage = ImageStorage.getInstance();
        imageStorage.uploadEventPoster(
                eventID,
                posterFile,
                imageTask -> {
                    if (imageTask.isSuccessful()) {
                        Uri downloadUrl = imageTask.getResult();
                        String posterDownloadUrl = downloadUrl.toString();
                        event.setEventPosterUrl(posterDownloadUrl);

                        database.updateEvent(event, task -> {
                            if (task.isSuccessful()) {
                                // Return to events page
                                NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                                        .navigate(R.id.action_create_or_edit_event_fragment_to_events_ui_fragment);
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "Poster upload to Storage Bucket failed.", Toast.LENGTH_SHORT).show();
                        // Return to events page anyways as event is created
                        NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                                .navigate(R.id.action_create_or_edit_event_fragment_to_events_ui_fragment);
                    }
                }
        );
    }

    /**
     * Creates a file from the given uri
     * @param uri The file uri
     * @return File that was linked at uri
     */
    private File getFileFromUri(Uri uri) {
        if (getContext() == null) {
            Log.e(TAG, "getFileFromUri: no context");
            return null;
        }

        try {
            // Create temp image file for poster
            File tempFile = File.createTempFile("temp_image", ".jpg");

            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e(TAG, "getFileFromUri: no input stream");
                return null;
            }

            OutputStream outputStream = new FileOutputStream(tempFile);

            // Copy the data from the file pointed to by the Uri to the temp file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}
