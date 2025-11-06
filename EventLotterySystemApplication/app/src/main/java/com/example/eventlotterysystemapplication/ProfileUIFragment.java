package com.example.eventlotterysystemapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.eventlotterysystemapplication.databinding.FragmentFirstTimeInputBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentProfileUiBinding;
import com.google.firebase.installations.FirebaseInstallations;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileUIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileUIFragment extends Fragment {
    private FragmentProfileUiBinding binding;
    private Database database;
    private User currentUser;

    /*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileUIFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileUI.
     */
    // TODO: Rename and change types and number of parameters
    /*
    public static ProfileUIFragment newInstance(String param1, String param2) {
        ProfileUIFragment fragment = new ProfileUIFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileUiBinding.inflate(inflater, container, false);
        database = new Database();


        // Fetch user using Device ID
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {
            database.getUserFromDeviceID(deviceId, task -> {
                if (task.isSuccessful()) {
                    currentUser = task.getResult();

                    // Prepopulate EditText values
                    binding.profileName.setText(currentUser.getName());
                    binding.profileEmail.setText(currentUser.getEmail());
                    binding.profilePhone.setText(currentUser.getPhoneNumber());

                } else {Log.e("ProfileUIFragment", "Failed to get user", task.getException());
                }
            });
        });

        // When update profile button is clicked update user's data
        binding.updateProfileButton.setOnClickListener(v -> {
            if (currentUser != null) {
                // Fetch the values in the EditTexts
                String userName = binding.profileName.getText().toString().trim();
                String userEmail = binding.profileEmail.getText().toString().trim();
                String userPhone = binding.profilePhone.getText().toString().trim();

                // Warn users to not leave name and email empty
                if (userName.isEmpty()) {
                    binding.profileName.setError("Name is required");
                    return;
                }
                if (userEmail.isEmpty()) {
                    binding.profileEmail.setError("Email is required");
                    return;
                }

                // Update the user's params
                currentUser.setName(userName);
                currentUser.setEmail(userEmail);
                currentUser.setPhoneNumber(userPhone);

                // Update the database
                database.modifyUser(currentUser, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        Log.e("ProfileUIFragment", "Error updating user", task.getException());
                    }
                });
            } else {
                Toast.makeText(getContext(), "User not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }
}