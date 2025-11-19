package com.example.eventlotterysystemapplication.Model;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.eventlotterysystemapplication.Controller.NotificationSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.checkerframework.checker.units.qual.N;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is designed to provide methods to the rest of the program for sending notifications to
 * the entrants in certain entrant lists, without having to worry about the details of how we are
 * sending the notification
 */
public class EventNotificationManager {

    /**
     * This function is called when we want to notify the winners and the losers of the lottery
     * selection. This function is meant to be called right after we have selected the initial
     * selection has occurred. This function assumes that the users have been correctly added to the
     * correct EntrantList objects by the LotterySelector object already
     * @param event The event to notify the winners and losers of
     */
    public static void notifyInitialLotterySelection(Event event){

        String notificationWinTitle = "Congratulations, you have won the lottery selection";
        String notificationWinBody = "Congratulations, you have won the lottery selection for " + event.getName()
                + ". Make sure to accept or decline the invitation by"
                + event.getInvitationAcceptanceDeadlineString();

        Notification winNotification = new Notification(event.getOrganizer(), event, notificationWinTitle, notificationWinBody, "lotteryWinNotification");

        Database.getDatabase().addNotification(winNotification, task -> {
            if (task.isSuccessful()){
                for (User user : event.getEntrantList().getChosen()){
                    winNotification.sendNotification(user);
                }
            } else {
                Log.e("EventNotificationManager", "Could not save notification to database");
            }
        });

        String notificationLoseTitle = "You lost the lottery selection";
        String notificationLoseBody = "You have lost the lottery selection for " + event.getName() + ". You may still be " +
                "given a chance to join if someone else declines their invitation";

        Notification loseNotification = new Notification(event.getOrganizer(), event, notificationLoseTitle, notificationLoseBody, "lotteryLoseNotification");

        Database.getDatabase().addNotification(winNotification, task -> {
            if (task.isSuccessful()){
                for (User user : event.getEntrantList().getWaiting()){
                    loseNotification.sendNotification(user);
                }
            } else {
                Log.e("EventNotificationManager", "Could not save notification to database");
            }
        });
    }

    /**
     * Notifies users who have been reselected by the lottery
     * @param user The user who has been selected by the lottery
     * @param event The event they have been reselected for
     */
    public static void notifyLotteryReselection(User user, Event event){
        String notificationTitle = "Congratulations, you have won the lottery re-selection";
        String notificationBody = "Congratulations, you have been selected to join " + event.getName()
                + " because someone else declined their invitataion. Make sure to accept or decline " +
                "the invitation by" + event.getInvitationAcceptanceDeadlineString();

        Database.getDatabase().getRedrawNotification(event.getEventID(), task -> {
            if (task.isSuccessful()){
                Notification notification = task.getResult();

                Database.getDatabase().addNotification(notification, task1 -> {
                    if (task1.isSuccessful()){
                        notification.sendNotification(user);
                    } else {
                        Log.e("EventNotificationManager", "Could not save notification to database");
                    }
                });
            } else {
                Notification notification =  new Notification(event.getOrganizer(), event, notificationTitle, notificationBody, "lotteryRedrawNotification");
                Database.getDatabase().addNotification(notification, task1 -> {
                    if (task1.isSuccessful()){
                        notification.sendNotification(user);
                    } else {
                        Log.e("EventNotificationManager", "Could not save notification to database");
                    }
                });
            }
        });
    }

    /**
     * Used to allow the organizer to send custom notifications to users in the waiting list
     * @param event The event that the organizer is sending notifications for
     * @param title The title of the notification
     * @param body The body of the notification
     */
    public static void notifyWaitingList(Event event, String title, String body){
        Notification notification = new Notification(event.getOrganizer(), event, title, body, "waitingListNotification");

        Database.getDatabase().addNotification(notification, task -> {
            if (task.isSuccessful()){
                for (User user : event.getEntrantList().getWaiting()){
                    notification.sendNotification(user);
                }
            } else {
                Log.e("EventNotificationManager", "Could not save notification to database");
            }
        });
    }

    /**
     * Used to allow the organizer to send custom notifications to users in the chosen list
     * @param event The event that the organizer is sending notifications for
     * @param title The title of the notification
     * @param body The body of the notification
     */
    public static void notifyChosenList(Event event, String title, String body){
        Notification notification = new Notification(event.getOrganizer(), event, title, body,"chosenListNotification");

        Database.getDatabase().addNotification(notification, task -> {
            if (task.isSuccessful()){
                for (User user : event.getEntrantList().getChosen()){
                    notification.sendNotification(user);
                }
            } else {
                Log.e("EventNotificationManager", "Could not save notification to database");
            }
        });
    }

    /**
     * Used to allow the organizer to send custom notifications to users in the cancelled list
     * @param event The event that the organizer is sending notifications for
     * @param title The title of the notification
     * @param body The body of the notification
     */
    public static void notifyCancelledList(Event event, String title, String body){
        Notification notification = new Notification(event.getOrganizer(), event, title, body, "cancelledListNotification");

        Database.getDatabase().addNotification(notification, task -> {
            if (task.isSuccessful()){
                for (User user : event.getEntrantList().getCancelled()){
                    notification.sendNotification(user);
                }
            } else {
                Log.e("EventNotificationManager", "Could not save notification to database");
            }
        });
    }
}
