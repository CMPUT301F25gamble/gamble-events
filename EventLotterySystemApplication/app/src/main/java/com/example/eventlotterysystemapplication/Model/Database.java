package com.example.eventlotterysystemapplication.Model;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An instance of this class represents a connection to the firebase firestore database. Since this
 * is following a singleton design, our program is desinged to only have a single point of access to
 * the database
 */
public class Database {

    // our singleton instance
    private static Database database = null;
    private CollectionReference userRef;
    private CollectionReference eventRef;
    private CollectionReference notificationRef;
    private FirebaseAuth firebaseAuth;

    /**
     * Initializes the database object, without being given any database or authorization
     */
    private Database() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    /**
     * Initializes the database object, given some database and authorization
     * @param firestore
     * @param firebaseAuth
     */
    private Database(FirebaseFirestore firestore, FirebaseAuth firebaseAuth) {
        this.userRef = firestore.collection("User");
        this.eventRef = firestore.collection("Event");
        this.notificationRef = firestore.collection("Notification");
        this.firebaseAuth = firebaseAuth;
    }

    /**
     * A lazy constructor of the singleton instance of the database
     * @return The singleton database instance
     */
    public static Database getDatabase(){
        if (database == null){
            database = new Database();
        }

        return database;
    }

    /**
     * A lazy constructor for the singleton instance for the database, where we are given some
     * database and authorization
     * @param firestore
     * @param firebaseAuth
     * @return The singleton database instance
     */
    public static Database getDatabase(FirebaseFirestore firestore, FirebaseAuth firebaseAuth){
        if (database == null){
            database = new Database(firestore, firebaseAuth);
        }

        return database;
    }

