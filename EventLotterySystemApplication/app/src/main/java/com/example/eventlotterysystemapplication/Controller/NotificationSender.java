package com.example.eventlotterysystemapplication.Controller;
import android.os.StrictMode;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationSender {

    public static final String NOTIFICATION_FUNCTION_URL = "https://us-central1-cmput301-gamblers.cloudfunctions.net/sendPushNotification";
    public static void sendNotification(String token, String title, String body) {
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

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}