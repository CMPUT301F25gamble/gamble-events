package com.example.eventlotterysystemapplication.View;

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

import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.ImageStorage;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentAllEventPostersUserBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A fragment that displays all the users that have event poster images
 */
public class AllEventPostersUserFragment extends Fragment {
    private FragmentAllEventPostersUserBinding binding;
    private List<User> allUsersThatHavePosters = new ArrayList<>();
    private Map<String, List<String>> userPosters; // user id -> list of poster download urls

    private final String TAG = "AllEventPostersUserFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllEventPostersUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Clear list to prevent duplicates on back button from one of the user event posters fragments
        allUsersThatHavePosters.clear();

        // Display loading screen when fetching from database and storage bucket
        binding.loadingAllEvents.setVisibility(View.VISIBLE);
        binding.contentGroupAllEvents.setVisibility(View.GONE);

        // Fetch the user-image url hashmap
        ImageStorage.getInstance().fetchAllPosterImageUrlsByUserId(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(requireContext(), "Failed to fetch users that have uploaded posters",
                        Toast.LENGTH_SHORT).show();
                binding.loadingAllEvents.setVisibility(View.GONE);
                return;
            }

            userPosters = task.getResult();
            List<Task<User>> userTasks = new ArrayList<>();

            // Add all users in userPosters to allUsersThatHavePosters
            for (String userId : userPosters.keySet()) {
                TaskCompletionSource<User> userTcs = new TaskCompletionSource<>();
                Database.getDatabase().getUser(userId, userTask -> {
                    userTcs.setResult(userTask.getResult());
                    if (!userTask.isSuccessful()) {
                        Toast.makeText(requireContext(), "Failed to fetch user from database",
                                Toast.LENGTH_SHORT).show();
                        binding.loadingAllEvents.setVisibility(View.GONE);
                        return;
                    }

                    User user = userTask.getResult();
                    allUsersThatHavePosters.add(user);
                });
                userTasks.add(userTcs.getTask());
            }

            // Only when all the user tasks have been completed we can bind the list view
            Tasks.whenAllComplete(userTasks).addOnCompleteListener(userListTask -> {
                if (!userListTask.isSuccessful()) {
                    Toast.makeText(requireContext(), "Failed to fetch users from database",
                            Toast.LENGTH_SHORT).show();
                    binding.loadingAllEvents.setVisibility(View.GONE);
                    return;
                }

                loadUsersIntoListView();
            });
        });

        // Goes to the user event posters fragment that contains all the uploaded images from the user
        binding.allUsersHavingPosterList.setOnItemClickListener((parent, v, position, id) -> {
            User selectedUser = allUsersThatHavePosters.get(position);
            String username = selectedUser.getName();
            // Use global variable to store the selected user's ID
            AdminSession.setSelectedUserId(selectedUser.getUserID());

            List<String> posterUrls = userPosters.get(selectedUser.getUserID());
            Log.d(TAG, "user id: " + selectedUser.getUserID());
            Log.d(TAG, "poster urls: " + posterUrls);
            if (posterUrls == null || posterUrls.isEmpty()) {
                Toast.makeText(getContext(), "No posters found for this user", Toast.LENGTH_SHORT).show();
                return;
            }

            // setting a string array size of 0 automatically allocates it to the length of the original array list
            String[] posterUrlsArray = posterUrls.toArray(new String[0]);

            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putStringArray("posterUrls", posterUrlsArray);

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_allEventPostersUserFragment_to_userEventPostersFragment, bundle);

        });
    }

    /**
     * Loads the all users that have posters list into the ListView
     */
    private void loadUsersIntoListView() {
        if (allUsersThatHavePosters == null) {
            Log.e(TAG, "All Users that have posters list is NULL!");
            return;
        }
        if (getContext() == null) {
            // Prevents crashing when repeatedly spamming the navbar
            return;
        }

        // Fetch each user from the database and add their names to the listview
        ArrayList<String> displayProfileNames = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                displayProfileNames
        );

        // Sort users list by name so that it remains fixed
        allUsersThatHavePosters.sort(Comparator.comparing(User::getName));
        for (User u : allUsersThatHavePosters) {
            displayProfileNames.add(u.getName());
        }

        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();

        // Set the adapter for the ListView
        binding.allUsersHavingPosterList.setAdapter(adapter);

        // Hide loading and show content
        binding.loadingAllEvents.setVisibility(View.GONE);
        binding.contentGroupAllEvents.setVisibility(View.VISIBLE);
    }
}