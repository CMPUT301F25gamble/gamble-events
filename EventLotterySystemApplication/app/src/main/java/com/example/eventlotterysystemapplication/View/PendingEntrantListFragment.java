package com.example.eventlotterysystemapplication.View;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.EntrantList;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.LotterySelector;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentPendingEntrantListBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PendingEntrantListFragment
 * Fragment that displays a ListView of all pending entrants that have yet to accept an
 * invitation to join the event
 * Navigated to from {@link EntrantListSelectionFragment}
 */

public class PendingEntrantListFragment extends Fragment {
    private FragmentPendingEntrantListBinding binding;
    private Database database;
    // Global scope event var
    private Event currentEvent;

    // List for pending entrants
    private ArrayList<CharSequence> data = new ArrayList<>();
    // Adapter for listview
    private ArrayAdapter<CharSequence> adapter;
    // List of pending entrants
    private final ArrayList<User> pendingEntrants = new ArrayList<>();

    LotterySelector lotterySelector;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPendingEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = Database.getDatabase();
        lotterySelector = new LotterySelector();

        // Initialize the adapter with the data
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                data
        );
        // Set the adapter for the ListView
        binding.pendingListOfEntrantsListView.setAdapter(adapter);

        // Safely read arguments
        Bundle args = getArguments();

        if (args == null || !args.containsKey("eventID")) {
            // No event ID -> we can't do anything, so show a message and go back
            Toast.makeText(requireContext(), "Error: missing event ID", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        String eventId = args.getString("eventID");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(requireContext(), "Error: invalid event ID", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        // Back Button to return to Event Lists page
        binding.pendingEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PendingEntrantListFragment.this)
                    .navigateUp();
        });

        // Display the loading screen while the data is being fetched
        binding.loadingPendingEntrantsList.setVisibility(View.VISIBLE);
        binding.contentGroupPendingEntrantsList.setVisibility(View.GONE);

        // call parseEventRegistration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            database.getEvent(eventId, task -> {

                // Error check if task is not successful
                if (!task.isSuccessful()) {
                    binding.loadingPendingEntrantsList.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch the event from the task
                Event event = task.getResult();
                currentEvent = event;

                // Populate the ListView with all entrants
                loadPendingEntrantsIntoList(event);

                // Hide loading and show content
                binding.loadingPendingEntrantsList.setVisibility(View.GONE);
                binding.contentGroupPendingEntrantsList.setVisibility(View.VISIBLE);
            });
        }

        // Randomly Select Entrants for Event when button pressed
        binding.selectEntrantsButton.setOnClickListener(v->{
            database.getEvent(eventId,  task -> {
                if (task.isSuccessful()) {
                    Event event = task.getResult();

                    // Use lottery selector to randomly select entrants
                    List<User> selectedEntrants = lotterySelector.drawAcceptedUsers(event);

                    for (User u : selectedEntrants)
                    {
                        event.joinChosenList(u); // add entrants to chosen list
                    }

                    Toast.makeText(requireContext(), "Entrants from waiting list randomly selected!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Private method to help with loading the data into the ListView
    private void loadPendingEntrantsIntoList(Event event) {
        // Loop through all pending entrants
        for (User u : event.getEntrantList().getWaiting()) {
            pendingEntrants.add(u);     // Add user details to the list
            String name = u.getName();
            data.add(name);             // Add ONLY user's name to the list
        }
        // Notify the adapter
        adapter.notifyDataSetChanged();

        // When you tap a name in the list, show the user details popup:
        binding.pendingListOfEntrantsListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = pendingEntrants.get(position);
            showPendingEntrantDialog(selectedUser);
        });
    }

    /**
     * Displays a dialog containing the selected user's information and provides the
     * option to decline their pending event invitation. If declined, the user is
     * moved from the event’s pending list to the cancelled list, the local ListView
     * and the database are updated accordingly, and a confirmation toast is shown.
     *
     * @param user The pending entrant whose details should be displayed and acted upon.
     */
    private void showPendingEntrantDialog(User user) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogPendingEntrantView = inflater.inflate(R.layout.dialog_pending_entrant, null);

        TextView nameText = dialogPendingEntrantView.findViewById(R.id.enterName);
        TextView emailText = dialogPendingEntrantView.findViewById(R.id.enterEmail);
        TextView phoneText = dialogPendingEntrantView.findViewById(R.id.enterPhone);
        // TODO: implement Geolocation
        // TextView locationText = dialogPendingEntrantView.findViewById(R.id.enterLocation);
        Button declineButton = dialogPendingEntrantView.findViewById(R.id.dialogDeclineButton);
        Button backButton1 = dialogPendingEntrantView.findViewById(R.id.dialogBackButton);

        nameText.setText(user.getName());
        emailText.setText(user.getEmail());

        // If phone number null, don't display
        if (user.getPhoneNumber() == null) {
            phoneText.setVisibility(View.GONE);
            dialogPendingEntrantView.findViewById(R.id.dialogUserPhone).setVisibility(View.GONE);
        } else {
            phoneText.setText(user.getPhoneNumber());
        }

        // TODO: implement Geolocation
        // locationText.setText(user.getLocation());

        // Setup first dialog for displaying user info
        AlertDialog dialog1 = new AlertDialog.Builder(requireContext())
                .setView(dialogPendingEntrantView)
                .setCancelable(true)
                .create();

        // Set the background to transparent so we can show the rounded corners
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Return to list view
        backButton1.setOnClickListener(v -> dialog1.dismiss());

        // Move to a new dialog
        declineButton.setOnClickListener(v -> {
            LayoutInflater inflater2 = LayoutInflater.from(requireContext());
            View dialogPendingEntrantDeclineView = inflater2
                    .inflate(R.layout.dialog_pending_entrant_decline, null);
            Button confirmButton = dialogPendingEntrantDeclineView
                    .findViewById(R.id.dialogConfirmButton);
            Button backButton2 = dialogPendingEntrantDeclineView
                    .findViewById(R.id.dialogBackButton);

            // Setup second dialog for declining the invitation
            AlertDialog dialog2 = new AlertDialog.Builder(requireContext())
                    .setView(dialogPendingEntrantDeclineView)
                    .setCancelable(true)
                    .create();

            // Strengthen background dimness (to emphasize the dialog)
            dialog2.getWindow().setDimAmount(.7f);
            // Set the background to transparent so we can show the rounded corners
            dialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Return to dialog pending entrant view (i.e., dialog1)
            backButton2.setOnClickListener(view -> dialog2.dismiss());

            // Remove User from pending list (i.e., send user to cancelled list)
            confirmButton.setOnClickListener(view -> {
                // Dismiss dialog1 first for a clean transition
                dialog1.dismiss();
                // Remove user from event's pending list DB
                currentEvent.joinCancelledList(user);

                // Remove user from LOCAL pending list
                pendingEntrants.remove(user);
                data.remove(user.getName());

                // Refresh the ListView
                adapter.notifyDataSetChanged();
                // Dismiss the dialog
                dialog2.dismiss();
                Toast.makeText(requireContext(),
                        "Invite declined for " + user.getName(),
                        Toast.LENGTH_SHORT).show();
            });
            // Show the dialog 2 (i.e., the user decline dialog)
            dialog2.show();
        });
        // Show the dialog 1 (i.e., the user info dialog)
        dialog1.show();
    }
}
