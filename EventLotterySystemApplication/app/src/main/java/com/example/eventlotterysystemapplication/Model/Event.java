package com.example.eventlotterysystemapplication.Model;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

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

/**
 * An instance of this object represents a single instance of an event
 */
public class Event {
    private String eventID;
    private String name;
    private String description;
    private String place;
    private ArrayList<String> eventTags;
    private User organizer;
    private String organizerID;
    private EntrantList entrantList;
    private int maxWaitingListCapacity;
    private int maxFinalListCapacity;
    private String eventPosterUrl;


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


    @Exclude
    private Bitmap QRCodeBitmap;

    @Exclude
    private ArrayList<Bitmap> posters;

    /*
    Include code to have some attributes that points to an event poster, I wouldn't know how to
    declare attributes of that type yet
     */

    /*
    Geolocation requirement
     */


    /**
     * An empty constructor for the Event class, is used because it makes it easier to parse a
     * document from Firebase and set specific fields to the values extracted from Firebase
     */
    public Event() {
        // Empty constructor used by Firebase to deserialize documents into Event object
    }

    /**
     * A constructor whose main purpose is to allow for the instantiation of the event object from
     * the program itself
     * @param name The event's name
     * @param description The event's description
     * @param place The event's location
     * @param eventTags The event's tags
     * @param organizerID The ID of the user who organizes the event
     * @param eventStartTime The start time of the event
     * @param eventEndTime The ending time of the event
     * @param registrationStartTime The time when the registration for the event opens
     * @param registrationEndTime The time when the registration for the event closes
     * @param invitationAcceptanceDeadline The deadline for accepting the invitation for the event,
     *                                     assuming that you were selected by the lottery
     * @param maxWaitingListCapacity The maximum capacity of the waiting list
     * @param maxFinalListCapacity The maximum number of people who can be chosen for the event by
     *                             the lottery system
     */
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
        this.maxWaitingListCapacity = maxWaitingListCapacity; // Default as no limit
        this.posters = posters;

