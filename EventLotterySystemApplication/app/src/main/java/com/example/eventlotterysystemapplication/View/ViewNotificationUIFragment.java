package com.example.eventlotterysystemapplication.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentViewNotificationUiBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewNotificationUIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewNotificationUIFragment extends Fragment {
    private FragmentViewNotificationUiBinding binding;

    public ViewNotificationUIFragment() {
        // Required empty public constructor
    }

    public static ViewNotificationUIFragment newInstance() {
        return new ViewNotificationUIFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewNotificationUiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}