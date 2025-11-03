package com.example.eventlotterysystemapplication;

import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ImageView;

import com.google.firebase.firestore.Exclude;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Event {
    // we need to add in some sort of eventID in here, not sure datatype and implementation
    private String name;
    private String description;
    private LocalDateTime eventTime;
    private LocalDateTime signupDeadline;
    private LocalDateTime invitationAcceptanceDeadline;
    private static DateTimeFormatter formatter;
    private ArrayList<String> eventTags;
    private User organizer;
    private String place;
    private EntrantList entrantList;
    private int maxWaitingListCapacity;
    private int maxFinalListCapacity;

    private String eventID;

    private Bitmap QRCodeBitmap;

    /*
    Include code to have some attributes that points to an event poster, I wouldn't know how to
    declare attributes of that type yet
     */

    /*
    Geolocation requirement
     */

    public Event(String name, String description, LocalDateTime eventTime, LocalDateTime signupDeadline,
                 LocalDateTime invitationAcceptanceDeadline, ArrayList<String> eventTags, String organizerID, String place,
                 int maxWaitingListCapacity, int maxFinalListCapacity){
        this.name = name;
        this.description = description;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }
        this.eventTime = eventTime;
        this.signupDeadline = signupDeadline;
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;
        this.eventTags = eventTags;
        this.place = place;
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = maxWaitingListCapacity;

        Database db = new Database();

        this.organizer = db.getUser(organizerID);

        db.addEvent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        Database db = new Database();
        db.updateEvent(this);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

        Database db = new Database();
        db.updateEvent(this);
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;

        Database db = new Database();
        db.updateEvent(this);
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
        db.updateEvent(this);
    }

    public LocalDateTime getSignupDeadline() {
        return signupDeadline;
    }

    public void setSignupDeadline(LocalDateTime signupDeadline) {
        this.signupDeadline = signupDeadline;

        Database db = new Database();
        db.updateEvent(this);
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
        db.updateEvent(this);
    }

    public LocalDateTime getInvitationAcceptanceDeadline() {
        return invitationAcceptanceDeadline;
    }

    public void setInvitationAcceptanceDeadline(LocalDateTime invitationAcceptanceDeadline) {
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;

        Database db = new Database();
        db.updateEvent(this);
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
        db.updateEvent(this);
    }

    public ArrayList<String> getEventTags() {
        return eventTags;
    }

    public void setEventTags(ArrayList<String> eventTags) {
        this.eventTags = eventTags;

        Database db = new Database();
        db.updateEvent(this);
    }

    public void addEventTag(String tag){
        this.eventTags.add(tag);

        Database db = new Database();
        db.updateEvent(this);
    }

    public void deleteEventTag(String tag){
        this.eventTags.remove(tag);

        Database db = new Database();
        db.updateEvent(this);
    }

    @Exclude
    public User getOrganizer() {
        return organizer;
    }

    @Exclude
    public void setOrganizer(User organizer) {
        this.organizer = organizer;

        Database db = new Database();
        db.updateEvent(this);
    }

    public String getOrganizerID(){
        return organizer.getUserID();
    }

    public void setOrganizerID(String organizerID){
        Database db = new Database();
        this.organizer = db.getUser(organizerID);

        db.updateEvent(this);
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;

        Database db = new Database();
        db.updateEvent(this);
    }

    @Exclude
    public EntrantList getEntrantList() {
        return entrantList;
    }

    @Exclude
    public void setEntrantList(EntrantList entrantList) {
        this.entrantList = entrantList;

        Database db = new Database();
        db.updateEvent(this);
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
        db.updateEvent(this);
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
        db.updateEvent(this);
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
        db.updateEvent(this);
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
        db.updateEvent(this);
    }

    public int getMaxFinalListCapacity() {
        return maxFinalListCapacity;
    }

    public void setMaxFinalListCapacity(int maxFinalListCapacity) {
        this.maxFinalListCapacity = maxFinalListCapacity;

        Database db = new Database();
        db.updateEvent(this);
    }

    @Exclude
    public void generateQRCode(String data){
        try {
            QRCodeBitmap = new BarcodeEncoder().encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
        } catch (WriterException e){
            e.printStackTrace();
        }
    }

    public ImageView QRCodeImageView(){
        ImageView QRCodeImageView = null;
        QRCodeImageView.setImageBitmap(QRCodeBitmap);
        return QRCodeImageView;
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
