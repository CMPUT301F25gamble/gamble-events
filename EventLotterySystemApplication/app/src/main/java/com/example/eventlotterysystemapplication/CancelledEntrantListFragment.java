package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentCancelledEntrantListBinding;

public class CancelledEntrantListFragment extends Fragment {
    private FragmentCancelledEntrantListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCancelledEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back Button to return to Event Lists page
        binding.cancelledEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(CancelledEntrantListFragment.this)
                    .navigate(R.id.action_cancelledEntrantListFragment_to_entrantListSelectionFragment);
        });
    }
}
