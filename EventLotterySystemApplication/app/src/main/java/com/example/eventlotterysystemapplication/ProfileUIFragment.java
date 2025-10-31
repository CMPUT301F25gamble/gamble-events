package com.example.eventlotterysystemapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileUIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileUIFragment extends Fragment {

    public ProfileUIFragment() {
    }

    public static ProfileUIFragment newInstance(String param1, String param2) {
        ProfileUIFragment fragment = new ProfileUIFragment();
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
        return inflater.inflate(R.layout.fragment_profile_ui, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Set up listeners for the edit buttons
        ImageButton editNameButton = view.findViewById(R.id.profile_name_edit_button);
        ImageButton editEmailButton = view.findViewById(R.id.profile_email_edit_button);
        ImageButton editPhoneButton = view.findViewById(R.id.profile_phone_edit_button);

        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit name button click
            }
        });

    }
}