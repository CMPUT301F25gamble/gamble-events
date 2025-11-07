package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventlotterysystemapplication.databinding.FragmentPendingEntrantListBinding;

public class PendingEntrantListFragment extends Fragment {
    private FragmentPendingEntrantListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPendingEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back Button to return to Event Lists page
        binding.pendingEntrantListBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PendingEntrantListFragment.this)
                    .navigate(R.id.action_pendingEntrantList_to_entrantListSelectionFragment);
        });
    }
}
