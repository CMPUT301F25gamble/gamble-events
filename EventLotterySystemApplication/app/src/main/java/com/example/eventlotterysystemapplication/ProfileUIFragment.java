package com.example.eventlotterysystemapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileUIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileUIFragment extends Fragment {

    public ProfileUIFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_ui, container, false);
    }
}