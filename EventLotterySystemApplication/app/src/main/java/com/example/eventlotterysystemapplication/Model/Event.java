package com.example.eventlotterysystemapplication.Model;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private List<Entrant> entrantList;
    private int maxWaitingListCapacity;
    private int maxFinalListCapacity;
    private boolean isRecurring;
    private Boolean geolocationRequirement = true;
    private int recurringFrequency;
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
    private transient LocalDateTime recurringEndDate;
    private static DateTimeFormatter formatter= DateTimeFormatter.ISO_LOCAL_DATE_TIME;;

    @Exclude
    private Bitmap QRCodeBitmap;

    @Exclude
    private ArrayList<Bitmap> posters;

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
     * @param geolocationRequirement
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event(String name, String description, String place, ArrayList<String> eventTags, String organizerID, LocalDateTime eventStartTime, LocalDateTime eventEndTime,
                 LocalDateTime registrationStartTime, LocalDateTime registrationEndTime, LocalDateTime invitationAcceptanceDeadline,
                 int maxWaitingListCapacity, int maxFinalListCapacity, Boolean geolocationRequirement){
        this.name = name;
        this.description = description;
        this.place = place;
        this.eventTags = eventTags;
        this.organizerID = organizerID;

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

        this.entrantList = new ArrayList<Entrant>();
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = maxWaitingListCapacity; // Default as no limit
        this.isRecurring = false; // Default as non-recurring
        this.recurringFrequency = -1; // Default as non-recurring
        this.geolocationRequirement = geolocationRequirement;
    }

    /**
     * This constructor can also allow for the instantiation of the event object from the program,
     * but the main purpose here is to help with testing
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
     * @param maxWaitingListCapacity The maximum capacity of the waiting list, or -1 if there's no limit
     * @param maxFinalListCapacity The maximum number of people who can be chosen for the event by
     *                             the lottery system
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event(String name, String description, String place, String[] eventTags, String organizerID, String eventStartTime, String eventEndTime,
                 String registrationStartTime, String registrationEndTime, String invitationAcceptanceDeadline,
                 int maxWaitingListCapacity, int maxFinalListCapacity, Boolean geolocationRequirement){

        this.name = name;
        this.description = description;
        this.place = place;
        this.eventTags = new ArrayList<>(Arrays.asList(eventTags));
        this.organizerID = organizerID;

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

        this.entrantList = new ArrayList<Entrant>();
        this.maxFinalListCapacity = maxFinalListCapacity;
        this.maxWaitingListCapacity = maxWaitingListCapacity; // Default as no limit
        this.geolocationRequirement = geolocationRequirement;

        Database db = Database.getDatabase();
        db.getUser(organizerID, task -> {
            if (task.isSuccessful()) {
                this.organizer = task.getResult();
            } else {
                Log.e("Event", "Cannot get user info");
            }
        });

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
        this.isRecurring = false; // Default as non-recurring
        this.recurringFrequency = -1; // Default as non-recurring
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
     * Returns the event poster download url
     * @return Download url of the event poster
     */
    public String getEventPosterUrl() {
        return eventPosterUrl;
    }

    /**
     * Sets the event poster download url (to be stored on Firebase Storage) of the event
     * @param eventPosterUrl the download url of the event poster image
     */
    public void setEventPosterUrl(String eventPosterUrl) {
        this.eventPosterUrl = eventPosterUrl;
    }

    /**
     * Gets if the event is an recurring event
     * @return true if event recurring, false otherwise
     */
    @Exclude
    public boolean getIsRecurring() {
        return this.isRecurring;
    }

    /**
     * Sets an event recurring status
     * @param isRecurring boolean indicating whether or not an event is recurring
     */
    @Exclude
    public void setIsRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    /**
     * Gets recurring frequency of an event
     * @return recurring frequency: 1 means daily, 2 means weekly, 3 means monthly, 4 means yearly, -1 means no recurrence
     */
    @Exclude
    public int getRecurringFrequency() {
        return this.recurringFrequency;
    }

    /**
     * Sets recurring frequency of an event
     * @param recurringFrequency: 1 means daily, 2 means weekly, 3 means monthly, 4 means yearly, -1 means no recurrence
     */
    @Exclude
    public void setRecurringFrequency(int recurringFrequency) {
        if (recurringFrequency != -1 && (recurringFrequency < 1 || recurringFrequency > 4)) {
            throw new IllegalArgumentException("Invalid recurring frequency");
        }
        this.recurringFrequency = recurringFrequency;
    }


    @PropertyName("geolocationRequirement")
    public Boolean isGeolocationRequirement() {
        return Objects.requireNonNullElse(geolocationRequirement, false);
    }

    @PropertyName("geolocationRequirement")
    public void setGeolocationRequirement(Boolean geolocationRequirement) {
        this.geolocationRequirement = geolocationRequirement;
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
     * Gets an event's recurring end date
     * @return The event's recurring end date
     */
    @Exclude
    public LocalDateTime getRecurringEndDate() {
        return this.recurringEndDate;
    }

    /**
     * Sets an event's recurring end date
     * @param recurringEndDate The event's recurring end date
     */
    @Exclude
    public void setRecurringEndDate(LocalDateTime recurringEndDate) {
        this.recurringEndDate = recurringEndDate;
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

    @Exclude
    public List<Entrant> getEntrantList() {
        if(entrantList==null){
            entrantList = new ArrayList<Entrant>();
        }
        return entrantList;
    }

    /**
     * A getter for the entrant list
     * @param entrantStatus
     * @return The entrant list object
     */
    @Exclude
    public List<Entrant> getEntrantListByStatus(EntrantStatus entrantStatus) {
        return getEntrantList().stream()
                .filter(e -> e.getStatus() == entrantStatus)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an event's recurrence frequency in String
     * @return The event's recurrence frequency in String
     */
    public String displayRecurrenceFrequency() {
        if (this.getRecurringFrequency() == 1) {
            return "Daily";
        }
        if (this.getRecurringFrequency() == 2) {
            return "Weekly";
        }
        if (this.getRecurringFrequency() == 3) {
            return "Monthly";
        }
        if (this.getRecurringFrequency() == 4) {
            return "Yearly";
        }
        return "Not a recurring event";
    }

    /**
     * Adds a new tag to the list of event tags
     * @param tag The tag to be added
     */
    public void addEventTag(String tag){
        this.eventTags.add(tag);
    }

    /**
     * Remove a tag from the list of event tags
     * @param tag The tag to be removed
     */
    public void deleteEventTag(String tag){
        this.eventTags.remove(tag);
    }

    /**
     * Remove a tag from the list of event tags based on index
     * @param position The index of the tag
     */
    public void deleteEventTag(int position){
        if (0 <= position && position < this.eventTags.size()){
            this.eventTags.remove(position);
        } else {
            Log.e("Event", "Index out of bound, cannot delete tag");
        }
    }

    public void setEntrantList(List<Entrant> entrantList) {
        this.entrantList = entrantList;
    }

    /**
     * A getter for the entrant waiting list
     * @return The entrant list object
     */
    @Exclude
    public List<Entrant> getEntrantWaitingList() {
        return getEntrantListByStatus(EntrantStatus.WAITING);
    }

    /**
     * A getter for the entrant chosen list
     * @return The entrant list object
     */
    @Exclude
    public List<Entrant> getEntrantChosenList() {
        return getEntrantListByStatus(EntrantStatus.CHOSEN);
    }

    /**
     * A getter for the entrant cancelled list
     * @return The entrant list object
     */
    @Exclude
    public List<Entrant> getEntrantCancelledList() {
        return getEntrantListByStatus(EntrantStatus.CANCELLED);
    }

    /**
     * A getter for the entrant finalized list
     * @return The entrant list object
     */
    @Exclude
    public List<Entrant> getEntrantFinalizedList() {
        return getEntrantListByStatus(EntrantStatus.FINALIZED);
    }


    /**
     * A getter for the user list
     * @param entrantStatus
     * @return The user list object
     */
    @Exclude
    public List<User> getUserListByStatus(EntrantStatus entrantStatus) {
        return getEntrantList().stream()
                .filter(e -> e.getStatus() == entrantStatus)
                .map(Entrant::getUser)   // transform Entrant ? User
                .collect(Collectors.toList());
    }

    /**
     * A getter for the user waiting list
     * @return The entrant list object
     */
    @Exclude
    public List<User> getUserWaitingList() {
        return getUserListByStatus(EntrantStatus.WAITING);
    }

    /**
     * A getter for the user chosen list
     * @return The entrant list object
     */
    @Exclude
    public List<User> getUserChosenList() {
        return getUserListByStatus(EntrantStatus.CHOSEN);
    }

    /**
     * A getter for the user cancelled list
     * @return The entrant list object
     */
    @Exclude
    public List<User> getUserCancelledList() {
        return getUserListByStatus(EntrantStatus.CANCELLED);
    }

    /**
     * A getter for the entrant finalized list
     * @return The entrant list object
     */
    @Exclude
    public List<User> getUserFinalizedList() {
        return getUserListByStatus(EntrantStatus.FINALIZED);
    }


    @Exclude
    public void addToEntrantList(User user, EntrantLocation entrantLocation) throws IllegalArgumentException {
        Entrant entrant = new Entrant();
        entrant.setUser(user);
        entrant.setStatus(EntrantStatus.WAITING);
        entrant.setLocation(entrantLocation);
        addToEntrantList(entrant);
    }
    /**
     * Adds a user to one of the entrant lists, based on which list is specified in the list
     * argument.
     * @param entrant The user to be added to one of the entrant lists
     */
    @Exclude
    public void addToEntrantList(Entrant entrant) throws IllegalArgumentException {
        if(!isEntrantExists(entrant)){
            getEntrantList().add(entrant);
        }
    }

    public void addEntrantToChosenList(Entrant entrant){
        if (entrant.getStatus().equals(EntrantStatus.WAITING) && isEntrantExists(entrant)){
            entrant.setStatus(EntrantStatus.CHOSEN);
        } else {
            Log.e ("Event", "Cannot add user to the chosen list they are not in the waiting list");
        }
    }

    public void addEntrantToCancelledList(Entrant entrant){
        if (entrant.getStatus().equals(EntrantStatus.CHOSEN) && isEntrantExists(entrant)){
            entrant.setStatus(EntrantStatus.CANCELLED);
        } else {
            Log.e ("Event", "Cannot add user to the chosen list they are not in the chosen list");
        }
    }

    public void addEntrantToFinalizedList(Entrant entrant){
        if (entrant.getStatus().equals(EntrantStatus.CHOSEN) && isEntrantExists(entrant)){
            entrant.setStatus(EntrantStatus.FINALIZED);
        } else {
            Log.e ("Event", "Cannot add user to the chosen list they are not in the chosen list");
        }
    }

    @Exclude
    public boolean isEntrantExists(Entrant entrant){
        for(Entrant entrant1:getEntrantList()){
            if(entrant1.equals(entrant)){
                return true;
            }
        }
        return false;
    }

    @Exclude
    public Entrant genEntrantIfExists(User user){
        for(Entrant entrant:getEntrantList()){
            if(entrant.getUser().getUserID().equals(user.getUserID())){
                return entrant;
            }
        }
        return null;
    }


    @Exclude
    public boolean removeEntrant(Entrant entrant){
        for(Entrant entrant1:getEntrantList()){
            if(entrant1.getUser().getUserID().equals(entrant.getUser().getUserID())){
                getEntrantList().remove(entrant1);
                return true;
            }
        }
        return false;
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

        Database db = Database.getDatabase();
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

        Database db = Database.getDatabase();
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

        Database db = Database.getDatabase();
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

        Database db = Database.getDatabase();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    /**
     * Sets the QR code bitmap to a specific bitmap
     * @param QRCodeBitmap The bitmap to set the QR code to
     */
    public void setQRCodeBitmap(Bitmap QRCodeBitmap) {
        this.QRCodeBitmap = QRCodeBitmap;
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

}
