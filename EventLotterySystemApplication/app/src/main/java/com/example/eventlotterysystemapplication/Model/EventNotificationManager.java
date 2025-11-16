package com.example.eventlotterysystemapplication.Model;





import android.content.Context;
import android.os.StrictMode;
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

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "BG6bXwpyPwxmERiCWfgVoQhknlX3nWo6qSM-eHAzWlWGpJMHB92Ml7SyJqHwOGR2TGe_BKp995UsxPePY5mnBHw";
    private static final String CONTENT_TYPE = "application/json";
    public void notifyInitialLotterySelection(Context context, Event event){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject json = new JSONObject();
        try {
            for (User user : event.getEntrantList().getChosen()) {
                json.put("to", user.getDeviceToken());
            }

            JSONObject notification = new JSONObject();
            notification.put("title", "Congratulations, you have won the lottery selection");
            notification.put("body", "Congratulations, you have won the lottery " +
                    "selection for" + event.getName() + ". Make sure to accept or decline the " +
                    "invitation by" + event.getInvitationAcceptanceDeadlineString());


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
                headers.put("Authorization", SERVER_KEY);
                headers.put("Content-Type", CONTENT_TYPE);
                return headers;
            }
        };

        queue.add(request);
    }
}
