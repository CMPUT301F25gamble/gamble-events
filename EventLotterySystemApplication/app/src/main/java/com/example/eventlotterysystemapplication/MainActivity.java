package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setSelectedItemId(R.id.events_icon);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment selected_fragment = null;
                int id = menuItem.getItemId();

                if (id == R.id.profile_icon) {
                    selected_fragment = new ProfileUIFragment();
                    return true;
                }
                else if (id == R.id.events_icon) {
                    selected_fragment = new EventsUIFragment();
                    return true;
                }
                else if (id == R.id.notifications_icon) {
                    Toast.makeText(MainActivity.this, "Notifications", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.settings_icon) {
                    Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (selected_fragment == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new EventsUIFragment()).commit();
                    return true;
                }

                return false;
            }
        });
    }
}