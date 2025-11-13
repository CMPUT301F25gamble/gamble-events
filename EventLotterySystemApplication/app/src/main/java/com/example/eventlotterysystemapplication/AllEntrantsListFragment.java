package com.example.eventlotterysystemapplication;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentAllEntrantsListBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Fragment for displaying a ListView of all entrants for the selected event
 * Will fetch a list of all entrants from the database and display them in a ListView
 * Navigated to from {@link EntrantListSelectionFragment}
 */

public class AllEntrantsListFragment extends Fragment {
    private FragmentAllEntrantsListBinding binding;
    private Database database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllEntrantsListBinding.inflate(inflater, container, false);
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
        binding.allEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(AllEntrantsListFragment.this)
                    .navigate(R.id.action_allEntrantsListFragment_to_entrantListSelectionFragment);
        });

        // Display the loading screen while the data is being fetched
        binding.loadingAllEntrantsList.setVisibility(View.VISIBLE);
        binding.contentGroupAllEntrantsList.setVisibility(View.GONE);

        // call parseEventRegistration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            database.getEvent(eventId, task -> {

                // Error check if task is not successful
                if (!task.isSuccessful()) {
                    binding.loadingAllEntrantsList.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch the event from the task
                Event event = task.getResult();
                //Toast.makeText(getContext(), "Event: " + event.getName(), Toast.LENGTH_SHORT).show();

                // Populate the ListView with all entrants
                loadAllEntrantsIntoList(event);

                // Hide loading and show content
                binding.loadingAllEntrantsList.setVisibility(View.GONE);
                binding.contentGroupAllEntrantsList.setVisibility(View.VISIBLE);
            });
        }
    }

    // Private method to help with loading the data into the ListView
    private void loadAllEntrantsIntoList(Event event) {
        // List for all entrants
        ArrayList<String> data = new ArrayList<>();

        //Toast.makeText(getContext(), "entrant list: " + event.getEntrantList().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(requireContext(), "Waiting: " + event.getEntrantList().getWaiting().size() , Toast.LENGTH_SHORT).show();

        for (User u : event.getEntrantList().getWaiting()) {
            Toast.makeText(requireContext(), "Waiting: " + u.getName(), Toast.LENGTH_SHORT).show();
            data.add(u.getName() + " (waiting)");
        }
        for (User u : event.getEntrantList().getChosen()) {
            data.add(u.getName() + " (chosen)");
        }
        for (User u : event.getEntrantList().getCancelled()) {
            data.add(u.getName() + " (cancelled)");
        }
        for (User u : event.getEntrantList().getFinalized()) {
            data.add(u.getName() + " (finalized)");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            data
        );

        // Set the adapter for the ListView
        binding.allListOfEntrantsListView.setAdapter(adapter);
    }
}
