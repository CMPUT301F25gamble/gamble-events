package com.example.eventlotterysystemapplication.Model;

import java.util.Objects;

public class Entrant {
    private User user;
    private EntrantLocation entrantLocation;

    private EntrantStatus status;



    public Entrant(){}

    public Entrant(User user, EntrantLocation entrantLocation, EntrantStatus entrantStatus){
        this.user = user;
        this.entrantLocation = entrantLocation;
        this.status = entrantStatus;
    }

    public Entrant(User user, EntrantStatus entrantStatus){
        this.user = user;
        this.status = entrantStatus;
    }

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entrant)) {
            return false;
        } else if (this == o) {
            return true;
        } else {
            Entrant entrant2 = (Entrant) o;
            return this.user.equals(entrant2.getUser());
        }
    }
}
