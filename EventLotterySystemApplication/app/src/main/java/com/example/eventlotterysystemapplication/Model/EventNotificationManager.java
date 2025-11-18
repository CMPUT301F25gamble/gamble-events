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
    public void notifyInitialLotterySelection(Event event){

        String title = "Congratulations, you have won the lottery selection";
        String body = "Congratulations, you have won the lottery selection for " + event.getName()
                + ". Make sure to accept or decline the invitation by"
                + event.getInvitationAcceptanceDeadlineString();

        for (User user : event.getEntrantList().getChosen()){
            NotificationSender.sendNotification(user.getDeviceToken(), title, body, event.getEventID(), "lotteryWinNotification");
        }

        title = "You lost the lottery selection";
        body = "You have lost the lottery selection for " + event.getName() + ". You may still be " +
                "given a chance to join if someone else declines their invitation";

        for (User user : event.getEntrantList().getWaiting()){
            NotificationSender.sendNotification(user.getDeviceToken(), title, body, event.getEventID(), "lotteryLoseNotification");
        }
    }

    public void notifyWaitingList(Event event, String title, String body){
        for (User user : event.getEntrantList().getWaiting()){
            NotificationSender.sendNotification(user.getDeviceToken(), title, body, event.getEventID(), "waitingListNotification");
        }
    }

    public void notifyChosenList(Event event, String title, String body){
        for (User user : event.getEntrantList().getChosen()){
            NotificationSender.sendNotification(user.getDeviceToken(), title, body, event.getEventID(), "chosenListNotification");
        }
    }

    public void notifyCancelledList(Event event, String title, String body){
        for (User user : event.getEntrantList().getCancelled()){
            NotificationSender.sendNotification(user.getDeviceToken(), title, body, event.getEventID(), "cancelledListNotification");
        }
    }
}
