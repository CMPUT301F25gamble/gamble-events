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

import com.example.eventlotterysystemapplication.Controller.ContentActivity;
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
            Log.d("LotteryFirebaseMessagingService", "Channel Name: " + channelName);

            checkNotificationChannel(channelName);

            if (remoteMessage.getData().containsKey("eventID")) {
                String eventID = remoteMessage.getData().get("eventID");
                Log.d("LotteryFirebaseMessagingService", "EventId: " +eventID);
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
        Intent intent = new Intent(this, ContentActivity.class);

        // add eventId to the intent
        intent.putExtra("eventId", remoteMessage.getData().get("eventID"));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder;

        if (channelName.equals("lotteryWinNotification")) {
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
        Log.d("LotteryFirebaseMessagingService", "Message Received");
        //TODO add a dynamic notificationID system
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }

    private void checkNotificationChannel(String channelName) {

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelName);

            String description = "Filler";

            if (channelName.equals("lotteryWinNotification")) {
                description = "This notification channel is used to notify entrants for lottery selection";
            } else if (channelName.equals("lotteryLoseNotification")){
                description = "This notification channel is used to notify entrants that they lost lottery selection";
            } else if (channelName.equals("waitingListNotification")) {
                description = "This notification channel is used to notify entrants in the waiting list";
            } else if (channelName.equals("chosenListNotification")){
                description = "This notification channel is used to notify entrants in the chosen list";
            } else if (channelName.equals("cancelledListNotification")){
                description = "This notification channel is used to notify entrants in the chosen list";
            }

            if (notificationChannel == null) {
                NotificationChannel channel = new NotificationChannel(
                        channelName,
                        description,
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationManager.createNotificationChannel(channel);
                channel.enableVibration(true);
            }

        }

}
