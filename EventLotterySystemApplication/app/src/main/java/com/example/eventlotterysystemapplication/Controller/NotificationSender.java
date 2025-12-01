package com.example.eventlotterysystemapplication.Controller;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class contains the method needed to send a notification that is generated in the app to the
 * appropriate firebase function, which will then use Firebase Messaging Service to send a push
 * notification to all devices
 */
public class NotificationSender {

    public static final String NOTIFICATION_FUNCTION_URL = "https://us-central1-cmput301-gamblers.cloudfunctions.net/sendPushNotification";

    /**
     * The function that you will want to use to send a push notification to a single recipient
     * @param token The Firebase Messaging Service device token of the user to whom you want to send
     *              the notification to
     * @param title The title of the notification
     * @param body The body of the notification
     * @param eventID The eventID of the event that the notification is being sent from
     * @param channelName The notification channel that the notification should be sent over
     */
    public static void sendNotification(String token, String title, String body, String eventID, String channelName) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); // Only for testing — use AsyncTask or Retrofit in production

        try {
            URL url = new URL(NOTIFICATION_FUNCTION_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("token", token);
            json.put("title", title);
            json.put("body", body);
            json.put("eventID", eventID);
            json.put("channelName", channelName);

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d("Response Code", Integer.toString(responseCode));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}