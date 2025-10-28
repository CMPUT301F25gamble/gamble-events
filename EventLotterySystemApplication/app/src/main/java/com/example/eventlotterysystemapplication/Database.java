package com.example.eventlotterysystemapplication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Database {

    CollectionReference userRef;
    CollectionReference eventRef;
    CollectionReference notificationRef;

    private Database(){
        FirebaseFirestore db = getDatabase();
        userRef = db.collection("User");
        eventRef = db.collection("Event");
        notificationRef = db.collection("Notification");
    }

    public static FirebaseFirestore getDatabase(){
        return FirebaseFirestore.getInstance();
    }

    public void addUser(User user){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Sign user in anonymously so that Firestore security rules can be applied
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously().addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   FirebaseUser authUser = auth.getCurrentUser();
                   DocumentReference userDoc = userRef.document(authUser.getUid());
                   userDoc.set(user);
               } else {
                   Log.e("Database", "Signing in user failed");
               }
            });
        } else {
            FirebaseUser authUser = auth.getCurrentUser();
            assert authUser != null;
            DocumentReference userDoc = userRef.document(authUser.getUid());
            userDoc.set(user);
        }
    }

    public void addEvent(Event event){
        DocumentReference eventDoc = eventRef.document();
        eventDoc.set(event);
    }

    public void addNotificationLog(/*add in parameters later*/){

    }
}
