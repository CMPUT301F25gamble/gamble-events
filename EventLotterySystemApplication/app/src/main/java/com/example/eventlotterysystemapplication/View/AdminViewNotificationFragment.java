package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Notification;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentAdminNotificationsBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentAdminViewNotificationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminViewNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminViewNotificationFragment extends Fragment {

    private FragmentAdminViewNotificationBinding binding;
    private String notificationId;
    private Notification notification;
    private final Database database = Database.getDatabase();
    private final String TAG = this.getClass().getSimpleName();

    public AdminViewNotificationFragment() {
        // Required empty public constructor
    }

    public static AdminViewNotificationFragment newInstance() {
        AdminViewNotificationFragment fragment = new AdminViewNotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get notification id from arguments
        assert getArguments() != null;
        AdminViewNotificationFragmentArgs args = AdminViewNotificationFragmentArgs.fromBundle(getArguments());
        notificationId = args.getNotificationId();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminViewNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get views
        ImageButton viewNotificationBackButton = binding.viewNotificationBackButton;
        TextView notificationSenderId = binding.notificationSenderId;
        TextView messageTitle = binding.messageTitle;
        TextView messageContent = binding.messageContent;
        TextView notificationEventId = binding.notificationEventId;
        TextView notificationSendTime = binding.notificationSendTime;
        TextView notificationChannelName = binding.notificationChannelName;
        Button goToEventButton = binding.goToEventButton;

        // set up back Button
        viewNotificationBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigateUp();
        });

        // Get notification from database
        database.getNotification(notificationId, task -> {
            if (task.isSuccessful()) {
                notification = task.getResult();
                Log.d(TAG, "Retrieved notification");

                // Set notification information in views
                notificationSenderId.setText("Sender Id: " + notification.getSenderID());
                messageTitle.setText(notification.getTitle());
                messageContent.setText(notification.getMessage());
                notificationEventId.setText("Event Id: " + notification.getEventID());

                // Process notification send time
                Timestamp timestamp = notification.getNotificationSendTime();
                Date date = timestamp.toDate();

                notificationSendTime.setText("Send Time: " + date);
                notificationChannelName.setText("Channel Name: " + notification.getChannelName());

                // Set up button to go to event
                goToEventButton.setOnClickListener(v -> {
                    // Set selected bottom navigation menu item to events
                    BottomNavigationView adminBottomNavMenu = requireActivity()
                            .findViewById(R.id.admin_bottom_nav_menu);
                    adminBottomNavMenu.setSelectedItemId(R.id.eventsUIFragment);

                    Bundle args = new Bundle();
                    args.putString("eventId", notification.getEventID());

                    // Navigate to the appropriate event
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_adminViewNotificationFragment_to_eventsUIFragment, args);
                });

            } else {
                Log.d(TAG, "Error getting notification");
            }
        });
    }
}