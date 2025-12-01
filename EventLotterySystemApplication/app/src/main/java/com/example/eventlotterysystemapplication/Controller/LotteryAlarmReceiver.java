package com.example.eventlotterysystemapplication.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.LotterySelector;

/**
 * When the scheduled alarm triggers, this class contains the functionality needed to call the draw
 * function for the event
 */
public class LotteryAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventId = intent.getStringExtra("eventID");
        LotterySelector lotterySelector = new LotterySelector();
        lotterySelector.processLotteryDraw(context,eventId);
/*
        // Trigger your task here (e.g., show a notification)
        String token = intent.getStringExtra("DeviceToken");
        String title = intent.getStringExtra("Title");
        String body = intent.getStringExtra("Body");
        //String eventId = "xdFHkqbQeHRsLcaEOugO";
        String channelName="lotteryWinNotification";
        NotificationSender.sendNotification(token,title,body,eventId,channelName);
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_SHORT).show();

 */
    }

}
