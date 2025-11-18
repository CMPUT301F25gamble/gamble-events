package com.example.eventlotterysystemapplication;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.util.Log;

import com.example.eventlotterysystemapplication.Model.Database;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
                true
        );
        return event;
    }

    public User mockOrganizer1(){
        User organizer = new User(
                "Best Organizer",
                "organizer@organizer.com",
                "123-456-7890",
                "fNnBwGwhaYStDGG6S3vs8sB52PU2",
                "new token"
        );
        organizer.setUserID(organizer.getDeviceID());
        return organizer;
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

    public User mockEntrant3(){
        User entrant = new User(
                "Best Entrant 3",
                "entrant@entrant.com",
                "123-456-7890",
                "testDeviceIDmockEntrant3",
                "new token"
        );
        entrant.setUserID(entrant.getDeviceID());
        return entrant;
    }

    public User mockEntrant4(){
        User entrant = new User(
                "Best Entrant 4",
                "entrant@entrant.com",
                "123-456-7890",
                "testDeviceIDmockEntrant4",
                "new token"
        );
        entrant.setUserID(entrant.getDeviceID());
        return entrant;
    }

    public User mockEntrant5(){
        User entrant = new User(
                "Best Entrant 5",
                "entrant@entrant.com",
                "123-456-7890",
                "testDeviceIDmockEntrant5",
                "new token"
        );
        entrant.setUserID(entrant.getDeviceID());
        return entrant;
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testParseTimestamps(){
        final Task<Event> mockCreateEventTask = mock(Task.class);

        Event event = mockEvent1();

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
    public void testSetEntrantListValues(){
        Event event = mockEvent1();

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.setEntrantListValues(new ArrayList<>(Arrays.asList(new User[]{mockEntrant1(), mockEntrant2()})), 0);
        event.setEntrantListValues(new ArrayList<>(Arrays.asList(new User[]{mockEntrant3()})), 1);
        event.setEntrantListValues(new ArrayList<>(Arrays.asList(new User[]{mockEntrant4()})), 2);
        event.setEntrantListValues(new ArrayList<>(Arrays.asList(new User[]{mockEntrant5()})), 3);

        assertEquals (2, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (1, event.getEntrantList().getFinalized().size());

        assertThrows(IllegalArgumentException.class, () -> event.setEntrantListValues(new ArrayList<>(Arrays.asList(new User[]{mockEntrant3()})), 4));

    }

    @Test
    public void testAddToEntrantList(){
        Event event = mockEvent1();

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        User entrant1 = mockEntrant1();

        event.addToEntrantList(entrant1, 0);
        event.addToEntrantList(mockEntrant2(), 0);
        event.addToEntrantList(mockEntrant3(), 1);
        event.addToEntrantList(mockEntrant4(), 2);
        event.addToEntrantList(mockEntrant5(), 3);

        assertEquals (2, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (1, event.getEntrantList().getFinalized().size());


        assertThrows(IllegalArgumentException.class, () -> event.addToEntrantList(mockEntrant1(), 4));
    }

    @Test
    public void testRemoveFromEntrantList(){
        Event event = mockEvent1();

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        User entrant1 = mockEntrant1();

        event.addToEntrantList(entrant1, 0);
        event.addToEntrantList(mockEntrant2(), 0);
        event.addToEntrantList(mockEntrant3(), 1);
        event.addToEntrantList(mockEntrant4(), 2);
        event.addToEntrantList(mockEntrant5(), 3);

        assertEquals (2, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (1, event.getEntrantList().getFinalized().size());


        assertThrows(IllegalArgumentException.class, () -> event.addToEntrantList(mockEntrant1(), 4));

        assert(!mockEntrant1().equals(mockEntrant2()));

        event.removeFromEntrantList(mockEntrant1(), 0);
        event.removeFromEntrantList(mockEntrant3(), 1);
        event.removeFromEntrantList(mockEntrant4(), 2);
        event.removeFromEntrantList(mockEntrant5(), 3);
        event.removeFromEntrantList(mockEntrant1(), 0);
        event.removeFromEntrantList(mockEntrant1(), 3);

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());
    }

    @Test
    public void testJoinWaitingList(){
        Event event = mockEvent1();

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant1());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant1());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant2());

        assertEquals (2, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.addToEntrantList(mockEntrant3(), 1);

        assertEquals (2, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant3());

        assertEquals (2, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

    }

    @Test
    public void testJoinChosenList(){
        Event event = mockEvent1();

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant1());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinChosenList(mockEntrant1());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant2());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinChosenList(mockEntrant3());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.addToEntrantList(mockEntrant4(), 1);

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (2, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinChosenList(mockEntrant4());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (2, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.addToEntrantList(mockEntrant5(), 2);

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (2, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinChosenList(mockEntrant5());
    }

    @Test
    public void testJoinCancelledList(){
        Event event = mockEvent1();

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant1());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinCancelledList(mockEntrant1());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant2());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinCancelledList(mockEntrant3());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (2, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());
    }

    @Test
    public void testJoinFinalizedList(){
        Event event = mockEvent1();

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant1());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinChosenList(mockEntrant1());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinFinalizedList(mockEntrant1());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (1, event.getEntrantList().getFinalized().size());

        event.joinCancelledList(mockEntrant3());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (1, event.getEntrantList().getFinalized().size());

        event.joinFinalizedList(mockEntrant3());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (1, event.getEntrantList().getFinalized().size());

        event.joinFinalizedList(mockEntrant4());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (1, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant5());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (1, event.getEntrantList().getFinalized().size());

        event.joinFinalizedList(mockEntrant5());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (1, event.getEntrantList().getChosen().size());
        assertEquals (1, event.getEntrantList().getCancelled().size());
        assertEquals (1, event.getEntrantList().getFinalized().size());
    }

    @Test
    public void testLeaveWaitingList(){
        Event event = mockEvent1();

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.joinWaitingList(mockEntrant1());

        assertEquals (1, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.leaveWaitingList(mockEntrant1());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.leaveWaitingList(mockEntrant1());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());

        event.leaveWaitingList(mockEntrant2());

        assertEquals (0, event.getEntrantList().getWaiting().size());
        assertEquals (0, event.getEntrantList().getChosen().size());
        assertEquals (0, event.getEntrantList().getCancelled().size());
        assertEquals (0, event.getEntrantList().getFinalized().size());
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
