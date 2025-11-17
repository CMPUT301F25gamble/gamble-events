package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventlotterysystemapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Displays a the user's notifications from all sources according to the user's notification
 * preferences
 * Fetches the user's notifications from the database and displays in a listview
 */
public class NotificationsUIFragment extends Fragment {

    public NotificationsUIFragment() {
        // Required empty public constructor
    }

    public static NotificationsUIFragment newInstance() {
        NotificationsUIFragment fragment = new NotificationsUIFragment();
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
        return inflater.inflate(R.layout.fragment_notifications_ui, container, false);
    }
}