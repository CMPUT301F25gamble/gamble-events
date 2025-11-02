package com.example.eventlotterysystemapplication;

import android.os.Build;

import com.google.firebase.firestore.Exclude;

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

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
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
    }

    public LocalDateTime getSignupDeadline() {
        return signupDeadline;
    }

    public void setSignupDeadline(LocalDateTime signupDeadline) {
        this.signupDeadline = signupDeadline;
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
    }

    public LocalDateTime getInvitationAcceptanceDeadline() {
        return invitationAcceptanceDeadline;
    }

    public void setInvitationAcceptanceDeadline(LocalDateTime invitationAcceptanceDeadline) {
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;
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
    }

    public ArrayList<String> getEventTags() {
        return eventTags;
    }

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

    @Exclude
    public EntrantList getEntrantList() {
        return entrantList;
    }

    @Exclude
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

    @Exclude
    public String getEventID() {
        return eventID;
    }

    @Exclude
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