    /**
     * Given some input deviceID, this function checks to see if the deviceID exists in the database
     * in the User collection
     * @param deviceID The device ID to query for in the database
     * @param listener An OnCompleteListener used to retrieve the boolean
     */
    public void queryDeviceID(String deviceID, OnCompleteListener<Boolean> listener) {
        Query deviceIDQuery = userRef.whereEqualTo("deviceID", deviceID);
        TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();

        deviceIDQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                int numberOfDeviceID = querySnapshot.size();
                if (numberOfDeviceID == 0) {
                    tcs.setResult(false);
                } else if (numberOfDeviceID == 1) {
                    tcs.setResult(true);
                } else {
                    tcs.setException(new IllegalStateException("Duplicate DeviceID found in database"));
                }
            } else {
                tcs.setException(task.getException());
            }
        });

        tcs.getTask().addOnCompleteListener(listener);
    }

    /**
     * Given some input deviceID, returns the User object that is associated with that deviceID
     * @param deviceID The deviceID of the user
     * @param listener An OnCompleteListener for callback
     */
    public void getUserFromDeviceID(String deviceID, OnCompleteListener<User> listener) {
        Query deviceIDQuery = userRef.whereEqualTo("deviceID", deviceID);
        TaskCompletionSource<User> tcs = new TaskCompletionSource<>();

        deviceIDQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<User> users = querySnapshot.toObjects(User.class);
                if (users.size() == 1) {
                    tcs.setResult(users.get(0));
                } else {
                    Log.e("Database", "More than one user with same device");
                    tcs.setException(new IllegalStateException("More than one user with same device"));
                }
            } else {
                Log.e("Database", task.getException().toString());
                tcs.setException(task.getException());
            }
        });

        tcs.getTask().addOnCompleteListener(listener);
    }

    /**
     * Given some userID, this function returns the corresponding User object
     * @param userID The userID to query against
     * @param listener An OnCompleteListener for callback
     * @throws IllegalStateException If the userID does not exist in the database
     */
    public void getUser(String userID, OnCompleteListener<User> listener) {
        DocumentReference userDocRef = userRef.document(userID);
        TaskCompletionSource<User> tcs = new TaskCompletionSource<>();

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        user.setUserID(userID);
                        tcs.setResult(user);
                    } else {
                        tcs.setException(new IllegalStateException("User data is null"));
                    }
                } else {
                    tcs.setException(new IllegalStateException("No user exists with that userID"));
                }
            } else {
                tcs.setException(task.getException());
            }
        });

        tcs.getTask().addOnCompleteListener(listener);
    }

    /**
     * Retrieves all users in the user collection
     * @param listener An OnCompleteListener used to retrieve a list of users
     */
    public void getAllUsers(OnCompleteListener<List<User>> listener) {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot doc: task.getResult()) {
                    User user = doc.toObject(User.class);
                    users.add(user);
                }
                listener.onComplete(Tasks.forResult(users));
            } else {
                Log.e("Database", "Fetch failed");
                listener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }


    /**
     * Given a user, add it to the database
     * @param user The user profile
     * @param listener An OnCompleteListener that will be called when the add operation finishes
     * Logs an error if the database cannot add the user
     */
    public void addUser(User user, OnCompleteListener<Void> listener) {
        Log.d("Database", "addUser called from: ", new Exception());

        // Sign user in anonymously
        firebaseAuth.signInAnonymously().addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser authUser = firebaseAuth.getCurrentUser();
                assert authUser != null;

                DocumentReference userDoc = userRef.document(authUser.getUid());
                userDoc.set(user)
                        .addOnCompleteListener(setTask -> {
                            if (setTask.isSuccessful()) {
                                user.setUserID(userDoc.getId());
                                Log.d("Database", "user ID is: " + user.getUserID());
                            }
                            // Notify completion
                            listener.onComplete(setTask);
                        });
            } else {
                Log.e("Database", "Firebase sign-in failed: " + authTask.getException());
                listener.onComplete(Tasks.forException(authTask.getException()));
            }
        });
    }


    /**
     * Given a user, update or create their record in the database
     * @param user The user profile
     * @param listener An OnCompleteListener that will be called when the modify operation finishes
     */
    public void modifyUser(User user, OnCompleteListener<Void> listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser authUser = auth.getCurrentUser();

        // Add user if the user does not exist
        if (authUser == null) {
            addUser(user, task -> {
                if (listener != null) {
                    listener.onComplete(task);
                }
            });
            return;
        }

        DocumentReference userDoc = userRef.document(authUser.getUid());
        userDoc.set(user, SetOptions.merge()).addOnCompleteListener(listener);
    }

    /**
     * Given a user, ADMIN can update or create their record in the database
     * @param userId ID of the user
     * @param user The user profile
     * @param listener An OnCompleteListener that will be called when the modify operation finishes
     */
    public void modifyUserById(String userId, User user, OnCompleteListener<Void> listener) {
        DocumentReference userDoc = userRef.document(userId);
        userDoc.set(user, SetOptions.merge()).addOnCompleteListener(listener);
    }

    /**
     * Given a user, delete their record from the database
     * @param user The user profile
     * @param listener An OnCompleteListener that will be called when the delete operation finishes
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteUser(User user, OnCompleteListener<Void> listener) {
        FirebaseUser authUser = firebaseAuth.getCurrentUser();
        if (authUser == null) {
            Log.e("Database", "User not authenticated");
            listener.onComplete(Tasks.forException(new IllegalStateException("User not authenticated")));
            return;
        }

        String targetUserId = user.getUserID();
        boolean deletingSelf = (authUser != null) && (authUser.getUid().equals(targetUserId));

        deleteOrganizedEvents(user, task -> {
            if (task.isSuccessful()){
                Log.d("Database", "Successfully deleted all organized events from user with userID: " + targetUserId);
            } else {
                Log.e("Database", "Couldn't delete all organized events from user with userID: " + targetUserId);
                listener.onComplete(task);
            }
        });

        // TODO: look at this function again
        eventRef.get().addOnSuccessListener(allEventsSnapshot -> {

            List<Task<Void>> regDeleteTasks = new ArrayList<>();

            for (DocumentSnapshot eventDoc : allEventsSnapshot.getDocuments()) {
                DocumentReference regDocRef = eventDoc.getReference()
                        .collection("Registration")
                        .document(targetUserId);
                Task<Void> deleteTask = regDocRef.get()
                        .continueWithTask(regDocTask -> {
                            if (regDocTask.isSuccessful() && regDocTask.getResult().exists()) {
                                return regDocRef.delete();
                            } else {
                                return Tasks.forResult(null);
                            }
                        });
                regDeleteTasks.add(deleteTask);
            }

            Tasks.whenAllComplete(regDeleteTasks).addOnCompleteListener(regDone -> {
                // Deletes user document
                userRef.document(targetUserId).delete().addOnCompleteListener(userDocDone -> {

                    if (!deletingSelf) {
                        // Admin deleting another user
                        if (userDocDone.isSuccessful()) {
                            Log.d("Database", "User deleted by an admin");
                            listener.onComplete(Tasks.forResult(null));
                            return;
                        } else {
                            Log.e("Database", "User deletion by admin failed");
                            listener.onComplete(Tasks.forException(userDocDone.getException()));
                        }
                        return;
                    }

                    // Deletes Firebase auth account (self-delete)
                    authUser.delete().addOnCompleteListener(done -> {
                        if (done.isSuccessful()) {
                            Log.d("Database", "User fully deleted");
                            listener.onComplete(Tasks.forResult(null));
                        } else {
                            Log.e("Database", "Firebase auth deletion failed");
                            listener.onComplete(Tasks.forException(done.getException()));
                        }});
                });
            });
        });
    }


    /**
     * Given some eventID, this method finds the event and returns that event object from the database
     * @param eventID The eventID of the event you are trying to retrieve
     * @param listener An OnCompleteListener used to retrieve the Event
     * @throws IllegalStateException This exception is thrown if no event exists with that eventID,
     * if the registration collection retrieval fails, or if the user status is not properly defined
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getEvent(String eventID, OnCompleteListener<Event> listener) {
        DocumentReference eventDocRef = eventRef.document(eventID);

        eventDocRef.get().addOnCompleteListener(eventTask -> {
            if (!eventTask.isSuccessful() || !eventTask.getResult().exists()) {
                listener.onComplete(Tasks.forException(
                        new IllegalStateException("Event not found or retrieval failed")
                ));
                return;
            }

            parseEvent(eventTask.getResult(), task -> {
                if (task.isSuccessful()) {
                    Event event = task.getResult();
                    if (event != null) {
                        event.setEventID(eventID);
                    }
                    listener.onComplete(Tasks.forResult(event));
                }
            });
        });
    }

    /**
     * Retrieves all events in the event collection
     * @param listener An OnCompleteListener used to retrieve a list of events
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAllEvents(OnCompleteListener<List<Event>> listener) {
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> events = new ArrayList<>();
                List<Task<Event>> parseTasks = new ArrayList<>();
                for (QueryDocumentSnapshot doc: task.getResult()) {
                    TaskCompletionSource<Event> tcs = new TaskCompletionSource<>();
                    parseTasks.add(tcs.getTask());
                    parseEvent(doc, task1 -> {
                        if (task1.isSuccessful()) {
                            Event event = task1.getResult();
                            events.add(event);
                            tcs.setResult(event);
                        } else {
                            tcs.setException(task1.getException());
                        }
                    });
                }
                Tasks.whenAllComplete(parseTasks).addOnCompleteListener(done -> {
                    listener.onComplete(Tasks.forResult(events));
                });
            } else {
                Log.e("Database", "Fetch failed");
                listener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Retrieves all events that the user has joined + their status
     * @param userId The user's ID
     * @param listener An OnCompleteListener used to retrieve a list of Events
     */
    public void getUserEventsHistory(String userId, OnCompleteListener<Pair<List<Event>, List<EntrantStatus>>> listener){
        eventRef.get().addOnSuccessListener(eventSnapshot -> {
            List<Task<Void>> getUserEventsHistoryList = new ArrayList<>();
            List<Event> userEventsHistory = new ArrayList<>();
            // For Entrant Status, DO NOT REMOVE
            List<EntrantStatus> userStatuses = new ArrayList<>();

            for (DocumentSnapshot eventDocSnapshot : eventSnapshot.getDocuments()){
                TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

                CollectionReference registration = eventDocSnapshot.getReference().collection("Registration");
                registration.document(userId).get().addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        tcs.setResult(null);
                    }

                    // Start Entrant Status Logic, DO NOT REMOVE
                    String statusString = documentSnapshot.getString("status");
                    EntrantStatus status = null;

                    if (statusString != null) {
                        try {
                            status = EntrantStatus.valueOf(statusString);
                        } catch (IllegalArgumentException e) {
                            Log.e("Database", "Invalid status: " + statusString);
                            status = null;
                        }
                    }

                    EntrantStatus finalStatus = status;
                    // End Entrant Status Logic, DO NOT REMOVE

                    if (documentSnapshot.exists()){
                        getEvent(eventDocSnapshot.getId(), task -> {
                            if (task.isSuccessful()){
                                Log.d("Database", "Successfully retrieved event from reference");
                                Event event = task.getResult();
                                userEventsHistory.add(event);
                                userStatuses.add(finalStatus);
                                tcs.setResult(null);
                            } else {
                                Log.e("Database", "Failed to retrieve event");
                                tcs.setException(task.getException());
                            }
                        });
                    } else {
                        tcs.setResult(null);
                    }
                });
                getUserEventsHistoryList.add(tcs.getTask());
            }

            Tasks.whenAllComplete(getUserEventsHistoryList).addOnCompleteListener(done -> {
                listener.onComplete(Tasks.forResult(new Pair<>(userEventsHistory, userStatuses)));
            });
        });
    }

    /**
     * Retrieves all events that the user can join
     * @param user The user profile
     * @param listener An OnCompleteListener used to retrieve a list of Events
     */
    public void viewAvailableEvents(User user, OnCompleteListener<List<Event>> listener) {
        Timestamp now = Timestamp.now();

        // Queries events that are open for registration
        Query query = eventRef.whereLessThanOrEqualTo("registrationStartTime", now)
                .whereGreaterThanOrEqualTo("registrationEndTime", now);

        query.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Fetch failed", task.getException());
                listener.onComplete(Tasks.forException(task.getException()));
                return;
            }

            List<Event> availableEvents = new ArrayList<>();
            List<Task<?>> allTasks = new ArrayList<>();

            for (QueryDocumentSnapshot doc : task.getResult()) {
                if (doc.getString("organizerID").equals(user.getUserID())) continue;

                DocumentReference eventRef = doc.getReference();
                CollectionReference regDocRef = eventRef.collection("Registration");
                int waitListCapacity = doc.getLong("maxWaitingListCapacity").intValue();

                // Checks if wait list is not full
                Task<Event> regTask = regDocRef.get().continueWithTask(regCountTask -> {
                    int count = regCountTask.getResult().size();

                    if (waitListCapacity == -1 || count < waitListCapacity) {
                        TaskCompletionSource<Event> parseTaskSource = new TaskCompletionSource<>();
                        parseEvent(doc, parseTask -> {
                            if (parseTask.isSuccessful()) {
                                parseTaskSource.setResult(parseTask.getResult());
                            } else {
                                parseTaskSource.setException(parseTask.getException());
                            }
                        });
                        return parseTaskSource.getTask();
                    } else {
                        return Tasks.forResult(null);
                    }
                }).addOnSuccessListener(event -> {
                    if (event != null) availableEvents.add(event);
                });

                allTasks.add(regTask);
            }

            Tasks.whenAllComplete(allTasks).addOnCompleteListener(done -> {
                listener.onComplete(Tasks.forResult(availableEvents));
            });
        });
    }


    /**
     * Given some event object, we add its data to the database
     * @param event The event that we want to add to the database
     * @param listener An OnCompleteListener that will be called when the add operation finishes
     */
    public void addEvent(Event event, OnCompleteListener<Void> listener){
        DocumentReference eventDocRef = eventRef.document();
        event.setEventID(eventDocRef.getId());

        eventDocRef.set(event)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Database", "Event added successfully with Event ID: " + event.getEventID());
                    Log.d("Database", "Event added successfully with timestamp: " + event.getRegistrationEndTimeTS());

                    updateEventRegistration(event, eventDocRef, task1 -> {
                        if (task1.isSuccessful()){
                            Log.d("Database", "Event registration added successfully with Event ID: " + event.getEventID());
                            listener.onComplete(task);
                        } else {
                            Log.e("Database", "Failed to add registration: " + task.getException());
                            listener.onComplete(Tasks.forException(
                                    Objects.requireNonNull(task.getException())
                            ));
                        }
                    });
                } else {
                    Log.e("Database", "Failed to add event: " + task.getException());
                    listener.onComplete(task);
                }
            });
    }


    /**
     * Given some event, update its data in the database
     * @param event The event that we want to update in the database
     * @param listener An OnCompleteListener that will be called when the update operation finishes
     */
    public void updateEvent(Event event, OnCompleteListener<Void> listener){
        if (event.getEventID() == null) {
            Log.e("Database", "Error: eventID is null");
            TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
            tcs.setException(new Exception("Event ID is null"));
            listener.onComplete(tcs.getTask());
            return;
        }

        DocumentReference eventDocRef = eventRef.document(event.getEventID());

        // USED TO ADMIN DON'T DELETE!!
        // Updates the event poster URL if the poster URL is not null
        if (event.getEventPosterUrl() == null) {
            eventDocRef.update("eventPosterUrl", null);
        }

        eventDocRef.set(event, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Database", "Event updated successfully with Event ID: " + event.getEventID());

                        CollectionReference registration = eventDocRef.collection("Registration");

                        // Delete all existing registrations first before re-adding the users in entrant lists
                        registration.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            List<Task<Void>> deleteRegTasks = new ArrayList<>();

                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                deleteRegTasks.add(doc.getReference().delete());
                            }

                            // Once all previous registrations are deleted we can re-add the users
                            Tasks.whenAllComplete(deleteRegTasks).addOnSuccessListener(tasks -> {
                                List<Task<Void>> regTasks = new ArrayList<>();

                                updateEventRegistration(event, eventDocRef, task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d("Database", "Event registration updated successfully with Event ID: " + event.getEventID());
                                        listener.onComplete(task1);
                                    } else {
                                        Log.e("Database", "Failed to update registration: " + task.getException());
                                        listener.onComplete(Tasks.forException(
                                                Objects.requireNonNull(task1.getException())
                                        ));
                                    }
                                });
                            });
                        });
                    } else {
                        Log.e("Database", "Failed to add event: " + task.getException());
                        listener.onComplete(task);
                    }
                }).addOnFailureListener(e -> Log.e("Database", "failed"));
    }

    /**
     * Given a user, delete all the events that the user organizes
     * @param user The user profile
     * @param listener An OnCompleteListener that will be called when the delete operation finishes
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteOrganizedEvents(User user, OnCompleteListener<Void> listener) {
        String userID = user.getUserID();
        eventRef.whereEqualTo("organizerID", userID).get().addOnSuccessListener(querySnapshot -> {
            List<Task<Void>> deleteTasks = new ArrayList<>();
            for (DocumentSnapshot eventDoc : querySnapshot.getDocuments()) {
                TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

                parseEvent(eventDoc, parseEventTask -> {
                    if (parseEventTask.isSuccessful()){
                        deleteEvent(parseEventTask.getResult(), deleteEventTask -> {
                            if (deleteEventTask.isSuccessful()) {
                                tcs.setResult(null);
                            } else {
                                Log.e("Database", "Failed to delete event");
                                tcs.setException(deleteEventTask.getException());
                            }
                        });
                        deleteTasks.add(tcs.getTask());
                    }
                });
            }

            Tasks.whenAllComplete(deleteTasks).addOnCompleteListener(done -> {
                listener.onComplete(Tasks.forResult(null));
            });
        });
    }

    /**
     * Given an event, delete it from the Event collection
     * @param event The event
     * @param listener An OnCompleteListener that will be called when the delete operation finishes
     */
    public void deleteEvent(Event event, OnCompleteListener<Void> listener){
        DocumentReference eventDocRef = eventRef.document(event.getEventID());
        CollectionReference regRef = eventDocRef.collection("Registration");
        regRef.get().addOnSuccessListener(querySnapshot -> {
            List<Task<Void>> deleteTasks = new ArrayList<>();
            for (DocumentSnapshot regDoc: querySnapshot.getDocuments()) {
                deleteTasks.add(regDoc.getReference().delete()
                        .addOnFailureListener(e -> Log.e("Database", "Failed to delete registration doc", e)));
            }
            Tasks.whenAllComplete(deleteTasks).addOnCompleteListener(done -> {
                eventDocRef.delete().addOnCompleteListener(listener)
                        .addOnFailureListener(e -> Log.e("Database", "Failed to delete event", e));
            });
        }).addOnFailureListener(e -> Log.e("Database", "Fail to get the event"));
    }

    /**
     * Given some notificationID, retrieve the notification object from the database
     * @param notificationID The notificationID we are querying against
     * @param listener An OnCompleteListener that will be called when the operation finishes
     */
    public void getNotification(String notificationID, OnCompleteListener<Notification> listener){

        DocumentReference notificationDocRef = notificationRef.document(notificationID);

        notificationDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()){
                Notification notification = task.getResult().toObject(Notification.class);
                notification.setNotificationID(notificationID);
                listener.onComplete(Tasks.forResult(notification));
            } else if (!task.getResult().exists()){
                Log.e("Database", "Document does not exist");
            } else {
                Log.e("Database", "Could not execute query");
            }
        });
    }

    /**
     * Retrieves all notifications in the notification collection
     * @param listener An OnCompleteListener used to retrieve a list of users
     */
    public void getAllNotifications(OnCompleteListener<List<Notification>> listener) {
        notificationRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Notification> notifications = new ArrayList<>();
                for (QueryDocumentSnapshot doc: task.getResult()) {
                    Notification notification = doc.toObject(Notification.class);
                    notifications.add(notification);
                }
                listener.onComplete(Tasks.forResult(notifications));
            } else {
                Log.e("Database", "Fetch failed");
                listener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    // TODO Add get notifications from a given recipient
    public void getUserNotificationHistory(String userId, OnCompleteListener<List<Notification>> listener){
        notificationRef.get().addOnSuccessListener(notificationSnapshot -> {
            List<Task<Notification>> getUserNotificationHistoryList = new ArrayList<>();
            List<Notification> userNotificationHistory = new ArrayList<>();

            for (DocumentSnapshot notificationDocSnapshot : notificationSnapshot.getDocuments()){
                TaskCompletionSource<Notification> tcs = new TaskCompletionSource<>();

                CollectionReference recipients = notificationDocSnapshot.getReference().collection("Recipients");
                recipients.document(userId).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        getNotification(notificationDocSnapshot.getId(), task -> {
                            if (task.isSuccessful()){
                                userNotificationHistory.add(task.getResult());
                                tcs.setResult(task.getResult());
                            } else {
                                Log.e("Database", "Failed to retrieve event");
                                tcs.setException(task.getException());
                            }
                        });
                    } else {
                        tcs.setResult(null);
                    }
                });

                getUserNotificationHistoryList.add(tcs.getTask());
            }

            Tasks.whenAllComplete(getUserNotificationHistoryList).addOnCompleteListener(done -> {
                listener.onComplete(Tasks.forResult(userNotificationHistory));
            });
        });
    }

    /**
     * This method is specific for allowing us to add to the recipient collection of the redraw of a
     * particular event, here check if the event already has a redraw notification, and if it does
     * we return that object, otherwise we return an exception indicating that a new redraw
     * notification object should be created
     * @param eventID The event we want to check for redraws
     * @param listener An OnCompleteListener that will be called when the operation finishes
     */
    public void getRedrawNotification(String eventID, OnCompleteListener<Notification> listener){
        Query redrawNotificationQuery = notificationRef.where(Filter.and(
                Filter.equalTo("eventID", eventID),
                Filter.equalTo("channelName", "lotteryRedrawNotification")
        ));

        redrawNotificationQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<Notification> notificationList = task.getResult().toObjects(Notification.class);
                ArrayList<Notification> notifications = new ArrayList<>(notificationList);

                if (notifications.size() > 0){
                    listener.onComplete(Tasks.forResult(notifications.get(0)));
                } else {
                    listener.onComplete(Tasks.forException(new IllegalArgumentException()));
                }
            } else {
                Log.e("Database", "Failed to query database");
                listener.onComplete(Tasks.forException(new IllegalArgumentException()));
            }
        });
    }

    /**
     * Adds a new notification object to the database
     * @param notification The notification object to be added to the database
     * @param listener An OnCompleteListener that will be called when the operation finishes
     */
    public void addNotification(Notification notification, OnCompleteListener<Void> listener){

        DocumentReference notificationDocRef = notificationRef.document();
        notification.setNotificationID(notificationDocRef.getId());

        notificationDocRef.set(notification).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Notification added successfully with Event ID: " + notification.getNotificationID());
                Log.d("Database", "Event added successfully with timestamp: " + notification.getNotificationSendTime());
                listener.onComplete(task);

            } else {
                Log.e("Database", "Failed to add notification: " + task.getException());
                listener.onComplete(task);
            }
        });
    }

    /**
     * Adds a userID to the recipient collection, indicating that the user is a recipient of the
     * notification
     * @param notification The notification object that is sent to the user
     * @param user The user who received the notification
     * @param listener An OnCompleteListener that will be called when the operation finishes
     */
    public void addNotificationRecipient(Notification notification, User user, OnCompleteListener<Void> listener){
        DocumentReference notificationDocRef = notificationRef.document(notification.getNotificationID());

        notificationDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("Database", "Successfully retrieved notification document from reference");
                DocumentSnapshot documentSnapshot = task.getResult();
                if (!documentSnapshot.exists()){
                    Log.d("Database", "Document does not previously exist in the database, adding it now");
                    addNotification(notification, task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("Database", "Document successfully added for the purpose of adding the recipient subcollection");
                            CollectionReference recipients = notificationDocRef.collection("Recipients");
                            Log.d("Database", "Notification document exists");

                            DocumentReference recipientDocRef = recipients.document(user.getUserID());
                            HashMap<String, Object> data = new HashMap<String, Object>();
                            data.put("userID", user.getUserID());
                            recipientDocRef.set(data).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    Log.d("Database", "Successfully added the recipient to the recipient subcollection");
                                }
                                listener.onComplete(task2);
                            });
                        }
                    });
                } else {
                    CollectionReference recipients = notificationDocRef.collection("Recipients");
                    Log.d("Database", "Notification document exists");

                    DocumentReference recipientDocRef = recipients.document(user.getUserID());
                    HashMap<String, Object> data = new HashMap<String, Object>();
                    data.put("userID", user.getUserID());
                    recipientDocRef.set(data).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            Log.d("Database", "Successfully added the recipient to the recipient subcollection");
                        }
                        listener.onComplete(task1);
                    });
                }
            }
        });
    }

    // TODO Add get events from userID

    /**
     * Given some DocumentSnapshot from the "Event" collection, this method takes the fields from
     * that document and manually matches it up with the fields from the event class, giving us fine
     * control over what is added to what fields in this class
     * @param doc The document from the Event collection to be parsed
     * @param listener An OnCompleteListener for retrieving the event object
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseEvent(@NonNull DocumentSnapshot doc, OnCompleteListener<Event> listener) {
        Event event = new Event();

        event.setEventID(doc.getId());
        event.setName(doc.getString("name"));
        event.setDescription(doc.getString("description"));
        event.setPlace(doc.getString("place"));
        event.setOrganizerID(doc.getString("organizerID"));
        List<String> tags = (List<String>) doc.get("eventTags");
        if (tags != null) {
            event.setEventTags(new ArrayList<>(tags));
        } else {
            event.setEventTags(new ArrayList<>());
        }

        event.setEventStartTimeTS(doc.getTimestamp("eventStartTime"));
        event.setEventEndTimeTS(doc.getTimestamp("eventEndTime"));
        event.setRegistrationStartTimeTS(doc.getTimestamp("registrationStartTime"));
        event.setRegistrationEndTimeTS(doc.getTimestamp("registrationEndTime"));
        event.setInvitationAcceptanceDeadlineTS(doc.getTimestamp("invitationAcceptanceDeadline"));

        event.setGeolocationRequirement(doc.getBoolean("geolocationRequirement"));

        event.parseTimestamps();

        if (doc.get("eventPosterUrl") != null) {
            event.setEventPosterUrl(doc.getString("eventPosterUrl"));
        }

        if (doc.getLong("maxWaitingListCapacity").intValue() > 0) {
            event.setMaxWaitingListCapacity(doc.getLong("maxWaitingListCapacity").intValue());
        }
        if (doc.getLong("maxFinalListCapacity").intValue() > 0) {
            event.setMaxFinalListCapacity(doc.getLong("maxFinalListCapacity").intValue());
        }
        parseEventRegistration(event, doc, task -> {
            if (task.isSuccessful()){
                listener.onComplete(task);
            } else {
                Log.e("Database", "Unable to parse event registration" + task.getException());
            }
        });
    }

    /**
     * Given some event and a document containing information for the event, this function will take
     * the users in the entrant list of the event and add them to the "Registration" subcollection
     * of the event's document
     * @param event The event document from which we want to extract the entrant list data
     * @param doc The DocumentReference of the document that we want to insert the users in the
     *            entrant list to the registration subcollection
     * @param listener An OnCompleteListener for callback
     */
    public void updateEventRegistration(Event event, DocumentReference doc, OnCompleteListener<Void> listener){
        CollectionReference registration = doc.collection("Registration");

        List<Task<Void>> regTasks = new ArrayList<>();

        for (Entrant entrant : event.getEntrantList()) {
            Map<String, Object> data = new HashMap<>();
            data.put("userID", entrant.getUser().getUserID());
            data.put("status", entrant.getStatus());
            Double latitude = null;
            Double longitude = null;
            EntrantLocation entrantLocation = entrant.getLocation();
            if(entrantLocation !=null) {
                latitude = entrantLocation.getLatitude();
                longitude = entrantLocation.getLongitude();
            }
            data.put("latitude",latitude);
            data.put("longitude", longitude);
            regTasks.add(registration.document(entrant.getUser().getUserID()).set(data));
        }
        Tasks.whenAllComplete(regTasks).addOnCompleteListener(done -> {
            listener.onComplete(Tasks.forResult(null));
        });
    }

    /**
     * Takes data from registration subcollection and stores it in the entrant list
     * object that is owned by the event class
     * @param event The event whose entrant lists we want to populate
     * @param doc The document from which we want to extract the entrants from its registration
     *            subcollection
     * @param listener An OnCompleteListener for callback
     */
    public void parseEventRegistration(Event event, DocumentSnapshot doc, OnCompleteListener<Event> listener){
        CollectionReference registration = doc.getReference().collection("Registration");
        List<Task<Event>> parseEventRegistrationTasks = new ArrayList<>();

        registration.get().addOnCompleteListener(regTask -> {

            if (!regTask.isSuccessful()) {
                listener.onComplete(Tasks.forException(
                        new IllegalStateException("Failed to retrieve registration")
                ));
                return;
            }

            for (DocumentSnapshot entrantDoc : regTask.getResult()) {
                TaskCompletionSource<Event> tcs = new TaskCompletionSource<>();

                getUser(entrantDoc.getId(), task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult();
                        String status = entrantDoc.getString("status");
                        Double latitude = entrantDoc.getDouble("latitude");
                        Double longitude = entrantDoc.getDouble("longitude");

                        EntrantLocation entrantLocation = null;
                        if(latitude!=null && longitude !=null) {
                            entrantLocation = new EntrantLocation();
                            entrantLocation.setLatitude(latitude);
                            entrantLocation.setLongitude(longitude);
                        }
                        EntrantStatus entrantStatus = null;
                        if(status!=null){
                            entrantStatus = EntrantStatus.valueOf(status.toUpperCase());
                        }else{
                            entrantStatus = EntrantStatus.WAITING;
                        }
                        Entrant entrant = new Entrant();
                        entrant.setUser(user);
                        entrant.setLocation(entrantLocation);
                        entrant.setStatus(entrantStatus);
                        event.addToEntrantList(entrant);
                        tcs.setResult(event);

                        Log.d("Test Database 1", "Success");

                    } else {
                        Log.e("Error", "Failed to get user", task.getException());
                        tcs.setException(task.getException());
                    }
                });
                parseEventRegistrationTasks.add(tcs.getTask());

            }

            Tasks.whenAllComplete(parseEventRegistrationTasks).addOnCompleteListener(done -> {
                listener.onComplete(Tasks.forResult(event));
            });
        });
    }

}