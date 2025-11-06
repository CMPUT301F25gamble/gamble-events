package com.example.eventlotterysystemapplication;

import java.util.ArrayList;

/**
 *
 */
public class EntrantList {
    private ArrayList<User> waiting;
    private ArrayList<User> chosen;
    private ArrayList<User> cancelled;
    private ArrayList<User> finalized;

    /**
     *
     */
    public EntrantList(){
        waiting = new ArrayList<>();
        chosen = new ArrayList<>();
        cancelled = new ArrayList<>();
        finalized = new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public ArrayList<User> getWaiting() {
        return waiting;
    }

    /**
     *
     * @return
     */
    public ArrayList<User> getChosen() {
        return chosen;
    }

    /**
     *
     * @return
     */
    public ArrayList<User> getCancelled() {
        return cancelled;
    }

    /**
     *
     * @return
     */
    public ArrayList<User> getFinalized() {
        return finalized;
    }

    /**
     *
     * @param waiting
     */
    public void setWaiting(ArrayList<User> waiting) {
        this.waiting = waiting;
    }

    /**
     *
     * @param chosen
     */
    public void setChosen(ArrayList<User> chosen) {
        this.chosen = chosen;
    }

    /**
     *
     * @param cancelled
     */
    public void setCancelled(ArrayList<User> cancelled) {
        this.cancelled = cancelled;
    }

    /**
     *
     * @param finalized
     */
    public void setFinalized(ArrayList<User> finalized) {
        this.finalized = finalized;
    }

    /**
     *
     * @param user
     */
    public void addToWaiting(User user){
        this.waiting.add(user);
    }

    /**
     *
     * @param user
     */
    public void addToChosen(User user){
        this.chosen.add(user);
    }

    /**
     *
     * @param user
     */
    public void addToCancelled(User user){
        this.cancelled.add(user);
    }

    /**
     *
     * @param user
     */
    public void addToFinalized(User user){
        this.finalized.add(user);
    }

    /**
     *
     * @param user
     */
    public void removeFromWaiting(User user){
        this.waiting.remove(user);
    }

    /**
     *
     * @param user
     */
    public void removeFromChosen(User user){
        this.chosen.remove(user);
    }

    /**
     *
     * @param user
     */
    public void removeFromCancelled(User user){
        this.cancelled.remove(user);
    }

    /**
     *
     * @param user
     */
    public void removeFromFinalized(User user){
        this.finalized.remove(user);
    }
}
