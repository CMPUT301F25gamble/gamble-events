package com.example.eventlotterysystemapplication.Model;

public class Entrant {
    private User user;
    private EntrantLocation entrantLocation;

    private EntrantStatus status;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EntrantLocation getLocation() {
        return entrantLocation;
    }

    public void setLocation(EntrantLocation entrantLocation) {
        this.entrantLocation = entrantLocation;
    }

    public EntrantStatus getStatus() {
        return status;
    }

    public void setStatus(EntrantStatus status) {
        this.status = status;
    }
}
