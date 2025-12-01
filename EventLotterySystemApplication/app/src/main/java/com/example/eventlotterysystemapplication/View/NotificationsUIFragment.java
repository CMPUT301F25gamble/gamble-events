package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Notification;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentNotificationsUiBinding;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a the user's notifications from all sources according to the user's notification
 * preferences
 * Fetches the user's notifications from the database and displays in a listview
 */
public class NotificationsUIFragment extends Fragment {

    private FragmentNotificationsUiBinding binding;
    private final Database database = Database.getDatabase();
    private ArrayList<String> notificationIds = new ArrayList<>();
    private ArrayList<String> notificationTitles = new ArrayList<>();
    private ArrayAdapter<String> notificationTitlesAdapter;
    private final String TAG = "NotificationsUIFragment";

    /**
     * Required empty public constructor
     */
    public NotificationsUIFragment() {
    }

    public static NotificationsUIFragment newInstance() {
        return new NotificationsUIFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsUiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get views
        ListView notificationsListView = binding.notificationsListView;
        Button updateNotificationsButton = binding.updateNotificationsButton;

        // Set adapter
        notificationTitlesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, notificationTitles);
        notificationsListView.setAdapter(notificationTitlesAdapter);

        updateNotifications();

        // Handle when a notification is tapped
        notificationsListView.setOnItemClickListener((parent, v, position, id) -> {
            Log.d(TAG, "Notification tapped: " + notificationIds.get(position));
            Bundle notificationArgs = new Bundle();
            notificationArgs.putString("notificationId", notificationIds.get(position));

            // Navigate to view notification fragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_notifications_ui_fragment_to_adminViewNotificationFragment2, notificationArgs);
        });

        // Refresh button
        updateNotificationsButton.setOnClickListener(v -> {
            updateNotifications();
        });
    }

    /**
     * If the user wants to refresh their notifications listview page, they can do so here
     */
    private void updateNotifications() {
        // Clear previous notifications
        notificationIds.clear();
        notificationTitles.clear();
        notificationTitlesAdapter.clear();
        // Get notifications
        // Get user id
        FirebaseInstallations.getInstance().getId()
                .addOnSuccessListener(deviceId -> {
                    database.getUserFromDeviceID(deviceId, userTask -> {
                        if (!userTask.isSuccessful()) {
                            Log.d(TAG, "Error getting user");
                            NavHostFragment.findNavController(this)
                                    .navigateUp();
                        }
                        // Get userid
                        String userId = userTask.getResult().getUserID();

                        database.getUserNotificationHistory(userId, notificationTask -> {
                            if (!notificationTask.isSuccessful()) {
                                Log.d(TAG, "Error getting notifications");
                                NavHostFragment.findNavController(this)
                                        .navigateUp();
                            }

                            for (Notification notification : notificationTask.getResult()) {
                                notificationIds.add(notification.getNotificationID());
                                notificationTitles.add(notification.getTitle());
                            }

                            notificationTitlesAdapter.notifyDataSetChanged();
                        });

                    });
                });
    }
}