        Database db = new Database();
        db.getUser(organizerID, task -> {
            if (task.isSuccessful()) {
                this.organizer = task.getResult();
            } else {
                Log.e("Event", "Cannot get user info");
            }
        });
    }

    /**
     * This constructor can also allow for the instantiation of the event object from the program,
     * but the main purpose here is to help with testing
     * @param name The event's name
     * @param description The event's description
     * @param place The event's location
     * @param eventTags The event's tags
     * @param organizerID The ID of the user who organizes the event
     *@param eventStartTime The start time of the event
     * @param eventEndTime The ending time of the event
     * @param registrationStartTime The time when the registration for the event opens
     * @param registrationEndTime The time when the registration for the event closes
     * @param invitationAcceptanceDeadline The deadline for accepting the invitation for the event,
     *                                     assuming that you were selected by the lottery
     * @param maxWaitingListCapacity The maximum capacity of the waiting list, or -1 if there's no limit
     * @param maxFinalListCapacity The maximum number of people who can be chosen for the event by
     *                             the lottery system
     */
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

        this.eventTags = new ArrayList<>(Arrays.asList(eventTags));
        this.place = place;

        this.entrantList = new EntrantList();
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = maxWaitingListCapacity; // Default as no limit

        Database db = new Database();
        db.getUser(organizerID, task -> {
            if (task.isSuccessful()) {
                this.organizer = task.getResult();
            } else {
                Log.e("Event", "Cannot get user info");
            }
        });

    }

    /**
     * This constructor can also allow for the instantiation of the event object from the program,
     * but the main purpose here is to help with mock testing
     * @param name The event's name
     * @param description The event's description
     * @param place The event's location
     * @param eventTags The event's tags
     * @param organizerID The ID of the user who organizes the event
     *@param eventStartTime The start time of the event
     * @param eventEndTime The ending time of the event
     * @param registrationStartTime The time when the registration for the event opens
     * @param registrationEndTime The time when the registration for the event closes
     * @param invitationAcceptanceDeadline The deadline for accepting the invitation for the event,
     *                                     assuming that you were selected by the lottery
     * @param maxWaitingListCapacity The maximum capacity of the waiting list, or -1 if there's no limit
     * @param maxFinalListCapacity The maximum number of people who can be chosen for the event by
     *                             the lottery system
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event(String name, String description, String place, String[] eventTags, String organizerID, String eventStartTime, String eventEndTime,
                 String registrationStartTime, String registrationEndTime, String invitationAcceptanceDeadline,
                 int maxWaitingListCapacity, int maxFinalListCapacity, boolean mock){
        // Used for mock test

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

        this.eventTags = new ArrayList<>(Arrays.asList(eventTags));
        this.place = place;

        this.entrantList = new EntrantList();
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = -1; // Default as no limit

    }


    /**
     * Parses the timestamp objects and saves them into the LocalDateTime objects
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseTimestamps() {
        if (eventStartTimeTS != null)
            eventStartTime = eventStartTimeTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (eventEndTimeTS != null)
            eventEndTime = eventEndTimeTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (registrationStartTimeTS != null)
            registrationStartTime = registrationStartTimeTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (registrationEndTimeTS != null)
            registrationEndTime = registrationEndTimeTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (invitationAcceptanceDeadlineTS != null)
            invitationAcceptanceDeadline = invitationAcceptanceDeadlineTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Gets the event's eventID
     * @return The eventID of the event
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Sets the event's eventID
     * @param eventID The eventID of the event
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Gets the event's name
     * @return The name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the event's eventID
     * @param name The name of the event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the event's description
     * @return The event's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the event's description
     * @param description The event's description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the event's place
     * @return The event's place
     */
    public String getPlace() {
        return place;
    }

    /**
     * Sets the event's place
     * @param place The event's place
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * Gets the event's tags
     * @return The event's tags
     */
    public ArrayList<String> getEventTags() {
        return eventTags;
    }

    /**
     * Sets the event's tags
     * @param eventTags The event's tags
     */
    public void setEventTags(ArrayList<String> eventTags) {
        this.eventTags = eventTags;
    }

    /**
     * Gets the event's organizerID
     * @return The event's organizerID
     */
    public String getOrganizerID(){
        return organizerID;
    }

    /**
     * Sets the event's organizerID
     * @param organizerID The event's organizerID
     */
    public void setOrganizerID(String organizerID){
        this.organizerID = organizerID;
    }

    /**
     * Gets the event start time, in the Firebase Timestamp format
     * @return The start time of the event
     */
    @PropertyName("eventStartTime")
    public Timestamp getEventStartTimeTS() {
        return eventStartTimeTS;
    }

    /**
     * Sets the event start time, in the Firebase Timestamp format
     * @param eventStartTimeTS The start time of the event
     */
    @PropertyName("eventStartTime")
    public void setEventStartTimeTS(Timestamp eventStartTimeTS) {
        this.eventStartTimeTS = eventStartTimeTS;
    }

    /**
     * Gets the event end time, in the Firebase Timestamp format
     * @return The end time of the event
     */
    @PropertyName("eventEndTime")
    public Timestamp getEventEndTimeTS() {
        return eventEndTimeTS;
    }

    /**
     * Sets the event end time, in the Firebase Timestamp format
     * @param eventEndTimeTS The end time of the event
     */
    @PropertyName("eventEndTime")
    public void setEventEndTimeTS(Timestamp eventEndTimeTS) {
        this.eventEndTimeTS = eventEndTimeTS;
    }

    /**
     * Gets the registration start time, in the Firebase Timestamp format
     * @return The start time of registration
     */
    @PropertyName("registrationStartTime")
    public Timestamp getRegistrationStartTimeTS() {
        return registrationStartTimeTS;
    }

    /**
     * Sets the registration start time, in the Firebase Timestamp format
     * @param registrationStartTimeTS The start time of registration
     */
    @PropertyName("registrationStartTime")
    public void setRegistrationStartTimeTS(Timestamp registrationStartTimeTS) {
        this.registrationStartTimeTS = registrationStartTimeTS;
    }

    /**
     * Gets the registration end time, in the Firebase Timestamp format
     * @return The end time of registration
     */
    @PropertyName("registrationEndTime")
    public Timestamp getRegistrationEndTimeTS() {
        return registrationEndTimeTS;
    }

    /**
     * Sets the registration end time, in the Firebase Timestamp format
     * @param registrationEndTimeTS The end time of registration
     */
    @PropertyName("registrationEndTime")
    public void setRegistrationEndTimeTS(Timestamp registrationEndTimeTS) {
        this.registrationEndTimeTS = registrationEndTimeTS;
    }

    /**
     * Gets the invitation acceptance deadline, in the Firebase Timestamp format
     * @return The deadline for accepting the invitation
     */
    @PropertyName("invitationAcceptanceDeadline")
    public Timestamp getInvitationAcceptanceDeadlineTS() {
        return invitationAcceptanceDeadlineTS;
    }

    /**
     * Sets the invitation acceptance deadline, in the Firebase Timestamp format
     * @param invitationAcceptanceDeadlineTS The deadline for accepting the invitation
     */
    @PropertyName("invitationAcceptanceDeadline")
    public void setInvitationAcceptanceDeadlineTS(Timestamp invitationAcceptanceDeadlineTS) {
        this.invitationAcceptanceDeadlineTS = invitationAcceptanceDeadlineTS;
    }

    /**
     * Gets the event's max waiting list capacity
     * @return The event's max waiting list capacity
     */
    public int getMaxWaitingListCapacity() {
        return maxWaitingListCapacity;
    }

    /**
     * Sets the event's max waiting list capacity
     * @param maxWaitingListCapacity The event's max waiting list capacity
     */
    public void setMaxWaitingListCapacity(int maxWaitingListCapacity) {
        this.maxWaitingListCapacity = maxWaitingListCapacity;
    }

    /**
     * Gets the event's max final list capacity
     * @return The event's max final list capacity
     */
    public int getMaxFinalListCapacity() {
        return maxFinalListCapacity;
    }

    /**
     * Sets the event's max final list capacity
     * @param maxFinalListCapacity The event's max final list capacity
     */
    public void setMaxFinalListCapacity(int maxFinalListCapacity) {
        this.maxFinalListCapacity = maxFinalListCapacity;
    }

    /**
     * Gets the event's start time
     * @return The event's start time
     */
    @Exclude
    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }

    /**
     * Sets the event's start time
     * @param eventStartTime The event's start time
     */
    @Exclude
    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    /**
     * Gets the event's start time string
     * @return The event's start time
     */
    @Exclude
    public String getEventStartTimeString(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return this.eventStartTime.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Sets the event's start time string
     * @param dateString The event's start time
     */
    @Exclude
    public void setEventStartTimeString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.eventStartTime = LocalDateTime.parse(dateString, formatter);
        }
    }

    /**
     * Gets the event's end time
     * @return The event's end time
     */
    @Exclude
    public LocalDateTime getEventEndTime() {
        return eventEndTime;
    }

    /**
     * Sets the event's end time
     * @param eventEndTime The event's end time
     */
    @Exclude
    public void setEventEndTime(LocalDateTime eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    /**
     * Gets the event's end time string
     * @return The event's end time
     */
    @Exclude
    public String getEventEndTimeString(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return this.eventEndTime.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Sets the event's end time string
     * @param dateString The event's end time
     */
    @Exclude
    public void setEventEndTimeString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.eventStartTime = LocalDateTime.parse(dateString, formatter);
        }
    }


    /**
     * Gets the event's registration start time
     * @return The registration start time
     */
    @Exclude
    public LocalDateTime getRegistrationStartTime(){
        return registrationStartTime;
    }

    /**
     * Sets the event's registration start time
     * @param registrationStartTime The registration start time
     */
    @Exclude
    public void setRegistrationStartTime(LocalDateTime registrationStartTime) {
        this.registrationStartTime = registrationStartTime;
    }

    /**
     * Gets the event's registration start time string
     * @return The registration start time
     */
    @Exclude
    public String getRegistrationStartTimeString(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return this.registrationStartTime.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Sets the event's registration start time string
     * @param dateString The registration start time
     */
    @Exclude
    public void setRegistrationStartTimeString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.registrationStartTime = LocalDateTime.parse(dateString, formatter);
        }
    }

    /**
     * Gets the event's registration end time
     * @return The registration end time
     */
    @Exclude
    public LocalDateTime getRegistrationEndTime() {
        return registrationEndTime;
    }

    /**
     * Sets the event's registration end time
     * @param registrationEndTime The registration end time
     */
    @Exclude
    public void setRegistrationEndTime(LocalDateTime registrationEndTime) {
        this.registrationEndTime = registrationEndTime;
    }

    /**
     * Gets the event's registration end time string
     * @return The registration end time
     */
    @Exclude
    public String getRegistrationEndTimeString(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return this.registrationEndTime.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Sets the event's registration end time string
     * @param dateString The registration end time
     */
    @Exclude
    public void setRegistrationEndTimeString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.registrationEndTime = LocalDateTime.parse(dateString, formatter);
        }
    }

    /**
     * Gets the event's invitation acceptance deadline
     * @return The invitation acceptance deadline
     */
    @Exclude
    public LocalDateTime getInvitationAcceptanceDeadline() {
        return invitationAcceptanceDeadline;
    }

    /**
     * Sets the event's invitation acceptance deadline
     * @param invitationAcceptanceDeadline The invitation acceptance deadline
     */
    @Exclude
    public void setInvitationAcceptanceDeadline(LocalDateTime invitationAcceptanceDeadline) {
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;
    }

    /**
     * Gets the event's invitation acceptance deadline string
     * @return The invitation acceptance deadline
     */
    @Exclude
    public String getInvitationAcceptanceDeadlineString(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return this.invitationAcceptanceDeadline.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Sets the event's invitation acceptance deadline string
     * @param dateString The invitation acceptance deadline
     */
    @Exclude
    public void setInvitationAcceptanceDeadlineString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.invitationAcceptanceDeadline = LocalDateTime.parse(dateString, formatter);
        }
    }

    /**
     * Adds a new tag to the list of event tags
     * @param tag The tag to be added
     */
    @Exclude
    public void addEventTag(String tag){
        this.eventTags.add(tag);
    }

    /**
     * Remove a tag from the list of event tags
     * @param tag The tag to be removed
     */
    @Exclude
    public void deleteEventTag(String tag){
        this.eventTags.remove(tag);
    }

    @Exclude
    public void deleteEventTag(int position){
        if (0 <= position && position < this.eventTags.size()){
            this.eventTags.remove(position);
        } else {
            Log.e("Event", "Index out of bound, cannot delete tag");
        }
    }

    /**
     * A getter for the user object that represents the user who is organizing the event
     * @return The user object of the organizer
     */
    @Exclude
    public User getOrganizer() {
        return organizer;
    }

    /**
     * A setter for the user object that represents the user who is organizing the event
     * @param organizer The user object of the organizer
     */
    @Exclude
    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    /**
     * A getter for the entrant list
     * @return The entrant list object
     */
    @Exclude
    public EntrantList getEntrantList() {
        return entrantList;
    }

    /**
     * A setter for the entrant list
     * @param entrantList The entrant list object
     */
    @Exclude
    public void setEntrantList(EntrantList entrantList) {
        this.entrantList = entrantList;
    }

    /**
     * Sets a single entrant list, based on the ArrayList that is passed into it and the integer
     * parameter specifying which list to set. 0 means to set waiting list to the input ArrayList, 1
     * means to set the chosen list, 2 means to set the cancelled list, 3 means to set the finalized
     * list
     * @param entrantListValues The ArrayList of users to set one of the entrant lists to
     * @param list An integer specifying which list you want to set to be equal to the input
     *             ArrayList, where:
     *             0: waiting list
     *             1: chosen list
     *             2: cancelled list
     *             3: finalized list
     * @throws IllegalArgumentException If the input list is not one of 0, 1, 2, 3
     */
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
                break;
            default:
                throw new IllegalArgumentException("List number out of range");
        }
    }

    /**
     * Adds a user to one of the entrant lists, based on which list is specified in the list
     * argument.
     * @param user The user to be added to one of the entrant lists
     * @param list An integer specifying which list you want to set to be equal to the input
     *             ArrayList, where:
     *             0: waiting list
     *             1: chosen list
     *             2: cancelled list
     *             3: finalized list
     * @throws IllegalArgumentException If the input list is not one of 0, 1, 2, 3
     */
    public void addToEntrantList(User user, int list) throws IllegalArgumentException {
        switch (list) {
            case 0:
                this.entrantList.addToWaiting(user);
//                Log.d("Event", "Waiting list size now: " + entrantList.getWaiting().size());
                break;
            case 1:
                this.entrantList.addToChosen(user);
//                Log.d("Event", "Chosen list size now: " + entrantList.getChosen().size());
                break;
            case 2:
                this.entrantList.addToCancelled(user);
//                Log.d("Event", "Cancelled list size now: " + entrantList.getCancelled().size());
                break;
            case 3:
                this.entrantList.addToFinalized(user);
//                Log.d("Event", "Finalized list size now: " + entrantList.getFinalized().size());
                break;
            default:
                throw new IllegalArgumentException("List number out of range");
        }
    }

    /**
     * Deletes a user from one of the entrant lists, based on which list is specified in the list
     * argument.
     * @param user The user to be deleted from one of the entrant lists
     * @param list An integer specifying which list you want to set to be equal to the input
     *             ArrayList, where:
     *             0: waiting list
     *             1: chosen list
     *             2: cancelled list
     *             3: finalized list
     * @throws IllegalArgumentException If the input list is not one of 0, 1, 2, 3
     */
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
                this.entrantList.removeFromFinalized(user);
                break;
            default:
                throw new IllegalArgumentException("List number out of range");
        }
    }

    /**
     * Allows a user to join the waiting list for the event, given that they are not already a part
     * of this or some other entrant list for this event
     * @param user The user who wants to join the waiting list for the event
     */
    public void joinWaitingList(User user){
        if (!(entrantList.getChosen().contains(user) || entrantList.getCancelled().contains(user) || entrantList.getFinalized().contains(user))){
            if (!entrantList.getWaiting().contains(user)){
                addToEntrantList(user, 0);
                Database db = new Database();
                db.updateEvent(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Event", "User successfully joins waiting list");
                    }
                });
            } else {
                Log.e("Event", "User is already in the waiting list");
            }
        } else {
            Log.e("Event","User has already been selected from the waiting list");
        }
    }

    /**
     * Allow a user to leave the waiting list for the event, given that they are already in the
     * waiting list for the event
     * @param user The user to be removed from the event waiting list
     */
    public void leaveWaitingList(User user){
        if (entrantList.getWaiting().contains(user)){
            removeFromEntrantList(user, 0);
            Database db = new Database();
            db.updateEvent(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Event", "User successfully leaves waiting list");
                }
            });
        } else {
            Log.e("Event", "User is not in the waiting list");
        }
    }

    /**
     * Allows the user to join the chosen list, given that they already are in the waiting list, and
     * are not in the cancelled and finalized lists
     * @param user The user that is chosen to accept the invitation
     */
    public void joinChosenList(User user) throws IllegalArgumentException{
        if (entrantList.getWaiting().contains(user)) {
            if (!(entrantList.getCancelled().contains(user) || entrantList.getFinalized().contains(user))) {
                if (!entrantList.getChosen().contains(user)) {
                    removeFromEntrantList(user, 0);
                    addToEntrantList(user, 1);
                    Database db = new Database();
                    db.updateEvent(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d("Event", "User successfully joins chosen list");
                        }
                    });
                } else {
                    Log.e("Event", "User is already in the chosen list");
                }
            } else {
                Log.e("Event", "User has already been confirmed/rejected");
            }
        } else {
            Log.e("Event", "User is not in the waiting list");
        }
    }

    /**
     * Allows a user to leave the chosen list, given that they are already in the chosen list
     * @param user The user who wants to leave the chosen list
     */
    public void leaveChosenList(User user) throws IllegalArgumentException{
        if (entrantList.getChosen().contains(user)){
            removeFromEntrantList(user, 1);
            Database db = new Database();
            db.updateEvent(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Database", "User successfully leaves chosen list");
                }
            });
        } else {
            Log.e("Event", "User is not in the chosen list");
        }
    }

    /**
     * Allows a user to join the cancelled list, unlike other joins, this one has almost no
     * conditions, as a user can enter the cancelled list from any entrant list, or even from just
     * not being in the registration at all
     * @param user The user that is to be put into the cancelled list
     */
    public void joinCancelledList(User user){
        if (!entrantList.getCancelled().contains(user)){
            removeFromEntrantList(user, 0);
            removeFromEntrantList(user, 1);
            removeFromEntrantList(user, 3);
            addToEntrantList(user, 2);
            Database db = new Database();
            db.updateEvent(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Event", "User successfully joins cancelled list");
                }
            });
        } else {
            Log.e("Event", "User is already in the cancelled list");
        }
    }

    /**
     * Allows the user to join the finalized entrant list, given that they are already chosen by the
     * lottery and are in the chosen list
     * @param user The user
     */
    public void joinFinalizedList(User user){
        if(entrantList.getChosen().contains(user)) {
            if (!entrantList.getWaiting().contains(user) && !entrantList.getCancelled().contains(user)) {
                if (!entrantList.getFinalized().contains(user)) {
                    addToEntrantList(user, 3);
                    Database db = new Database();
                    db.updateEvent(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d("Database", "User successfully joins finalized list");
                        }
                    });
                } else {
                    Log.e("Event", "User is already in the finalized list");
                }
            } else {
                Log.e("Event", "User cannot join the finalized list for the event");
            }
        } else {
            Log.e("Event", "User has not been chosen");
        }
    }

    /**
     * Allows the user to leave the finalized list, given that they are already in the list
     * @param user The user who wants to leave the finalized list
     */
    public void leaveFinalizedList(User user){
        if (entrantList.getFinalized().contains(user)){
            removeFromEntrantList(user, 3);
            Database db = new Database();
            db.updateEvent(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Event", "User successfully leaves finalized list");
                }
            });
        } else {
            Log.e("Event", "User is not in the finalized list");
        }
    }

    /**
     * A getter for the posters list
     * @return The list of posters, which are bitmap objects in the program
     */
    @Exclude
    public ArrayList<Bitmap> getPosters() {
        return posters;
    }

    /**
     * A setter for the posters list
     * @param posters The list of posters, which are bitmap objects in the program
     */
    @Exclude
    public void setPosters(ArrayList<Bitmap> posters) {
        this.posters = posters;

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    /**
     * Adds a new poster to the posters list
     * @param poster The bitmap of the new poster to be added to the event list
     */
    public void addPoster(Bitmap poster){
        posters.add(poster);

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    /**
     * Deletes a poster from the posters list
     * @param poster The bitmap of the poster to be removed
     */
    public void deletePoster(Bitmap poster){
        posters.remove(poster);

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    /**
     * Deletes a poster based on its position in the posters ArrayList
     * @param position The zero indexed position of the poster to be removed
     */
    public void deletePoster(int position) {
        if (0 <= position && position < posters.size()) {
            posters.remove(position);
        } else {
            Log.e("Event", "Index out of bounds");
        }

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    /**
     * Using the eventID, and the hardcoded scheme and activity/host, we can create a QR code that,
     * when scanned by a phone with this app installed, will open up the app with the event
     * description page
     */
    public void generateQRCode(){
        try {
            String data = "cmput301gamblers://gamble/" + eventID;
            QRCodeBitmap = new BarcodeEncoder().encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
        } catch (WriterException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns the QR Code bitmap object, if it hasn't been generated yet then we generate first
     * before returning
     * @return A bitmap object of the QR code image
     */
    @Exclude
    public Bitmap getQRCodeBitmap() {
        if (QRCodeBitmap == null){
            generateQRCode();
        }
        return QRCodeBitmap;
    }

    /**
     * Sets the QR code bitmap to a specific bitmap
     * @param QRCodeBitmap The bitmap to set the QR code to
     */
    @Exclude
    public void setQRCodeBitmap(Bitmap QRCodeBitmap) {
        this.QRCodeBitmap = QRCodeBitmap;
    }

    /**
     * Sets the event poster download url (to be stored on Firebase Storage) of the event
     * @param eventPosterUrl the download url of the event poster image
     */
    public void setEventPosterUrl(String eventPosterUrl) {
        this.eventPosterUrl = eventPosterUrl;
    }

    /**
     * Returns the event poster download url
     * @return Download url of the event poster
     */
    public String getEventPosterUrl() {
        return eventPosterUrl;
    }
}