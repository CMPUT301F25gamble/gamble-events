package com.example.eventlotterysystemapplication;

import android.os.Build;

import com.google.firebase.firestore.Exclude;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatFlagsException;

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

    /*
    Include code to have some attributes that points to an event poster, I wouldn't know how to
    declare attributes of that type yet
     */

    /*
    Geolocation requirement
     */

    public Event(String name, String description, String eventTime, String signupDeadline,
                 String invitationAcceptanceDeadline, String [] eventTags, String organizerID, String place,
                 int maxWaitingListCapacity, int maxFinalListCapacity){
        this.name = name;
        this.description = description;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            this.eventTime = LocalDateTime.parse(eventTime, formatter);
            this.signupDeadline = LocalDateTime.parse(signupDeadline, formatter);
            this.invitationAcceptanceDeadline = LocalDateTime.parse(invitationAcceptanceDeadline, formatter);
        }
        this.eventTags = new ArrayList<>(Arrays.asList(eventTags));
        this.place = place;
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = maxWaitingListCapacity;

        Database db = Database.getDatabase();

        this.organizer = db.getUser(organizerID);

        db.addEvent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public LocalDateTime getEventTime() {
        return eventTime;
    }

    @Exclude
    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

//    public String getEventTime(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return this.eventTime.format(formatter);
//        } else {
//            throw new IllegalStateException();
//        }
//    }

    @Exclude
    public LocalDateTime getSignupDeadline() {
        return signupDeadline;
    }

    @Exclude
    public void setSignupDeadline(LocalDateTime signupDeadline) {
        this.signupDeadline = signupDeadline;
    }

    @Exclude
    public LocalDateTime getInvitationAcceptanceDeadline() {
        return invitationAcceptanceDeadline;
    }

    @Exclude
    public void setInvitationAcceptanceDeadline(LocalDateTime invitationAcceptanceDeadline) {
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;
    }

    @Exclude
    public ArrayList<String> getEventTags() {
        return eventTags;
    }

    @Exclude
    public void setEventTags(ArrayList<String> eventTags) {
        this.eventTags = eventTags;
    }

    public void addEventTag(String tag){
        this.eventTags.add(tag);
    }

    public void deleteEventTag(String tag){
        this.eventTags.remove(tag);
    }

    @Exclude
    public User getOrganizer() {
        return organizer;
    }

    @Exclude
    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public String getOrganizerID(){
        return organizer.getUserID();
    }

    public void setOrganizerID(String organizerID){
        Database db = Database.getDatabase();
        this.organizer = db.getUser(organizerID);
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public EntrantList getEntrantList() {
        return entrantList;
    }

    public void setEntrantList(EntrantList entrantList) {
        this.entrantList = entrantList;
    }

    public int getMaxWaitingListCapacity() {
        return maxWaitingListCapacity;
    }

    public void setMaxWaitingListCapacity(int maxWaitingListCapacity) {
        this.maxWaitingListCapacity = maxWaitingListCapacity;
    }

    public int getMaxFinalListCapacity() {
        return maxFinalListCapacity;
    }

    public void setMaxFinalListCapacity(int maxFinalListCapacity) {
        this.maxFinalListCapacity = maxFinalListCapacity;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
