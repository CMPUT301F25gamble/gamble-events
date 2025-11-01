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

import java.util.concurrent.atomic.AtomicBoolean;

public class Database {

    private static Database database = null;

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

    public Event getEvent(Event event){
        // TODO: add in code for getting event from ID
        return event;
    }

    public void addEvent(Event event){
        DocumentReference eventDoc = eventRef.document();
        eventDoc.set(event);
    }

    public void addNotificationLog(){
        // TODO: implement this method
    }
}
