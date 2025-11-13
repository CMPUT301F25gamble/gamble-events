package com.example.eventlotterysystemapplication;

import android.os.Bundle;
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

        // Back Button to return to Event Lists page
        binding.allEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(AllEntrantsListFragment.this)
                    .navigate(R.id.action_allEntrantsListFragment_to_entrantListSelectionFragment);
        });

        String eventId = getArguments().getString("eventID");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Event").document(eventId);

        // Display the loading screen while the data is being fetched
        binding.loadingAllEntrantsList.setVisibility(View.VISIBLE);
        binding.contentGroupAllEntrantsList.setVisibility(View.GONE);

        // Fetch event document from Firestore
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            Event event = documentSnapshot.toObject(Event.class);

            // Error checking
            if (event == null) {
                // Raise error with toast
                Toast.makeText(getContext(), "Error retrieving event data", Toast.LENGTH_SHORT).show();
                binding.loadingAllEntrantsList.setVisibility(View.GONE);
                return;
            }

            // call parseEventRegistration
            database.parseEventRegistration(event, documentSnapshot, task -> {
                // Populate the ListView with all entrants
                loadAllEntrantsIntoList(event);

                // Hide loading and show content
                binding.loadingAllEntrantsList.setVisibility(View.GONE);
                binding.contentGroupAllEntrantsList.setVisibility(View.VISIBLE);
            });
        });
    }

    // Private method to help with loading the data into the ListView
    private void loadAllEntrantsIntoList(Event event) {
        // List for all entrants
        ArrayList<String> data = new ArrayList<>();

        for (User u : event.getEntrantList().getWaiting()) {
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

        binding.contentGroupAllEntrantsList.setVisibility(View.VISIBLE);
        binding.loadingAllEntrantsList.setVisibility(View.GONE);
    }
}
