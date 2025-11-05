package com.example.eventlotterysystemapplication;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Event {
    private String eventID;
    private String name;
    private String description;
    private String place;
    private ArrayList<String> eventTags;
    private  User organizer;
    private String organizerID;
    private  EntrantList entrantList;
    private int maxWaitingListCapacity;
    private int maxFinalListCapacity;



    // Firestore timestamp format
    private Timestamp eventStartTimeTS;
    private Timestamp eventEndTimeTS;
    private Timestamp registrationStartTimeTS;
    private Timestamp registrationEndTimeTS;
    private Timestamp invitationAcceptanceDeadlineTS;


    // Should not serialize LocalDataTime objects
    private transient LocalDateTime eventStartTime;
    private transient LocalDateTime eventEndTime;
    private transient LocalDateTime registrationStartTime;
    private transient LocalDateTime registrationEndTime;
    private transient LocalDateTime invitationAcceptanceDeadline;
    private static DateTimeFormatter formatter;


    private Bitmap QRCodeBitmap;

    /*
    Include code to have some attributes that points to an event poster, I wouldn't know how to
    declare attributes of that type yet
     */

    /*
    Geolocation requirement
     */


    public Event() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event(String name, String description, String place, ArrayList<String> eventTags, String organizerID, LocalDateTime eventStartTime, LocalDateTime eventEndTime,
                 LocalDateTime registrationStartTime, LocalDateTime registrationEndTime, LocalDateTime invitationAcceptanceDeadline,
                 int maxWaitingListCapacity, int maxFinalListCapacity){
        this.name = name;
        this.description = description;
        this.place = place;
        this.eventTags = eventTags;
        this.organizerID = organizerID;

        formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.registrationStartTime = registrationStartTime;
        this.registrationEndTime = registrationEndTime;
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;

        // Convert LocalDateTime to Timestamp for Firestore
        this.eventStartTimeTS = new Timestamp(eventStartTime.atZone(ZoneId.systemDefault()).toInstant());
        this.eventEndTimeTS = new Timestamp(eventEndTime.atZone(ZoneId.systemDefault()).toInstant());
        this.registrationStartTimeTS = new Timestamp(registrationStartTime.atZone(ZoneId.systemDefault()).toInstant());
        this.registrationEndTimeTS = new Timestamp(registrationEndTime.atZone(ZoneId.systemDefault()).toInstant());
        this.invitationAcceptanceDeadlineTS = new Timestamp(invitationAcceptanceDeadline.atZone(ZoneId.systemDefault()).toInstant());

        this.entrantList = new EntrantList();
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = maxWaitingListCapacity;

        Database db = new Database();
        db.getUser(organizerID, task -> {
            if (task.isSuccessful()) {
                this.organizer = task.getResult();
            } else {
                Log.e("Database", "Cannot get user info");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event(String name, String description, String place, String[] eventTags, String organizerID, String eventStartTime, String eventEndTime,
                 String registrationStartTime, String registrationEndTime, String invitationAcceptanceDeadline,
                 int maxWaitingListCapacity, int maxFinalListCapacity){

        this.name = name;
        this.description = description;
        this.place = place;
        this.eventTags = new ArrayList<>(Arrays.asList(eventTags));
        this.organizerID = organizerID;

        formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        this.eventStartTime = LocalDateTime.parse(eventStartTime, formatter);
        this.eventEndTime = LocalDateTime.parse(eventEndTime, formatter);
        this.registrationStartTime = LocalDateTime.parse(registrationStartTime, formatter);
        this.registrationEndTime = LocalDateTime.parse(registrationEndTime, formatter);
        this.invitationAcceptanceDeadline = LocalDateTime.parse(invitationAcceptanceDeadline, formatter);

        this.eventStartTimeTS = new Timestamp(this.eventStartTime.atZone(ZoneId.systemDefault()).toInstant());
        this.eventEndTimeTS = new Timestamp(this.eventEndTime.atZone(ZoneId.systemDefault()).toInstant());
        this.registrationStartTimeTS = new Timestamp(this.registrationStartTime.atZone(ZoneId.systemDefault()).toInstant());
        this.registrationEndTimeTS = new Timestamp(this.registrationEndTime.atZone(ZoneId.systemDefault()).toInstant());
        this.invitationAcceptanceDeadlineTS = new Timestamp(this.invitationAcceptanceDeadline.atZone(ZoneId.systemDefault()).toInstant());

        this.entrantList = new EntrantList();
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = maxWaitingListCapacity;

        Database db = new Database();
        db.getUser(organizerID, task -> {
            if (task.isSuccessful()) {
                this.organizer = task.getResult();
            } else {
                Log.e("Database", "Cannot get user info");
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseTimestamps() {
        if (eventStartTimeTS != null)
            eventStartTime = eventStartTimeTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (registrationEndTimeTS != null)
            registrationEndTime = registrationEndTimeTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (invitationAcceptanceDeadlineTS != null)
            invitationAcceptanceDeadline = invitationAcceptanceDeadlineTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }

    @Exclude
    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public String getEventTimeString(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return this.eventStartTime.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    @Exclude
    public void setEventTimeString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.eventStartTime = LocalDateTime.parse(dateString, formatter);
        }

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }


    @PropertyName("eventStartTime")
    public Timestamp getEventStartTimeTS() {
        return eventStartTimeTS;
    }


    @PropertyName("eventStartTime")
    public void setEventStartTimeTS(Timestamp eventStartTimeTS) {
        this.eventStartTimeTS = eventStartTimeTS;
    }

    @Exclude
    public LocalDateTime getRegistrationEndTime() {
        return registrationEndTime;
    }

    @Exclude
    public void setRegistrationEndTime(LocalDateTime registrationEndTime) {
        this.registrationEndTime = registrationEndTime;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public String getSignupDeadlineString(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return this.registrationEndTime.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    @Exclude
    public void setSignupDeadlineString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.registrationEndTime = LocalDateTime.parse(dateString, formatter);
        }

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }


    @PropertyName("registrationEndTime")
    public Timestamp getRegistrationEndTimeTS() {
        return registrationEndTimeTS;
    }


    @PropertyName("registrationEndTime")
    public void setRegistrationEndTimeTS(Timestamp registrationEndTimeTS) {
        this.registrationEndTimeTS = registrationEndTimeTS;
    }

    @Exclude
    public LocalDateTime getInvitationAcceptanceDeadline() {
        return invitationAcceptanceDeadline;
    }

    @Exclude
    public void setInvitationAcceptanceDeadline(LocalDateTime invitationAcceptanceDeadline) {
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public String getInvitationAcceptanceDeadlineString(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return this.invitationAcceptanceDeadline.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    @Exclude
    public void setInvitationAcceptanceDeadlineString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.invitationAcceptanceDeadline = LocalDateTime.parse(dateString, formatter);
        }

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }


    @PropertyName("invitationAcceptanceDeadline")
    public Timestamp getInvitationAcceptanceDeadlineTS() {
        return invitationAcceptanceDeadlineTS;
    }


    @PropertyName("invitationAcceptanceDeadline")
    public void setInvitationAcceptanceDeadlineTS(Timestamp invitationAcceptanceDeadlineTS) {
        this.invitationAcceptanceDeadlineTS = invitationAcceptanceDeadlineTS;
    }

    public ArrayList<String> getEventTags() {
        return eventTags;
    }

    public void setEventTags(ArrayList<String> eventTags) {
        this.eventTags = eventTags;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public void addEventTag(String tag){
        this.eventTags.add(tag);

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public void deleteEventTag(String tag){
        this.eventTags.remove(tag);

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public User getOrganizer() {
        return organizer;
    }

    @Exclude
    public void setOrganizer(User organizer) {
        this.organizer = organizer;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    public String getOrganizerID(){
        return organizerID;
    }

    public void setOrganizerID(String organizerID){
        this.organizerID = organizerID;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public EntrantList getEntrantList() {
        return entrantList;
    }

    @Exclude
    public void setEntrantList(EntrantList entrantList) {
        this.entrantList = entrantList;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public void setEntrantListValues(ArrayList<User> entrantListValues, int list) throws IllegalArgumentException{
        switch (list) {
            case 0:
                this.entrantList.setWaiting(entrantListValues);
                break;
            case 1:
                this.entrantList.setChosen(entrantListValues);
                break;
            case 2:
                this.entrantList.setCancelled(entrantListValues);
                break;
            case 3:
                this.entrantList.setFinalized(entrantListValues);
            default:
                throw new IllegalArgumentException("List number out of range");
        }

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public Task<Void> addToEntrantList(User user, int list) throws IllegalArgumentException {
        switch (list) {
            case 0:
                this.entrantList.addToWaiting(user);
                Log.d("Database", "Waiting list size now: " + this.entrantList.getWaiting().size());
                break;
            case 1:
                this.entrantList.addToChosen(user);
                break;
            case 2:
                this.entrantList.addToCancelled(user);
                break;
            case 3:
                this.entrantList.addToFinalized(user);
                break;
            default:
                throw new IllegalArgumentException("List number out of range");
        }

        Database db = new Database();
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        db.updateEvent(this, task -> {
            if (task.isSuccessful()) {
                tcs.setResult(null);
            } else {
                tcs.setException(task.getException());
                Log.e("Database", "Cannot update event", task.getException());
            }
        });
        return tcs.getTask();
    }

    @Exclude
    public void removeFromEntrantList(User user, int list) throws IllegalArgumentException{
        switch (list) {
            case 0:
                this.entrantList.removeFromWaiting(user);
                break;
            case 1:
                this.entrantList.removeFromChosen(user);
                break;
            case 2:
                this.entrantList.removeFromCancelled(user);
                break;
            case 3:
                this.entrantList.removeFromCancelled(user);
            default:
                throw new IllegalArgumentException("List number out of range");
        }

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public void joinWaitingList(User user) throws IllegalArgumentException{
        if (!(entrantList.getChosen().contains(user) || entrantList.getCancelled().contains(user) || entrantList.getFinalized().contains(user))){
            if (!entrantList.getWaiting().contains(user)){
                addToEntrantList(user, 0);
            } else {
                throw new IllegalArgumentException("User is already in the waiting list");
            }
        } else {
            throw new IllegalArgumentException("User has already been selected from the waiting list");
        }
    }

    @Exclude
    public void leaveWaitingList(User user){
        if (entrantList.getChosen().contains(user)){
            removeFromEntrantList(user, 0);
        } else {
            throw new IllegalArgumentException("User is not in the waiting list");
        }
    }

    public int getMaxWaitingListCapacity() {
        return maxWaitingListCapacity;
    }

    public void setMaxWaitingListCapacity(int maxWaitingListCapacity) {
        this.maxWaitingListCapacity = maxWaitingListCapacity;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    public int getMaxFinalListCapacity() {
        return maxFinalListCapacity;
    }

    public void setMaxFinalListCapacity(int maxFinalListCapacity) {
        this.maxFinalListCapacity = maxFinalListCapacity;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    @Exclude
    public void generateQRCode(String data){
        try {
            QRCodeBitmap = new BarcodeEncoder().encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
        } catch (WriterException e){
            e.printStackTrace();
        }
    }

    @Exclude
    public ImageView QRCodeImageView(){
        ImageView QRCodeImageView = null;
        QRCodeImageView.setImageBitmap(QRCodeBitmap);
        return QRCodeImageView;
    }


    public String getEventID() {
        return eventID;
    }


    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    @PropertyName("eventEndTime")
    public Timestamp getEventEndTimeTS() {
        return eventEndTimeTS;
    }

    @PropertyName("eventEndTime")
    public void setEventEndTimeTS(Timestamp eventEndTimeTS) {
        this.eventEndTimeTS = eventEndTimeTS;
    }

    @PropertyName("registrationStartTime")
    public Timestamp getRegistrationStartTimeTS() {
        return registrationStartTimeTS;
    }

    @PropertyName("registrationStartTime")
    public void setRegistrationStartTimeTS(Timestamp registrationStartTimeTS) {
        this.registrationStartTimeTS = registrationStartTimeTS;
    }
}
