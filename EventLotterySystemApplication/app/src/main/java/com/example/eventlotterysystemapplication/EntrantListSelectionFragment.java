package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventlotterysystemapplication.databinding.FragmentEntrantListSelectionBinding;
import com.example.eventlotterysystemapplication.databinding.FragmentEventsUiBinding;

public class EntrantListSelectionFragment extends Fragment {

    private FragmentEntrantListSelectionBinding binding;

    public EntrantListSelectionFragment() {
        // required empty constructor
    }

    public static EntrantListSelectionFragment newInstance() {
        EntrantListSelectionFragment fragment = new EntrantListSelectionFragment();
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
        binding = FragmentEntrantListSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
