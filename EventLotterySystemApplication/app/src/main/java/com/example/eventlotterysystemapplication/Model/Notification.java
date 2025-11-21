package com.example.eventlotterysystemapplication.Model;

import com.example.eventlotterysystemapplication.Controller.NotificationSender;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Notification {
    private String senderID;
    private String eventID;
    private String title;
    private String message;
    private String channelName;
    private Timestamp notificationSendTime;
    private String notificationID;

    public Notification(String senderID, String eventID, String title, String message, String channelName, Timestamp notificationSendTime){
        this.senderID = senderID;
        this.eventID = eventID;
        this.title = title;
        this. message = message;
        this.channelName = channelName;
        this.notificationSendTime = notificationSendTime;
    }

    public Notification(String senderID, String eventID, String title, String message, String channelName){
        this.senderID = senderID;
        this.eventID = eventID;
        this.title = title;
        this. message = message;
        this.channelName = channelName;
        this.notificationSendTime = Timestamp.now();
    }

    /**
     * A no argument public constructor for firebase
     */
    public Notification(){

    }

    public String getSenderID(){
        return senderID;
    }

    public void setSenderID(String senderID){
        this.senderID = senderID;
    }

    public String getEventID(){
        return eventID;
    }

    public void setEventID(String eventID){
        this.eventID = eventID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Timestamp getNotificationSendTime() {
        return notificationSendTime;
    }

    public void setNotificationSendTime(Timestamp notificationSendTime) {
        this.notificationSendTime = notificationSendTime;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public void sendNotification(User user){
        NotificationSender.sendNotification(user.getDeviceToken(), title, message, eventID, channelName);
        Database.getDatabase().addNotificationRecipient(this, user, task -> {});
    }
}
