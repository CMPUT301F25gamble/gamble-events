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
        // List for all entrants
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
        binding.pendingListOfEntrantsListView.setAdapter(adapter);
    }
}
