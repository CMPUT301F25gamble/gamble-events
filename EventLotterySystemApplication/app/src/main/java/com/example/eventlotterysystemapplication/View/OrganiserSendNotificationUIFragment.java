package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.Controller.NotificationSender;
import com.example.eventlotterysystemapplication.View.OrganiserSendNotificationUIFragmentArgs;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentOrganiserSendNotificationUiBinding;

/**
 * OrganiserSendNotificationUIFragment
 * Allows the user who is an organiser to send a message/notification to all entrants of the
 * selected category based on previous input in {@link OrganiserNotificationsUIFragment}
 * EditTextview requires text to be inputted, otherwise error message will appear
 * 'Send Notification' button will add the notification to the database and send the notification
 * to appropriate entrants
 */

public class OrganiserSendNotificationUIFragment extends Fragment {

    private FragmentOrganiserSendNotificationUiBinding binding;
    private OrganiserSendNotificationUIFragmentArgs args;

    private String notificationType;

    private String eventId;

    public OrganiserSendNotificationUIFragment() {
        // Required empty public constructor
    }

    public static OrganiserSendNotificationUIFragment newInstance(String notification_type) {
        OrganiserSendNotificationUIFragment fragment = new OrganiserSendNotificationUIFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OrganiserSendNotificationUIFragmentArgs args = OrganiserSendNotificationUIFragmentArgs
                .fromBundle(getArguments());

        notificationType = args.getNotificationType();
        eventId = args.getEventId();

        Log.d("OrganiserSendNotificationUIFragment", "EventId: " + eventId);
        Log.d("OrganiserSendNotificationUIFragment", "NotificationType: " + notificationType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganiserSendNotificationUiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get views
        TextView notificationHeader = binding.sendNotificationHeader;
        EditText notificationMessage = binding.notificationMessageContent;

        // get buttons
        ImageButton backButton = binding.sendNotificationBackButton;
        Button sendNotificationButton = binding.sendNotificationButton;

        backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(OrganiserSendNotificationUIFragment.this)
                    .navigate(R.id.action_organiserSendNotificationUIFragment_to_organiserNotificationsUIFragment);

        });

        // Set header text
        switch (notificationType) {
            case "waitlist":
                notificationHeader.setText("Waitlist Entrants Notification");
                break;
            case "chosen":
                notificationHeader.setText("Chosen Entrants Notification");
                break;
            case "cancelled":
                notificationHeader.setText("Cancelled Entrants Notification");
                break;
        }

        // Send the notification
        String messageContent = notificationMessage.getText().toString();

        if (messageContent.isEmpty()) {
            notificationMessage.setError("Message is required");
        }

        sendNotificationButton.setOnClickListener(v -> {

            Log.d("OrganiserSendNotificationUIFragment", "Sent notification button clicked");

            String token = "cVmUisgDQUaawt3q0JHrGg:APA91bFdrg7fRCPxQHZ50pIvSQR1tkxtsefnLVh70dgKAun1XNiGc689gzaYrcSKJb7ymvDJC9E4_PFWsAtoQxCgoR4wAW7AOjDWqWga6gzYkVK_674U8VA";
            String title = notificationType + " Notification";
            String channelName = "lotteryWinNotification";

            NotificationSender.sendNotification(token, title, messageContent, eventId, channelName);
        });

        // Add notification to database here
    }
}
