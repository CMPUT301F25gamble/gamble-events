package com.example.eventlotterysystemapplication.View;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.View.EntrantListSelectionFragment;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
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
        ArrayList<CharSequence> data = new ArrayList<>();
        // Adapter for listview
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                data
        );

        //Toast.makeText(getContext(), "entrant list: " + event.getEntrantList().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(requireContext(), "Waiting: " + event.getEntrantList().getWaiting().size() , Toast.LENGTH_SHORT).show();

        for (User u : event.getEntrantList().getWaiting()) {
            String line = String.format("%-60s %10s", u.getName(), "(waiting)");
            // Create a SpannableString from the line
            SpannableString span = new SpannableString(line);

            // Fetch the target string
            String target = "(waiting)";
            int start = line.indexOf(target);
            int end = start + target.length();

            // Set Waiting to bold
            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set Color to Orange
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#FF9800")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add the span to the list and notify the adapter
            data.add(span);
            adapter.notifyDataSetChanged();
        }
        for (User u : event.getEntrantList().getChosen()) {
            String line = String.format("%-60s %10s", u.getName(), "(chosen)");
            // Create a SpannableString from the line
            SpannableString span = new SpannableString(line);

            // Fetch the target string
            String target = "(chosen)";
            int start = line.indexOf(target);
            int end = start + target.length();

            // Set Waiting to bold
            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set Color to Green
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add the span to the list and notify the adapter
            data.add(span);
            adapter.notifyDataSetChanged();
        }
        for (User u : event.getEntrantList().getCancelled()) {
            String line = String.format("%-60s %10s", u.getName(), "(cancelled)");
            // Create a SpannableString from the line
            SpannableString span = new SpannableString(line);

            // Fetch the target string
            String target = "(cancelled)";
            int start = line.indexOf(target);
            int end = start + target.length();

            // Set Waiting to bold
            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set Color to Red
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#F44336")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add the span to the list and notify the adapter
            data.add(span);
            adapter.notifyDataSetChanged();
        }
        for (User u : event.getEntrantList().getFinalized()) {
            String line = String.format("%-60s %10s", u.getName(), "(finalized)");
            // Create a SpannableString from the line
            SpannableString span = new SpannableString(line);

            // Fetch the target string
            String target = "(finalized)";
            int start = line.indexOf(target);
            int end = start + target.length();

            // Set Waiting to bold
            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set Color to Black (maybe I'll change to GREY)
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#4A4A4A")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add the span to the list and notify the adapter
            data.add(span);
            adapter.notifyDataSetChanged();
        }

        // Set the adapter for the ListView
        binding.allListOfEntrantsListView.setAdapter(adapter);
    }
}
