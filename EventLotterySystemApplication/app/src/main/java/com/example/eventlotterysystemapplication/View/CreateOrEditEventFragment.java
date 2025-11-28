package com.example.eventlotterysystemapplication.View;

import static androidx.core.content.ContextCompat.getColor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Controller.LotteryDrawScheduler;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.ImageStorage;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentCreateOrEditEventBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.Timestamp;
import com.google.firebase.installations.FirebaseInstallations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private Event fetchedEvent = null;

    private PlacesClient placesClient;
    private AutoCompleteTextView addressAutoComplete;
    private ArrayAdapter<String> adapter;

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
        View root = binding.getRoot();
        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(root.getContext(),"AIzaSyC8_6AhPVJRqUAeEY5xkhcxdZX7GX2EBT8",Locale.CANADA);
        }
        placesClient = Places.createClient(requireContext());

        addressAutoComplete = root.findViewById(R.id.createOrEditEventLocationTextView);

        adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        addressAutoComplete.setAdapter(adapter);

        // Listen for text changes
        addressAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    fetchPredictions(s.toString());
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        return binding.getRoot();
    }

    private void fetchPredictions(String query) {
        // Build request
        FindAutocompletePredictionsRequest request =
                FindAutocompletePredictionsRequest.builder()
                        .setQuery(query)
                        .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    List<String> suggestions = new ArrayList<>();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        suggestions.add(prediction.getFullText(null).toString());
                    }
                    adapter.clear();
                    adapter.addAll(suggestions);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(exception -> {
                    Log.e("Places", "Prediction error", exception);
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = Database.getDatabase();
        // Change title/button text depending on if the user is editing the event or creating one
        if (eventId != null) {
            binding.createOrEditEventTitle.setText(R.string.edit_event_title_text);
            binding.createOrEditEventDoneButton.setEnabled(true);
            binding.createOrEditEventDoneButton.setText(R.string.done_editing_event_text);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Fetch event from db and pre-fill the input fields with the event details
                fetchEvent();
            }
        } else {
            binding.createOrEditEventTitle.setText(R.string.create_event_title_text);
            binding.createOrEditEventDoneButton.setText(R.string.create_event_title_text);
        }

        // Create Event
        // Back Button to return to Events page
        binding.createOrEditEventBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                    .navigateUp();
        });

        //location autocomplete


        // Upload Poster Button Listener
        binding.uploadPhotoButton.setOnClickListener(v -> userUploadEventPoster());



        // Once done button pressed, update database
        binding.createOrEditEventDoneButton.setOnClickListener(v-> {

            // Get values from EditTexts
            // String userName = binding.nameEditText.getText().toString().trim();
            String eventName = binding.createOrEditEventEventNameEditText.getText().toString().trim();
            String eventDesc = binding.createOrEditEventEventDescEditText.getText().toString().trim();
            String tagsStr = binding.createOrEditEventTagsEditText.getText().toString().trim();
            String eventLocation = binding.createOrEditEventLocationTextView.getText().toString().trim();
            String eventStartTimeStr = binding.createOrEditEventEventStartDateAndTimeEditText.getText().toString().trim();
            String eventEndTimeStr = binding.createOrEditEventEventEndDateAndTimeEditText.getText().toString().trim();
            String regStartTimeStr = binding.createOrEditEventRegistrationStartEditText.getText().toString().trim();
            String regEndTimeStr = binding.createOrEditEventRegistrationEndEditText.getText().toString().trim();
            String invitationAcceptanceDeadlineStr = binding.createOrEditEventInvitationEditText.getText().toString().trim();
            String limitWaitlistStr = binding.createOrEditLimitWaitlistEditText.getText().toString().trim();
            String numOfSelectedEntrantsStr = binding.createOrEditEventSelectedEntrantsNumEditText.getText().toString().trim();

           // Check that mandatory fields are filled
            if (eventName.isEmpty()) {
                binding.createOrEditEventEventNameEditText.setError("Event Name is required");
                binding.createOrEditEventEventNameEditText.requestFocus();
                return;
            }
            if (eventDesc.isEmpty()) {
                binding.createOrEditEventEventDescEditText.setError("Event Description is required");
                binding.createOrEditEventEventDescEditText.requestFocus();
                return;
            }
            if (eventLocation.isEmpty()) {
                binding.createOrEditEventLocationTextView.setError("EntrantLocation is required");
                binding.createOrEditEventLocationTextView.requestFocus();
                return;
            }
            if (eventStartTimeStr.isEmpty()) {
                binding.createOrEditEventEventStartDateAndTimeEditText.setError("Event Start Date and Time is required");
                binding.createOrEditEventEventStartDateAndTimeEditText.requestFocus();
                return;
            }
            if (eventEndTimeStr.isEmpty()) {
                binding.createOrEditEventEventEndDateAndTimeEditText.setError("Event End Date and Time is required");
                binding.createOrEditEventEventEndDateAndTimeEditText.requestFocus();
                return;
            }
            if (regStartTimeStr.isEmpty()) {
                binding.createOrEditEventRegistrationStartEditText.setError("Registration Start Date and Time is required");
                binding.createOrEditEventRegistrationStartEditText.requestFocus();
                return;
            }
            if (regEndTimeStr.isEmpty()) {
                binding.createOrEditEventRegistrationEndEditText.setError("Registration End Date and Time is required");
                binding.createOrEditEventRegistrationEndEditText.requestFocus();
                return;
            }
            if (invitationAcceptanceDeadlineStr.isEmpty()) {
                binding.createOrEditEventInvitationEditText.setError("Invitation Acceptance Deadline is required");
                binding.createOrEditEventInvitationEditText.requestFocus();
                return;
            }
            if (numOfSelectedEntrantsStr.isEmpty()) {
                binding.createOrEditEventSelectedEntrantsNumEditText.setError("Number of Selected Entrants is required");
                binding.createOrEditEventSelectedEntrantsNumEditText.requestFocus();
                return;
            }

            // Parse dateTime types
            LocalDateTime eventStartTime;
            LocalDateTime eventEndTime;
            LocalDateTime regStartTime;
            LocalDateTime regEndTime;
            LocalDateTime invitationAcceptanceDeadline;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                eventStartTime = DateTimeFormatter(binding.createOrEditEventEventStartDateAndTimeEditText);
                eventEndTime = DateTimeFormatter(binding.createOrEditEventEventEndDateAndTimeEditText);
                regStartTime = DateTimeFormatter(binding.createOrEditEventRegistrationStartEditText);
                regEndTime = DateTimeFormatter(binding.createOrEditEventRegistrationEndEditText);
                invitationAcceptanceDeadline = DateTimeFormatter(binding.createOrEditEventInvitationEditText);
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

            if(!validateDateCompare(regStartTime,regEndTime)){
                binding.createOrEditEventRegistrationEndEditText.setError("Registration End Date and Time should be after Registration Start Date and Time.");
                binding.createOrEditEventRegistrationEndEditText.requestFocus();
                return;
            }

            if(!validateDateCompare(regEndTime,invitationAcceptanceDeadline)){
                binding.createOrEditEventRegistrationEndEditText.setError("Registration End Date and Time should be before Event Acceptance Deadline.");
                binding.createOrEditEventRegistrationEndEditText.requestFocus();
                return;
            }

            if(!validateDateCompare(invitationAcceptanceDeadline,eventStartTime)){
                binding.createOrEditEventInvitationEditText.setError("Invitation Acceptance Date and Time should be before Event Start Date and Time.");
                binding.createOrEditEventInvitationEditText.requestFocus();
                return;
            }

            if(!validateDateCompare(eventStartTime,eventEndTime)){
                binding.createOrEditEventEventEndDateAndTimeEditText.setError("Event End Date and Time should be after Event Start Date and Time.");
                binding.createOrEditEventEventEndDateAndTimeEditText.requestFocus();
                return;
            }

            // Check fields that should be integers
            int limitWaitlistValue;
            if (!limitWaitlistStr.isEmpty()) {
                limitWaitlistValue = Integer.parseInt(limitWaitlistStr);
                if (limitWaitlistValue <= 0) {
                    binding.createOrEditEventLimitWaitlistText.setError("Invalid wait list capacity");
                    binding.createOrEditEventLimitWaitlistText.requestFocus();
                    return;
                }
            } else {
                limitWaitlistValue = -1; // means no limit
            }
            int numOfSelectedEntrantsValue = Integer.parseInt(numOfSelectedEntrantsStr);
            if (numOfSelectedEntrantsValue <= 0) {
                binding.createOrEditEventSelectedEntrantsNumEditText.setError("Invalid number of selected entrants");
                binding.createOrEditEventSelectedEntrantsNumEditText.requestFocus();
                return;
            }

            v.setEnabled(false);

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
                                CheckBox geolocationCheckbox = view.findViewById(R.id.checkbox_enableGeolocation);
                                event.setGeolocationRequirement(geolocationCheckbox.isChecked());
                                // Set event int values
                                if (limitWaitlistValue > 0){
                                    // If editing event then can only set the max waitlist capacity to be larger than what it originally was
                                    // Otherwise if creating an event then max waitlist capacity just has to be a value larger than 0
                                    if (fetchedEvent == null || limitWaitlistValue > fetchedEvent.getMaxWaitingListCapacity()) {
                                        event.setMaxWaitingListCapacity(limitWaitlistValue);
                                    } else {
                                        event.setMaxWaitingListCapacity(fetchedEvent.getMaxWaitingListCapacity());
                                    }
                                }
                                if (limitWaitlistValue == -1) {
                                    event.setMaxWaitingListCapacity(limitWaitlistValue); // Set default
                                }
                                event.setMaxFinalListCapacity(numOfSelectedEntrantsValue);

                                // Set organizer ID
                                User currentUser = task.getResult();
                                event.setOrganizerID(currentUser.getUserID());

                                // update event if editing the event
                                if (fetchedEvent != null) {
                                    event.setEventID(fetchedEvent.getEventID());
                                    event.setEventPosterUrl(fetchedEvent.getEventPosterUrl());

                                    database.updateEvent(event, updateEventTask ->{
                                        if (updateEventTask.isSuccessful()) {
                                            // Upload event poster to storage bucket
                                            if (posterFile != null) {
                                                Log.d(TAG, "Adding poster image to event...");
                                                uploadEventPosterToStorage(event);
                                            } else {
                                                // Return to events page if no poster was uploaded
                                                Log.d(TAG, "No poster after adding event, going straight to event page...");
                                                Bundle args = new Bundle();
//                                                navigateFromCreateEditEvent(event);
                                                NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                                                        .navigateUp();
                                            }
                                            LotteryDrawScheduler lotteryDrawScheduler = new LotteryDrawScheduler();
                                            lotteryDrawScheduler.scheduleUpdateLotteryDraw(v.getContext(),event);
                                        } else {
                                            Log.d(TAG, "Failed to add event");
                                        }
                                    });
                                    return;
                                }

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
//                                            navigateFromCreateEditEvent(event);
                                            NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                                                    .navigate(R.id.action_create_or_edit_event_fragment_to_events_ui_fragment);
                                        }
                                        LotteryDrawScheduler lotteryDrawScheduler = new LotteryDrawScheduler();
                                        lotteryDrawScheduler.scheduleNewLotteryDraw(v.getContext(),event);
                                    } else {
                                        Log.d(TAG, "Failed to add event");
                                    }

                                });
                            }
                        });

                    });
        });

        EditText startDateTime = view.findViewById(R.id.createOrEditEventEventStartDateAndTimeEditText);
        attachDateTimePicker(startDateTime,view);
        EditText endDateTime = view.findViewById(R.id.createOrEditEventEventEndDateAndTimeEditText);
        attachDateTimePicker(endDateTime,view);
        EditText regStartDateTime = view.findViewById(R.id.createOrEditEventRegistrationStartEditText);
        attachDateTimePicker(regStartDateTime,view);
        EditText regEndDateTime = view.findViewById(R.id.createOrEditEventRegistrationEndEditText);
        attachDateTimePicker(regEndDateTime,view);
        EditText invitationDateTime = view.findViewById(R.id.createOrEditEventInvitationEditText);
        attachDateTimePicker(invitationDateTime,view);
    }

    /**
     * Converts the user inputted string into a LocalDateTime object
     * @param dateField The user inputted datetime string
     * @return A LocalDateTime object
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime DateTimeFormatter(EditText dateField) {
        String dateTimeStr = dateField.getText().toString().trim();
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime eventDateTime = null;
        try {
                eventDateTime = LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            dateField.setError("Invalid date/time format. Use yyyy-MM-dd HH:mm");
            dateField.requestFocus();
            //Toast.makeText(getContext(), "Invalid date/time format. Use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show();
            return null;
        }
        return eventDateTime;
    }

    /**
     * Fetches an event from the database and stores it in fetchedEvent. This function should only
     * be used when user is editing the event
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchEvent() {
        if (eventId == null) return;

        database.getEvent(eventId, task -> {
            if (task.isSuccessful()) {
                fetchedEvent = task.getResult();
                updateFields(fetchedEvent);
            } else {
                Toast.makeText(getContext(), "Failed to prepopulate fields for editing event", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to fetch event for editing", task.getException());
            }
        });
    }

    /**
     * Updates the input edit text fields with an event. This function should only be used when user
     * is editing the event. Additionally it sets event end time and registration start time to be
     * an empty string if it is null for backwards compatibility since we didn't make those edit
     * texts fields prior to November 14, 2025 1:00pm
     * @param event Fetched event to populate fields with
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateFields(Event event) {
        // Set text fields
        binding.createOrEditEventEventNameEditText.setText(event.getName());
        binding.createOrEditEventEventDescEditText.setText(event.getDescription());
        // Set tags to be a comma separated string
        if (event.getEventTags() != null && !event.getEventTags().isEmpty()) {
            String tagsStr = String.join(",", event.getEventTags());
            binding.createOrEditEventTagsEditText.setText(tagsStr);
        }
        binding.createOrEditEventLocationTextView.setText(event.getPlace());

        // Set dates
        binding.createOrEditEventEventStartDateAndTimeEditText.setText(
                event.getEventStartTime() == null ? "" : event.getEventStartTime().toString().replace("T", " ")
        );
        disableDateFieldIfPastDate(event.getEventStartTime(), binding.createOrEditEventEventStartDateAndTimeEditText);

        binding.createOrEditEventEventEndDateAndTimeEditText.setText(
                event.getEventEndTime() == null ? "" : event.getEventEndTime().toString().replace("T", " ")
        );
        disableDateFieldIfPastDate(event.getEventEndTime(), binding.createOrEditEventEventEndDateAndTimeEditText);

        binding.createOrEditEventRegistrationStartEditText.setText(
                event.getRegistrationStartTime() == null ? "" : event.getRegistrationStartTime().toString().replace("T", " ")
        );
        disableDateFieldIfPastDate(event.getRegistrationStartTime(), binding.createOrEditEventRegistrationStartEditText);

        binding.createOrEditEventRegistrationEndEditText.setText(
                event.getRegistrationEndTime() == null ? "" : event.getRegistrationEndTime().toString().replace("T", " ")
        );
        disableDateFieldIfPastDate(event.getRegistrationEndTime(), binding.createOrEditEventRegistrationEndEditText);

        binding.createOrEditEventInvitationEditText.setText(
                event.getInvitationAcceptanceDeadline() == null ? "" : event.getInvitationAcceptanceDeadline().toString().replace("T", " ")
        );
        disableDateFieldIfPastDate(event.getInvitationAcceptanceDeadline(), binding.createOrEditEventInvitationEditText);

        // Set waitlists
        if (event.getMaxWaitingListCapacity() > 0) {
            binding.createOrEditLimitWaitlistEditText.setText(String.valueOf(event.getMaxWaitingListCapacity()));
        }
        binding.createOrEditEventSelectedEntrantsNumEditText.setText(String.valueOf(event.getMaxFinalListCapacity()));
        Log.d("Geolocation Req", Boolean.toString(event.isGeolocationRequirement()));
        binding.checkboxEnableGeolocation.setChecked(event.isGeolocationRequirement());

    }

    /**
     * Disables a datetime edit text field if current datetime (today) is past the given date time.
     * This function should only be used when the user is editing an event and not creating one.
     * @param dateTime The edit text's given datetime (e.g. registration end datetime)
     * @param editText The edit text to be disabled
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void disableDateFieldIfPastDate(LocalDateTime dateTime, EditText editText) {
        // Disable editing a registration event's date field (e.g. registration start date)
        // if it is past that datetime
        if (dateTime == null) return;

        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(dateTime)) {
            editText.setBackgroundColor(getColor(requireContext(), R.color.grey));
            editText.setEnabled(false);
        }
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
                    .navigate(R.id.action_create_or_edit_event_fragment_to_my_events_fragment);
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
//                                navigateFromCreateEditEvent(event);
                                NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                                        .navigate(R.id.action_create_or_edit_event_fragment_to_events_ui_fragment);
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "Poster upload to Storage Bucket failed.", Toast.LENGTH_SHORT).show();
                        // Return to events page anyways as event is created
//                        navigateFromCreateEditEvent(event);
                        NavHostFragment.findNavController(CreateOrEditEventFragment.this)
                                .navigate(R.id.action_create_or_edit_event_fragment_to_events_ui_fragment);
                    }
                }
        );
    }

//    private void navigateFromCreateEditEvent(Event event){
//        String argEventId=eventId!=null? eventId:event!=null?event.getEventID():null;
//        if(argEventId!=null) {
//                        Bundle args = new Bundle();
//            args.putString("eventId", argEventId);
//                        NavHostFragment.findNavController(CreateOrEditEventFragment.this)
//                                .navigate(R.id.action_create_or_edit_event_fragment_to_my_event_detail_screen,args);
//        }else{
//            NavHostFragment.findNavController(CreateOrEditEventFragment.this)
//                    .navigate(R.id.action_create_or_edit_event_fragment_to_my_events_fragment);
//                    }
//    }

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

    private void attachDateTimePicker(EditText editText, View view) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    view.getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                getContext(),
                                (timeView, hourOfDay, minute) -> {
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                    editText.setText(sdf.format(calendar.getTime()));
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        );
                        timePickerDialog.show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private boolean validateDateCompare(LocalDateTime startDateTime, LocalDateTime endDateTime){
        boolean valid = true;
        if (startDateTime!=null && endDateTime!=null && !endDateTime.isAfter(startDateTime)) {
            valid = false;
        }
        return valid;
    }

}
