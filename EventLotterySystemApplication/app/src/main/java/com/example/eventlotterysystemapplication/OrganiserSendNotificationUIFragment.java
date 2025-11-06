package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventlotterysystemapplication.databinding.FragmentOrganiserNotificationUiBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentOrganiserSendNotificationUiBinding;

public class OrganiserSendNotificationUIFragment extends Fragment {

    private FragmentOrganiserSendNotificationUiBinding binding;
    private OrganiserSendNotificationUIFragmentArgs args;

    private String notificationType;

    public OrganiserSendNotificationUIFragment() {
        // Required empty public constructor
    }

    public static NotificationsUIFragment newInstance(String notification_type) {
        NotificationsUIFragment fragment = new NotificationsUIFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OrganiserSendNotificationUIFragmentArgs args = OrganiserSendNotificationUIFragmentArgs
                .fromBundle(getArguments());

        notificationType = args.getNotificationType();
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
        Button sendNotificationButton = binding.sendNotificationButton;  // get the button

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

        // Add notification to database here
    }
}
