package com.example.eventlotterysystemapplication.Model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.eventlotterysystemapplication.Controller.LotteryDrawScheduler;
import com.example.eventlotterysystemapplication.Controller.NotificationSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import com.google.firebase.Timestamp;

/**
 * Lottery selector that contains methods for selecting from the waiting list and drawing
 * replacement users
 */

public class LotterySelector {


    public void processLotteryDraw(Context context, String eventId) {

        Database db = Database.getDatabase();
        db.getEvent(eventId, task ->  {
            // Error check if task is not successful
            if (!task.isSuccessful()) {
                Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch the event from the task
            Event event = task.getResult();
            Timestamp scheduledAt = event.getEventEndTimeTS();
            Timestamp now = Timestamp.now();

            if (scheduledAt == null && scheduledAt.compareTo(now) > 0) {
                Log.d("EventCheck", "Event time is in the future");
                Toast.makeText(context, "Event " + event.getName() + " draw is not performed as it is scheduled for Future date.", Toast.LENGTH_LONG).show();
                LotteryDrawScheduler lotteryDrawScheduler = new LotteryDrawScheduler();
                lotteryDrawScheduler.scheduleNewLotteryDraw(context,event);
                return;
            }

            if(event.getEntrantList().getWaiting().size() == 0){
                Toast.makeText(context, "Event " + event.getName() + " is already drawn or no one register for it.", Toast.LENGTH_LONG).show();
            }



            List<User> acceptedUsers = drawAcceptedUsers(event);

            // This code automatically takes care of taking the users from the waiting list and
            // adding to the chosen list
            for (User user : acceptedUsers){
                event.joinChosenList(user);
            }

            db.updateEvent(event,task1 ->  {
                // Error check if task is not successful
                if (!task1.isSuccessful()) {
                    Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
            });

            EventNotificationManager.notifyInitialLotterySelection(event);

            Toast.makeText(context, "Draw for event " + event.getName() + " is completed successfully.", Toast.LENGTH_LONG).show();
        });
    }
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
     * @param event Event containing waiting list and accepted list of users
     * @return A user to add to accepted list that was originally on waiting list
     * @throws IllegalStateException When the accepted list is identical to the waiting list
     * (cannot draw a unique replacement user)
     */
    public User drawReplacementUser(Event event) {
        Set<User> waitingList = new HashSet<>(event.getEntrantList().getWaiting());
        Set<User> acceptedList = new HashSet<>(event.getEntrantList().getChosen());
        Set<User> cancelledList = new HashSet<>(event.getEntrantList().getCancelled());

        if (waitingList.equals(acceptedList)) {
            throw new IllegalStateException("Cannot draw unique replacement user; accepted list and waiting list are identical");
        }

        Random rnd = new Random();
        User user;
        ArrayList<User> waitingListArray = new ArrayList<>(waitingList);

        do {
            // Get a user from waiting list that isn't from accepted list
            user = waitingListArray.get(rnd.nextInt(waitingList.size()));
        } while (acceptedList.contains(user) && !cancelledList.contains(user));

        return user;
    }
}
