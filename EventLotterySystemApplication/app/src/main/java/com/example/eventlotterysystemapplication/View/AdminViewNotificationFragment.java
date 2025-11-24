package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Notification;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentAdminNotificationsBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentAdminViewNotificationBinding;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminViewNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminViewNotificationFragment extends Fragment {

    private FragmentAdminViewNotificationBinding binding;
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
        String notificationId = args.getNotificationId();
        Log.d(TAG, "Notification Id: " + notificationId);

        // Get notification from database
        database.getNotification(notificationId, notificationTask -> {
            if (notificationTask.isSuccessful()) {
                Log.d(TAG, "Notification retrieved");
                notification = notificationTask.getResult();
            } else {
                // Handle error
                Log.d(TAG, "Error getting notification");
            }
        });
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

        // set up back Button
        viewNotificationBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigateUp();
        });

        // Change placeholder texts to reflect notification information
        notificationSenderId.setText(notification.getSenderID());
        messageTitle.setText(notification.getTitle());
        messageContent.setText(notification.getMessage());
    }
}