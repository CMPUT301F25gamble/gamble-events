package com.example.eventlotterysystemapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Controller.EditEventActivity;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentMyEventsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Displays the user's current events that they are organising
 * Fetches a list of events filtered by a matching organiser id to the user's id and displays
 * in a listview
 */

public class MyEventsFragment extends Fragment {
    private FragmentMyEventsBinding binding;
    private ArrayAdapter<String> myEventNamesAdapter;
    private final ArrayList<String> myEventNames = new ArrayList<>();
    private final ArrayList<String> myEventDocIds = new ArrayList<>();
    // Admin flow
    private String userId;
    private boolean isAdminMode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyEventsBinding.inflate(inflater, container, false);

        // Populate the list of user's created events
        myEventNamesAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                myEventNames
        );
        binding.myEventsListView.setAdapter(myEventNamesAdapter);

        // Fetch the global user ID and admin mode from the AdminSession class
        userId = AdminSession.getSelectedUserId();
        isAdminMode = AdminSession.getAdminMode();
        Log.d("ProfileUIFragment",
                "userId arg = " + userId + "; isAdminMode = " + isAdminMode);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Back button navigates to events page
        // Source: ChatGPT (I didn't know how to fix this bug)
        binding.myEventsBackButton.setOnClickListener(v -> {
            int hostId = isAdminMode
                    ? R.id.admin_nav_host_fragment
                    : R.id.content_nav_host_fragment;

            NavHostFragment navHostFragment =
                    (NavHostFragment) requireActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(hostId);

            if (navHostFragment == null) {
                Log.e("NAV", "NavHostFragment is NULL for hostId=" + hostId);
                return; // prevents crash
            }

            NavController navController = navHostFragment.getNavController();

            if (isAdminMode) {
                navController.navigate(R.id.profileUIFragment);
            } else {
                navController.navigate(R.id.events_ui_fragment);
            }
        });

        // Show loading and hide content until it is fetched
        binding.loadingMyEvents.setVisibility(View.VISIBLE);
        binding.contentGroupMyEvents.setVisibility(View.GONE);

        // Get the user/device id
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            binding.loadingMyEvents.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Not signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        // Admin flow: use the selected user ID
        if (isAdminMode) {
            uid = userId;
            // Display the organizer's name + "'s Events" under admin control
            Database.getDatabase().getUser(uid, task -> {
                if (task.isSuccessful()) {
                    User organizer = task.getResult();
                    String organizerName = organizer.getName();
                    binding.myEventsText.setText(organizerName + "'s Events");
                    Log.d("MyEvents", "Organizer name = " + organizerName);
                }
            });
        }

        Log.d("MyEventsFragment", "User ID: " + uid);

        // Firestore: Only events where organizerID == uid
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Event")
            .whereEqualTo("organizerID", uid)
            .get()
            .addOnSuccessListener(qs -> {
                myEventNames.clear();
                myEventDocIds.clear();

                for (DocumentSnapshot doc : qs.getDocuments()) {
                    String myEventName = doc.getString("name");

                    // Fallback on the doc ID if event name is missing
                    if (myEventName == null) {
                        myEventName = doc.getId();
                        myEventNames.add(myEventName);
                    } else {
                        myEventNames.add(myEventName);
                    }

                    // Add docId in parallel list
                    myEventDocIds.add(doc.getId());
                }
                // Notify the adapter that the data set has changed
                myEventNamesAdapter.notifyDataSetChanged();

                // Hide loading and show content
                binding.loadingMyEvents.setVisibility(View.GONE);
                binding.contentGroupMyEvents.setVisibility(View.VISIBLE);
            })
                // Hide loading and add a listener to handle errors
            .addOnFailureListener(e -> {
                binding.loadingMyEvents.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            });

            // Handle the on click event for each list item
            binding.myEventsListView.setOnItemClickListener((parent, v, position, id) -> {
                String eventId = myEventDocIds.get(position); // docIds parallel list we built
                // Bundle to indicate that we are coming from MyEventsFragment
                Bundle args = new Bundle();
                args.putString("eventId", myEventDocIds.get(position));
                // DEFAULT VALUE
                args.putBoolean("isOwnedEvent", false); // Does not matter since you're admin

                if (isAdminMode) {
                    NavHostFragment.findNavController(MyEventsFragment.this)
                            .navigate(R.id.action_myEventsFragment_to_eventDetailScreenFragment, args);
                } else {
                    // Launch RegisterActivity as a fresh task and clear the old one
                    Intent intent = new Intent(requireContext(), EditEventActivity.class);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("isOwnedEvent", true);
                    startActivity(intent);
                }
            });
    }
}
