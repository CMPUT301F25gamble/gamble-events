package com.example.eventlotterysystemapplication.Model;

import java.util.Objects;

/**
 * An instance of this class bundles the metadata of the user joining a particular event
 **/

public class Entrant {
    private User user;
    private EntrantLocation entrantLocation;

    private EntrantStatus status;


    /**
     * Empty constructor for firebase, and also for the convenience of defining attributes through
     * the setters after object creation
     */
    public Entrant(){}

    /**
     * constructor for the Entrant object
     * @param user The entrant's user
     * @param entrantLocation The entrant's entrant location
     * @param entrantStatus The entrant's entrant status
     */
    public Entrant(User user, EntrantLocation entrantLocation, EntrantStatus entrantStatus){
        this.user = user;
        this.entrantLocation = entrantLocation;
        this.status = entrantStatus;
    }

    /**
     * constructor for the Entrant object
     * @param user The entrant's user
     * @param entrantStatus The entrant's entrant status
     */
    public Entrant(User user, EntrantStatus entrantStatus){
        this.user = user;
        this.status = entrantStatus;
    }

    /**
     * Gets the user
     * @return The user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user
     * @param user The user
     */

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the location where the entrant joined the waiting list
     * @return The location
     */

    public EntrantLocation getLocation() {
        return entrantLocation;
    }

    /**
     * Sets the location where the entrant joined the waiting list
     * @param entrantLocation The location
     */

    public void setLocation(EntrantLocation entrantLocation) {
        this.entrantLocation = entrantLocation;
    }

    /**
     * Gets the status
     * @return The status
     */
    public EntrantStatus getStatus() {
        return status;
    }

    /**
     * Sets the status
     * @param status The status
     */

    public void setStatus(EntrantStatus status) {
        this.status = status;
    }

    /**
     * Check if a user object is equivalent to another user object
     * @param o An object that we are trying to test if this object is equal to
     * @return True if the two objects are equal, false otherwise
     */

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
