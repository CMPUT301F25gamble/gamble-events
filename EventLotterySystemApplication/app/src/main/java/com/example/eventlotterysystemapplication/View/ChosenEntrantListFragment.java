package com.example.eventlotterysystemapplication.View;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentChosenEntrantListBinding;

import java.util.ArrayList;

/**
 * Displays a listview of chosen entrants that includes pending entrants, cancelled entrants,
 * and entrants that accepted and invitation to the event
 * Will fetch a list of the above from the database to be displayed in the listview
 * Navigated to from {@link EntrantListSelectionFragment}
 */

public class ChosenEntrantListFragment extends Fragment {
    private FragmentChosenEntrantListBinding binding;
    private Database database;

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
        // List for chosen entrants
        ArrayList<CharSequence> data = new ArrayList<>();
        // Adapter for listview
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                data
        );

        for (User u : event.getUserChosenList()) {
            String name = u.getName();
            data.add(name);
        }
        // Notify the adapter
        adapter.notifyDataSetChanged();

        // Set the adapter for the ListView
        binding.chosenListOfEntrantsListView.setAdapter(adapter);
    }
}
