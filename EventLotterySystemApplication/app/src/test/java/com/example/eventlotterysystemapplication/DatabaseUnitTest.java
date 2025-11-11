package com.example.eventlotterysystemapplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.util.Log;

import com.example.eventlotterysystemapplication.Controller.Database;
import com.example.eventlotterysystemapplication.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseUnitTest {
    //Tests are ran using the Mockito library. Mockito creates a mocked version of the database.
    //All objects from Firestore must be mocked using @Mock

    private Database database;

    @Mock
    private CollectionReference userRef;

    @Mock
    private CollectionReference eventRef;

    @Mock
    private CollectionReference notificationRef;

    @Mock
    private DocumentReference docRef;

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockAuthUser;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(mockDb.collection("User")).thenReturn(userRef);
        when(mockDb.collection("Event")).thenReturn(eventRef);
        when(mockDb.collection("Notification")).thenReturn(notificationRef);

        database = new Database(mockDb, mockAuth);
    }


    @Test
    public void testDeleteUser() {
        //User and mock tasks setup
        final Task<Void> mockDocDeleteTask = mock(Task.class);
        final Task<Void> mockAuthDeleteTask = mock(Task.class);
        final Task<QuerySnapshot> mockEventGetTask = mock(Task.class);
        final Task<DocumentSnapshot> mockRegGetTask = mock(Task.class);
        final Task<Void> mockRegDeleteTask = mock(Task.class);
        User user = new User("John Doe", "johndoe@gmail.com", "4036767", "deviceID2");

        //Mocks the current logged-in Firebase user
        when(mockAuth.getCurrentUser()).thenReturn(mockAuthUser);
        when(mockAuthUser.getUid()).thenReturn("testUID");

        //Mocks Firestore document references
        docRef = mock(DocumentReference.class);
        when(userRef.document("testUID")).thenReturn(docRef);

        //Mocks delete task for Firestore
        when(docRef.delete()).thenReturn(mockDocDeleteTask);
        when(mockAuthUser.delete()).thenReturn(mockAuthDeleteTask);

        //Mocks event.get()
        when(eventRef.get()).thenReturn(mockEventGetTask);
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);

        //Adds event document to collection
        DocumentSnapshot mockEventDoc = mock(DocumentSnapshot.class);
        List<DocumentSnapshot> eventDocs = new ArrayList<>();
        eventDocs.add(mockEventDoc);
        when(mockQuerySnapshot.getDocuments()).thenReturn(eventDocs);

        //Mocks registration document
        DocumentReference mockRegDocRef = mock(DocumentReference.class);
        DocumentSnapshot mockRegDoc = mock(DocumentSnapshot.class);
        when(mockEventDoc.getReference()).thenReturn(mock(DocumentReference.class));

        //Creates registration subcollection
        CollectionReference mockRegCollection = mock(CollectionReference.class);
        when(mockEventDoc.getReference().collection("Registration")).thenReturn(mockRegCollection);

        when(mockRegCollection.document("testUID")).thenReturn(mockRegDocRef);
        when(mockRegDocRef.get()).thenReturn(mockRegGetTask);
        when(mockRegDoc.exists()).thenReturn(true);

        //Simulates user document delete success
        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0, OnSuccessListener.class);
            listener.onSuccess(null);
            return mockDocDeleteTask;
        }).when(mockDocDeleteTask).addOnSuccessListener(any(OnSuccessListener.class));

        //Simulates auth delete success
        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0, OnSuccessListener.class);
            listener.onSuccess(null);
            return mockAuthDeleteTask;
        }).when(mockAuthDeleteTask).addOnSuccessListener(any(OnSuccessListener.class));

        //Simulate event documents retrieval success
        doAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockQuerySnapshot);
            return mockEventGetTask;
        }).when(mockEventGetTask).addOnSuccessListener(any(OnSuccessListener.class));

        //Simulates registration documents retrieval success
        doAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockRegDoc);
            return mockRegGetTask;
        }).when(mockRegGetTask).addOnSuccessListener(any(OnSuccessListener.class));

        //Simulates registration document delete success
        when(mockRegDocRef.delete()).thenReturn(mockRegDeleteTask);
        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockRegDeleteTask;
        }).when(mockRegDeleteTask).addOnSuccessListener(any(OnSuccessListener.class));


        database.deleteUser(user, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Deletion failed");
            }
        });

        //Verifies deletion
        verify(docRef, times(1)).delete();
        verify(mockAuthUser, times(1)).delete();
    }

    @Test
    public void testDeleteUserTwoEvents() {
        //User and mock tasks setup
        final Task<Void> mockDocDeleteTask = mock(Task.class);
        final Task<Void> mockAuthDeleteTask = mock(Task.class);
        final Task<QuerySnapshot> mockEventGetTask = mock(Task.class);
        final Task<DocumentSnapshot> mockRegGetTask1 = mock(Task.class);
        final Task<DocumentSnapshot> mockRegGetTask2 = mock(Task.class);
        final Task<Void> mockRegDeleteTask1 = mock(Task.class);
        final Task<Void> mockRegDeleteTask2 = mock(Task.class);
        User user = new User("John Doe", "johndoe@gmail.com", "4036767", "deviceID2");

        //Mocks the current logged-in Firebase user
        when(mockAuth.getCurrentUser()).thenReturn(mockAuthUser);
        when(mockAuthUser.getUid()).thenReturn("testUID");

        //Mocks Firestore document references
        docRef = mock(DocumentReference.class);
        when(userRef.document("testUID")).thenReturn(docRef);

        //Mocks delete task for Firestore
        when(docRef.delete()).thenReturn(mockDocDeleteTask);
        when(mockAuthUser.delete()).thenReturn(mockAuthDeleteTask);

        //Mocks event.get()
        when(eventRef.get()).thenReturn(mockEventGetTask);
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);

        //Adds two event documents to collection
        DocumentSnapshot mockEventDoc1 = mock(DocumentSnapshot.class);
        DocumentSnapshot mockEventDoc2 = mock(DocumentSnapshot.class);
        List<DocumentSnapshot> eventDocs = List.of(mockEventDoc1, mockEventDoc2);
        when(mockQuerySnapshot.getDocuments()).thenReturn(eventDocs);

        //Creates registration subcollection
        DocumentReference mockEventRef1 = mock(DocumentReference.class);
        DocumentReference mockEventRef2 = mock(DocumentReference.class);
        when(mockEventDoc1.getReference()).thenReturn(mockEventRef1);
        when(mockEventDoc2.getReference()).thenReturn(mockEventRef2);

        CollectionReference mockRegCollection1 = mock(CollectionReference.class);
        CollectionReference mockRegCollection2 = mock(CollectionReference.class);
        when(mockEventRef1.collection("Registration")).thenReturn(mockRegCollection1);
        when(mockEventRef2.collection("Registration")).thenReturn(mockRegCollection2);

        DocumentReference mockRegDocRef1 = mock(DocumentReference.class);
        DocumentReference mockRegDocRef2 = mock(DocumentReference.class);
        when(mockRegCollection1.document("testUID")).thenReturn(mockRegDocRef1);
        when(mockRegCollection2.document("testUID")).thenReturn(mockRegDocRef2);

        DocumentSnapshot mockRegDoc1 = mock(DocumentSnapshot.class);
        DocumentSnapshot mockRegDoc2 = mock(DocumentSnapshot.class);
        when(mockRegDoc1.exists()).thenReturn(true);
        when(mockRegDoc2.exists()).thenReturn(true);

        when(mockRegDocRef1.get()).thenReturn(mockRegGetTask1);
        when(mockRegDocRef2.get()).thenReturn(mockRegGetTask2);
        when(mockRegDocRef1.delete()).thenReturn(mockRegDeleteTask1);
        when(mockRegDocRef2.delete()).thenReturn(mockRegDeleteTask2);

        //Simulate task successes similar to testDeleteUser()
        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockDocDeleteTask;
        }).when(mockDocDeleteTask).addOnSuccessListener(any());

        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockAuthDeleteTask;
        }).when(mockAuthDeleteTask).addOnSuccessListener(any());

        doAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockQuerySnapshot);
            return mockEventGetTask;
        }).when(mockEventGetTask).addOnSuccessListener(any());

        doAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockRegDoc1);
            return mockRegGetTask1;
        }).when(mockRegGetTask1).addOnSuccessListener(any());

        doAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockRegDoc2);
            return mockRegGetTask2;
        }).when(mockRegGetTask2).addOnSuccessListener(any());

        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockRegDeleteTask1;
        }).when(mockRegDeleteTask1).addOnSuccessListener(any());

        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockRegDeleteTask2;
        }).when(mockRegDeleteTask2).addOnSuccessListener(any());


        database.deleteUser(user, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Deletion failed");
            }
        });

        //Verifies deletions
        verify(docRef, times(1)).delete();
        verify(mockAuthUser, times(1)).delete();
        verify(mockRegDocRef1, times(1)).delete();
        verify(mockRegDocRef2, times(1)).delete();
    }

}
