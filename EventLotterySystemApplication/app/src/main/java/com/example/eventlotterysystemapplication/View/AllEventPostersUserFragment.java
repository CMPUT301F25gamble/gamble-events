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

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.ImageStorage;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.databinding.FragmentAllEventPostersUserBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment that displays all the users that have event poster images
 */
public class AllEventPostersUserFragment extends Fragment {
    private FragmentAllEventPostersUserBinding binding;
    private List<User> allUsersThatHavePosters = new ArrayList<>();
    private Map<String, List<String>> userPosters;

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
    }

    private void loadUsersIntoListView() {
        if (allUsersThatHavePosters == null) {
            Log.e(TAG, "All Users that have posters list is NULL!");
            return;
        }

        // Fetch each user from the database and add their names to the listview
        ArrayList<String> displayProfileNames = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                displayProfileNames
        );

        for (User u : allUsersThatHavePosters) {
            displayProfileNames.add(u.getName());
        }

        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();

        // Set the adapter for the ListView
        binding.allPosterList.setAdapter(adapter);

        // Hide loading and show content
        binding.loadingAllEvents.setVisibility(View.GONE);
        binding.contentGroupAllEvents.setVisibility(View.VISIBLE);
    }
}