package com.example.eventlotterysystemapplication;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LotterySelector {

    /**
     * Draws N users from the waiting list where N is min(waitingListLength, maxFinalizedListCapacity)
     * @param event Event containing waiting list for users
     * @return The list of randomly accepted users to event
     */
    public List<User> drawAcceptedUsers(Event event) {
        int finalListCapacity = event.getMaxFinalListCapacity();
        ArrayList<User> waitingList = event.getEntrantList().getWaiting();

        if (waitingList.size() <= finalListCapacity) {
            // Don't need to draw randomly since under capacity so EVERYONE IS ACCEPTED WOOHOO
            return waitingList;
        }

        Random seed = new Random();
        Collections.shuffle(waitingList, seed);
        // subList returns a view (a reference) and not a copy
        return waitingList.subList(0, finalListCapacity);
    }

    /**
     * Draw a replacement user from the waiting list
     * if one of the users on the accepted list dropped out
     * @Precondition: Dropped out user is no longer on waiting list or accepted list
     * @param event Event containing waiting list and accepted list of users
     * @return A user to add to accepted list that was originally on waiting list
     * @throws IllegalStateException When the accepted list is identical to the waiting list
     * (cannot draw a unique replacement user)
     */
    public User drawReplacementUser(Event event) {
        Set<User> waitingList = new HashSet<>(event.getEntrantList().getWaiting());
        Set<User> acceptedList = new HashSet<>(event.getEntrantList().getChosen());

        if (waitingList.equals(acceptedList)) {
            throw new IllegalStateException("Cannot draw unique replacement user; accepted list and waiting list are identical");
        }

        Random rnd = new Random();
        User user;
        ArrayList<User> waitingListArray = new ArrayList<>(waitingList);

        do {
            // Get a user from waiting list that isn't from accepted list
            user = waitingListArray.get(rnd.nextInt(waitingList.size()));
        } while (acceptedList.contains(user));

        return user;
    }
}
