package com.example.eventlotterysystemapplication.Model;

import java.util.HashMap;

public class EntrantListLocationTracking extends EntrantList{
    private HashMap<User, Double> joiningLatitude;
    private HashMap<User, Double> joiningLongitude;

    public EntrantListLocationTracking(){
        super();
    }

    /**
     * Adds a user to the waiting list
     * @param user The user to be added
     */
    public void addToWaiting(User user, double latitude, double longitude){
        this.waiting.add(user);
        joiningLatitude.put(user, latitude);
        joiningLongitude.put(user, longitude);
    }

    /**
     * Adds a user to the chosen list
     * @param user The user to be added
     */
    public void addToChosen(User user){
        this.chosen.add(user);
    }

    /**
     * Adds a user to the cancelled list
     * @param user The user to be added
     */
    @Override
    public void addToCancelled(User user){
        this.cancelled.add(user);
    }

    /**
     * Adds a user to the finalized list
     * @param user The user to be added
     */
    @Override
    public void addToFinalized(User user){
        this.finalized.add(user);
    }

    /**
     * Removes a user to the waiting list
     * @param user The user to be removed
     */
    @Override
    public void removeFromWaiting(User user){
        this.waiting.remove(user);
        this.joiningLatitude.remove(user);
        this.joiningLongitude.remove(user);
    }

    /**
     * Removes a user to the chosen list
     * @param user The user to be removed
     */
    @Override
    public void removeFromChosen(User user){
        this.chosen.remove(user);
        this.joiningLatitude.remove(user);
        this.joiningLongitude.remove(user);
    }

    /**
     * Removes a user to the cancelled list
     * @param user The user to be removed
     */
    @Override
    public void removeFromCancelled(User user){
        this.cancelled.remove(user);
        this.joiningLatitude.remove(user);
        this.joiningLongitude.remove(user);
    }

    /**
     * Removes a user to the finalized list
     * @param user The user to be removed
     */
    @Override
    public void removeFromFinalized(User user){
        this.finalized.remove(user);
        this.joiningLatitude.remove(user);
        this.joiningLongitude.remove(user);
    }
}
