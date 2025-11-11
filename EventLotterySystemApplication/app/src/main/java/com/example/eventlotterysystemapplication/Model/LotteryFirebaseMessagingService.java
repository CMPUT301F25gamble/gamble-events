package com.example.eventlotterysystemapplication.Model;

import com.google.firebase.messaging.FirebaseMessagingService;

public class LotteryFirebaseMessagingService extends FirebaseMessagingService {

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // ...
//
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d("", "From: ${remoteMessage.from}");
//
//        // Check if message contains a data payload.
//        if (!remoteMessage.getData().isEmpty()) {
//            Log.d("LotteryFirebaseMessagingService", "Message data payload: ${remoteMessage.data}");
//
//        }
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//
//        sendNotification();
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel(
//                    channelId,
//                    description,
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            notificationChannel.enableLights(true); // Turn on notification light
//            notificationChannel.setLightColor(Color.GREEN);
//            notificationChannel.enableVibration(true); // Allow vibration for notifications
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private void sendNotification() {
//        // Intent that triggers when the notification is tapped
//        Intent intent = new Intent(this, Notification.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        // Custom layout for the notification content
//        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.activity_after_notification);
//
//        // Build the notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.ic_launcher_foreground) // Notification icon
//                .setContent(contentView) // Custom notification content
//                .setContentTitle("Hello") // Title displayed in the notification
//                .setContentText("Welcome to GeeksforGeeks!!") // Text displayed in the notification
//                .setContentIntent(pendingIntent) // Pending intent triggered when tapped
//                .setAutoCancel(true) // Dismiss notification when tapped
//                .setPriority(NotificationCompat.PRIORITY_HIGH); // Notification priority for better visibility
//
//        // Display the notification
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(notificationId, builder.build());
//    }
}
