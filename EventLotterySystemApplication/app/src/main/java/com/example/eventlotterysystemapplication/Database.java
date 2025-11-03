package com.example.eventlotterysystemapplication;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Database {

    CollectionReference userRef;
    CollectionReference eventRef;
    CollectionReference notificationRef;
    FirebaseAuth firebaseAuth;

    public Database() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

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
     * @return True if the deviceID exists exactly once, false if it exists zero times
     * @throws IllegalStateException If the query fails or duplicate deviceID is found
     */
    public boolean queryDeviceID(String deviceID) throws IllegalStateException{
        AtomicBoolean deviceIDInDatabase = new AtomicBoolean(false);

        Query deviceIDQuery = userRef.whereEqualTo("deviceID", deviceID);

        deviceIDQuery.get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        int numberOfDeviceID = querySnapshot.size();
                        if (numberOfDeviceID == 0){
                            deviceIDInDatabase.set(false);
                        } else if (numberOfDeviceID == 1){
                            deviceIDInDatabase.set(true);
                        } else {
                            throw new IllegalStateException("Duplicate DeviceID found in database");
                        }
                    } else {
                        Log.e("Database","Query failed");
                    }
                }
        );

        return deviceIDInDatabase.get();
    }

    /**
     * Given some input deviceID, returns the User object that is associated with that deviceID
     * @param deviceID The deviceID of the user
     * @return A user object containing the corresponding data from the database
     * @throws IllegalArgumentException If the deviceID does not exist in the database
     * @throws IllegalStateException If the query fails
     */
    public User getUserFromDeviceID(String deviceID) throws IllegalArgumentException, IllegalStateException{
        if (!queryDeviceID(deviceID)){
            throw new IllegalArgumentException("Cannot retrieve DeviceID from the database");
        }

        Query deviceIDQuery = userRef.whereEqualTo("deviceID", deviceID);

        final User[] queriedUser = new User[1];

        deviceIDQuery.get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        queriedUser[0] = querySnapshot.toObjects(User.class).get(0);
                    } else {
                        Log.e("Database","Query failed");
                    }
                }
        );

        return queriedUser[0];

    }

    /**
     * Given some userID, this function returns the corresponding User object
     * @param userID The userID to query against
     * @return A user object corresponding with the userID
     * @throws IllegalStateException If the userID does not exist in the database
     */
    public User getUser(String userID) throws IllegalStateException{
        DocumentReference userDocRef = userRef.document(userID);

        final User[] user = new User[1];

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                user[0] = documentSnapshot.toObject(User.class);
                if (user[0] != null) {
                    user[0].setUserID(userID);
                }
            } else {
                Log.e("Database","No user exists with that userID");
            }
        });

        return user[0];

    }

    /**
     * Given a user, add it to the database
     * @param user The user profile
     * Logs an error if the database cannot add the user
     */
    public void addUser(User user){

        // Sign user in anonymously so that Firestore security rules can be applied
        firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser authUser = firebaseAuth.getCurrentUser();
                assert authUser != null;
                DocumentReference userDoc = userRef.document(authUser.getUid());
                userDoc.set(user);

                user.setUserID(userDoc.getId());

            } else {
                Log.e("Database", "Firebase signing in user failed");
            }
        });
    }

    /**
     * Given a user, update or create their record in the database
     * @param user The user profile
     */
    public void modifyUser(User user){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser authUser = auth.getCurrentUser();
        // Add user if the user does not exist
        if (authUser == null) {
            addUser(user);
            return;
        }
        DocumentReference userDoc = userRef.document(authUser.getUid());
        userDoc.set(user);
    }

    /**
     * Given a user, delete their record from the database
     * @param user The user profile
     */
    public void deleteUser(User user) {
        FirebaseUser authUser = firebaseAuth.getCurrentUser();

        if (authUser != null) {
            String userId = authUser.getUid();

            // deletes from the user collection
            userRef.document(userId).delete().addOnSuccessListener(task -> {
                authUser.delete()
                        .addOnSuccessListener(aVoid -> {})
                        .addOnFailureListener(e -> Log.e("Database", "Firebase deleting auth failed"));
            }).addOnFailureListener(e -> Log.e("Database", "Firebase deleting user document failed"));

            // deletes from the event collection
            eventRef.get().addOnSuccessListener(querySnapshot -> {
                for (DocumentSnapshot eventDoc : querySnapshot.getDocuments()) {
                    DocumentReference regDocRef = eventDoc.getReference()
                            .collection("Registration")
                            .document(userId);
                    regDocRef.get().addOnSuccessListener(regDoc -> {
                        if (regDoc.exists()) {
                            regDocRef.delete()
                                    .addOnSuccessListener(aVoid -> {})
                                    .addOnFailureListener(e ->
                                            Log.e("Database", "Error deleting registration"));
                        }
                    }).addOnFailureListener(e ->
                            Log.e("Database", "Error checking registration"));
                }
            });
        } else {
            Log.e("Database", "User not found");
        }
    }

    /**
     * Given some eventID, this method finds the event and returns that event object from the
     * database
     * @param eventID The eventID of the event you are trying to retrieve
     * @return An event object containing all of the information about the user
     * @throws IllegalStateException This exception is thrown if no event exists with that eventID,
     * if the registration collection retrieval fails, or if the user status is not properly defined
     */
    public Event getEvent(String eventID) throws IllegalStateException{
        DocumentReference eventDocRef = eventRef.document(eventID);

        final Event[] event = new Event[1];

        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
           if (documentSnapshot.exists()){

               /* this code here takes all data that is not in the registration subcollection and
               assigns it to the parameters in the event class
                */
               event[0] = documentSnapshot.toObject(Event.class);
               if (event[0] != null) {
                   event[0].setEventID(eventID);
               }

               /* takes data from registration subcollection and stores it in the entrant list
               object that is owned by the event class
                */
               CollectionReference registration = eventDocRef.collection("Registration");

               registration.get().addOnCompleteListener(task -> {
                   if (task.isSuccessful()){
                       for (DocumentSnapshot entrantDocument : task.getResult()){
                            String status = entrantDocument.get("Status").toString();
                            switch (status){
                                case "waiting":
                                   event[0].getEntrantList().addToWaiting(getUser(entrantDocument.getId()));
                                   break;
                                case "chosen":
                                    event[0].getEntrantList().addToChosen(getUser(entrantDocument.getId()));
                                    break;
                                case "cancelled":
                                    event[0].getEntrantList().addToCancelled(getUser(entrantDocument.getId()));
                                    break;
                                case "finalized":
                                    event[0].getEntrantList().addToFinalized(getUser(entrantDocument.getId()));
                                    break;
                                default:
                                    throw new IllegalStateException("User status is not properly defined");
                            }
                       }
                   } else {
                       throw new IllegalStateException("Registration collection retrieval failed");
                   }
               });


           } else {
               throw new IllegalStateException("No event exists with that eventID");
           }
        });

        return event[0];

    }

    /**
     * Given some event object, we add its data to the database
     * @param event The event that we want to add to the database
     */
    public void addEvent(Event event){
        DocumentReference eventDocRef = eventRef.document();
        eventDocRef.set(event);

        event.setEventID(eventDocRef.getId());
        
        CollectionReference registration = eventDocRef.collection("Registration");

        for (User user : event.getEntrantList().getWaiting()){
            DocumentReference registrationDocRef = registration.document(user.getUserID());
            registrationDocRef.set(new HashMap<String, String>().put("status", "waiting"));
            registrationDocRef.set(new HashMap<String, String>().put("organizerID", event.getOrganizerID()));
        }

        for (User user : event.getEntrantList().getChosen()){
            DocumentReference registrationDocRef = registration.document(user.getUserID());
            registrationDocRef.set(new HashMap<String, String>().put("status", "chosen"));
            registrationDocRef.set(new HashMap<String, String>().put("organizerID", event.getOrganizerID()));
        }

        for (User user : event.getEntrantList().getCancelled()){
            DocumentReference registrationDocRef = registration.document(user.getUserID());
            registrationDocRef.set(new HashMap<String, String>().put("status", "cancelled"));
            registrationDocRef.set(new HashMap<String, String>().put("organizerID", event.getOrganizerID()));
        }

        for (User user : event.getEntrantList().getFinalized()){
            DocumentReference registrationDocRef = registration.document(user.getUserID());
            registrationDocRef.set(new HashMap<String, String>().put("status", "finalized"));
            registrationDocRef.set(new HashMap<String, String>().put("organizerID", event.getOrganizerID()));
        }

        event.setEventID(eventDocRef.getId());
    }

    /**
     * Given some event, we update its data in the database
     * @param event The event that we want to update in the database
     */
    public void updateEvent(Event event){
        DocumentReference eventDocRef = eventRef.document(event.getEventID());
        eventDocRef.set(event);

        CollectionReference registration = eventDocRef.collection("Registration");

        for (User user : event.getEntrantList().getWaiting()){
            DocumentReference registrationDocRef = registration.document(user.getUserID());
            registrationDocRef.set(new HashMap<String, String>().put("status", "waiting"));
            registrationDocRef.set(new HashMap<String, String>().put("organizerID", event.getOrganizerID()));
        }

        for (User user : event.getEntrantList().getChosen()){
            DocumentReference registrationDocRef = registration.document(user.getUserID());
            registrationDocRef.set(new HashMap<String, String>().put("status", "chosen"));
            registrationDocRef.set(new HashMap<String, String>().put("organizerID", event.getOrganizerID()));
        }

        for (User user : event.getEntrantList().getCancelled()){
            DocumentReference registrationDocRef = registration.document(user.getUserID());
            registrationDocRef.set(new HashMap<String, String>().put("status", "cancelled"));
            registrationDocRef.set(new HashMap<String, String>().put("organizerID", event.getOrganizerID()));
        }

        for (User user : event.getEntrantList().getFinalized()){
            DocumentReference registrationDocRef = registration.document(user.getUserID());
            registrationDocRef.set(new HashMap<String, String>().put("status", "finalized"));
            registrationDocRef.set(new HashMap<String, String>().put("organizerID", event.getOrganizerID()));
        }

        event.setEventID(eventDocRef.getId());
    }

    public void deleteEvent(Event event){

    }

    public void addNotificationLog(){
        // TODO: implement this method
    }
}
