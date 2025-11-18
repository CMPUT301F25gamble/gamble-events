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
import com.example.eventlotterysystemapplication.databinding.FragmentAllProfilesBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentCancelledEntrantListBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentProfileUiBinding;

import java.util.ArrayList;
import java.util.List;

public class AllProfilesFragment extends Fragment {
    private FragmentAllProfilesBinding binding;
    private Database database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllProfilesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = new Database();

//        // Safely read arguments
//        Bundle args = getArguments();
//
//        if (args == null || !args.containsKey("eventID")) {
//            // No event ID -> we can't do anything, so show a message and go back
//            Toast.makeText(requireContext(), "Error: missing event ID", Toast.LENGTH_SHORT).show();
//            NavHostFragment.findNavController(this).navigateUp();
//            return;
//        }
//
//        String eventId = args.getString("eventID");
//        if (eventId == null || eventId.isEmpty()) {
//            Toast.makeText(requireContext(), "Error: invalid event ID", Toast.LENGTH_SHORT).show();
//            NavHostFragment.findNavController(this).navigateUp();
//            return;
//        }

        // Back Button to return to Event Lists page
        binding.browseProfileBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(AllProfilesFragment.this)
                    .navigateUp();
        });

        // Display the loading screen while the data is being fetched
        binding.loadingAllProfiles.setVisibility(View.VISIBLE);
        binding.contentGroupAllProfiles.setVisibility(View.GONE);

        // Call database to fetch all users
        database.getAllUsers(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(requireContext(), "Failed to load profiles",
                        Toast.LENGTH_SHORT).show();
                binding.loadingAllProfiles.setVisibility(View.GONE);
                return;
            }

            // Populate the list with all profiles
            List<User> users = task.getResult();
            loadProfilesIntoList(users);

            // Hide loading and show content
            binding.loadingAllProfiles.setVisibility(View.GONE);
            binding.contentGroupAllProfiles.setVisibility(View.VISIBLE);
        });
    }

    // Private method to help with loading the data into the ListView
    private void loadProfilesIntoList(List<User> users) {
        // List for all profiles
        ArrayList<String> displayProfileNames = new ArrayList<>();
        // Adapter for listview
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                displayProfileNames
        );

        for (User u : users) {
            displayProfileNames.add(u.getName());
        }
        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();

        // Set the adapter for the ListView
        binding.allProfilesList.setAdapter(adapter);
    }
}
