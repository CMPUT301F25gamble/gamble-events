package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.eventlotterysystemapplication.databinding.FragmentSettingsNotificationsBinding;

/**
 * SettingsNotificationFragment
 * Fragment for displaying notification related settings
 * Allows the user to opt in or out of some notifications from the app, namely Lottery
 * notifications, admin notifications, and organiser notifications
 * This fragment will then update the user's preferences in the database
 * Currently this fragment does not update the user's preferences regardless of the user's input
 */

public class SettingsNotificationsFragment extends Fragment {

    private FragmentSettingsNotificationsBinding binding;

    public SettingsNotificationsFragment() {
        // Required empty public constructor
    }

    public static SettingsNotificationsFragment newInstance(String param1, String param2) {
        SettingsNotificationsFragment fragment = new SettingsNotificationsFragment();
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
        binding = FragmentSettingsNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get back button
        ImageButton backButton = binding.backButton;

        // Set onclick listener
        backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(SettingsNotificationsFragment.this)
                    .navigateUp();
        });

    }
}