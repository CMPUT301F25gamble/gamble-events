package com.example.eventlotterysystemapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DatabaseIntegrationTests {
    private Database database;

    CollectionReference userRef;
    CollectionReference eventRef;
    CollectionReference notificationRef;

    public DatabaseIntegrationTests() {
        database = new Database();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userRef = db.collection("User");
        eventRef = db.collection("Event");
        notificationRef = db.collection("Notification");
    }

    @Test
    public void testAddUser1(){

        User testUser1 = new User("john@john.com", "19034623","John",  "deviceIDJohn1");

        database.addUser(testUser1);

        Query testAddUserQuery1 = userRef.whereEqualTo("deviceID", "deviceIDJohn1");

        testAddUserQuery1.get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        int numberOfDeviceID = querySnapshot.size();
                        assertEquals(1, numberOfDeviceID);
                    } else {
                        throw new IllegalStateException("Query failed");
                    }
                });

        testAddUserQuery1.get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            batch.delete(document.getReference());
                        }
                        batch.commit();
                    } else {
                        throw new IllegalStateException("Deletion failed");
                    }
                }
        );
    }
}
