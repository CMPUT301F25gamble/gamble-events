package com.example.eventlotterysystemapplication.Model;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.eventlotterysystemapplication.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class LotteryFirebaseMessagingService extends FirebaseMessagingService {

    private final String channelId = "i.apps.notifications"; // Unique channel ID for notifications

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("LotteryFirebaseMessagingService", remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (!remoteMessage.getData().isEmpty()) {
            Log.d("LotteryFirebaseMessagingService", remoteMessage.getData().get("eventID"));
            String eventID = remoteMessage.getData().get("eventID");
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        sendNotification();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Test notification";

            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    description,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true); // Turn on notification light
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true); // Allow vibration for notifications

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification() {
        // Intent that triggers when the notification is tapped
        Intent intent = new Intent(this, Notification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Notification icon
                .setContentTitle("Hello") // Title displayed in the notification
                .setContentText("Welcome to GeeksforGeeks!!") // Text displayed in the notification
                .setContentIntent(pendingIntent) // Pending intent triggered when tapped
                .setAutoCancel(true) // Dismiss notification when tapped
                .setPriority(NotificationCompat.PRIORITY_HIGH); // Notification priority for better visibility

        // Display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //TODO add a dynamic notificationID system
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }
}
