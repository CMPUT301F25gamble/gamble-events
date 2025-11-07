package com.example.eventlotterysystemapplication;

import java.util.ArrayList;

/**
 * EntrantList class to contain the entrants list for an event
 * To be implemented
 */

public class EntrantList {
    private ArrayList<User> waiting;
    private ArrayList<User> chosen;
    private ArrayList<User> cancelled;
    private ArrayList<User> finalized;

    public EntrantList(){
        waiting = new ArrayList<>();
        chosen = new ArrayList<>();
        cancelled = new ArrayList<>();
        finalized = new ArrayList<>();
    }

    public ArrayList<User> getWaiting() {
        return waiting;
    }

    public ArrayList<User> getChosen() {
        return chosen;
    }

    public ArrayList<User> getCancelled() {
        return cancelled;
    }

    public ArrayList<User> getFinalized() {
        return finalized;
    }

    public void setWaiting(ArrayList<User> waiting) {
        this.waiting = waiting;
    }

    public void setChosen(ArrayList<User> chosen) {
        this.chosen = chosen;
    }

    public void setCancelled(ArrayList<User> cancelled) {
        this.cancelled = cancelled;
    }

    public void setFinalized(ArrayList<User> finalized) {
        this.finalized = finalized;
    }

    public void addToWaiting(User user){
        this.waiting.add(user);
    }

    public void addToChosen(User user){
        this.chosen.add(user);
    }

    public void addToCancelled(User user){
        this.cancelled.add(user);
    }

    public void addToFinalized(User user){
        this.finalized.add(user);
    }

    public void removeFromWaiting(User user){
        this.waiting.remove(user);
    }

    public void removeFromChosen(User user){
        this.chosen.remove(user);
    }
    public void removeFromCancelled(User user){
        this.cancelled.remove(user);
    }
    public void removeFromFinalized(User user){
        this.finalized.remove(user);
    }
}
