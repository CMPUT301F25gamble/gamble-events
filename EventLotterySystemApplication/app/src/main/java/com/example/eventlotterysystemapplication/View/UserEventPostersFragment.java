package com.example.eventlotterysystemapplication.View;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapplication.Model.Admin;
import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.databinding.FragmentUserEventPostersBinding;

import java.util.ArrayList;
import java.util.Arrays;


public class UserEventPostersFragment extends Fragment {
   private FragmentUserEventPostersBinding binding;
   private String username;
   private ArrayList<String> posterUrls = new ArrayList<>();
   private PosterAdapter adapter;

    private class PosterAdapter extends ArrayAdapter<String> {
        public PosterAdapter(@NonNull Context context, ArrayList<String> urls) {
            super(context, 0, urls);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_poster_item, parent, false);
            }

            String url = getItem(position);
            ImageView imageView = convertView.findViewById(R.id.posterImage);
            ImageButton deleteBtn = convertView.findViewById(R.id.deletePosterButton);

            Glide.with(getContext()).load(url).into(imageView);

            // Poster image loaded so now we can show content and hide loading screen
            binding.loadingAllProfiles.setVisibility(View.GONE);
            binding.contentGroupAllProfiles.setVisibility(View.VISIBLE);


            deleteBtn.setOnClickListener(v -> showDeleteConfirmation(url, position));

            return convertView;
        }
    }

    private void showDeleteConfirmation(String url, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Poster")
                .setMessage("Are you sure you want to delete this event poster? This action IS IRREVERSIBLE.")
                .setPositiveButton("Delete", (dialog, which) -> deletePoster(url, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePoster(String url, int position) {
        // Display loading screen as it's deleting
        binding.loadingAllProfiles.setVisibility(View.VISIBLE);
        binding.contentGroupAllProfiles.setVisibility(View.GONE);

        // Delete the event poster after confirmation has gone through
        Admin.removeImage(url, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(getContext(), "Failed to delete poster", Toast.LENGTH_SHORT).show();
                return;
            }

            // Remove from list and update UI
            posterUrls.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Poster deleted successfully", Toast.LENGTH_SHORT).show();

            // Hide loading and show content now
            binding.loadingAllProfiles.setVisibility(View.GONE);
            binding.contentGroupAllProfiles.setVisibility(View.VISIBLE);
        });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.example.eventlotterysystemapplication.databinding.FragmentUserEventPostersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Display loading screen as it's loading
        binding.loadingAllProfiles.setVisibility(View.VISIBLE);
        binding.contentGroupAllProfiles.setVisibility(View.GONE);

        // Grab arguments from bundle
        if (getArguments() != null) {
            username = getArguments().getString("username");
            String[] posterUrlsArray = getArguments().getStringArray("posterUrls");
            if (posterUrlsArray != null) posterUrls = new ArrayList<>(Arrays.asList(posterUrlsArray));
        }

        binding.userTitle.setText(username + "'s uploaded event posters");
        binding.userEventHistoryBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(UserEventPostersFragment.this).navigateUp();
        });

        adapter = new PosterAdapter(requireContext(), posterUrls);
        binding.allPostersList.setAdapter(adapter);

        // Poster image loaded so now we can show content and hide loading screen
        binding.loadingAllProfiles.setVisibility(View.GONE);
        binding.contentGroupAllProfiles.setVisibility(View.VISIBLE);
    }
}
