package com.example.eventlotterysystemapplication;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.util.Log;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class UserUnitTest {
    private User user;
    private Database database;
    @Mock
    private FirebaseFirestore mockDb;
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private CollectionReference userRef;
    private MockedStatic<FirebaseAuth> mockAuthStatic;
    private MockedStatic<FirebaseFirestore> mockFirestoreStatic;
    private MockedStatic<Log> mockLogStatic;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockAuthStatic = mockStatic(FirebaseAuth.class); // Mocks FirebaseFirestore
        mockFirestoreStatic = mockStatic(FirebaseFirestore.class); // Mocks FirebaseAuth
        mockLogStatic = mockStatic(Log.class); // Mocks the logs
        mockAuthStatic.when(FirebaseAuth::getInstance).thenReturn(mockAuth);
        mockFirestoreStatic.when(FirebaseFirestore::getInstance).thenReturn(mockDb);

        // Mocks FirebaseAuth
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("mockUID");

        // Mocks signInAnonymously()
        Task<AuthResult> mockAuthTask = mock(Task.class);
        when(mockAuth.signInAnonymously()).thenReturn(mockAuthTask);
        when(mockAuthTask.isSuccessful()).thenReturn(true);

        // Mocks addOnCompleteListener for the authTask
        when(mockAuthTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<AuthResult> listener = invocation.getArgument(0);
            listener.onComplete(mockAuthTask);
            return mockAuthTask;
        });

        // Mocks Firestore user collection
        when(mockDb.collection("User")).thenReturn(userRef);

        // Mocks userRef.document()
        DocumentReference mockUserDoc = mock(DocumentReference.class);
        when(userRef.document(anyString())).thenReturn(mockUserDoc);

        // Mocks userDoc.set()
        Task<Void> mockSetTask = mock(Task.class);
        when(mockSetTask.isSuccessful()).thenReturn(true);
        when(mockUserDoc.set(any(), any())).thenReturn(mockSetTask);

        // Mocks Database
        database = mock(Database.class);

        // Mocks modifyUser()
        doAnswer(invocation -> {
            OnCompleteListener<Void> listener = invocation.getArgument(1);
            Task<Void> task = mock(Task.class);
            when(task.isSuccessful()).thenReturn(true);
            listener.onComplete(task);
            return null;
        }).when(database).modifyUser(any(User.class), any(OnCompleteListener.class));

        // Mocks addUser()
        doAnswer(invocation -> {
            OnCompleteListener<Void> listener = invocation.getArgument(1);
            Task<Void> task = mock(Task.class);
            when(task.isSuccessful()).thenReturn(true);
            listener.onComplete(task);
            return null;
        }).when(database).addUser(any(User.class), any(OnCompleteListener.class));
    }

    @Test
    public void testUpdateAllUserInfo() {
        User user = new User("John Doe", "johndoe@gmail.com", "00000", "deviceID", "new token");
        user.updateUserInfo(user, "Midterm Wizard", "wizard@gmail.com", "67-67-67");
        assertEquals("Midterm Wizard", user.getName());
        assertEquals("wizard@gmail.com", user.getEmail());
        assertEquals("67-67-67", user.getPhoneNumber());
        assertEquals("deviceID", user.getDeviceID());
    }

    @Test
    public void testUpdateNameOnly() {
        User user = new User("John Doe", "johndoe@gmail.com", "00000", "deviceID", "new token");
        user.updateUserInfo(user, "Henry", null, "");
        assertEquals("Henry", user.getName());
        assertEquals("johndoe@gmail.com", user.getEmail());
        assertEquals("00000", user.getPhoneNumber());
        assertEquals("deviceID", user.getDeviceID());
    }

    @Test
    public void testUpdatePhoneAndEmail() {
        User user = new User("John Doe", "johndoe@gmail.com", "00000", "deviceID", "new token");
        user.updateUserInfo(user, null, "johndough@gmail.com", "12345");
        assertEquals("John Doe", user.getName());
        assertEquals("johndough@gmail.com", user.getEmail());
        assertEquals("12345", user.getPhoneNumber());
        assertEquals("deviceID", user.getDeviceID());
    }

    @Test
    public void testUpdateNothing() {
        User user = new User("John Doe", "johndoe@gmail.com", "00000", "deviceID", "new token");
        user.updateUserInfo(user, null, null, null);
        assertEquals("John Doe", user.getName());
        assertEquals("johndoe@gmail.com", user.getEmail());
        assertEquals("00000", user.getPhoneNumber());
        assertEquals("deviceID", user.getDeviceID());
    }

    @After
    public void tearDown() {
        mockAuthStatic.close();
        mockFirestoreStatic.close();
        mockLogStatic.close();
    }
}