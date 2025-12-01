package com.example.eventlotterysystemapplication.Controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;

import com.example.eventlotterysystemapplication.Model.Event;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * When an event is scheduled, this class schedules the alarm on the organizer's phone to call the
 * draw at the event registration deadline for the next users
 */
public class LotteryDrawScheduler {

    /**
     * Schedules a new lottery draw alarm for an event that did not previously have a lottery draw
     * alarm
     * @param context A context object representing the current state of the system
     * @param event The event that we want to schedule the new alarm for
     */
    public void scheduleNewLotteryDraw(Context context, Event event) {
        scheduleLotteryDraw(context,event, false,false);
    }

    /**
     * Given some event with an already existing lottery draw, we replace that lottery draw alarm
     * with an updated lottery draw alarm
     * @param context A context object representing the current state of the system
     * @param event The event that we want to update the alarm for
     */
    public void scheduleUpdateLotteryDraw(Context context, Event event) {
        scheduleLotteryDraw(context,event, true,false);
    }

    /**
     * Given some event with an already existing lottery draw, we remove that lottery draw alarm
     * @param context A context object representing the current state of the system
     * @param event The event that we want to update the alarm for
     */
    public void scheduleRemoveLotteryDraw(Context context, Event event) {
        scheduleLotteryDraw(context,event, false,true);
    }

    /**
     * Schedules a lottery draw alarm to trigger at a later date and time
     * @param context A context object representing the current state of the system
     * @param event The event that we are scheduling the alarm for
     * @param update A boolean that represents whether or not we are updating a current event
     *               scheduled alarm
     * @param remove A boolean that represents whether or not we are trying to remove a current
     *               event scheduled alarm
     */
    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    public void scheduleLotteryDraw(Context context, Event event, boolean update, boolean remove) {

        // create a new alarm manager object
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null ) {
            Toast.makeText(context, "Could not schedule Lottery draw.", Toast.LENGTH_LONG).show();
            return;
        }

        // checks if the application can schedule alarms, if not, then we request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
           // return;
        }

        try{
            // create the intent with the correct eventId, and then we build a PendingIntent that
            // will trigger the LotteryAlarmReceiver
            Intent intent = new Intent(context, LotteryAlarmReceiver.class);
            String eventId = event.getEventID();
            intent.putExtra("eventID",eventId);
            int requestCode = eventId.hashCode();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
            com.google.firebase.Timestamp scheduledTime = event.getRegistrationEndTimeTS();

            if(scheduledTime ==null ){
                Toast.makeText(context, "Could not schedule lottery draw as schedule date time is not available." , Toast.LENGTH_LONG).show();
                return;
            }
            if(scheduledTime.toDate().before(new Date())){
                Toast.makeText(context, "Could not schedule lottery draw for past date time " + scheduledTime.toDate(). toString() , Toast.LENGTH_LONG).show();
                return;
            }

            String message = "";

            /*
            * if we want to either update or remove the current alarm, we will want to remove the
            * current alarm that is set in place (for update we will replace it with a new alarm)
            */
            if(update || remove) {
                alarmManager.cancel(pendingIntent);
                message = "Lottery draw for " + event.getName() + " successfully rescheduled at " + scheduledTime.toDate().toString();
            }else {
                message = "Lottery draw for " + event.getName() + " successfully scheduled at " + scheduledTime.toDate().toString();
            }

            /*
            * if we don't want to remove the current alarm (either we are creating a new alarm, or
            * we are updating the alarm), we create a new alarm
            */
            if(!remove) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, scheduledTime.toDate().getTime(), pendingIntent);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }catch(Exception exp){
            Toast.makeText(context, "Error occurred while scheduling Lottery draw.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A dead method now that the functionality is moved into scheduleLotteryDraw, this method would
     * schedule notifications
     * @param context A context object representing the current state of the system
     * @param deviceToken The Firebase Messaging Service device token for sending notifications
     * @param title The notification title
     * @param body The notification body
     * @param scheduledTime The scheduled time to send the notification
     */
    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    public void scheduleAlarm(Context context, String deviceToken, String title, String body, LocalDateTime scheduledTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null || scheduledTime == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
            return;
        }

        Intent intent = new Intent(context, LotteryAlarmReceiver.class);
        intent.putExtra("DeviceToken", deviceToken);
        intent.putExtra("Title", title);
        intent.putExtra("Body", body);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        ZonedDateTime zonedDateTime = scheduledTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, instant.toEpochMilli(), pendingIntent);
    }

}
