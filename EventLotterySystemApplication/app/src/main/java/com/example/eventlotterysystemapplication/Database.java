package com.example.eventlotterysystemapplication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.atomic.AtomicBoolean;

public class Database {

    private static Database database = null;

    CollectionReference userRef;
    CollectionReference eventRef;
    CollectionReference notificationRef;

    private Database(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userRef = db.collection("User");
        eventRef = db.collection("Event");
        notificationRef = db.collection("Notification");
    }

    public static Database getDatabase(){
        if (database == null){
            database = new Database();
        }
        return database;
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
                        throw new IllegalStateException("Query failed");
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
     */
    public User getUserFromDeviceID(String deviceID) throws IllegalArgumentException{
        if (!queryDeviceID(deviceID)){
            throw new IllegalArgumentException("Cannot retrieve DeviceID from the database");
        }

        Query deviceIDQuery = userRef.whereEqualTo("deviceID", deviceID);

        final User[] queriedUser = new User[1];

        String email;
        String name;
        String phoneNumber;

        deviceIDQuery.get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        queriedUser[0] = querySnapshot.toObjects(User.class).get(0);
                    } else {
                        throw new IllegalStateException("Query failed");
                    }
                }
        );

        return queriedUser[0];

    }

    public void addUser(User user){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Sign user in anonymously so that Firestore security rules can be applied
        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser authUser = auth.getCurrentUser();
                assert authUser != null;
                DocumentReference userDoc = userRef.document(authUser.getUid());
                userDoc.set(user);
            } else {
                Log.e("Database", "Firebase signing in user failed");
            }
        });
    }

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

//    public Event getEvent(/*some arguments*/){
//        // add in code for getting event from ID here
//    }

    public void addEvent(Event event){
        DocumentReference eventDoc = eventRef.document();
        eventDoc.set(event);
    }

    public void addNotificationLog(/*add in parameters later*/){

    }
}
