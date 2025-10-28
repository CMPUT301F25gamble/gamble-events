package com.example.eventlotterysystemapplication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Database {

    private static Database database;

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
        boolean deviceIDInDatabase;

        Query deviceIDQuery = userRef.whereEqualTo("deviceID", deviceID);

        deviceIDQuery.get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        int numberOfDeviceID = querySnapshot.size();
                        if (numberOfDeviceID == 0){
                            deviceIDInDatabase = false;
                        } else if (numberOfDeviceID == 1){
                            deviceIDInDatabase = true;
                        } else {
                            throw new IllegalStateException("Duplicate DeviceID found in database");
                        }
                    } else {
                        throw new IllegalStateException("Query failed");
                    }
                }
        );

        return deviceIDInDatabase;
    }

    public void addUser(User user){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser authUser = auth.getCurrentUser();
                DocumentReference userDoc = userRef.document(authUser.getUid());
                userDoc.set(user);
            } else {
                Log.e("Database", "Signing in user failed");
            }
        });
    }

    public void authenticateUser(User user){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Sign user in anonymously so that Firestore security rules can be applied
        FirebaseUser authUser = auth.getCurrentUser();
        assert authUser != null;
        DocumentReference userDoc = userRef.document(authUser.getUid());
        userDoc.set(user);
    }

    public void addEvent(Event event){
        DocumentReference eventDoc = eventRef.document();
        eventDoc.set(event);
    }

    public void addNotificationLog(/*add in parameters later*/){

    }
}
