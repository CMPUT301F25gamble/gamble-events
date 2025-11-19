package com.example.eventlotterysystemapplication.Model;

import com.example.eventlotterysystemapplication.Controller.NotificationSender;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Notification {
    private User sender;
    private Event event;
    private String title;
    private String message;
    private String channelName;
    private Timestamp notificationSendTime;
    private String notificationID;

    public Notification(User sender, Event event, String title, String message, String channelName, Timestamp notificationSendTime){
        this.sender = sender;
        this.event = event;
        this.title = title;
        this. message = message;
        this.channelName = channelName;
        this.notificationSendTime = notificationSendTime;
    }

    public Notification(User sender, Event event, String title, String message, String channelName){
        this.sender = sender;
        this.event = event;
        this.title = title;
        this. message = message;
        this.channelName = channelName;
        this.notificationSendTime = Timestamp.now();
    }

    @Exclude
    public User getSender() {
        return sender;
    }

    @Exclude
    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderID(){
        return sender.getUserID();
    }

    public void setSenderID(String senderID){
        sender.setUserID(senderID);
    }


    @Exclude
    public Event getEvent() {
        return event;
    }

    @Exclude
    public void setEvent(Event event) {
        this.event = event;
    }

    public String getEventID(){
        return event.getEventID();
    }

    public void setEventID(String eventID){
        event.setEventID(eventID);
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
        NotificationSender.sendNotification(user.getDeviceToken(), title, message, event.getEventID(), channelName);
        Database.getDatabase().addNotificationRecipient(this, user, task -> {});
    }
}
