package com.example.eventlotterysystemapplication;

import android.os.Build;

import java.time.LocalDateTime;

public class Event {
    // we need to add in some sort of eventID in here, not sure datatype and implementation
    private String name;
    private String description;
    private LocalDateTime eventTime;
    private LocalDateTime signupDeadline;
    private LocalDateTime invitationAcceptanceDeadline;
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
                 String invitationAcceptanceDeadline, User organizer, String place,
                 int maxWaitingListCapacity, int maxFinalListCapacity){
        this.name = name;
        this.description = description;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.eventTime = LocalDateTime.parse(eventTime);
            this.signupDeadline = LocalDateTime.parse(signupDeadline);
            this.invitationAcceptanceDeadline = LocalDateTime.parse(invitationAcceptanceDeadline);
        }

        this.organizer = organizer;
        this.place = place;
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = maxWaitingListCapacity;
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

    public LocalDateTime getSignupDeadline() {
        return signupDeadline;
    }

    public void setSignupDeadline(LocalDateTime signupDeadline) {
        this.signupDeadline = signupDeadline;
    }

    public LocalDateTime getInvitationAcceptanceDeadline() {
        return invitationAcceptanceDeadline;
    }

    public void setInvitationAcceptanceDeadline(LocalDateTime invitationAcceptanceDeadline) {
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
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
