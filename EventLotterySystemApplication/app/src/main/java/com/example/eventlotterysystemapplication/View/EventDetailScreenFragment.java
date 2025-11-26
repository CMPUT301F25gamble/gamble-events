package com.example.eventlotterysystemapplication.View;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Controller.AdminActivity;
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
import com.example.eventlotterysystemapplication.databinding.FragmentEventDetailScreenBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.installations.FirebaseInstallations;

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
    private String userId;
    private boolean isOwnedEvent = false;
    private final String TAG = "EventDetailScreen";

    // Used for ADMIN control
    private String organizerID;
    private boolean isAdminMode;
    private Event event;

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

        Log.d(TAG, "Event ID: " + eventId + ", isOwnedEvent=" + isOwnedEvent);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailScreenBinding.inflate(inflater, container, false);

        // TODO implement checking event geolocation requirements
        if ( ContextCompat.checkSelfPermission(binding.getRoot().getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1001);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isAdminMode = AdminSession.getAdminMode();
        userId = AdminSession.getSelectedUserId();

        ImageButton backButton = binding.eventDetailScreenBackButton;

        if (getActivity() instanceof EditEventActivity) {
            backButton.setOnClickListener(v -> {
               getActivity().finish();
            });
        } else if (getActivity() instanceof AdminActivity) {
            backButton.setOnClickListener(v -> {
                NavHostFragment.findNavController(this)
                        .popBackStack();
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

        // Obtain deviceID
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceID -> {
            Log.d(TAG, "Device ID obtained is: " + deviceID);
            assert deviceID != null;

            // Fetch this event and bind
            Database.getDatabase().getEvent(eventId, task -> {
                if (task.isSuccessful()) {
                    // Grab event and bind it
                    event = task.getResult();
                    organizerID = event.getOrganizerID(); // Used for admin control
                    Log.d(TAG, "Event retrieved is: " + event);
                    bindEvent(event);

                    // Update the "looks" of the button based on if the user is the organizer, in the waiting list, or not in the waiting list
                    Database.getDatabase().getUserFromDeviceID(deviceID, taskUser -> {
                        if (taskUser.isSuccessful()) {
                            User user = taskUser.getResult();
                            Log.d(TAG, "Grabbed user is: " + user);
                            Log.d(TAG, "Initial Waiting list: " + event.getEntrantWaitingList());

                            // If Admin mode, hide generateQR button, else show it
                            if (isAdminMode) {
                                binding.generateQRCodeButton.setVisibility(View.GONE);
                            } else {
                                showGenerateQRCodeButton();
                            }

                            Entrant entrant = event.genEntrantIfExists(user);
                            changeWaitlistBtn(entrant != null);
                        } else {
                            // Failed to load user; hide loading and show error
                            binding.loadingEventDetailScreen.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Failed to fetch user from device ID",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    binding.loadingEventDetailScreen.setVisibility(View.GONE);
                    if (isAdminMode) {
                        binding.contentGroupAdminEventsDetailScreen.setVisibility(View.VISIBLE);
                        binding.contentGroupEventsDetailScreen.setVisibility(View.VISIBLE);
                        // Hide join waitlist/edit event button
                        binding.navigationBarButton.setVisibility(View.GONE);
                        // Hide generate QR Code button logic is done when db called (line 152)
                    } else {
                        // Show join waitlist/edit event button
                        binding.contentGroupEventsDetailScreen.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Failed to load event; hide loading and show error
                    Log.e(TAG, "Failed to load event, " + task.getResult());
                    binding.loadingEventDetailScreen.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to load event",
                            Toast.LENGTH_LONG).show();
                }
            });

            // Remove Event Button (Only in admin mode)
            binding.removeEventButton.setOnClickListener(v -> {
                removeAction("event");
            });

            // Remove Image Button (Only in admin mode)
            binding.removeImageButton.setOnClickListener(v -> {
                // TODO: add functionality for image remove
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
                        getUserFromDeviceID(deviceID, taskUser -> {
                            if (taskUser.isSuccessful()) {
                                // Grab user and check if already in waiting list
                                User user = taskUser.getResult();
                                Entrant entrant = event.genEntrantIfExists(user);
                                if (entrant == null) {
                                    //Get geo entrantLocation
                                    Context context = v.getContext();
                                    //If Event Geo location requirement is off or device is not allowing geo location, save null as location
                                    if (!event.isGeolocationRequirement() || (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                                        Entrant newEntrant = new Entrant();
                                        newEntrant.setLocation(null);
                                        newEntrant.setStatus(EntrantStatus.WAITING);
                                        newEntrant.setUser(user);
                                        event.addToEntrantList(newEntrant);
                                        updateEventDB(event);
                                        changeWaitlistBtn(true);
                                        Log.d("EventDetailScreen", "User successfully joins waiting list");
                                    } else {
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
                                                    newEntrant.setUser(user);
                                                    event.addToEntrantList(newEntrant);
                                                    updateEventDB(event);
                                                    changeWaitlistBtn(true);

                                                });
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
     * @param action
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
     * Wrapper function for calling getUserFromDeviceID on the database
     * @param deviceID the user's device ID
     * @param callback a callback function that runs when the query is done running
     */
    private void getUserFromDeviceID(String deviceID, OnCompleteListener<User> callback) {
        Database db = Database.getDatabase();

        db.getUserFromDeviceID(deviceID, callback);
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
            binding.eventImage.setImageResource(R.drawable.image_template);
            //binding.eventImage.setVisibility(View.GONE);
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
    }
}
