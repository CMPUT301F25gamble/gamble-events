package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.eventlotterysystemapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch(item.getItemId()) {

                case R.id.profile_button:
                    break;
                case R.id.events_button:
                    break;
                case R.id.notifications_button:
                    break;
                case R.id.settings_button:
                    break;

            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {

    }
}