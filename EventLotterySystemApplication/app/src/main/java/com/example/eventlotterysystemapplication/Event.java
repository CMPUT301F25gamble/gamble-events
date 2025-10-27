package com.example.eventlotterysystemapplication;

import java.time.LocalDateTime;

public class Event {
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
}
