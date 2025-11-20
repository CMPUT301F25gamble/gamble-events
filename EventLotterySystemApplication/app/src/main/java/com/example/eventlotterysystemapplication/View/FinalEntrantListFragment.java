package com.example.eventlotterysystemapplication.View;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentFinalEntrantListBinding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Displays a listview of all final entrants that have accepted an invitation to join the event
 * Will fetch a list of final entrants from the database to be displayed in the listview
 */

public class FinalEntrantListFragment extends Fragment {
    private FragmentFinalEntrantListBinding binding;
    private Database database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFinalEntrantListBinding.inflate(inflater, container, false);
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
        binding.finalEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(FinalEntrantListFragment.this)
                    .navigateUp();
        });

        // Display the loading screen while the data is being fetched
        binding.loadingFinalEntrantsList.setVisibility(View.VISIBLE);
        binding.contentGroupFinalEntrantsList.setVisibility(View.GONE);

        // call parseEventRegistration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            database.getEvent(eventId, task -> {

                // Error check if task is not successful
                if (!task.isSuccessful()) {
                    binding.loadingFinalEntrantsList.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch the event from the task
                Event event = task.getResult();

                // Populate the ListView with all entrants
                loadFinalEntrantsIntoList(event);

                // Hide loading and show content
                binding.loadingFinalEntrantsList.setVisibility(View.GONE);
                binding.contentGroupFinalEntrantsList.setVisibility(View.VISIBLE);
            });
        }

        // Create a CSV file of final entrants when button pressed
        binding.printListButton.setOnClickListener(v -> {
            database.getEvent(eventId,  task -> {
                if (task.isSuccessful()) {
                    Event event = task.getResult();
                    String eventName = event.getName();

                    // Get a list of finalized entrants
                    ArrayList<User> finalizedEntrants = event.getEntrantList().getFinalized();

                    try {
                        exportCSV(eventName, finalizedEntrants); // try to create the CSV file
                    } catch (IOException e) {
                        Toast.makeText(requireContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    // Private method to help with loading the data into the ListView
    private void loadFinalEntrantsIntoList(Event event) {
        // List for final entrants
        ArrayList<CharSequence> data = new ArrayList<>();
        // Adapter for listview
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                data
        );

        for (User u : event.getEntrantList().getFinalized()) {
            String name = u.getName();
            data.add(name);
        }
        // Notify the adapter
        adapter.notifyDataSetChanged();

        // Set the adapter for the ListView
        binding.finalListOfEntrantsListView.setAdapter(adapter);
    }

    /**
     * Exports a CSV file to the device's public Downloads folder.
     *
     * @param eventName The event name used for the CSV header and filename
     * @param users     The list of users to write into the CSV
     */
    private void exportCSV(String eventName, ArrayList<User> users) throws IOException {
        // Let the user know the export is being attempted
        Toast.makeText(requireContext(), "Trying to create CSV file", Toast.LENGTH_SHORT).show();

        // CSV filename that will appear in Downloads
        String fileName = eventName + "_Finalized_Entrants.csv";

        // Metadata for the file we are creating using the MediaStore API
        ContentValues values = new ContentValues(); // key-value map container to hold metadata
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName); // set name of file to appear in do
        values.put(MediaStore.Downloads.MIME_TYPE, "text/csv"); // indicate file type, which is csv
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS); // indicate path to store the file, which is downloads
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // create empty file in downloads using metadata provided
            uri = requireContext().getContentResolver()
                    .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        }

        // File creation failed if uri null
        if (uri == null) {
            Toast.makeText(requireContext(), "Unable to create file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an OutputStream to write to the file we just created
        try (OutputStream out = requireContext().getContentResolver().openOutputStream(uri);
             OutputStreamWriter writer = new OutputStreamWriter(out)) {

            // Write header line
            writer.write("Finalized List for " + eventName + "\n");

            // Write each user's name on their own line, numbered
            int i = 1;
            for (User u : users) {
                writer.write(i + ". " + u.getName() + "\n");
                ++i;
            }
        }

        // Notify upon success
        Toast.makeText(requireContext(), "CSV saved to Downloads/" + fileName, Toast.LENGTH_LONG).show();
    }
}
