package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentSettingsTosBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentSettingsUiBinding;

public class SettingsUIFragment extends Fragment {

    private FragmentSettingsUiBinding binding;

    public SettingsUIFragment () {
        // Required empty public constructor
    }

    public static SettingsUIFragment newInstance() {
        SettingsUIFragment fragment = new SettingsUIFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsUiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get buttons
        Button notificationSettingsButton = binding.notificationSettingsButton;
        Button tosButton = binding.tosButton;

        // Set click listeners
        notificationSettingsButton.setOnClickListener(v -> {
            // Navigate to notification settings fragment
            NavHostFragment.findNavController(SettingsUIFragment.this)
                    .navigate(R.id.action_settingsUIFragment_to_settings_notifications_fragment);
        });

        tosButton.setOnClickListener(v -> {
            // Navigate to TOS fragment
            NavHostFragment.findNavController(SettingsUIFragment.this)
                    .navigate(R.id.action_settingsUIFragment_to_settingsTOSFragment);
        });

    }
}
