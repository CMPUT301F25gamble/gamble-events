package com.example.eventlotterysystemapplication.Model;

import com.example.eventlotterysystemapplication.Controller.NotificationSender;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

/**
 * An instance of this class represents a single notification that is sent out from some event
 */
public class Notification {
    private String senderID;
    private String eventID;
    private String title;
    private String message;
    private String channelName;
    private Timestamp notificationSendTime;
    private String notificationID;

    /**
     * A constructor for this notification object
     * @param senderID The userID of the notification sender
     * @param eventID The eventID of the event that sends the notification
     * @param title The title of the notification
     * @param message The message of the notification
     * @param channelName The receiving channel of the notification
     * @param notificationSendTime The time that the notification was sent at
     */
    public Notification(String senderID, String eventID, String title, String message, String channelName, Timestamp notificationSendTime){
        this.senderID = senderID;
        this.eventID = eventID;
        this.title = title;
        this. message = message;
        this.channelName = channelName;
        this.notificationSendTime = notificationSendTime;
    }

    /**
     * A constructor for this notification object
     * @param senderID The userID of the notification sender
     * @param eventID The eventID of the event that sends the notification
     * @param title The title of the notification
     * @param message The message of the notification
     * @param channelName The receiving channel of the notification
     */
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

    /**
     * A getter for the senderID
     * @return The sender's ID
     */
    public String getSenderID(){
        return senderID;
    }

    /**
     * A setter for the senderID
     * @param senderID The sender's ID
     */
    public void setSenderID(String senderID){
        this.senderID = senderID;
    }

    /**
     * A getter for the eventID
     * @return The event ID
     */
    public String getEventID(){
        return eventID;
    }

    /**
     * A setter for the eventID
     * @param eventID The event ID
     */
    public void setEventID(String eventID){
        this.eventID = eventID;
    }

    /**
     * A getter for the notification title
     * @return The notification title
     */
    public String getTitle() {
        return title;
    }

    /**
     * A setter for the notification title
     * @param title The notification title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * A getter for the notification body
     * @return The notification body
     */
    public String getMessage() {
        return message;
    }

    /**
     * A setter for the notification message
     * @param message The notification message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * A getter for the notification channel
     * @return The notification channel
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * A setter for the notification channel
     * @param channelName The notification channel
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * A getter for the notification send time
     * @return The notification send time
     */
    public Timestamp getNotificationSendTime() {
        return notificationSendTime;
    }

    /**
     * A setter for the notification send time
     * @param notificationSendTime The notification send time
     */
    public void setNotificationSendTime(Timestamp notificationSendTime) {
        this.notificationSendTime = notificationSendTime;
    }

    /**
     * A getter for the notificationID
     * @return  The notificationID
     */
    public String getNotificationID() {
        return notificationID;
    }

    /**
     * A setter for the notificationID
     * @param notificationID The notificationID
     */
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    /**
     * Once the notification is prepared, call this statement to send the notification to its
     * intended recipient
     * @param user The user that we want to send the notification to
     */
    public void sendNotification(User user){
        NotificationSender.sendNotification(user.getDeviceToken(), title, message, eventID, channelName);
        Database.getDatabase().addNotificationRecipient(this, user, task -> {});
    }
}