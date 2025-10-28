package com.example.eventlotterysystemapplication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
