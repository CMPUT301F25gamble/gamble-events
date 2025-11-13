package com.example.eventlotterysystemapplication.Model;





import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class EventNotificationManager {
    public void notifyLottery(EntrantList entrantList){

//        FirebaseOptions options = new FirebaseOptions.Builder().build();
//
//        FirebaseApp.initializeApp(options);
//
//        // This registration token comes from the client FCM SDKs.
//        String registrationToken = "YOUR_REGISTRATION_TOKEN";
//
//        // See documentation on defining a message payload.
//        RemoteMessage message = new RemoteMessage()
//                .putData("channelName", "850")
//                .putData("time", "2:45")
//                .setToken(registrationToken)
//                .build();
//
//        // Send a message to the device corresponding to the provided registration token.
//        String response = FirebaseMessaging.getInstance().send(message);
//        // Response is a message ID string.
//        System.out.println("Successfully sent message: " + response);

    }
}
