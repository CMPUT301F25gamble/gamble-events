package com.example.eventlotterysystemapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eventlotterysystemapplication.databinding.FragmentProfileUiBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileUIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileUIFragment extends Fragment {

    private FragmentProfileUiBinding binding;
    private Database database;

    public ProfileUIFragment() {
        // Required empty public constructor
    }

    public static ProfileUIFragment newInstance() {
        ProfileUIFragment fragment = new ProfileUIFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = Database.getDatabase();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileUiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Make buttons
        ImageButton nameEditButton = binding.nameEditButton;
        ImageButton emailEditButton = binding.emailEditButton;
        ImageButton phoneEditButton = binding.phoneEditButton;
        Button showHistoryButton = binding.showProfileHistoryButton;
        Button deleteAccountButton = binding.deleteProfileButton;

        // Replace placeholder text
        TextView userName = binding.profileName;
        TextView userEmail = binding.profileEmail;
        TextView userPhone = binding.profilePhone;

        // Todo: Retrieve user info from database and replace placeholder text
    }
}