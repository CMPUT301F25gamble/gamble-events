package com.example.eventlotterysystemapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.AdminSession;
import com.example.eventlotterysystemapplication.Controller.AdminActivity;
import com.example.eventlotterysystemapplication.Controller.ContentActivity;
import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.User;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentSettingsUiBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * SettingsUIFragment
 * Menu to display buttons to navigate to sub-menus for notification settings and important
 * information
 * Non-admin users will have two buttons labelled as such that navigate to the fragments
 * Admin users will have the previous buttons and an addition button that swaps to admin view
 */

public class SettingsUIFragment extends Fragment {

    private FragmentSettingsUiBinding binding;

    Database database = Database.getDatabase();

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
        Button adminViewButton = binding.adminViewButton;

        // set the admin view text
        if (getActivity() instanceof ContentActivity) {
            adminViewButton.setText("Switch to Admin View");
        } else {
            adminViewButton.setText("Switch to User View");
            // Reset admin mode and user ID
            AdminSession.setAdminMode(false);
            AdminSession.setSelectedUserId(null);
        }

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

        // Show loading and hide content until it is fetched
        binding.loadingSettingsScreen.setVisibility(View.VISIBLE);
        binding.contentGroupSettingsScreen.setVisibility(View.GONE);

        // get admin status of user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        database.getUser(uid, task -> {
            if (task.isSuccessful()) {
                User adminUser = task.getResult();
                binding.contentGroupSettingsScreen.setVisibility(View.VISIBLE);
                adminViewButton.setVisibility(adminUser.isAdmin() ? View.VISIBLE : View.GONE);

            } else {
                Toast.makeText(getContext(), "Error getting user", Toast.LENGTH_SHORT).show();
            }
            // Hide loading and show content
            binding.loadingSettingsScreen.setVisibility(View.GONE);
        });


        adminViewButton.setOnClickListener(v -> {
            if (getActivity() instanceof ContentActivity) {
                Intent goToAdminViewIntent = new Intent(requireContext(), AdminActivity.class);

                // Set admin mode and selected user id
                AdminSession.setAdminMode(true);

                startActivity(goToAdminViewIntent);
                getActivity().finish();
            } else {
                Intent goToEntrantViewIntent = new Intent(requireContext(), ContentActivity.class);
                startActivity(goToEntrantViewIntent);
                getActivity().finish();
            }
        });
    }
}
