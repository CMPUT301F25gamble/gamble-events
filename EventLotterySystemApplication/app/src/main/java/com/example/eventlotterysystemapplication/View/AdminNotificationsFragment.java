package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentAdminNotificationsBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminNotificationsFragment extends Fragment {

    private FragmentAdminNotificationsBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> notificationTitlesList = new ArrayList<>();
    private ArrayList<String> notificationIdList = new ArrayList<>();
    private ArrayAdapter<String> notificationTitlesAdapter;
    private final String TAG = this.getClass().getSimpleName();

    public static AdminNotificationsFragment newInstance() {
        AdminNotificationsFragment fragment = new AdminNotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get views
        ListView notificationsListView = binding.notificationsListView;
        Button updateNotificationsButton = binding.updateNotificationsButton;

        // set adapter
        notificationTitlesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, notificationTitlesList);
        notificationsListView.setAdapter(notificationTitlesAdapter);

        // get notifications from database
        db.collection("Notification").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    // Iterate through document snapshots
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String title = documentSnapshot.getString("title");
                        String notificationId = documentSnapshot.getString("notificationID");

                        // add to arrays
                        notificationTitlesList.add(title);
                        notificationIdList.add(notificationId);
                    }

                    // notify adapter dataset has changed
                    notificationTitlesAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Failed to load notification");
                });

        // handle when admin taps a notification
        notificationsListView.setOnItemClickListener((parent, v, position, id) -> {
            Log.d(TAG, "Notification tapped: " + notificationTitlesList.get(position));
            Bundle notificationArgs = new Bundle();
            notificationArgs.putString("notificationId", notificationIdList.get(position));

            // Navigate to view notification fragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_adminNotifications_to_adminViewNotificationFragment, notificationArgs);
        });

        // handle updating notifications
        updateNotificationsButton.setOnClickListener(v -> {
            db.collection("Notification").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        // Iterate through document snapshots
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            String title = documentSnapshot.getString("title");
                            String notificationId = documentSnapshot.getString("notificationID");

                            // add to arrays
                            notificationTitlesList.add(title);
                            notificationIdList.add(notificationId);
                        }

                        // notify adapter dataset has changed
                        notificationTitlesAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "Failed to load notification");
                    });
        });
    }

}