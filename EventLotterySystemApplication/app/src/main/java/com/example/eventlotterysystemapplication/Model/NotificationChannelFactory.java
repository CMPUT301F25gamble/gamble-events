package com.example.eventlotterysystemapplication.Model;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationChannelFactory {
    public void createNotificationChannel(String channelName){
        createNotificationChannel("lotteryWinNotification", "This notification channel is used to notify entrants for lottery selection");
        createNotificationChannel("lotteryLoseNotification", "This notification channel is used to notify entrants that they lost lottery selection");
        createNotificationChannel("lotteryRedrawNotification", "This notification channel is used to notify entrants if they have won lottery redrawing");
        createNotificationChannel("waitingListNotification", "This notification channel is used to notify entrants in the waiting list");
        createNotificationChannel("chosenListNotification", "This notification channel is used to notify entrants in the chosen list");
        createNotificationChannel("cancelledListNotification", "This notification channel is used to notify entrants in the chosen list");
    }

    /**
     * In case the notification channel does not already exist, we want to be able to add it to the
     * list of notification channels
     * @param channelName The notification channel we want to check if already exists
     */
    public static void checkNotificationChannel(String channelName) {

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelName);

        String description = "Filler";

        if (channelName.equals("lotteryWinNotification")) {
            description = "This notification channel is used to notify entrants for lottery selection";
        } else if (channelName.equals("lotteryLoseNotification")) {
            description = "This notification channel is used to notify entrants that they lost lottery selection";
        } else if (channelName.equals("lotteryRedrawNotification")) {
            description = "This notification channel is used to notify entrants if they have won lottery redrawing";
        }
        if (channelName.equals("waitingListNotification")) {
            description = "This notification channel is used to notify entrants in the waiting list";
        } else if (channelName.equals("chosenListNotification")) {
            description = "This notification channel is used to notify entrants in the chosen list";
        } else if (channelName.equals("cancelledListNotification")) {
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

    private void createNotificationChannel(String channelName, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    channelName,
                    description,
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.enableVibration(true); // Allow vibration for notifications

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}
