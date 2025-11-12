package com.example.eventlotterysystemapplication.Model;

import java.util.ArrayList;

/**
 * An instance of this class represents the waiting lists for a single event
 */
public class EntrantList {
    private ArrayList<User> waiting;
    private ArrayList<User> chosen;
    private ArrayList<User> cancelled;
    private ArrayList<User> finalized;

    /**
     * The general constructor for this class does not require any arguments, all attributes are
     * initialized to be empty ArrayLists, where the waiting list represents people who have signed
     * up in the waiting list but haven't been selected yet, chosen represents people who have been
     * selected by the lottery system but haven't accepted or declined yet, cancelled are the people
     * who declined their invitation to participate, and finalized are the people who have accepted
     * their invitation to participate
     */
    public EntrantList(){
        waiting = new ArrayList<>();
        chosen = new ArrayList<>();
        cancelled = new ArrayList<>();
        finalized = new ArrayList<>();
    }

    /**
     * A getter for the waiting list
     * @return The waiting list
     */
    public ArrayList<User> getWaiting() {
        return waiting;
    }

    /**
     * A getter for the chosen list
     * @return The chosen list
     */
    public ArrayList<User> getChosen() {
        return chosen;
    }

    /**
     * A getter for the cancelled list
     * @return The cancelled list
     */
    public ArrayList<User> getCancelled() {
        return cancelled;
    }

    /**
     * A getter for the finalized list
     * @return The finalized list
     */
    public ArrayList<User> getFinalized() {
        return finalized;
    }

    /**
     * A setter for the waiting list
     * @param waiting The waiting list
     */
    public void setWaiting(ArrayList<User> waiting) {
        this.waiting = waiting;
    }

    /**
     * A setter for the chosen list
     * @param chosen The chosen list
     */
    public void setChosen(ArrayList<User> chosen) {
        this.chosen = chosen;
    }

    /**
     * A setter for the cancelled list
     * @param cancelled The cancelled list
     */
    public void setCancelled(ArrayList<User> cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * A setter for the finalized list
     * @param finalized The finalized list
     */
    public void setFinalized(ArrayList<User> finalized) {
        this.finalized = finalized;
    }

    /**
     * Adds a user to the waiting list
     * @param user The user to be added
     */
    public void addToWaiting(User user){
        this.waiting.add(user);
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
    public void addToCancelled(User user){
        this.cancelled.add(user);
    }

    /**
     * Adds a user to the finalized list
     * @param user The user to be added
     */
    public void addToFinalized(User user){
        this.finalized.add(user);
    }

    /**
     * Removes a user to the waiting list
     * @param user The user to be removed
     */
    public void removeFromWaiting(User user){
        this.waiting.remove(user);
    }

    /**
     * Removes a user to the chosen list
     * @param user The user to be removed
     */
    public void removeFromChosen(User user){
        this.chosen.remove(user);
    }

    /**
     * Removes a user to the cancelled list
     * @param user The user to be removed
     */
    public void removeFromCancelled(User user){
        this.cancelled.remove(user);
    }

    /**
     * Removes a user to the finalized list
     * @param user The user to be removed
     */
    public void removeFromFinalized(User user){
        this.finalized.remove(user);
    }
}
