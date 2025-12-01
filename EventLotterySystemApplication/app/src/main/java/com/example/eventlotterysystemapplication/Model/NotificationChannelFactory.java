package com.example.eventlotterysystemapplication.Model;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

/**
 * This class is follows the factory method pattern to create all of the notification channels in
 * one place instead of all over the program, making it really easy to add more notification
 * channels as needed
 */
public class NotificationChannelFactory {

    /**
     * Creates all of the default notification channels
     * @param context A context object representing the current state of the system
     */
    public static void createNotificationChannels(Context context){
        checkAndCreateNotificationChannel(context, "lotteryWinNotification");
        checkAndCreateNotificationChannel(context,"lotteryLoseNotification");
        checkAndCreateNotificationChannel(context,"lotteryRedrawNotification");
        checkAndCreateNotificationChannel(context,"waitingListNotification");
        checkAndCreateNotificationChannel(context,"chosenListNotification");
        checkAndCreateNotificationChannel(context,"cancelledListNotification");
        checkAndCreateNotificationChannel(context, "finalizedListNotification");
        checkAndCreateNotificationChannel(context, "manualNotification");
        // TODO notification deleteEvent
    }

    /**
     * In case the notification channel does not already exist, we want to be able to add it to the
     * list of notification channels. If it is a default one, we add a description to it
     * @param channelName The notification channel we want to check if already exists
     */
    public static void checkAndCreateNotificationChannel(Context context, String channelName) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelName);

        String description = "New channel";

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
        } else if (channelName.equals("finalizedListNotification")){
            description = "This notification channel is used to notify entrants in the finalized list";
        } else if (channelName.equals("manualNotification")){
            description = "This notification channel is used to notify entrants who are drawn manually";
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

    /**
     * We create a new notification channel
     * @param context A context object representing the current state of the system
     * @param channelName The name of the notification channel
     * @param description The description of the notification channel
     */
    private void createNotificationChannel(Context context, String channelName, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    channelName,
                    description,
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.enableVibration(true); // Allow vibration for notifications

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}
