package com.example.eventlotterysystemapplication.View;

import  android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Controller.AdminActivity;
import com.example.eventlotterysystemapplication.Controller.ContentActivity;
import com.example.eventlotterysystemapplication.Model.Admin;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Entrant;
import com.example.eventlotterysystemapplication.Model.EntrantStatus;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.EntrantLocation;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.Controller.EditEventActivity;
import com.example.eventlotterysystemapplication.Controller.EventTagsAdapter;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.SharedUserViewModel;
import com.example.eventlotterysystemapplication.databinding.FragmentEventDetailScreenBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.installations.FirebaseInstallations;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

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
    private String userId;
    private boolean isOwnedEvent = false;
    private final String TAG = "EventDetailScreen";

    // Used for ADMIN control
    private String organizerID;
    private boolean isAdminMode;
    private Event event;
    private Entrant entrant;

    private User currentUser;


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
        //isOwnedEvent = args.toBundle().getBoolean("isOwnedEvent", false);
        Log.d(TAG, "Event ID: " + eventId + ", isOwnedEvent=" + isOwnedEvent);
        if(currentUser==null) {
            getCurrentUser(task -> {
                if (task.isSuccessful()) {
                    currentUser = task.getResult();
                } else {
                    Log.e(TAG, "Failed to get current user");
                }
            });
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailScreenBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isAdminMode = AdminSession.getAdminMode();
        userId = AdminSession.getSelectedUserId();
        //currentUser = getUserFromDeviceID();
        ImageButton backButton = binding.eventDetailScreenBackButton;

        if (getActivity() instanceof EditEventActivity) {
            backButton.setOnClickListener(v -> {
                getActivity().finish();
            });
        } else if (getActivity() instanceof AdminActivity) {
            backButton.setOnClickListener(v -> {
                NavHostFragment.findNavController(this)
                        .navigateUp();
            });
        } else {
            backButton.setOnClickListener(v -> {
                NavHostFragment.findNavController(this)
                        .navigateUp();
            });
        }

        // Show loading and hide content until it is fetched
        binding.loadingEventDetailScreen.setVisibility(View.VISIBLE);
        binding.contentGroupEventsDetailScreen.setVisibility(View.GONE);
        binding.contentGroupAdminEventsDetailScreen.setVisibility(View.GONE);


        // Fetch this event and bind
        Database.getDatabase().getEvent(eventId, task -> {
            if (task.isSuccessful()) {
                // Grab event and bind it
                event = task.getResult();

                // Hide image remove button if poster URL is null
                if (event.getEventPosterUrl() == null) {
                    binding.removeImageButton.setVisibility(View.GONE);
                }

                // Checking event geolocation requirements
                if(event.isGeolocationRequirement()) {
                    if (ContextCompat.checkSelfPermission(binding.getRoot().getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                1001);
                    }
                }

                organizerID = event.getOrganizerID(); // Used for admin control
                if(!isOwnedEvent && currentUser!=null) {
                    isOwnedEvent =  currentUser.getUserID().equals(organizerID);
                }
                Log.d(TAG, "Event retrieved is: " + event);
                bindEvent(event);

                entrant = event.genEntrantIfExists(currentUser);

                // Update the waitlist button colors and text based on if the user is in the waitlist
                changeWaitlistBtn(entrant != null &&
                        entrant.getStatus() == EntrantStatus.WAITING);


                binding.loadingEventDetailScreen.setVisibility(View.GONE);
                if (isAdminMode) {
                    binding.contentGroupAdminEventsDetailScreen.setVisibility(View.VISIBLE);
                    binding.contentGroupEventsDetailScreen.setVisibility(View.VISIBLE);
                    // Hide accept/decline chosen entrant buttons
                    binding.contentGroupChosenEntrant.setVisibility(View.GONE);
                    // Hide join waitlist/edit event button
                    binding.navigationBarButton.setVisibility(View.GONE);
                    // Hide generate QR Code button logic is done when db called
                    binding.generateQRCodeButton.setVisibility(View.GONE);
                } else {
                    // Show join waitlist/edit event button
                    binding.contentGroupEventsDetailScreen.setVisibility(View.VISIBLE);
                    showGenerateQRCodeButton();
                    // If the user is in the chosen list, show the chosen button
                    if (entrant != null && entrant.getStatus() == EntrantStatus.CHOSEN) {
                        showChosenEntrantButtons(entrant.getStatus());
                    }
                    // If user status == finalized, display finalized text
                    else if (entrant != null && entrant.getStatus() == EntrantStatus.FINALIZED) {
                        showFinalizedOrCancelledText(entrant.getStatus());
                    }
                    // If user status == cancelled, display cancelled text
                    else if (entrant != null && entrant.getStatus() == EntrantStatus.CANCELLED) {
                        showFinalizedOrCancelledText(entrant.getStatus());
                    }

                    // Check if registration has ended
                    LocalDateTime timeNow =  LocalDateTime.now();
                    LocalDateTime registrationEndTime = event.getRegistrationEndTime();
                    if (timeNow.isAfter(registrationEndTime) && !isOwnedEvent) {
                        binding.registrationClosedText.setVisibility(View.VISIBLE);
                        binding.navigationBarButton.setVisibility(View.GONE);
                    }
                }
            } else {
                // Failed to load event; hide loading and show error
                Log.e(TAG, "Failed to load event, ") ;
                binding.loadingEventDetailScreen.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load event",
                        Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(EventDetailScreenFragment.this)
                        .navigate(R.id.events_ui_fragment);
            }
        });

        // Chosen Entrant Buttons //
        // Accept Button
        binding.acceptChosenEntrantButton.setOnClickListener(v -> {
            // Accept invitation
            entrant.setStatus(EntrantStatus.FINALIZED);
            binding.contentGroupChosenEntrant.setVisibility(View.GONE);

            // Display status on screen
            showFinalizedOrCancelledText(EntrantStatus.FINALIZED);

            // Update DB
            updateEventDB(event);
        });
        // Decline Button
        binding.declineChosenEntrantButton.setOnClickListener(v -> {
            // Decline invitation
            entrant.setStatus(EntrantStatus.CANCELLED);
            binding.contentGroupChosenEntrant.setVisibility(View.GONE);

            // Display status on screen
            showFinalizedOrCancelledText(EntrantStatus.CANCELLED);

            // Update DB
            updateEventDB(event);
        });

        // Remove Event Button (Only in admin mode)
        binding.removeEventButton.setOnClickListener(v -> {
            removeAction("event");
        });

        // Remove Image Button (Only in admin mode)
        binding.removeImageButton.setOnClickListener(v -> {
            removeAction("image");
        });

        // Remove Organizer Button (Only in admin mode)
        binding.removeOrganizerButton.setOnClickListener(v -> {
            removeAction("organizer");
        });

        // Generate QR Code when the GenerateQRCode Button is pressed
        binding.generateQRCodeButton.setOnClickListener(v -> {
            Database.getDatabase().getEvent(eventId, taskEvent -> {
                Event event = taskEvent.getResult();
                Bitmap qrBitmap = event.getQRCodeBitmap();
                showQRCodeDialog(qrBitmap);
                saveQRCodeToDownloads(qrBitmap, event.getName());  // Save QRCode to Downloads
                Toast.makeText(requireContext(), "QR Code Generated!",
                        Toast.LENGTH_LONG).show();
            });
        });

        // Add joining/leaving waitlist functionality to button
        binding.navigationBarButton.setOnClickListener(v -> {
            // Navigate to edit event page if the user is the organizer of the event
            if (isOwnedEvent) {
                Bundle args = new Bundle();
                args.putString("eventId", eventId);
                NavHostFragment.findNavController(EventDetailScreenFragment.this)
                        .navigate(R.id.create_or_edit_event_fragment, args);
                return;
            }

            Database.getDatabase().getEvent(eventId, taskEvent -> {
                if (taskEvent.isSuccessful()) {
                    Event event = taskEvent.getResult();

                    Entrant entrant = event.genEntrantIfExists(currentUser);
                    if (entrant == null) {
                        //Get geo entrantLocation
                        Context context = v.getContext();
                        //If Event Geo location requirement is off or device is not allowing geo location, save null as location
                        if (!event.isGeolocationRequirement()) {
                            Entrant newEntrant = new Entrant();
                            newEntrant.setLocation(null);
                            newEntrant.setStatus(EntrantStatus.WAITING);
                            newEntrant.setUser(currentUser);
                            event.addToEntrantList(newEntrant);
                            updateEventDB(event);
                            changeWaitlistBtn(true);
                            Log.d("EventDetailScreen", "User successfully joins waiting list");
                        } else {
                            if ((ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(v.getContext());
                                // Make entrant effectively final by using a final variable
                                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,null)
                                        .addOnSuccessListener(ContextCompat.getMainExecutor(context), location -> {
                                            EntrantLocation entrantLocation = null;
                                            if (location != null) {
                                                entrantLocation = new EntrantLocation();
                                                entrantLocation.setLatitude(location.getLatitude());
                                                entrantLocation.setLongitude(location.getLongitude());
                                            }
                                            Entrant newEntrant = new Entrant();
                                            newEntrant.setLocation(entrantLocation);
                                            newEntrant.setStatus(EntrantStatus.WAITING);
                                            newEntrant.setUser(currentUser);

                                            event.addToEntrantList(newEntrant);
                                            updateEventDB(event);
                                            changeWaitlistBtn(true);
                                        });
                            } else {
                                Toast.makeText(requireContext(), "Geolocation is required, enable geolocation in phone settings",
                                        Toast.LENGTH_LONG).show();
                            }
                            // User is not in waiting list, so join the waitlist
                        }
                    } else {
                        // User is in waiting list, so leave the waitlist
                        event.removeEntrant(entrant);
                        updateEventDB(event);
                        changeWaitlistBtn(false);
                        Log.d("EventDetailScreen", "User successfully left waiting list");
                    }
                    Log.d(TAG, "After button press, Waiting list: " + event.getEntrantList());


                } else {
                    // Failed to load event; hide loading and show error
                    binding.loadingEventDetailScreen.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to load event",
                            Toast.LENGTH_LONG).show();
                }
            });
        });

    }

    private void updateEventDB(Event event){
        Database db = Database.getDatabase();
        db.updateEvent(event, task -> {});
    }

    /**
     * Shows Generate QR Code button in event details screen if current user is the organizer of the event
     */
    private void showGenerateQRCodeButton() {
        if (isOwnedEvent) {
            binding.generateQRCodeButton.setVisibility(View.VISIBLE);
        }
        else {
            binding.generateQRCodeButton.setVisibility(View.GONE); // make generate QR Code button gone if not owner of the event
        }
    }

    /**
     * Removes an action from the database (action could be: event, image, or organizer)
     * @param action the action to remove from the database
     */
    private void removeAction(String action) {
        // Inflate the layout
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogAdminRemoveAction = inflater
                .inflate(R.layout.dialog_admin_remove_action, null);
        Button dialogConfirmRemoveButton = dialogAdminRemoveAction
                .findViewById(R.id.dialogConfirmRemoveButton);
        Button dialogBackButton = dialogAdminRemoveAction
                .findViewById(R.id.dialogBackButton);
        TextView dialogTextView1 = dialogAdminRemoveAction
                .findViewById(R.id.dialogTextView1);

        // Set text of dialog
        switch (action) {
            case "event":
                dialogTextView1.setText(R.string.dialog_remove_event_text);
                break;

            case "image":
                dialogTextView1.setText(R.string.dialog_remove_image_text);
                break;

            case "organizer":
                dialogTextView1.setText(R.string.dialog_remove_organizer_text);
                break;
        }

        // Setup dialog for removing event
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogAdminRemoveAction)
                .setCancelable(true)
                .create();

        // Strengthen background dimness (to emphasize the dialog)
        dialog.getWindow().setDimAmount(.7f);
        // Set the background to transparent so we can show the rounded corners
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Return to event details screen
        dialogBackButton.setOnClickListener(dialogView -> dialog.dismiss());

        // ConfirmRemove button
        dialogConfirmRemoveButton.setOnClickListener(dialogView -> {
            switch (action) {
                case "event":
                    // Admin confirms remove event from DB
                    Admin.removeEvent(event);
                    // Show toast that event has been removed
                    Toast.makeText(requireContext(), "Event removed",
                            Toast.LENGTH_SHORT).show();
                    break;

                case "image":
                    String eventImageURL = event.getEventPosterUrl();
                    Admin.removeImage(eventImageURL, task -> {
                        if (task.isSuccessful()) {
                            // Show toast that image has been removed
                            Toast.makeText(requireContext(), "Image removed",
                                    Toast.LENGTH_SHORT).show();
                            // Set the event poster url to null in the DB
                            event.setEventPosterUrl(null);
                            // Update event in DB and bind
                            updateEventDB(event);
                            bindEvent(event);
                        } else {
                            // Show toast on image removal fail
                            Toast.makeText(requireContext(), "Failed to remove Image",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;

                case "organizer":
                    // Fetch Organizer by organizerID
                    Database.getDatabase().getUser(organizerID, taskOrganizer -> {
                        User organizer = taskOrganizer.getResult();
                        // Admin confirms remove organizer from DB
                        Admin.removeOrganizer(organizer);
                    });
                    // Show toast that organizer has been removed
                    Toast.makeText(requireContext(), "Organizer removed",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            if (action.equals("event") || action.equals("organizer")) {
                // Dismiss dialog
                dialog.dismiss();
                // Return to event details screen
                NavHostFragment.findNavController(EventDetailScreenFragment.this)
                        .navigateUp();
            } else if (action.equals("image")) {
                // Dismiss dialog
                dialog.dismiss();
            }
        });
        // Show the dialog (i.e., confirm action dialog)
        dialog.show();
    }

    /**
     * Shows generated QR Code in a pop up
     * @param qrBitmap Bitmap to pass to ImageView to display
     */
    private void showQRCodeDialog(Bitmap qrBitmap) {
        Dialog dialog = new Dialog(requireContext());

        // CreateImageView
        ImageView qrImageView = new ImageView(requireContext());
        qrImageView.setImageBitmap(qrBitmap);
        qrImageView.setPadding(32, 32, 32, 32); // padding

        dialog.setContentView(qrImageView); // Set the ImageView as the dialog content
        dialog.setCancelable(true); // Allow tapping outside to dismiss
        dialog.show();
    }

    /**
     * Saves a QR code Bitmap as a PNG file to the public Downloads folder.
     *
     * @param qrBitmap The QR bitmap to save
     * @param eventName The name of the event used for the filename
     */
    private void saveQRCodeToDownloads(Bitmap qrBitmap, String eventName) {
        if (qrBitmap == null) {
            Toast.makeText(requireContext(), "QR Code is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // File name that will appear in Downloads
        String fileName = eventName + "_QRCode.png";

        // Metadata for the file we are creating using the MediaStore API
        ContentValues values = new ContentValues(); // key-value map container to hold metadata
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName); // set name of file to appear in downloads
        values.put(MediaStore.Downloads.MIME_TYPE, "image/png"); // indicate file type, which is png
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);  // indicate path to store the file, which is downloads

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // create empty file in downloads using metadata provided
            uri = requireContext().getContentResolver()
                    .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        }

        if (uri == null) {
            Toast.makeText(requireContext(), "Unable to create image file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an OutputStream to write to the file we just created
        try (OutputStream out = requireContext().getContentResolver().openOutputStream(uri)) {
            assert out != null;
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(requireContext(),
                    "QR code saved to Downloads/" + fileName,
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    "Failed to save QR code: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Updates the waitlist button colors and text based on if the user is in the waitlist
     * @param userInWaitlist Boolean whether user is in waitlist of event or not
     */
    private void changeWaitlistBtn(boolean userInWaitlist) {
        if (isOwnedEvent) {
            binding.navigationBarButton.setText("Edit Event");
            binding.navigationBarButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.app_beige)
            );
            return;
        }

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
     * Shows the finalized or cancelled text based on the status of the entrant
     * @param status the status of the entrant (either FINALIZED or CANCELLED)
     */
    private void showFinalizedOrCancelledText(EntrantStatus status) {
        binding.contentGroupCancelledOrFinalized.setVisibility(View.VISIBLE);
        // Hide join waitlist/edit event button
        binding.navigationBarButton.setVisibility(View.GONE);

        // Check which status it is (finalized or cancelled) and show the correct text respectively
        if (status.equals(EntrantStatus.FINALIZED)) {
            binding.cancelledOrFinalizedText.setText("FINALIZED");
            binding.cancelledOrFinalizedText
                    .setTextColor(ContextCompat.getColor(requireContext(),R.color.dark_grey));
            binding.cancelledOrFinalizedText
                    .setBackgroundTintList(ContextCompat
                            .getColorStateList(requireContext(), R.color.light_grey));
        } else if (status.equals(EntrantStatus.CANCELLED)) {
            binding.cancelledOrFinalizedText.setText("CANCELLED");
            binding.cancelledOrFinalizedText
                    .setTextColor(ContextCompat.getColor(requireContext(),R.color.black));
            binding.cancelledOrFinalizedText
                    .setBackgroundTintList(ContextCompat
                            .getColorStateList(requireContext(), R.color.important_field));
        }
    }

    /**
     * Updates the chosen entrant buttons based on the status of the entrant
     * @param status the status of the entrant (CHOSEN)
     */
    private void showChosenEntrantButtons(EntrantStatus status) {
        if (status.equals(EntrantStatus.CHOSEN)) {
            binding.ChosenEntrantButtonContainer.setVisibility(View.VISIBLE);
            binding.navigationBarButton.setVisibility(View.GONE);
        }
    }

    /**
     * Wrapper function for calling getUserFromDeviceID on the database
     * @param callback a callback function that runs when the query is done running
     */
    private void getCurrentUser(OnCompleteListener<User> callback) {

        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceID -> {
            Log.d(TAG, "Device ID obtained is: " + deviceID);
            assert deviceID != null;

            Database db = Database.getDatabase();

            db.getUserFromDeviceID(deviceID, callback);
        });
    }

    private void getEvent(OnCompleteListener<Event> callback) {
        Database db = Database.getDatabase();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API level must be 26 or above
            Log.d(TAG, "Fetching event from DB...");
            db.getEvent(eventId, task ->  {
                if (task.isSuccessful()) {
                    callback.onComplete(task);
                } else {
                    Log.e(TAG, "Error fetching event from database");
                }
            });
        }
    }

    /**
     * Renders the event details of the page using an event object
     * @param event details to fill the page with
     */
    private void bindEvent(Event event) {
        // Get event name, description, location, waitlist and chosen capacity
        String eventName = event.getName();
        String eventDesc = event.getDescription();
        String eventLoc = event.getPlace();
        int eventCurrentWaitlist = event.getEntrantWaitingList().size();
        int eventWaitlistCapacity = event.getMaxWaitingListCapacity();
        int eventChosenCapacity = event.getMaxFinalListCapacity();

        // Error Checking for null name, desc, and location. (Don't think we need, may remove later)
        if (eventName == null || eventDesc == null || eventLoc == null) {
            Toast.makeText(requireContext(), "Missing event name or description", Toast.LENGTH_LONG).show();
            return;
        }
        // Set UI event name, description, and location
        binding.eventNameText.setText(eventName);
        binding.eventDetailsDescText.setText(eventDesc);
        binding.eventLocationText.setText(eventLoc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // format times
        String formattedEventStartTime = event.getEventStartTime()==null? "": event.getEventStartTime().format(formatter);
        String formattedEventEndTime = event.getEventEndTime()==null? "":  event.getEventEndTime().format(formatter);
        String formattedRegEndTime = event.getRegistrationEndTime()==null? "":  event.getRegistrationEndTime().format(formatter);
        String formattedRegStartTime = event.getRegistrationStartTime()==null? "":  event.getRegistrationStartTime().format(formatter);
        String formattedInvAccTime = event.getInvitationAcceptanceDeadline()==null? "":  event.getInvitationAcceptanceDeadline().format(formatter);

        // Error Checking for event and registration times. (Don't think we need, may remove later)
        if (formattedEventStartTime == null || formattedEventEndTime == null || formattedRegEndTime == null || formattedRegStartTime == null || formattedInvAccTime == null) {
            Toast.makeText(requireContext(), "Missing event registration time details", Toast.LENGTH_LONG).show();
            return;
        }
        // set periods
        binding.eventPeriodText.setText(formattedEventStartTime + " to " + formattedEventEndTime);
        binding.eventRegPeriodText.setText(formattedRegStartTime + " to " + formattedRegEndTime);
        binding.eventInvitationDLText.setText(formattedInvAccTime);

        // set capacities
        if (eventWaitlistCapacity <= 0) {
            binding.waitlistText.setText(String.valueOf(eventCurrentWaitlist));
        } else {
            binding.waitlistText.setText(eventCurrentWaitlist + "/" + eventWaitlistCapacity);
        }
        binding.chosenCapText.setText(String.valueOf(eventChosenCapacity));

        // Fetch tags from event
        // Get tags
        List<String> tags = event.getEventTags();
        if (tags == null)
            tags = new ArrayList<>(); // prevent NullPointerException if there are no tags by making an empty arraylist

        // Update image of the event if a download url exists
        String eventPosterUrl = event.getEventPosterUrl();
        Log.d(TAG, "Poster image url: " + eventPosterUrl);
        if (eventPosterUrl != null && !eventPosterUrl.isEmpty()) {
            Glide.with(this)
                    .load(eventPosterUrl)
                    .placeholder(R.drawable.image_template)
                    .into(binding.eventImage);
        } else {
            // Set the image template to default image
             binding.eventImage.setImageResource(R.drawable.image_template);
        }

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

        // Disable join waitlist button until registration starts
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime regStart = event.getRegistrationStartTime();

        if (now.isBefore(regStart)) {
            // Disable button until registration starts
            binding.navigationBarButton.setEnabled(false);
            binding.navigationBarButton.setText("Registration not open");
            binding.navigationBarButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey));
        } else {
            // Enable button
            binding.navigationBarButton.setEnabled(true);
            changeWaitlistBtn(false); // or update button based on user's waitlist status
        }
    }
}