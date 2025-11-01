package com.example.eventlotterysystemapplication;

import java.util.ArrayList;

public class EntrantList {
    private ArrayList<User> waiting;
    private ArrayList<User> chosen;
    private ArrayList<User> cancelled;
    private ArrayList<User> finalized;

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
}
