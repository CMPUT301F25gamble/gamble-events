package com.example.eventlotterysystemapplication;

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
 *
 */
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

    private ArrayList<Bitmap> posters;

    /*
    Include code to have some attributes that points to an event poster, I wouldn't know how to
    declare attributes of that type yet
     */

    /*
    Geolocation requirement
     */


    /**
     *
     */
    public Event() {

    }

    /**
     *
     * @param name
     * @param description
     * @param place
     * @param eventTags
     * @param organizerID
     * @param eventStartTime
     * @param eventEndTime
     * @param registrationStartTime
     * @param registrationEndTime
     * @param invitationAcceptanceDeadline
     * @param maxWaitingListCapacity
     * @param maxFinalListCapacity
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

    /**
     *
     * @param name
     * @param description
     * @param place
     * @param eventTags
     * @param organizerID
     * @param eventStartTime
     * @param eventEndTime
     * @param registrationStartTime
     * @param registrationEndTime
     * @param invitationAcceptanceDeadline
     * @param maxWaitingListCapacity
     * @param maxFinalListCapacity
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

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseTimestamps() {
        if (eventStartTimeTS != null)
            eventStartTime = eventStartTimeTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (registrationEndTimeTS != null)
            registrationEndTime = registrationEndTimeTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (invitationAcceptanceDeadlineTS != null)
            invitationAcceptanceDeadline = invitationAcceptanceDeadlineTS.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     *
     * @return
     */
    public String getEventID() {
        return eventID;
    }

    /**
     *
     * @param eventID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getPlace() {
        return place;
    }

    /**
     *
     * @param place
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getEventTags() {
        return eventTags;
    }

    /**
     *
     * @param eventTags
     */
    public void setEventTags(ArrayList<String> eventTags) {
        this.eventTags = eventTags;
    }

    /**
     *
     * @return
     */
    public String getOrganizerID(){
        return organizerID;
    }

    /**
     *
     * @param organizerID
     */
    public void setOrganizerID(String organizerID){
        this.organizerID = organizerID;
    }

    /**
     *
     * @return
     */
    @PropertyName("eventStartTime")
    public Timestamp getEventStartTimeTS() {
        return eventStartTimeTS;
    }

    /**
     *
     * @param eventStartTimeTS
     */
    @PropertyName("eventStartTime")
    public void setEventStartTimeTS(Timestamp eventStartTimeTS) {
        this.eventStartTimeTS = eventStartTimeTS;
    }

    /**
     *
     * @return
     */
    @PropertyName("eventEndTime")
    public Timestamp getEventEndTimeTS() {
        return eventEndTimeTS;
    }

    /**
     *
     * @param eventEndTimeTS
     */
    @PropertyName("eventEndTime")
    public void setEventEndTimeTS(Timestamp eventEndTimeTS) {
        this.eventEndTimeTS = eventEndTimeTS;
    }

    /**
     *
     * @return
     */
    @PropertyName("registrationStartTime")
    public Timestamp getRegistrationStartTimeTS() {
        return registrationStartTimeTS;
    }

    /**
     *
     * @param registrationStartTimeTS
     */
    @PropertyName("registrationStartTime")
    public void setRegistrationStartTimeTS(Timestamp registrationStartTimeTS) {
        this.registrationStartTimeTS = registrationStartTimeTS;
    }

    /**
     *
     * @return
     */
    @PropertyName("registrationEndTime")
    public Timestamp getRegistrationEndTimeTS() {
        return registrationEndTimeTS;
    }

    /**
     *
     * @param registrationEndTimeTS
     */
    @PropertyName("registrationEndTime")
    public void setRegistrationEndTimeTS(Timestamp registrationEndTimeTS) {
        this.registrationEndTimeTS = registrationEndTimeTS;
    }

    /**
     *
     * @return
     */
    @PropertyName("invitationAcceptanceDeadline")
    public Timestamp getInvitationAcceptanceDeadlineTS() {
        return invitationAcceptanceDeadlineTS;
    }

    /**
     *
     * @param invitationAcceptanceDeadlineTS
     */
    @PropertyName("invitationAcceptanceDeadline")
    public void setInvitationAcceptanceDeadlineTS(Timestamp invitationAcceptanceDeadlineTS) {
        this.invitationAcceptanceDeadlineTS = invitationAcceptanceDeadlineTS;
    }

    /**
     *
     * @return
     */
    public int getMaxWaitingListCapacity() {
        return maxWaitingListCapacity;
    }

    /**
     *
     * @param maxWaitingListCapacity
     */
    public void setMaxWaitingListCapacity(int maxWaitingListCapacity) {
        this.maxWaitingListCapacity = maxWaitingListCapacity;
    }

    /**
     *
     * @return
     */
    public int getMaxFinalListCapacity() {
        return maxFinalListCapacity;
    }

    /**
     *
     * @param maxFinalListCapacity
     */
    public void setMaxFinalListCapacity(int maxFinalListCapacity) {
        this.maxFinalListCapacity = maxFinalListCapacity;
    }

    /**
     *
     * @return
     */
    @Exclude
    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }

    /**
     *
     * @param eventStartTime
     */
    @Exclude
    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    /**
     *
     * @return
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
     *
     * @param dateString
     */
    @Exclude
    public void setEventStartTimeString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.eventStartTime = LocalDateTime.parse(dateString, formatter);
        }
    }

    /**
     *
     * @return
     */
    @Exclude
    public LocalDateTime getRegistrationEndTime() {
        return registrationEndTime;
    }

    /**
     *
     * @param registrationEndTime
     */
    @Exclude
    public void setRegistrationEndTime(LocalDateTime registrationEndTime) {
        this.registrationEndTime = registrationEndTime;
    }

    /**
     *
     * @return
     */
    @Exclude
    public String getSignupDeadlineString(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return this.registrationEndTime.format(formatter);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     *
     * @param dateString
     */
    @Exclude
    public void setSignupDeadlineString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.registrationEndTime = LocalDateTime.parse(dateString, formatter);
        }
    }

    /**
     *
     * @return
     */
    @Exclude
    public LocalDateTime getInvitationAcceptanceDeadline() {
        return invitationAcceptanceDeadline;
    }

    /**
     *
     * @param invitationAcceptanceDeadline
     */
    @Exclude
    public void setInvitationAcceptanceDeadline(LocalDateTime invitationAcceptanceDeadline) {
        this.invitationAcceptanceDeadline = invitationAcceptanceDeadline;
    }

    /**
     *
     * @return
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
     *
     * @param dateString
     */
    @Exclude
    public void setInvitationAcceptanceDeadlineString(String dateString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.invitationAcceptanceDeadline = LocalDateTime.parse(dateString, formatter);
        }
    }

    /**
     *
     * @param tag
     */
    @Exclude
    public void addEventTag(String tag){
        this.eventTags.add(tag);
    }

    /**
     *
     * @param tag
     */
    @Exclude
    public void deleteEventTag(String tag){
        this.eventTags.remove(tag);
    }

    /**
     *
     * @return
     */
    @Exclude
    public User getOrganizer() {
        return organizer;
    }

    /**
     *
     * @param organizer
     */
    @Exclude
    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    /**
     *
     * @return
     */
    @Exclude
    public EntrantList getEntrantList() {
        return entrantList;
    }

    /**
     *
     * @param entrantList
     */
    @Exclude
    public void setEntrantList(EntrantList entrantList) {
        this.entrantList = entrantList;
    }

    /**
     *
     * @param entrantListValues
     * @param list
     * @throws IllegalArgumentException
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
     *
     * @param user
     * @param list
     * @throws IllegalArgumentException
     */
    public void addToEntrantList(User user, int list) throws IllegalArgumentException {
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
    }

    /**
     *
     * @param user
     * @param list
     * @throws IllegalArgumentException
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
                this.entrantList.removeFromCancelled(user);
                break;
            default:
                throw new IllegalArgumentException("List number out of range");
        }
    }

    /**
     *
     * @param user
     * @throws IllegalArgumentException
     */
    public void joinWaitingList(User user) throws IllegalArgumentException{
        if (!(entrantList.getChosen().contains(user) || entrantList.getCancelled().contains(user) || entrantList.getFinalized().contains(user))){
            if (!entrantList.getWaiting().contains(user)){
                addToEntrantList(user, 0);
                Database db = new Database();
                db.updateEvent(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Database", "User successfully joins waiting list");
                    }
                });
            } else {
                throw new IllegalArgumentException("User is already in the waiting list");
            }
        } else {
            throw new IllegalArgumentException("User has already been selected from the waiting list");
        }
    }

    /**
     *
     * @param user
     * @throws IllegalArgumentException
     */
    public void leaveWaitingList(User user) throws IllegalArgumentException{
        if (entrantList.getWaiting().contains(user)){
            removeFromEntrantList(user, 0);
            Database db = new Database();
            db.updateEvent(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Database", "User successfully leaves waiting list");
                }
            });
        } else {
            throw new IllegalArgumentException("User is not in the waiting list");
        }
    }

    /**
     *
     * @param user
     * @throws IllegalArgumentException
     */
    public void joinChosenList(User user) throws IllegalArgumentException{
        if (!(entrantList.getCancelled().contains(user) || entrantList.getFinalized().contains(user))){
            if (!entrantList.getChosen().contains(user)){
                addToEntrantList(user, 1);
                Database db = new Database();
                db.updateEvent(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Database", "User successfully joins chosen list");
                    }
                });
            } else {
                throw new IllegalArgumentException("User is already in the chosen list");
            }
        } else {
            throw new IllegalArgumentException("User has already been confirmed/rejected");
        }
    }

    /**
     *
     * @param user
     * @throws IllegalArgumentException
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
            throw new IllegalArgumentException("User is not in the chosen list");
        }
    }

    /**
     *
     * @param user
     * @throws IllegalArgumentException
     */
    public void joinCancelledList(User user) throws IllegalArgumentException{
        if (!entrantList.getCancelled().contains(user)){
            addToEntrantList(user, 2);
            Database db = new Database();
            db.updateEvent(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Database", "User successfully joins cancelled list");
                }
            });
        } else {
            throw new IllegalArgumentException("User is already in the cancelled list");
        }
    }

    /**
     *
     * @param user
     * @throws IllegalArgumentException
     */
    public void joinFinalizedList(User user) throws IllegalArgumentException{
        if(entrantList.getChosen().contains(user)) {
            if (!entrantList.getFinalized().contains(user)){
                addToEntrantList(user, 3);
                Database db = new Database();
                db.updateEvent(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Database", "User successfully joins finalized list");
                    }
                });
            } else {
                throw new IllegalArgumentException("User is already in the finalized list");
            }
        } else {
            throw new IllegalArgumentException("User has not been chosen");
        }
    }

    /**
     *
     * @param user
     * @throws IllegalArgumentException
     */
    public void leaveFinalizedList(User user) throws IllegalArgumentException{
        if (entrantList.getFinalized().contains(user)){
            removeFromEntrantList(user, 3);
            Database db = new Database();
            db.updateEvent(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Database", "User successfully leaves finalized list");
                }
            });
        } else {
            throw new IllegalArgumentException("User is not in the finalized list");
        }
    }

    /**
     *
     * @return
     */
    public ArrayList<Bitmap> getPosters() {
        return posters;
    }

    /**
     *
     * @param posters
     */
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
     *
     * @param poster
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
     *
     * @param poster
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
     *
     * @param position
     */
    public void deletePoster(int position) {
        if (0 <= position && position < posters.size()) {
            posters.remove(position);
        } else {
            Log.e("Poster Removal", "Index out of bounds");
        }

        Database db = new Database();
        db.updateEvent(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot update event");
            }
        });
    }

    /**
     *
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
     *
     * @return
     */
    public Bitmap getQRCodeBitmap() {
        if (QRCodeBitmap == null){
            generateQRCode();
        }
        return QRCodeBitmap;
    }

    /**
     *
     * @param QRCodeBitmap
     */
    public void setQRCodeBitmap(Bitmap QRCodeBitmap) {
        this.QRCodeBitmap = QRCodeBitmap;
    }
}
