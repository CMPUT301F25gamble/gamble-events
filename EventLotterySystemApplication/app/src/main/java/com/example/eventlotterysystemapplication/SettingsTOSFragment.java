package com.example.eventlotterysystemapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.eventlotterysystemapplication.databinding.FragmentSettingsNotificationsBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentSettingsTosBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsTOSFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsTOSFragment extends Fragment {

    private FragmentSettingsTosBinding binding;

    public SettingsTOSFragment() {
        // Required empty public constructor
    }

    public static SettingsTOSFragment newInstance(String param1, String param2) {
        SettingsTOSFragment fragment = new SettingsTOSFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsTosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // back button
        ImageButton backButton = binding.backButton;

        backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(SettingsTOSFragment.this)
                    .navigateUp();
        });
    }
}