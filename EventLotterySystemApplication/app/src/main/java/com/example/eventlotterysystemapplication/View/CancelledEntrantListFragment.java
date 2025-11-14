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
import com.example.eventlotterysystemapplication.databinding.FragmentCancelledEntrantListBinding;

import java.util.ArrayList;

/**
 * Displays a listview of all cancelled entrants for the selected event
 * Will fetch all cancelled entrants from the database to display in the listview
 * Navigated to from {@link EntrantListSelectionFragment}
 */

public class CancelledEntrantListFragment extends Fragment {
    private FragmentCancelledEntrantListBinding binding;
    private Database database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCancelledEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = new Database();

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
        binding.cancelledEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(CancelledEntrantListFragment.this)
                    .navigateUp();
        });

        // Display the loading screen while the data is being fetched
        binding.loadingCancelledEntrantsList.setVisibility(View.VISIBLE);
        binding.contentGroupCancelledEntrantsList.setVisibility(View.GONE);

        // call parseEventRegistration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            database.getEvent(eventId, task -> {

                // Error check if task is not successful
                if (!task.isSuccessful()) {
                    binding.loadingCancelledEntrantsList.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch the event from the task
                Event event = task.getResult();

                // Populate the ListView with all entrants
                loadCancelledEntrantsIntoList(event);

                // Hide loading and show content
                binding.loadingCancelledEntrantsList.setVisibility(View.GONE);
                binding.contentGroupCancelledEntrantsList.setVisibility(View.VISIBLE);
            });
        }
    }

    // Private method to help with loading the data into the ListView
    private void loadCancelledEntrantsIntoList(Event event) {
        // List for cancelled entrants
        ArrayList<CharSequence> data = new ArrayList<>();
        // Adapter for listview
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                data
        );

        for (User u : event.getEntrantList().getCancelled()) {
            String name = u.getName();
            data.add(name);
        }
        // Notify the adapter
        adapter.notifyDataSetChanged();

        // Set the adapter for the ListView
        binding.cancelledListOfEntrantsListView.setAdapter(adapter);
    }
}
