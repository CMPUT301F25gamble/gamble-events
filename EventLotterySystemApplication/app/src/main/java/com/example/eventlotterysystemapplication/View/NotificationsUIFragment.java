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
    private ArrayList<Notification> notifications;
    private ArrayAdapter<Notification> notificationArrayAdapter;
    private final String TAG = "NotificationsUIFragment";

    private String userId;

    public NotificationsUIFragment() {
        // Required empty public constructor
    }

    public static NotificationsUIFragment newInstance() {
        return new NotificationsUIFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        userId = userTask.getResult().getUserID();
                        updateNotifications(userId);
                    });
                });
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
        notificationsListView.setAdapter(notificationArrayAdapter);

        updateNotifications(userId);
    }

    private void updateNotifications(String userId) {
        // Clear previous notifications
        notifications.clear();
        notificationArrayAdapter.clear();
        // Get notifications
        database.getUserNotificationHistory(userId, notificationTask -> {
            if (!notificationTask.isSuccessful()) {
                Log.d(TAG, "Error getting notifications");
                NavHostFragment.findNavController(this)
                        .navigateUp();
            }
            notifications = new ArrayList<>(notificationTask.getResult());
            notificationArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, notifications);
        });

        notificationArrayAdapter.notifyDataSetChanged();
    }
}