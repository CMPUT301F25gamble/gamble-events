package com.example.eventlotterysystemapplication;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.google.firebase.auth.FirebaseAuth;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DatabaseIntegrationTests {

    private Database database;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CollectionReference userRef;
    private CollectionReference eventRef;

    private final List<String> createdUserIds = new ArrayList<>();
    private final List<String> createdEventIds = new ArrayList<>();

    @Before
    public void setup() {
        database = new Database();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userRef = db.collection("User");
        eventRef = db.collection("Event");
    }

    @Test
    public void testAddUser() throws InterruptedException {
        User user = new User("john@john.com", "19034623", "John", "deviceID1");

        // Adds user
        database.addUser(user, task -> {
            // Verifies user added
            final List<DocumentSnapshot> results = new ArrayList<>();

            userRef.whereEqualTo("deviceID", user.getDeviceID())
                    .get()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            results.addAll(task1.getResult().getDocuments());
                        }
                    });

            assertEquals(1, results.size());

            // Tracks for cleanup
            createdUserIds.add(results.get(0).getId());
        });
    }

    @Test
    public void testDeleteUserStartingPoint() throws Exception {
        User user = new User("wizard@wizard.com", "676767", "Wizard", "deviceID2");

        // Adds user
        database.addUser(user, task -> {
            if (task.isSuccessful()) {
                try {
                    testDeleteUserStep2(user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.e("Database", "Adding user failed");
            }
        });
    }

    public void testDeleteUserStep2(User user) throws Exception {
        // Confirms added user exists
        final List<DocumentSnapshot> beforeDelete = new ArrayList<>();
        userRef.whereEqualTo("deviceID", user.getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                beforeDelete.addAll(task.getResult().getDocuments());

                assertEquals(1, beforeDelete.size());

                try {
                    testDeleteUserStep3(user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } else {
                Log.e("Database", "Verification of User being added to database failed");
            }
        });
    }

    public void testDeleteUserStep3(User user) throws Exception{


        // Deletes user
        database.deleteUser(user, task -> {
            if (task.isSuccessful()) {
                // Verifies deletion
                try {
                    testDeleteUserStep3(user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.e("Database", "Deletion failed");
            }
        });
    }

    public void testDeleteUserStep4(User user) throws Exception{
        final List<DocumentSnapshot> afterDelete = new ArrayList<>();
        userRef.whereEqualTo("deviceID", user.getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                afterDelete.addAll(task.getResult().getDocuments());
                assertEquals(0, afterDelete.size());
            } else {
                Log.e("Database", "Verification of delete failed");
            }
        });
    }

    @Test
    public void testDeleteUserOrganizedEventsStartingPoint() throws Exception {
        User user = new User("wizard@wizard.com", "676767", "Wizard", "deviceID3");

        // Adds user
        database.addUser(user, task -> {
            if (task.isSuccessful()){
                try {
                    testDeleteUserOrganizedEventsStep2(user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    public void testDeleteUserOrganizedEventsStep2(User user) throws Exception{
        // Retrieves user ID from Firestore
        final List<DocumentSnapshot> userDocs = new ArrayList<>();

        userRef.whereEqualTo("deviceID", user.getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userDocs.addAll(task.getResult().getDocuments());

                assertEquals(1, userDocs.size());

                String userID = user.getUserID();
                createdUserIds.add(userID);

                // Creates events for the user
                Event event1 = new Event(
                        "Wizard Training",
                        "Learn how to pass your midterms",
                        "Online",
                        new String[]{"magic", "training"},
                        userID,
                        "2025-11-15T14:00",
                        "2025-11-15T16:00",
                        "2025-11-01T23:59",
                        "2025-11-10T23:59",
                        "2025-11-12T23:59",
                        50,
                        20
                );

                Event event2 = new Event(
                        "Skiing",
                        "Everyone should go skiing at Kicking Horse",
                        "Kicking Horse Resort",
                        new String[]{"ski", "outdoors"},
                        userID,
                        "2025-12-20T09:00",
                        "2025-12-20T12:00",
                        "2025-11-15T23:59",
                        "2025-11-30T23:59",
                        "2025-12-05T23:59",
                        30,
                        10
                );

                database.addEvent(event1, task1 -> {
                    if (task1.isSuccessful()){
                        database.addEvent(event2, task2 -> {
                            try {
                                testDeleteUserOrganizedEventsStep3(user, userID);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                });
            }
        });
    }

    public void testDeleteUserOrganizedEventsStep3(User user, String userID) throws Exception{
        // Confirms events added
        final List<DocumentSnapshot> preDeleteEvents = new ArrayList<>();

        eventRef.whereEqualTo("organizerID", userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                preDeleteEvents.addAll(task.getResult().getDocuments());
                assertTrue(preDeleteEvents.size() == 2);

                for (DocumentSnapshot doc : preDeleteEvents) {
                    createdEventIds.add(doc.getId());
                }

                try {
                    testDeleteUserOrganizedEventsStep4(user, userID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void testDeleteUserOrganizedEventsStep4(User user, String userID) throws Exception{
        // Deletes all events organized by user
        database.deleteOrganizedEvents(user, task -> {
            // Verifies deletion
            CountDownLatch postDeleteLatch = new CountDownLatch(1);
            final List<DocumentSnapshot> postDeleteEvents = new ArrayList<>();

            try {
                testDeleteUserOrganizedEventsStep5(user, userID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void testDeleteUserOrganizedEventsStep5(User user, String userID) throws Exception{
        eventRef.whereEqualTo("organizerID", userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Deletes user last
                CountDownLatch deleteUserLatch = new CountDownLatch(1);
                database.deleteUser(user, task1 -> {

                });
            }
        });
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

//    @Test
//    public void testManualUpdate() throws InterruptedException {
//        // Creates events for the user
//        Event event1 = new Event(
//                "Twice concert watch party",
//                "We love Twice",
//                "Online",
//                new String[]{"Twice", "concert"},
//                "fNnBwGwhaYStDGG6S3vs8sB52PU2",
//                "2025-11-15T14:00",
//                "2025-11-15T16:00",
//                "2025-11-01T23:59",
//                "2025-11-10T23:59",
//                "2025-11-12T23:59",
//                50,
//                20
//        );
//
//        // Adds both events
//        CountDownLatch eventLatch1 = new CountDownLatch(1);
//        database.addEvent(event1, task -> eventLatch1.countDown());
//        eventLatch1.await(20, TimeUnit.SECONDS);
//    }

    @Test
    public void testViewAvailableEvents() throws InterruptedException{
        User user = new User("wizard@wizard.com", "676767", "Wizard", "deviceID5");

        CountDownLatch addUserLatch = new CountDownLatch(1);
        database.addUser(user, task -> addUserLatch.countDown());
        addUserLatch.await(10, TimeUnit.SECONDS);
        createdUserIds.add(user.getUserID());

        CountDownLatch latch = new CountDownLatch(1);
        database.viewAvailableEvents(user, task ->{
            if (task.isSuccessful()) {
                List<Event> events = task.getResult();
                assertEquals(1, events.size()); // watch party only
            }
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
    }

//    @Test
//    public void updateEventEntrant() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//
//        database.getEvent("dmc35tBWVUsLX6foqBDt", task -> {
//            if (task.isSuccessful()) {
//                Event event = task.getResult();
//
//                database.getUser("YVBXN1tq2eZuEVP0K7Lfodv39Mh1", userTask -> {
//                    if (userTask.isSuccessful()) {
//                        User user = userTask.getResult();
//                        user.setUserID("YVBXN1tq2eZuEVP0K7Lfodv39Mh1");
//
//                        event.addToEntrantList(user, 0)
//                                .addOnCompleteListener(task2 -> {
//                                    if (task2.isSuccessful()) {
//                                        Log.d("Test", "User added successfully");
//                                    } else {
//                                        Log.e("Test", "Error adding user", task2.getException());
//                                    }
//                                    latch.countDown();
//                                });
//                    } else {
//                        Log.e("Test", "Failed to get user", userTask.getException());
//                        latch.countDown();
//                    }
//                });
//            } else {
//                Log.e("Test", "Failed to get event", task.getException());
//                latch.countDown();
//            }
//        });
//
//        latch.await(20, TimeUnit.SECONDS);
//    }


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
        auth.signOut();
    }
}

