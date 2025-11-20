package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentAllProfilesBinding;


import java.util.ArrayList;
import java.util.List;

public class AllProfilesFragment extends Fragment {
    private FragmentAllProfilesBinding binding;
    private List<User> allUsers = new ArrayList<>();
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
            allUsers = task.getResult();
            loadProfilesIntoList(allUsers);

            // Hide loading and show content
            binding.loadingAllProfiles.setVisibility(View.GONE);
            binding.contentGroupAllProfiles.setVisibility(View.VISIBLE);
        });

        // Listener for when a profile is clicked
        binding.allProfilesList.setOnItemClickListener((parent, v, position, id) -> {
            User selectedUser = allUsers.get(position);

            // Use global variable to store the selected user's ID
            AdminSession.setSelectedUserId(selectedUser.getUserID());

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_allProfilesFragment_to_profileUIFragment);

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

        // Populating the list with all profiles
        for (User u : users) {
            displayProfileNames.add(u.getName());
        }
        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();

        // Set the adapter for the ListView
        binding.allProfilesList.setAdapter(adapter);
    }
}
