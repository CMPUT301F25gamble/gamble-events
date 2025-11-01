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


}
