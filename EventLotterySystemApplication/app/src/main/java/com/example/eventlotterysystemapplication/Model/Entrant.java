package com.example.eventlotterysystemapplication.Model;

import java.util.Objects;

public class Entrant {
    private User user;
    private Location entrantLocation;

    private EntrantStatus status;

    private Event event;

    public Entrant(){}

    public Entrant(User user, Location entrantLocation, EntrantStatus entrantStatus, Event event){
        this.user = user;
        this.entrantLocation = entrantLocation;
        this.status = entrantStatus;
        this.event = event;
    }

    public Entrant(User user, EntrantStatus entrantStatus, Event event){
        this.user = user;
        this.entrantLocation = entrantLocation;
        this.status = entrantStatus;
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return entrantLocation;
    }

    public void setLocation(Location entrantLocation) {
        this.entrantLocation = entrantLocation;
    }

    public EntrantStatus getStatus() {
        return status;
    }

    public void setStatus(EntrantStatus status) {
        this.status = status;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entrant)) {
            return false;
        } else if (this == o) {
            return true;
        } else {
            Entrant entrant2 = (Entrant) o;
            return this.user.equals(entrant2.getUser()) && Objects.equals(this.event.getEventID(), entrant2.event.getEventID());
        }
    }
}
