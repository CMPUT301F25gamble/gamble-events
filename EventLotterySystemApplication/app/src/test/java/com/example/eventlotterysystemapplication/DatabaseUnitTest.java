package com.example.eventlotterysystemapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.junit.Test;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseUnitTest {
    //Tests are ran using the Mockito library. Mockito creates a mocked version of the database.
    //All objects from Firestore must be mocked using @Mock
    private Database database;

    @Mock
    CollectionReference userRef;

    @Mock
    CollectionReference eventRef;

    @Mock
    CollectionReference notificationRef;

    @Mock
    FirebaseFirestore db;

    @Before
    public void setup() {
        database = Database.getDatabase();

        when(db.collection("User")).thenReturn(userRef);
        when(db.collection("Event")).thenReturn(eventRef);
        when(db.collection("Notification")).thenReturn(notificationRef);
    }

    @Test
    public void testAddUser1(){
        setup();

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
                    }
                }
        );
    }

    @Test
    public void testDeleteUser() {
        String deviceID = "deviceID2";
        User user = new User("johndoe@gmail.com", "4036767", "John Doe", deviceID);
        database.addUser(user);
        database.deleteUser(user);
        assertThrows(IllegalStateException.class, () -> database.getUserFromDeviceID(deviceID));
    }
}
