package com.example.eventlotterysystemapplication;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import org.mockito.MockitoAnnotations;
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
    DocumentReference docRef;

    @Mock
    FirebaseFirestore mockDb;

    @Mock
    FirebaseAuth mockAuth;
    @Mock
    FirebaseUser mockAuthUser;


    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(mockDb.collection("User")).thenReturn(userRef);
        when(mockDb.collection("Event")).thenReturn(eventRef);
        when(mockDb.collection("Notification")).thenReturn(notificationRef);

        database = Database.getMockDatabase(mockDb, mockAuth);
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
        //Tests whether or not deleteUser() deletes both the Firestore user document and the Firebase Auth user record
        final Task<Void> mockDocDeleteTask = mock(Task.class);
        final Task<Void> mockAuthDeleteTask = mock(Task.class);
        String deviceID = "deviceID2";
        User user = new User("johndoe@gmail.com", "4036767", "John Doe", deviceID);

        //Mocks the current logged-in Firebase user
        when(mockAuth.getCurrentUser()).thenReturn(mockAuthUser);
        when(mockAuthUser.getUid()).thenReturn("testUID");

        //Mocks Firestore document references
        docRef = mock(DocumentReference.class);
        when(userRef.document("testUID")).thenReturn(docRef);

        //Mocks delete task for Firestore
        when(docRef.delete()).thenReturn(mockDocDeleteTask);
        when(mockAuthUser.delete()).thenReturn(mockAuthDeleteTask);

        //Simulates Firestore document delete success
        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0, OnSuccessListener.class);
            listener.onSuccess(null);
            return mockDocDeleteTask;
        }).when(mockDocDeleteTask).addOnSuccessListener(any(OnSuccessListener.class));

        //Simulates Auth delete success
        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0, OnSuccessListener.class);
            listener.onSuccess(null);
            return mockAuthDeleteTask;
        }).when(mockAuthDeleteTask).addOnSuccessListener(any(OnSuccessListener.class));

        database.deleteUser(user);

        //Verifies delete() is called
        verify(docRef, times(1)).delete();
        verify(mockAuthUser, times(1)).delete();
    }
}
