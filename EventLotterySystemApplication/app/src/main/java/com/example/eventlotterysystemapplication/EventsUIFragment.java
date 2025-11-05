package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsUIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsUIFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EventsUIFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsUIFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsUIFragment newInstance(String param1, String param2) {
        EventsUIFragment fragment = new EventsUIFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events_ui, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cancel button to go back to previous register screen
        binding.registerCancelButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstTimeUserInfoFragment.this)
                    .navigate(R.id.action_first_time_user_info_fragment_to_register_screen_fragment);
        });

        // Confirm button to get user inputs and move to content activity
        binding.registerConfirmButton.setOnClickListener(v -> {
            // Get user input from text fields
            // Todo: add functionality for using these values to create a new account
            String userName = binding.nameEditText.getText().toString();
            String userEmail = binding.emailEditText.getText().toString();
            String userPhone = binding.phoneEditText.getText().toString();

            // Create new intent
            Intent nextActivityIntent = new Intent(getActivity(), ContentActivity.class);
            startActivity(nextActivityIntent);
            requireActivity().finish();  // finish the activity to free memory
        });
}