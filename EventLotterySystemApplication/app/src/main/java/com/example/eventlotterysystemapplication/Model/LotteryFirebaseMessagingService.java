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
import com.example.eventlotterysystemapplication.View.EventDetailScreenFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class LotteryFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("LotteryFirebaseMessagingService", remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().containsKey("channelName")) {

            String channelName = remoteMessage.getData().get("channelName");
            Log.d("LotteryFirebaseMessagingService", channelName);

            if (remoteMessage.getData().containsKey("eventID")) {
                String eventID = remoteMessage.getData().get("eventID");
                Log.d("LotteryFirebaseMessagingService", eventID);
            }

            sendNotification(channelName, remoteMessage);


        } else {
            Log.e("LotteryFirebaseMessagingService", "Does not have a channel");
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(String channelName, RemoteMessage remoteMessage) {

        // TODO Do the intent that is triggered when the notification is tapped
        // Intent that triggers when the notification is tapped
        Intent intent = new Intent(this, EventDetailScreenFragment.class);

        // add eventId to the intent
        intent.putExtra("eventId", remoteMessage.getData().get("eventID"));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder;

        if (channelName.equals("lotteryNotification")) {
            builder = new NotificationCompat.Builder(this, channelName)
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Notification icon
                    .setContentTitle(remoteMessage.getNotification().getTitle()) // Title displayed in the notification
                    .setContentText(remoteMessage.getNotification().getBody()) // Text displayed in the notification
                    .setContentIntent(pendingIntent) // Pending intent triggered when tapped
                    .setAutoCancel(true) // Dismiss notification when tapped
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(0, "Accept", pendingIntent)
                    .addAction(1, "Decline", pendingIntent);
        } else {
            builder = new NotificationCompat.Builder(this, channelName)
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Notification icon
                    .setContentTitle(remoteMessage.getNotification().getTitle()) // Title displayed in the notification
                    .setContentText(remoteMessage.getNotification().getBody()) // Text displayed in the notification
                    .setContentIntent(pendingIntent) // Pending intent triggered when tapped
                    .setAutoCancel(true) // Dismiss notification when tapped
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // Display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //TODO add a dynamic notificationID system
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }
}
