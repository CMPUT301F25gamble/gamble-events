package com.example.eventlotterysystemapplication;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DatabaseIntegrationTests {

    private Database database;
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private CollectionReference eventRef;

    private final List<String> createdUserIds = new ArrayList<>();
    private final List<String> createdEventIds = new ArrayList<>();

    @Before
    public void setup() {
        database = new Database();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("User");
        eventRef = db.collection("Event");
    }

    @Test
    public void testAddUser() throws InterruptedException {
        User user = new User("john@john.com", "19034623", "John", "deviceID1");

        // Adds user
        CountDownLatch addLatch = new CountDownLatch(1);
        database.addUser(user, task -> addLatch.countDown());
        addLatch.await(10, TimeUnit.SECONDS);

        // Verifies user added
        CountDownLatch verifyLatch = new CountDownLatch(1);
        final List<DocumentSnapshot> results = new ArrayList<>();

        userRef.whereEqualTo("deviceID", user.getDeviceID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        results.addAll(task.getResult().getDocuments());
                    }
                    verifyLatch.countDown();
                });

        verifyLatch.await(10, TimeUnit.SECONDS);
        assertEquals(1, results.size());

        // Tracks for cleanup
        createdUserIds.add(results.get(0).getId());
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User("wizard@wizard.com", "676767", "Wizard", "deviceID2");

        // Adds user
        CountDownLatch addLatch = new CountDownLatch(1);
        database.addUser(user, task -> addLatch.countDown());
        addLatch.await(10, TimeUnit.SECONDS);

        // Confirms added user exists
        CountDownLatch confirmLatch = new CountDownLatch(1);
        final List<DocumentSnapshot> beforeDelete = new ArrayList<>();
        userRef.whereEqualTo("deviceID", user.getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                beforeDelete.addAll(task.getResult().getDocuments());
            }
            confirmLatch.countDown();
        });
        confirmLatch.await(10, TimeUnit.SECONDS);
        assertEquals(1, beforeDelete.size());

        // Deletes user
        CountDownLatch deleteLatch = new CountDownLatch(1);
        database.deleteUser(user, task -> deleteLatch.countDown());
        deleteLatch.await(10, TimeUnit.SECONDS);

        // Verifies deletion
        CountDownLatch verifyLatch = new CountDownLatch(1);
        final List<DocumentSnapshot> afterDelete = new ArrayList<>();
        userRef.whereEqualTo("deviceID", user.getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                afterDelete.addAll(task.getResult().getDocuments());
            }
            verifyLatch.countDown();
        });
        verifyLatch.await(10, TimeUnit.SECONDS);
        assertEquals(0, afterDelete.size());
    }

    @Test
    public void testDeleteUserOrganizedEvents() throws Exception {
        User user = new User("wizard@wizard.com", "676767", "Wizard", "deviceID3");

        // Adds user
        CountDownLatch addUserLatch = new CountDownLatch(1);
        database.addUser(user, task -> addUserLatch.countDown());
        addUserLatch.await(10, TimeUnit.SECONDS);

        // Retrieves user ID from Firestore
        CountDownLatch getUserLatch = new CountDownLatch(1);
        final List<DocumentSnapshot> userDocs = new ArrayList<>();

        userRef.whereEqualTo("deviceID", user.getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userDocs.addAll(task.getResult().getDocuments());
            }
            getUserLatch.countDown();
        });
        getUserLatch.await(10, TimeUnit.SECONDS);
        assertEquals(1, userDocs.size());

        String userID = user.getUserID();
        createdUserIds.add(userID);

        // Creates events for the user
        Event event1 = new Event(
                "Wizard Training " + System.currentTimeMillis(),
                "Learn how to pass your midterms",
                "2025-11-15T14:00",
                "2025-11-10T23:59",
                "2025-11-12T23:59",
                new String[]{"magic", "training"},
                userID,
                "Online",
                5,
                20
        );

        Event event2 = new Event(
                "Skiing " + System.currentTimeMillis(),
                "Everyone should go skiing at Kicking Horse",
                "2025-12-20T09:00",
                "2025-12-10T23:59",
                "2025-12-15T23:59",
                new String[]{"ski", "outdoors"},
                userID,
                "Kicking Horse Resort",
                10,
                50
        );

        // Adds both events
        CountDownLatch eventLatch1 = new CountDownLatch(1);
        database.addEvent(event1, task -> eventLatch1.countDown());
        eventLatch1.await(10, TimeUnit.SECONDS);

        CountDownLatch eventLatch2 = new CountDownLatch(1);
        database.addEvent(event2, task -> eventLatch2.countDown());
        eventLatch2.await(10, TimeUnit.SECONDS);

        // Confirms events added
        CountDownLatch preDeleteLatch = new CountDownLatch(1);
        final List<DocumentSnapshot> preDeleteEvents = new ArrayList<>();

        eventRef.whereEqualTo("organizerID", userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                preDeleteEvents.addAll(task.getResult().getDocuments());
            }
            preDeleteLatch.countDown();
        });
        preDeleteLatch.await(10, TimeUnit.SECONDS);
        assertTrue(preDeleteEvents.size() >= 2);

        for (DocumentSnapshot doc : preDeleteEvents) {
            createdEventIds.add(doc.getId());
        }

        // Deletes all events organized by user
        CountDownLatch deleteEventsLatch = new CountDownLatch(1);
        database.deleteOrganizedEvents(user, task -> deleteEventsLatch.countDown());
        deleteEventsLatch.await(15, TimeUnit.SECONDS);

        // Verifies deletion
        CountDownLatch postDeleteLatch = new CountDownLatch(1);
        final List<DocumentSnapshot> postDeleteEvents = new ArrayList<>();
        eventRef.whereEqualTo("organizerID", userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                postDeleteEvents.addAll(task.getResult().getDocuments());
            }
            postDeleteLatch.countDown();
        });
        postDeleteLatch.await(10, TimeUnit.SECONDS);
        assertEquals(0, postDeleteEvents.size());

        // Deletes user last
        CountDownLatch deleteUserLatch = new CountDownLatch(1);
        database.deleteUser(user, task -> deleteUserLatch.countDown());
        deleteUserLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testUpdateUser() throws InterruptedException{
        User user = new User("wizard@wizard.com", "676767", "Wizard", "deviceID4");

        CountDownLatch addUserLatch = new CountDownLatch(1);
        database.addUser(user, task -> addUserLatch.countDown());
        addUserLatch.await(10, TimeUnit.SECONDS);

        DocumentReference userDocRef = userRef.document(user.getUserID());
        createdUserIds.add(user.getUserID());

        CountDownLatch updateUserLatch = new CountDownLatch(1);
        user.updateUserInfo(user, "Roberto", null, null);
        updateUserLatch.countDown();
        updateUserLatch.await(10, TimeUnit.SECONDS);

        CountDownLatch latch = new CountDownLatch(1);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Checks values in the Firebase document
                assertEquals("Roberto", documentSnapshot.getString("name"));
                assertEquals("deviceID4", documentSnapshot.getString("deviceID"));
                assertEquals("wizard@wizard.com", documentSnapshot.getString("email"));
                assertEquals("676767", documentSnapshot.getString("phoneNumber"));
                // Checks values in the user object
                assertEquals("Roberto", user.getName());
                assertEquals("deviceID4", user.getDeviceID());
                assertEquals("wizard@wizard.com", user.getEmail());
                assertEquals("676767", user.getPhoneNumber());
            }
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws InterruptedException {
        // Deletes any users and events created during tests
        if (createdUserIds.isEmpty() && createdEventIds.isEmpty()) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        WriteBatch batch = db.batch();

        for (String userId : createdUserIds) {
            batch.delete(userRef.document(userId));
        }
        for (String eventId : createdEventIds) {
            batch.delete(eventRef.document(eventId));
        }

        batch.commit().addOnCompleteListener(task -> latch.countDown());
        latch.await(10, TimeUnit.SECONDS);
    }
}

