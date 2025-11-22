package com.example.eventlotterysystemapplication.View;

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

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.LotterySelector;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.databinding.FragmentWaitingEntrantListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * WaitingEntrantListFragment
 * Fragment that displays a ListView of all waiting entrants that have yet to accept an
 * invitation to join the event
 * Navigated to from {@link EntrantListSelectionFragment}
 */

public class WaitingEntrantListFragment extends Fragment {
    private FragmentWaitingEntrantListBinding binding;
    private Database database;
    LotterySelector lotterySelector;

    // Adapter fields for listview
    private ArrayAdapter<CharSequence> waitingAdapter;
    private ArrayList<CharSequence> waitingData;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWaitingEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = Database.getDatabase();
        lotterySelector = new LotterySelector();

        // Initialize adapter
        waitingData = new ArrayList<>();
        waitingAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                waitingData
        );
        binding.waitingListOfEntrantsListView.setAdapter(waitingAdapter);

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
        binding.waitingEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(WaitingEntrantListFragment.this)
                    .navigateUp();
        });

        // Display the loading screen while the data is being fetched
        binding.loadingWaitingEntrantsList.setVisibility(View.VISIBLE);
        binding.contentGroupWaitingEntrantsList.setVisibility(View.GONE);

        // call parseEventRegistration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            database.getEvent(eventId, task -> {

                // Error check if task is not successful
                if (!task.isSuccessful()) {
                    binding.loadingWaitingEntrantsList.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch the event from the task
                Event event = task.getResult();

                // Populate the ListView with all entrants
                loadWaitingEntrantsIntoList(event);

                // Hide loading and show content
                binding.loadingWaitingEntrantsList.setVisibility(View.GONE);
                binding.contentGroupWaitingEntrantsList.setVisibility(View.VISIBLE);
            });
        }

        // Randomly Select Entrants for Event when button pressed
        binding.selectEntrantsButton.setOnClickListener(v->{
            database.getEvent(eventId,  task -> {
                if (task.isSuccessful()) {
                    Event event = task.getResult();

                    // Use lottery selector to randomly select entrants
                    List<User> selectedEntrants =  lotterySelector.drawAcceptedUsers(event);

                    List<User> waitingToMove = new ArrayList<>(selectedEntrants);
                    for (User u : waitingToMove)
                    {
                        event.joinChosenList(u); // add entrants to chosen list
                    }
                    loadWaitingEntrantsIntoList(event);
                    Toast.makeText(requireContext(), "Entrants from waiting list randomly selected!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Private method to help with loading the data into the ListView
    private void loadWaitingEntrantsIntoList(Event event) {
        // Clear old data
        waitingData.clear();

        // Loop through all waiting entrants
        for (User u : event.getEntrantList().getWaiting()) {
            waitingData.add(u.getName());
        }

        // Notify the adapter; refresh the UI
        waitingAdapter.notifyDataSetChanged();
    }
}
