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
import android.widget.Toast;

import com.example.eventlotterysystemapplication.Controller.AdminActivity;
import com.example.eventlotterysystemapplication.Controller.ContentActivity;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Notification;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentViewNotificationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewNotificationFragment extends Fragment {

    private FragmentViewNotificationBinding binding;
    private String notificationId;
    private Notification notification;
    private final Database database = Database.getDatabase();
    private final String TAG = this.getClass().getSimpleName();

    public ViewNotificationFragment() {
        // Required empty public constructor
    }

    public static ViewNotificationFragment newInstance() {
        ViewNotificationFragment fragment = new ViewNotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get notification id from arguments
        assert getArguments() != null;
        ViewNotificationFragmentArgs args = ViewNotificationFragmentArgs.fromBundle(getArguments());
        notificationId = args.getNotificationId();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewNotificationBinding.inflate(inflater, container, false);
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
                    Log.d(TAG, "Go to event button clicked");
                    // Must first check that event exists
                    database.getEvent(notification.getEventID(), eventTask -> {
                        Log.d("Database", "Checking if event exists");
                        try {
                            if (task.isSuccessful() && eventTask.getResult() != null) {
                                Log.d(TAG, String.valueOf(eventTask.getResult()));
                                Log.d(TAG, "Event exists");

                                Bundle args = new Bundle();
                                args.putString("eventId", notification.getEventID());
                                Log.d(TAG, "Navigating");

                                if (requireActivity() instanceof AdminActivity) {
                                    NavHostFragment.findNavController(this)
                                            .navigate(R.id.action_adminViewNotificationFragment_to_eventDetailScreenFragment, args);
                                } else if (requireActivity() instanceof ContentActivity) {
                                    NavHostFragment.findNavController(this)
                                            .navigate(R.id.action_viewNotificationFragment_to_event_detail_screen, args);
                                }

                            }
                        } catch (Exception e) {
                            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                            Toast toast = Toast.makeText(getContext(), "Event does not exist", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                });

            } else {
                Log.d(TAG, "Error getting notification");
            }
        });
    }
}