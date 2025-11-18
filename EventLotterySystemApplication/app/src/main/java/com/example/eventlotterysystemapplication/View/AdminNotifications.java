package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventlotterysystemapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminNotifications#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminNotifications extends Fragment {


    public static AdminNotifications newInstance(String param1, String param2) {
        AdminNotifications fragment = new AdminNotifications();
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
        return inflater.inflate(R.layout.fragment_admin_notifications, container, false);
    }
}