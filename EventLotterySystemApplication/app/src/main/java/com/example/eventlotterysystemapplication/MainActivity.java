package com.example.eventlotterysystemapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri data = intent.getData();
        String action = intent.getAction();
        assert action != null;
        if (data != null){
            String eventID = data.getLastPathSegment();
            setContentView(R.layout.activity_main);

            // TODO First check that the deviceID and user are registered in the database, and only
            //  then do we open up events page with eventID
        } else {
            setContentView(R.layout.activity_main);
        }
    }
}