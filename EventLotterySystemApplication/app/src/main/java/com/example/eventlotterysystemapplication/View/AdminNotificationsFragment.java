package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.eventlotterysystemapplication.databinding.FragmentAdminNotificationsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminNotificationsFragment extends Fragment {

    private FragmentAdminNotificationsBinding binding;

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

        ArrayAdapter<String> notificationsAdapter;

        // get views
        ListView notificationsListView = binding.notificationsListview;
        Button refreshNotificationsButton = binding.refreshNotificationsButton;

        // set up adapter




    }
}