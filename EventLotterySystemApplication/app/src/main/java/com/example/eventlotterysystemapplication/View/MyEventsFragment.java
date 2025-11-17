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
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Controller.EditEventActivity;
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

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button navigates to events page
        binding.myEventsBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(MyEventsFragment.this)
                    .navigate(R.id.action_my_events_fragment_to_events_ui_fragment);
        });

        // Show loading and hide content until it is fetched
        binding.loadingMyEvents.setVisibility(View.VISIBLE);
        binding.contentGroupMyEvents.setVisibility(View.GONE);


        // Get the user from device id
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            binding.loadingMyEvents.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Not signed in", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = user.getUid();

        Log.d("MyEventsFragment", "User ID: " + uid);

        // Firestore: Only events where organizerID == uid
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Event")
            .whereEqualTo("organizerID", uid)
            .get()
            .addOnSuccessListener(qs -> {
                // Hide loading and show content
                binding.loadingMyEvents.setVisibility(View.GONE);
                binding.contentGroupMyEvents.setVisibility(View.VISIBLE);
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
            })
                // Hide loading and add a listener to handle errors
            .addOnFailureListener(e -> {
                binding.loadingMyEvents.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            });

        // Handle the on click event for each list item
        binding.myEventsListView.setOnItemClickListener((parent, v, position, id) -> {
            String eventId = myEventDocIds.get(position); // docIds parallel list we built
            // Launch RegisterActivity as a fresh task and clear the old one
            Intent intent = new Intent(requireContext(), EditEventActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putExtra("isOwnedEvent", true);
            startActivity(intent);
        });
    }
}
