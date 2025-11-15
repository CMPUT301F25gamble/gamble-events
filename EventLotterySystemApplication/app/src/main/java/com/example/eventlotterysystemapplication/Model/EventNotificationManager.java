package com.example.eventlotterysystemapplication.Model;





import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventNotificationManager {

    private String baseURL = "https://fcm.googleapis.com/fcm/send";
    private String serverKey = "BG6bXwpyPwxmERiCWfgVoQhknlX3nWo6qSM-eHAzWlWGpJMHB92Ml7SyJqHwOGR2TGe_BKp995UsxPePY5mnBHw";
    public void notifyLottery(EntrantList entrantList, OnCompleteListener<Void> listener){
        
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

        // get device registration token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {

                String currentDeviceToken = task1.getResult();

                RequestQueue queue = Volley.newRequestQueue(context);

                JSONObject json = new JSONObject();
                try {
                    json.put("to", DEVICE_TOKEN);

                    JSONObject notification = new JSONObject();
                    notification.put("title", "Hello from Android");
                    notification.put("body", "This is a test notification");


                    json.put("notification", notification);
                    //         json.put("sendTime", "2025-06-13T10:00:00-06:00"); // ISO 8601 format

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FCM_API, json,
                        response -> Log.d("FCM", "Notification sent"),
                        error -> Log.e("FCM", "Error sending notification", error)
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", serverKey);
                        headers.put("Content-Type", CONTENT_TYPE);
                        return headers;
                    }
                };

                queue.add(request);

                listener.onComplete(null);
            }
        });
    }
}
