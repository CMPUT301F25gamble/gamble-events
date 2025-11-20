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

/**
 * This class inherits functionality from the already created and built in FirebaseMessagingService
 * class, and this class is responsible for handling the notification event when the app receives a
 * push notification from Firebase
 */
public class LotteryFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * The function that is called whenever a messaging event has occurred
     * @param remoteMessage Remote message that has been received.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("LotteryFirebaseMessagingService", remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().containsKey("channelName")) {

            String channelName = remoteMessage.getData().get("channelName");
            Log.d("LotteryFirebaseMessagingService", "Channel Name: " + channelName);

            NotificationChannelFactory.checkNotificationChannel(channelName);

            if (remoteMessage.getData().containsKey("eventID")) {
                String eventID = remoteMessage.getData().get("eventID");
                Log.d("LotteryFirebaseMessagingService", "EventId: " +eventID);
            }

            sendNotification(channelName, remoteMessage);


        } else {
            Log.e("LotteryFirebaseMessagingService", "Does not have a channel");
        }
    }

    /**
     * Is responsible for taking the data from the RemoteMessage object, and then using that to
     * build the notification object and then send that notification
     * @param channelName The name of the notification channel we want to send the notification to
     * @param remoteMessage The RemoteMessage object that contains all of our data
     */
    @SuppressLint("MissingPermission")
    private void sendNotification(String channelName, RemoteMessage remoteMessage) {

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

        /* If the notification is a win or a redraw notification, we will want to include actions in
        * the notification itself as buttons, therefore we have two different ways in which the
        * notification is built
        */
        if (channelName.equals("lotteryWinNotification") || channelName.equals("lotteryRedrawNotification")) {
            builder = new NotificationCompat.Builder(this, channelName)
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Notification icon
                    .setContentTitle(remoteMessage.getNotification().getTitle()) // Title displayed in the notification
                    .setContentText(remoteMessage.getNotification().getBody()) // Text displayed in the notification
                    .setContentIntent(pendingIntent) // Pending intent triggered when tapped
                    .setAutoCancel(true)
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

}
