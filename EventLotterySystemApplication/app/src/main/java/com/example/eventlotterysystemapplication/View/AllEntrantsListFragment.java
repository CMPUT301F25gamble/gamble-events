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

        database = Database.getDatabase();

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
                    .navigateUp();
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

    /**
     * Private method to help with loading the data into the ListView
     * @param event The event whose entrants we want to load into a list
     */
    private void loadAllEntrantsIntoList(Event event) {
        // List for all entrants
        ArrayList<CharSequence> data = new ArrayList<>();
        // Adapter for listview
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                data
        );

        // Loop "waiting" users
        for (User u : event.getUserWaitingList()) {
            String status = "(WAITING)";
            String line = String.format("\n%s\n%s\n", u.getName(), status);
            // Create a SpannableString from the line
            SpannableString span = new SpannableString(line);

            // Fetch the target string for waiting
            String target = "(WAITING)";
            int start = line.indexOf(target);
            int end = start + target.length();
            // Set Waiting to bold
            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set Color to Orange
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#FF9800")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Fetch the target string for status
//            target = "Status:";
//            start = line.indexOf(target);
//            end = start + target.length();
//            // Set Status to bold
//            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add the span to the list
            data.add(span);
        }

        // Loop "chosen" users
        for (User u : event.getUserChosenList()) {
            String status = "(CHOSEN)";
            String line = String.format("\n%s\n%s\n", u.getName(), status);
            // Create a SpannableString from the line
            SpannableString span = new SpannableString(line);

            // Fetch the target string for chosen
            String target = "(CHOSEN)";
            int start = line.indexOf(target);
            int end = start + target.length();
            // Set Waiting to bold
            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set Color to Orange
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Fetch the target string for status
//            target = "Status:";
//            start = line.indexOf(target);
//            end = start + target.length();
//            // Set Status to bold
//            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add the span to the list
            data.add(span);
        }

        // Loop "cancelled" users
        for (User u : event.getUserCancelledList()) {
            String status = "(CANCELLED)";
            String line = String.format("\n%s\n%s\n", u.getName(), status);
            // Create a SpannableString from the line
            SpannableString span = new SpannableString(line);

            // Fetch the target string for cancelled
            String target = "(CANCELLED)";
            int start = line.indexOf(target);
            int end = start + target.length();
            // Set Waiting to bold
            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set Color to Orange
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#F44336")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Fetch the target string for status
//            target = "Status:";
//            start = line.indexOf(target);
//            end = start + target.length();
//            // Set Status to bold
//            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add the span to the list
            data.add(span);
        }

        // Loop "finalized" users
        for (User u : event.getUserFinalizedList()) {
            String status = "(FINALIZED)";
            String line = String.format("\n%s\n%s\n", u.getName(), status);
            // Create a SpannableString from the line
            SpannableString span = new SpannableString(line);

            // Fetch the target string for finalized
            String target = "(FINALIZED)";
            int start = line.indexOf(target);
            int end = start + target.length();
            // Set Waiting to bold
            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set Color to Orange
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#4A4A4A")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Fetch the target string for status
//            target = "Status:";
//            start = line.indexOf(target);
//            end = start + target.length();
//            // Set Status to bold
//            span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add the span to the list
            data.add(span);
        }

        // Notify the adapter of all changes
        adapter.notifyDataSetChanged();

        // Set the adapter for the ListView
        binding.allListOfEntrantsListView.setAdapter(adapter);
    }
}
