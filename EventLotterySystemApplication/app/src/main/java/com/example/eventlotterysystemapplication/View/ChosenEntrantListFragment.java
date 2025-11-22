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
import com.example.eventlotterysystemapplication.Model.Entrant;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentChosenEntrantListBinding;

import java.util.ArrayList;

/**
 * Displays a listview of chosen entrants that includes chosen entrants.
 * Will fetch a list of the above from the database to be displayed in the listview
 * Navigated to from {@link EntrantListSelectionFragment}
 */

public class ChosenEntrantListFragment extends Fragment {
    private FragmentChosenEntrantListBinding binding;
    private Database database;
    // Global scope event var
    private Event currentEvent;

    // List of data for each chosen entrant
    private ArrayList<CharSequence> data = new ArrayList<>();
    // Adapter for listview
    private ArrayAdapter<CharSequence> adapter;
    // List of chosen entrants
    private final ArrayList<Entrant> chosenEntrants = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChosenEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = Database.getDatabase();

        // Initialize the adapter with the data
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                data
        );
        // Set the adapter for the ListView
        binding.chosenListOfEntrantsListView.setAdapter(adapter);

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
        binding.chosenEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(ChosenEntrantListFragment.this)
                    .navigateUp();
        });

        // Display the loading screen while the data is being fetched
        binding.loadingChosenEntrantsList.setVisibility(View.VISIBLE);
        binding.contentGroupChosenEntrantsList.setVisibility(View.GONE);

        // call parseEventRegistration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            database.getEvent(eventId, task -> {

                // Error check if task is not successful
                if (!task.isSuccessful()) {
                    binding.loadingChosenEntrantsList.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch the event from the task
                Event event = task.getResult();
                currentEvent = event;

                // Populate the ListView with all entrants
                loadChosenEntrantsIntoList(event);

                // Hide loading and show content
                binding.loadingChosenEntrantsList.setVisibility(View.GONE);
                binding.contentGroupChosenEntrantsList.setVisibility(View.VISIBLE);
            });
        }
    }

    // Private method to help with loading the data into the ListView
    private void loadChosenEntrantsIntoList(Event event) {
//        // List for chosen entrants
//        ArrayList<CharSequence> data = new ArrayList<>();
//        // Adapter for listview
//        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
//                requireContext(),
//                android.R.layout.simple_list_item_1,
//                data
//        );

        for (User u : event.getUserChosenList()) {
            String name = u.getName();
            data.add(name);             // Add ONLY user's name to the list
        }
        // Notify the adapter
        adapter.notifyDataSetChanged();

        // When you tap a name in the list, show the user details popup:
        binding.chosenListOfEntrantsListView.setOnItemClickListener((parent, view, position, id) -> {
            Entrant selectedEntrant = chosenEntrants.get(position);
            showChosenEntrantDialog(selectedEntrant);
        });
    }

    /**
     * Displays a dialog containing the selected user's information and provides the
     * option to decline their pending event invitation. If declined, the user is
     * moved from the event’s chosen list to the cancelled list, the local ListView
     * and the database are updated accordingly, and a confirmation toast is shown.
     *
     * @param entrant The chosen entrant whose details should be displayed and acted upon.
     */
    private void showChosenEntrantDialog(Entrant entrant) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogChosenEntrantView = inflater.inflate(R.layout.dialog_chosen_entrant, null);

        TextView nameText = dialogChosenEntrantView.findViewById(R.id.enterName);
        TextView emailText = dialogChosenEntrantView.findViewById(R.id.enterEmail);
        TextView phoneText = dialogChosenEntrantView.findViewById(R.id.enterPhone);
        // TODO: implement Geolocation
        // TextView locationText = dialogChosenEntrantView.findViewById(R.id.enterLocation);
        Button declineButton = dialogChosenEntrantView.findViewById(R.id.dialogDeclineButton);
        Button backButton1 = dialogChosenEntrantView.findViewById(R.id.dialogBackButton);

        nameText.setText(entrant.getUser().getName());
        emailText.setText(entrant.getUser().getEmail());

        // If phone number null, don't display
        if (entrant.getUser().getPhoneNumber() == null) {
            phoneText.setVisibility(View.GONE);
            dialogChosenEntrantView.findViewById(R.id.dialogUserPhone).setVisibility(View.GONE);
        } else {
            phoneText.setText(entrant.getUser().getPhoneNumber());
        }

        // TODO: implement Geolocation
        // locationText.setText(user.getLocation());

        // Setup first dialog for displaying user info
        AlertDialog dialog1 = new AlertDialog.Builder(requireContext())
                .setView(dialogChosenEntrantView)
                .setCancelable(true)
                .create();

        // Set the background to transparent so we can show the rounded corners
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Return to list view
        backButton1.setOnClickListener(v -> dialog1.dismiss());

        // Move to a new dialog
        declineButton.setOnClickListener(v -> {
            LayoutInflater inflater2 = LayoutInflater.from(requireContext());
            View dialogChosenEntrantDeclineView = inflater2
                    .inflate(R.layout.dialog_chosen_entrant_decline, null);
            Button confirmButton = dialogChosenEntrantDeclineView
                    .findViewById(R.id.dialogConfirmButton);
            Button backButton2 = dialogChosenEntrantDeclineView
                    .findViewById(R.id.dialogBackButton);

            // Setup second dialog for declining the invitation
            AlertDialog dialog2 = new AlertDialog.Builder(requireContext())
                    .setView(dialogChosenEntrantDeclineView)
                    .setCancelable(true)
                    .create();

            // Strengthen background dimness (to emphasize the dialog)
            dialog2.getWindow().setDimAmount(.7f);
            // Set the background to transparent so we can show the rounded corners
            dialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Return to dialog chosen entrant view (i.e., dialog1)
            backButton2.setOnClickListener(view -> dialog2.dismiss());

            // Remove User from chosen list (i.e., send user to cancelled list)
            confirmButton.setOnClickListener(view -> {
                // Dismiss dialog1 first for a clean transition
                dialog1.dismiss();
                // Remove user from event's chosen list DB
                currentEvent.addEntrantToChosenList(entrant);

                // Remove user from LOCAL chosen list
                chosenEntrants.remove(entrant.getUser());
                data.remove(entrant.getUser().getName());

                // Refresh the ListView
                adapter.notifyDataSetChanged();
                // Dismiss the dialog
                dialog2.dismiss();
                Toast.makeText(requireContext(),
                        "Invite declined for " + entrant.getUser().getName(),
                        Toast.LENGTH_SHORT).show();
            });
            // Show the dialog 2 (i.e., the user decline dialog)
            dialog2.show();
        });
        // Show the dialog 1 (i.e., the user info dialog)
        dialog1.show();
    }
}
