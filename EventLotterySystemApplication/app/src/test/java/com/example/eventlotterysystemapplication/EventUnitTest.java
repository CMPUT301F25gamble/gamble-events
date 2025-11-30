package com.example.eventlotterysystemapplication;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.util.Log;

import com.example.eventlotterysystemapplication.Model.Entrant;
import com.example.eventlotterysystemapplication.Model.EntrantList;
import com.example.eventlotterysystemapplication.Model.EntrantLocation;
import com.example.eventlotterysystemapplication.Model.EntrantStatus;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class EventUnitTest {

    /*
    Note: getters and setters will not be tested because they are very simple
     */

    @Mock
    private FirebaseFirestore mockDb;
    @Mock
    private FirebaseAuth mockAuth;
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
    }

    public Event mockEvent1(){
        Event event = new Event(
                "Twice concert watch party",
                "We love Twice",
                "Online",
                new String[]{"Twice", "concert"},
                "12345",
                "2025-11-15T14:00",
                "2025-11-15T16:00",
                "2025-11-01T23:59",
                "2025-11-10T23:59",
                "2025-11-12T23:59",
                50,
                20,
                false
        );
        return event;
    }

    public User mockEntrant1(){
        User entrant = new User(
                "Best Entrant",
                "entrant@entrant.com",
                "123-456-7890",
                "testDeviceIDmockEntrant1",
                "new token"
        );
        entrant.setUserID(entrant.getDeviceID());
        return entrant;
    }

    public User mockEntrant2(){
        User entrant = new User(
                "Best Entrant 2",
                "entrant@entrant.com",
                "123-456-7890",
                "testDeviceIDmockEntrant2",
                "new token"
        );
        entrant.setUserID(entrant.getDeviceID());
        return entrant;
    }

    @Test
    public void testParseTimestamps(){

        Event event = mockEvent1();

        event.parseTimestamps();

        assertNotNull(event.getEventStartTime());
        assertNotNull(event.getEventEndTime());
        assertNotNull(event.getRegistrationStartTime());
        assertNotNull(event.getRegistrationEndTime());
        assertNotNull(event.getInvitationAcceptanceDeadline());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        assertEquals(LocalDateTime.parse("2025-11-15T14:00", formatter), event.getEventStartTime());
        assertEquals(LocalDateTime.parse("2025-11-15T16:00", formatter), event.getEventEndTime());
        assertEquals(LocalDateTime.parse("2025-11-01T23:59", formatter), event.getRegistrationStartTime());
        assertEquals(LocalDateTime.parse("2025-11-10T23:59", formatter), event.getRegistrationEndTime());
        assertEquals(LocalDateTime.parse("2025-11-12T23:59", formatter), event.getInvitationAcceptanceDeadline());
    }

    @Test
    public void testAddEventTag(){
        Event event = mockEvent1();

        event.addEventTag("Euler");

        ArrayList<String> test = new ArrayList<>(Arrays.asList(new String []{"Twice", "concert", "Euler"}));

        assert(test.equals(event.getEventTags()));
    }

    @Test
    public void testDeleteEventTag(){
        Event event = mockEvent1();

        event.deleteEventTag(1);

        assert(event.getEventTags().equals(Arrays.asList(new String[]{"Twice"})));

        event.deleteEventTag("Twice");

        assert(event.getEventTags().equals(Arrays.asList(new String[]{})));

        event = mockEvent1();

        event.deleteEventTag("concert");

        assert(event.getEventTags().equals(Arrays.asList(new String[]{"Twice"})));

    }

    @Test
    public void testAddToEntrantList(){
        Event event = mockEvent1();
        User user = mockEntrant1();
        EntrantLocation location = mock(EntrantLocation.class);
        Entrant entrant1 = new Entrant();
        entrant1.setUser(user);
        entrant1.setLocation(location);
        entrant1.setStatus(EntrantStatus.WAITING);

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        event.addToEntrantList(entrant1);

        assertEquals (1, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());
    }

    @Test
    public void testRemoveFromEntrantList(){
        Event event = mockEvent1();
        User user = mockEntrant1();
        EntrantLocation location = mock(EntrantLocation.class);
        Entrant entrant1 = new Entrant();
        entrant1.setUser(user);
        entrant1.setLocation(location);
        entrant1.setStatus(EntrantStatus.WAITING);

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        event.addToEntrantList(entrant1);

        assertEquals (1, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        assertTrue(event.removeEntrant(entrant1));

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());
    }

    @Test
    public void testJoinChosenList(){
        Event event = mockEvent1();
        EntrantLocation location = mock(EntrantLocation.class);

        User user = mockEntrant1();
        Entrant entrant1 = new Entrant();
        entrant1.setUser(user);
        entrant1.setLocation(location);
        entrant1.setStatus(EntrantStatus.WAITING);

        User user2 = mockEntrant2();
        Entrant entrant2 = new Entrant();
        entrant2.setUser(user2);
        entrant2.setLocation(location);

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        event.addToEntrantList(entrant1);

        assertEquals (1, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        event.addEntrantToChosenList(entrant1);

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (1, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        // Attempts to add a user that is not in the waitlist to chosen list
        assertThrows(Exception.class, () -> event.addEntrantToChosenList(entrant2));

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (1, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());
    }

    @Test
    public void testJoinCancelledList(){
        Event event = mockEvent1();
        EntrantLocation location = mock(EntrantLocation.class);

        User user = mockEntrant1();
        Entrant entrant1 = new Entrant();
        entrant1.setUser(user);
        entrant1.setLocation(location);
        entrant1.setStatus(EntrantStatus.WAITING);

        User user2 = mockEntrant2();
        Entrant entrant2 = new Entrant();
        entrant2.setUser(user2);
        entrant2.setLocation(location);

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        event.addToEntrantList(entrant1);

        assertEquals (1, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        // Attempts to add entrant that is not chosen to cancelled list
        event.addEntrantToCancelledList(entrant1);

        assertEquals (1, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        // Now add entrant to chosen list
        event.addEntrantToChosenList(entrant1);
        event.addEntrantToCancelledList(entrant1);

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (1, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        // Attempts to add a user that is not in the waitlist to cancelled list
        assertThrows(Exception.class, () -> event.addEntrantToCancelledList(entrant2));

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (1, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());
    }

    @Test
    public void testJoinFinalizedList(){
        Event event = mockEvent1();
        EntrantLocation location = mock(EntrantLocation.class);

        User user = mockEntrant1();
        Entrant entrant1 = new Entrant();
        entrant1.setUser(user);
        entrant1.setLocation(location);
        entrant1.setStatus(EntrantStatus.WAITING);

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        event.addToEntrantList(entrant1);

        assertEquals (1, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        // Attempts to add an entrant who is not chosen to the finalized list
        event.addEntrantToFinalizedList(entrant1);

        assertEquals (1, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        // Now add the entrant to chosen list
        event.addEntrantToChosenList(entrant1);

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (1, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (0, event.getEntrantFinalizedList().size());

        event.addEntrantToFinalizedList(entrant1);

        assertEquals (0, event.getEntrantWaitingList().size());
        assertEquals (0, event.getEntrantChosenList().size());
        assertEquals (0, event.getEntrantCancelledList().size());
        assertEquals (1, event.getEntrantFinalizedList().size());
    }

    @After
    public void tearDown() {
        mockAuthStatic.close();
        mockFirestoreStatic.close();
        mockLogStatic.close();
    }

    /*
     since all of the other leave entrant list functions are nearly identical to this one, it
     doesn't make sense to run the same input tests on all of them
     */

    /*
    Will leave the tests for adding/deleting posters later since those are more complex bitmap
    objects that will require more resources to set up and test
     */
}