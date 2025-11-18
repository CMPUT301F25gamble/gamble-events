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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventNotificationManager {
    public static void notifyInitialLotterySelection(Event event){

        String notificationWinTitle = "Congratulations, you have won the lottery selection";
        String notificationWinBody = "Congratulations, you have won the lottery selection for " + event.getName()
                + ". Make sure to accept or decline the invitation by"
                + event.getInvitationAcceptanceDeadlineString();

        Notification winNotification = new Notification(event.getOrganizer(), event, notificationWinTitle, notificationWinBody, "lotteryWinNotification");

        for (User user : event.getEntrantList().getChosen()){
            winNotification.sendNotification(user.getDeviceToken());
        }

        String notificationLoseTitle = "You lost the lottery selection";
        String notificationLoseBody = "You have lost the lottery selection for " + event.getName() + ". You may still be " +
                "given a chance to join if someone else declines their invitation";

        Notification loseNotification = new Notification(event.getOrganizer(), event, notificationLoseTitle, notificationLoseBody, "lotteryLoseNotification");

        for (User user : event.getEntrantList().getWaiting()){
            loseNotification.sendNotification(user.getDeviceID());
        }
    }

    public static void notifyWaitingList(Event event, String title, String body){
        Notification notification = new Notification(event.getOrganizer(), event, title, body, "waitingListNotification");

        for (User user : event.getEntrantList().getWaiting()){
            notification.sendNotification(user.getDeviceID());
        }
    }

    public static void notifyChosenList(Event event, String title, String body){
        Notification notification = new Notification(event.getOrganizer(), event, title, body,"chosenListNotification");

        for (User user : event.getEntrantList().getChosen()){
            notification.sendNotification(user.getDeviceToken());
        }
    }

    public static void notifyCancelledList(Event event, String title, String body){
        Notification notification = new Notification(event.getOrganizer(), event, title, body, "cancelledListNotification");

        for (User user : event.getEntrantList().getCancelled()){
            notification.sendNotification(user.getDeviceToken());
        }
    }
}
