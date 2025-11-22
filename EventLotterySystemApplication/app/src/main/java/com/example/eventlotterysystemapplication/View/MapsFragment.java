package com.example.eventlotterysystemapplication.View;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Entrant;
import com.example.eventlotterysystemapplication.Model.EntrantStatus;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.Location;
import com.example.eventlotterysystemapplication.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.List;

public class MapsFragment extends Fragment {

    private String eventID;
    private String entrantStatus;

    private final String TAG = "EntrantsMap";
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (eventID == null) {
                return;
            }
            getEvent(task -> {
                if (task.isSuccessful()) {
                    // Grab event and bind it
                    Event event = task.getResult();
                    Log.d(TAG, "Event retrieved is: " + event);

                    List<Entrant> waitingEntrants = event.getEntrantListByStatus(EntrantStatus.valueOf(entrantStatus));

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    if (waitingEntrants != null && waitingEntrants.size() > 0) {
                        for (Entrant entrant : waitingEntrants) {
                            Location entrantLocation = entrant.getLocation();
                            if (entrantLocation != null) {
                                Double latitude = entrantLocation.getLatitude();
                                Double longitude = entrantLocation.getLongitude();
                                if (latitude != null && longitude != null) {
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    builder.include(latLng);
                                    googleMap.addMarker(new MarkerOptions().position(latLng).title(entrant.getUser().getName()));
                                }
                            }
                            int padding = 100; // offset from edges in pixels
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            googleMap.animateCamera(cu);
                        }
                    }
                }
            });
        }

        private void getEvent(OnCompleteListener<Event> callback) {
            Database db = Database.getDatabase();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API level must be 26 or above
                Log.d(TAG, "Fetching event from DB...");
                db.getEvent(eventID, task -> {
                    if (task.isSuccessful()) {
                        callback.onComplete(task);
                    } else {
                        Log.e(TAG, "Error fetching event from database");
                    }
                });
            }
        }

    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            eventID = args.getString("eventID");
            entrantStatus = args.getString("entrantStatus");
        }

        return inflater.inflate(R.layout.fragment_maps, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle("Event Name");
    }
}
