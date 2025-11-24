package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentAdminNotificationsBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentAdminViewNotificationBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminViewNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminViewNotificationFragment extends Fragment {

    private FragmentAdminViewNotificationBinding binding;
    private String notificationId;

    public AdminViewNotificationFragment() {
        // Required empty public constructor
    }

    public static AdminViewNotificationFragment newInstance() {
        AdminViewNotificationFragment fragment = new AdminViewNotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get notification id from arguments
        AdminViewNotificationFragmentArgs args = AdminViewNotificationFragmentArgs.fromBundle(getArguments());
        notificationId = args.getNotificationId();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminViewNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get views
    }
}