package com.example.eventlotterysystemapplication.View;

import android.app.AlertDialog;
import android.graphics.Color;
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
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentPendingEntrantListBinding;

import java.util.ArrayList;

/**
 * PendingEntrantListFragment
 * Fragment that displays a ListView of all pending entrants that have yet to accept an
 * invitation to join the event
 * Navigated to from {@link EntrantListSelectionFragment}
 */

public class PendingEntrantListFragment extends Fragment {
    private FragmentPendingEntrantListBinding binding;
    private Database database;

    // List for pending entrants
    private ArrayList<CharSequence> data = new ArrayList<>();
    // Adapter for listview
    private ArrayAdapter<CharSequence> adapter;
    // List of pending entrants
    private final ArrayList<User> pendingEntrants = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPendingEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = new Database();

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

                // Populate the ListView with all entrants
                loadPendingEntrantsIntoList(event);

                // Hide loading and show content
                binding.loadingPendingEntrantsList.setVisibility(View.GONE);
                binding.contentGroupPendingEntrantsList.setVisibility(View.VISIBLE);
            });
        }
    }

    // Private method to help with loading the data into the ListView
    private void loadPendingEntrantsIntoList(Event event) {
        // Loop through all pending entrants
        for (User u : event.getEntrantList().getCancelled()) {
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

    private void showPendingEntrantDialog(User user) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_pending_entrant_list, null);

        TextView nameText = dialogView.findViewById(R.id.enterName);
        TextView emailText = dialogView.findViewById(R.id.enterEmail);
        TextView phoneText = dialogView.findViewById(R.id.enterPhone);
        // TODO: implement Geolocation
        // TextView locationText = dialogView.findViewById(R.id.enterLocation);
        Button declineButton = dialogView.findViewById(R.id.dialogDeclineButton);
        Button backButton = dialogView.findViewById(R.id.dialogBackButton);

        nameText.setText(user.getName());
        emailText.setText(user.getEmail());

        // If phone number null, don't display
        if (user.getPhoneNumber() == null) {
            phoneText.setVisibility(View.GONE);
            dialogView.findViewById(R.id.dialogUserPhone).setVisibility(View.GONE);
        } else {
            phoneText.setText(user.getPhoneNumber());
        }

        // TODO: implement Geolocation
        // locationText.setText(user.getLocation());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Set the background to transparent so we can show the rounded corners
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Return to list view
        backButton.setOnClickListener(v -> dialog.dismiss());

        // TODO: make new dialog screen
        // Move to a new dialog
        declineButton.setOnClickListener(v -> {
            // TODO: call method to reject an invite

//            Toast.makeText(requireContext(),
//                    "Invite declined for " + user.getName(),
//                    Toast.LENGTH_SHORT).show();
//
//            dialog.dismiss();
        });
        dialog.show();
    }


}
