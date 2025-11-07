package com.example.eventlotterysystemapplication;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventUnitTest {

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
    public Event mockEvent1(){
        Event event = new Event(
                "Twice concert watch party",
                "We love Twice",
                "Online",
                new String[]{"Twice", "concert"},
                "fNnBwGwhaYStDGG6S3vs8sB52PU2",
                "2025-11-15T14:00",
                "2025-11-15T16:00",
                "2025-11-01T23:59",
                "2025-11-10T23:59",
                "2025-11-12T23:59",
                50,
                20
        );
        return event;
    }

    public User mockOrganizer1(){
        User organizer = new User(
                "Best Organizer",
                "organizer@organizer.com",
                "123-456-7890",
                "fNnBwGwhaYStDGG6S3vs8sB52PU2"
        );
        return organizer;
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testParseTimestamps(){
        final Task<Void> mockDocDeleteTask = mock(Task.class);
        final Task<Void> mockAuthDeleteTask = mock(Task.class);
        final Task<QuerySnapshot> mockEventGetTask = mock(Task.class);
        final Task<DocumentSnapshot> mockRegGetTask = mock(Task.class);
        final Task<Void> mockRegDeleteTask = mock(Task.class);

        when(mockEvent1()).thenReturn();

        event.setEventStartTime(null);
        event.setEventEndTime(null);
        event.setRegistrationStartTime(null);
        event.setRegistrationEndTime(null);
        event.setInvitationAcceptanceDeadline(null);

        event.parseTimestamps();

        assertNotNull(event.getEventStartTime());
        assertNotNull(event.getEventEndTime());
        assertNotNull(event.getRegistrationStartTime());
        assertNotNull(event.getRegistrationEndTime());
        assertNotNull(event.getInvitationAcceptanceDeadline());
    }

    @Test
    public void testGetEventID(){
        Event event = mockEvent1();
    }

    @Test
    public void testSetEventID(){

    }

    @Test
    public void testGetEventName(){

    }

    @Test
    public void testSetEventName(){

    }

    @Test
    public void testGetEventDescription(){

    }
    @Test
    public void testSetEventDescription(){

    }

    @Test
    public void testAddEventTag(){

    }

    @Test
    public void testDeleteEventTag(){

    }

    @Test
    public void testGetOrganizer(){

    }

    @Test
    public void testSetOrganizer(){

    }

    @Test
    public void testEventStartTimeTS(){

    }

    @Test
    public void testEventEndTimeTS(){

    }

    @Test
    public void testRegistrationStartTimeTS(){

    }

    @Test
    public void testRegistrationEndTimeTS(){

    }

    @Test
    public void testInvitationAcceptanceDeadlineTS(){

    }

    @Test
    public void testEventStartTime(){

    }

    @Test
    public void testEventEndTime(){

    }

    @Test
    public void testRegistrationStartTime(){

    }

    @Test
    public void testRegistrationEndTime(){

    }

    @Test
    public void testInvitationAcceptanceDeadline(){

    }


}
