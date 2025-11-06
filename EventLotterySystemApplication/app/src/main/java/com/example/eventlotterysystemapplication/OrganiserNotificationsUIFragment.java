package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentOrganiserNotificationUiBinding;

public class OrganiserNotificationsUIFragment extends Fragment {

    private FragmentOrganiserNotificationUiBinding binding;

    public OrganiserNotificationsUIFragment() {
        // Required empty constructor
    }

    public static NotificationsUIFragment newInstance(String param1, String param2) {
        NotificationsUIFragment fragment = new NotificationsUIFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.fragment_notifications_ui, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get buttons
        Button waitlistEntrantsButton = binding.waitlistEntrantsButton;
        Button chosenEntrantsButton = binding.chosenEntrantsButton;
        Button cancelledEntrantsbutton = binding.cancelledEntrantsButton;

    }
}
