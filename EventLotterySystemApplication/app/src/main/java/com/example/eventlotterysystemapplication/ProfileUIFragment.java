package com.example.eventlotterysystemapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventlotterysystemapplication.databinding.FragmentProfileUiBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileUIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileUIFragment extends Fragment {

    private FragmentProfileUiBinding binding;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileUiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}