package com.example.eventlotterysystemapplication;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

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
    private String name;
    private String description;
    private static DateTimeFormatter formatter;
    private ArrayList<String> eventTags;
    private User organizer;
    private String organizerID;
    private String place;
    private EntrantList entrantList;
    private int maxWaitingListCapacity;
    private int maxFinalListCapacity;

    private String eventID;

    // Firestore timestamp format
    private Timestamp eventTimeTS;

    private Timestamp signupDeadlineTS;


    private Timestamp invitationAcceptanceDeadlineTS;


    // Should not serialize LocalDataTime objects
    private transient LocalDateTime eventTime;
    private transient LocalDateTime signupDeadline;
    private transient LocalDateTime invitationAcceptanceDeadline;


    private Bitmap QRCodeBitmap;

    private ArrayList<Bitmap> posters;

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
    public Event(String name, String description, LocalDateTime eventTime, LocalDateTime signupDeadline,
                 LocalDateTime invitationAcceptanceDeadline, ArrayList<String> eventTags, String organizerID, String place,
                 int maxWaitingListCapacity, int maxFinalListCapacity, ArrayList<Bitmap> posters){
        this.name = name;
        this.description = description;

        formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        this.eventTime = eventTime;
        this.signupDeadline = signupDeadline;
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;
        // Convert LocalDateTime to Timestamp for Firestore
        this.eventTimeTS = new Timestamp(eventTime.atZone(ZoneId.systemDefault()).toInstant());
        this.signupDeadlineTS = new Timestamp(signupDeadline.atZone(ZoneId.systemDefault()).toInstant());
        this.invitationAcceptanceDeadlineTS = new Timestamp(invitationAcceptanceDeadline.atZone(ZoneId.systemDefault()).toInstant());

        this.eventTags = eventTags;
        this.organizerID = organizerID;
        this.place = place;
        this.entrantList = new EntrantList();
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = maxWaitingListCapacity;
        this.posters = posters;

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
    public Event(String name, String description, String eventTime, String signupDeadline,
                 String invitationAcceptanceDeadline, String[] eventTags, String organizerID, String place,
                 int maxWaitingListCapacity, int maxFinalListCapacity){

        this.name = name;
        this.description = description;

        formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        this.eventTime = LocalDateTime.parse(eventTime, formatter);
        this.signupDeadline = LocalDateTime.parse(signupDeadline, formatter);
        this.invitationAcceptanceDeadline = LocalDateTime.parse(invitationAcceptanceDeadline, formatter);

        this.eventTimeTS = new Timestamp(this.eventTime.atZone(ZoneId.systemDefault()).toInstant());
        this.signupDeadlineTS = new Timestamp(this.signupDeadline.atZone(ZoneId.systemDefault()).toInstant());
        this.invitationAcceptanceDeadlineTS = new Timestamp(this.invitationAcceptanceDeadline.atZone(ZoneId.systemDefault()).toInstant());

        this.eventTags = new ArrayList<>(Arrays.asList(eventTags));
        this.organizerID = organizerID;
        this.place = place;
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
        if (eventTimeTS != null)
            eventTime = eventTimeTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (signupDeadlineTS != null)
            signupDeadline = signupDeadlineTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
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
    public LocalDateTime getEventTime() {
        return eventTime;
    }

    @Exclude
    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;

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
            return this.eventTime.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    @Exclude
    public void setEventTimeString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.eventTime = LocalDateTime.parse(dateString, formatter);
        }

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }


    @PropertyName("eventTime")
    public Timestamp getEventTimeTS() {
        return eventTimeTS;
    }


    @PropertyName("eventTime")
    public void setEventTimeTS(Timestamp eventTimeTS) {
        this.eventTimeTS = eventTimeTS;
    }

    @Exclude
    public LocalDateTime getSignupDeadline() {
        return signupDeadline;
    }

    @Exclude
    public void setSignupDeadline(LocalDateTime signupDeadline) {
        this.signupDeadline = signupDeadline;

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
            return this.signupDeadline.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    @Exclude
    public void setSignupDeadlineString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.signupDeadline = LocalDateTime.parse(dateString, formatter);
        }

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }


    @PropertyName("signUpDeadline")
    public Timestamp getSignupDeadlineTS() {
        return signupDeadlineTS;
    }


    @PropertyName("signUpDeadline")
    public void setSignupDeadlineTS(Timestamp signupDeadlineTS) {
        this.signupDeadlineTS = signupDeadlineTS;
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

    public void addEventTag(String tag){
        this.eventTags.add(tag);

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

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
        Database db = new Database();

        db.getUser(organizerID, task -> {
            if (task.isSuccessful()) {
                this.organizer = task.getResult();
            } else {
                Log.e("Database", "Cannot get user info");
            }
        });

        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
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
    public void addToEntrantList(User user, int list) throws IllegalArgumentException{
        switch (list) {
            case 0:
                this.entrantList.addToWaiting(user);
                break;
            case 1:
                this.entrantList.addToChosen(user);
                break;
            case 2:
                this.entrantList.addToCancelled(user);
                break;
            case 3:
                this.entrantList.addToFinalized(user);
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

    public ArrayList<Bitmap> getPosters() {
        return posters;
    }

    public void setPosters(ArrayList<Bitmap> posters) {
        this.posters = posters;
    }

    @Exclude
    public void addPoster(Bitmap poster){
        posters.add(poster);
    }

    public void deletePoster(Bitmap poster){
        posters.remove(poster);
    }

    public void deletePoster(int position) {
        if (0 <= position && position < posters.size()) {
            posters.remove(position);
        } else {
            Log.e("Poster Removal", "Index out of bounds");
        }
    }

    public Bitmap getQRCodeBitmap() {
        if (QRCodeBitmap == null){
            generateQRCode();
        }
        return QRCodeBitmap;
    }

    public void setQRCodeBitmap(Bitmap QRCodeBitmap) {
        this.QRCodeBitmap = QRCodeBitmap;
    }

    @Exclude
    public void generateQRCode(){
        try {
            String data = "cmput301gamblers://gamble/"+this.eventID;
            QRCodeBitmap = new BarcodeEncoder().encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
        } catch (WriterException e){
            e.printStackTrace();
        }
    }

    @Exclude
    public String getEventID() {
        return eventID;
    }

    @Exclude
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
