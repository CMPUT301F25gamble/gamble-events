package com.example.eventlotterysystemapplication;

import android.os.Build;
import android.util.Log;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An instance of this class represents a connection to the firebase firestore database
 */
public class Database {
    private CollectionReference userRef;
    private CollectionReference eventRef;
    private CollectionReference notificationRef;
    private FirebaseAuth firebaseAuth;

    public static boolean flag = false;

    /**
     * Initializes the database object, without being given any database or authorization
     */
    public Database() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    /**
     * Initializes the database object, given some database and authorization
     * @param firestore
     * @param firebaseAuth
     */
    public Database(FirebaseFirestore firestore, FirebaseAuth firebaseAuth) {
        this.userRef = firestore.collection("User");
        this.eventRef = firestore.collection("Event");
        this.notificationRef = firestore.collection("Notification");
        this.firebaseAuth = firebaseAuth;
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
     * @return A user object containing the corresponding data from the database
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
                    tcs.setException(new IllegalStateException("More than one user with same device"));
                }
            } else {
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

        String userId = authUser.getUid();

        deleteOrganizedEvents(user, task -> {
            if (task == null){ // TODO: handle success case better
                Log.d("Database", "Successfully deleted all organized events from user with userID: " + userId);
            } else {
                Log.e("Database", "Couldn't delete all organized events from user with userID: " + userId);
                listener.onComplete(task);
            }
        });

        // TODO: look at this function again
        eventRef.get().addOnSuccessListener(allEventsSnapshot -> {

            List<Task<Void>> regDeleteTasks = new ArrayList<>();

            for (DocumentSnapshot eventDoc : allEventsSnapshot.getDocuments()) {
                DocumentReference regDocRef = eventDoc.getReference()
                        .collection("Registration")
                        .document(userId);
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
                userRef.document(userId).delete().addOnCompleteListener(userDocDone -> {
                    // Deletes Firebase auth account
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

            while (!flag) {
                Event event = parseEvent(eventTask.getResult(), listener);
                if (event != null) {
                    event.setEventID(eventID);
                }
            }

            //Log.d("Database 1", String.valueOf(event.getEntrantList().getWaiting()));

        });
    }


    /**
     * Retrieves all events that the user can join
     * @param user The user profile
     * @param listener An OnCompleteListener used to retrieve a list of Events
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void viewAvailableEvents(User user, OnCompleteListener<List<Event>> listener) {
        Timestamp now = Timestamp.now();
        // Queries events that are open for registration
        Query query = eventRef.whereLessThanOrEqualTo("registrationStartTime", now)
                .whereGreaterThanOrEqualTo("registrationEndTime", now);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Fetch successful");
                List<Event> availableEvents = new ArrayList<>();
                List<Task<QuerySnapshot>> subTasks = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if (!doc.getString("organizerID").equals(user.getUserID())) {
                        DocumentReference eventRef = doc.getReference();
                        CollectionReference regDocRef = eventRef.collection("Registration");
                        int waitListCapacity = doc.getLong("maxWaitingListCapacity").intValue();

                        // Checks if wait list is not full
                        Task<QuerySnapshot> regTask = regDocRef.get().addOnSuccessListener(regCount -> {
                            int count = regCount.size();
                            if (count < waitListCapacity) {
                                if ((waitListCapacity == -1) || (waitListCapacity > 0 && count < waitListCapacity)) {
                                    Event event = parseEvent(doc, task1 -> {
                                        if (task1.isSuccessful()) {
                                            // TODO
                                        } else {
                                            // TODO
                                        }
                                    });
                                    availableEvents.add(event);
                                }
                            }
                        });
                        subTasks.add(regTask);
                    }
                    Tasks.whenAllComplete(subTasks).addOnCompleteListener(done -> {
                        listener.onComplete(Tasks.forResult(availableEvents));
                    });
                }
            } else {
                Log.e("Database", "Fetch failed");
            }
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
                            } else {
                                Log.e("Database", "Failed to add registration: " + task.getException());
                                listener.onComplete(task);
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
        eventDocRef.set(event, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Database", "Event updated successfully with Event ID: " + event.getEventID());

                        updateEventRegistration(event, eventDocRef, task1 -> {
                            if (task1.isSuccessful()){
                                Log.d("Database", "Event registration updated successfully with Event ID: " + event.getEventID());
                            } else {
                                Log.e("Database", "Failed to update registration: " + task.getException());
                                listener.onComplete(task);
                            }
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

                Event event = parseEvent(eventDoc, parseEventTask -> {
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
                listener.onComplete(null);
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

    public void addNotificationLog(){
        // TODO: implement this method
    }

    /**
     * Given some DocumentSnapshot from the "Event" collection, this method takes the fields from
     * that document and manually matches it up with the fields from the event class, giving us fine
     * control over what is added to what fields in this class
     * @param doc The document from the Event collection to be parsed
     * @param listener An OnCompleteListener for callback
     * @return An event object, where the correct fields are extracted from the document
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event parseEvent(DocumentSnapshot doc, OnCompleteListener<Event> listener) {
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
        event.parseTimestamps();

        if (doc.getLong("maxWaitingListCapacity").intValue() > 0) {
            event.setMaxWaitingListCapacity(doc.getLong("maxWaitingListCapacity").intValue());
        }
        if (doc.getLong("maxFinalListCapacity").intValue() > 0) {
            event.setMaxFinalListCapacity(doc.getLong("maxFinalListCapacity").intValue());
        }

        if (event.getEntrantList() == null) {
            event.setEntrantList(new EntrantList());
            Log.d("ParseEvent", "entrantList initialized");

            parseEventRegistration(event, doc, task -> {
                Log.d("ParseEvent", "parseEventRegistration complete");
                if (task.isSuccessful()) { // TODO: handle success case better
                    listener.onComplete(Tasks.forResult(event));
                } else {
                    // TODO Failure condition
                }
            });
            Log.d("Database 2", "Entrant list size 555: " + event.getEntrantList().getWaiting().size());


        }

        return event;
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

        // Add waiting users
        for (User user : event.getEntrantList().getWaiting()) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "waiting");
            data.put("organizerID", event.getOrganizerID());
            regTasks.add(registration.document(user.getUserID()).set(data));
        }

        // Add chosen users
        for (User user : event.getEntrantList().getChosen()) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "chosen");
            data.put("organizerID", event.getOrganizerID());
            regTasks.add(registration.document(user.getUserID()).set(data));
        }

        // Add cancelled users
        for (User user : event.getEntrantList().getCancelled()) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "cancelled");
            data.put("organizerID", event.getOrganizerID());
            regTasks.add(registration.document(user.getUserID()).set(data));
        }

        // Add finalized users
        for (User user : event.getEntrantList().getFinalized()) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "finalized");
            data.put("organizerID", event.getOrganizerID());
            regTasks.add(registration.document(user.getUserID()).set(data));
        }

        Tasks.whenAllComplete(regTasks).addOnCompleteListener(done -> {
            listener.onComplete(null);
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
    public void parseEventRegistration(Event event, DocumentSnapshot doc, OnCompleteListener<Void> listener){
        CollectionReference registration = doc.getReference().collection("Registration");

        registration.get().addOnCompleteListener(regTask -> {
            if (!regTask.isSuccessful()) {
                listener.onComplete(Tasks.forException(
                        new IllegalStateException("Failed to retrieve registration")
                ));
                return;
            }

            for (DocumentSnapshot entrantDoc : regTask.getResult()) {
                String status = entrantDoc.getString("status");
                switch (status) {
                    case "waiting":
                        getUser(entrantDoc.getId(), task -> {
                            if (task.isSuccessful()) {
                                User user = task.getResult();
                                event.addToEntrantList(user, 0);
                                Log.d("Database 3", "parse: " + entrantDoc.getId());
                                Log.d("Database 4", "parse: " + event.getEntrantList().getWaiting().size());
                            } else {
                                Log.e("Error", "Failed to get user", task.getException());
                            }
                        });
                        break;
                    case "chosen":
                        getUser(entrantDoc.getId(), task -> {
                            if (task.isSuccessful()) {
                                User user = task.getResult();
                                event.addToEntrantList(user, 1);
                            } else {
                                Log.e("Error", "Failed to get user", task.getException());
                            }
                        });
                        break;
                    case "cancelled":
                        getUser(entrantDoc.getId(), task -> {
                            if (task.isSuccessful()) {
                                User user = task.getResult();
                                event.addToEntrantList(user, 2);
                            } else {
                                Log.e("Error", "Failed to get user", task.getException());
                            }
                        });
                        break;
                    case "finalized":
                        getUser(entrantDoc.getId(), task -> {
                            if (task.isSuccessful()) {
                                User user = task.getResult();
                                event.addToEntrantList(user, 3);
                            } else {
                                Log.e("Error", "Failed to get user", task.getException());
                            }
                        });
                        break;
                    default:
                        listener.onComplete(Tasks.forException(
                                new IllegalStateException("Invalid status")
                        ));
                        //return;
                }
            }
            flag = true;
            listener.onComplete(Tasks.forResult(null));
        });
    }

}
