package com.example.eventlotterysystemapplication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.util.Log;

import com.example.eventlotterysystemapplication.Model.Entrant;
import com.example.eventlotterysystemapplication.Model.EntrantList;
import com.example.eventlotterysystemapplication.Model.EntrantLocation;
import com.example.eventlotterysystemapplication.Model.EntrantStatus;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.Model.EventNotificationManager;
import com.example.eventlotterysystemapplication.Model.LotterySelector;
import com.example.eventlotterysystemapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LotterySelectorUnitTest {

    @Mock
    private FirebaseFirestore mockDb;
    @Mock
    private FirebaseAuth mockAuth;
    private MockedStatic<FirebaseAuth> mockAuthStatic;
    private MockedStatic<FirebaseFirestore> mockFirestoreStatic;
    private MockedStatic<Log> mockLogStatic;
    private final String userID = "mwahahahahah";
    private Entrant billyBob;
    private Entrant alice;
    private Entrant santa;
    private Entrant wizard;
    private Entrant elf67;
    private Event event = new Event(
            "Casino Paradise Gambling 18+",
            "Have lots of fun throwing your life savings away",
            "1234 Gambler Debt Road",
            new String[]{"gambling", "alcohol"},
            userID,
            "2025-11-15T14:00",
            "2025-11-25T16:00",
            "2025-11-01T23:59",
            "2025-11-10T23:59",
            "2025-11-12T23:59",
            10,
            5,
            false
    );

    private Entrant mockEntrant(String userID) {
        User user = mock(User.class);
        when(user.getUserID()).thenReturn(userID);
        EntrantLocation location = mock(EntrantLocation.class);
        return new Entrant(user, location, EntrantStatus.WAITING);
    }


    // Note that I am not adding user ids to each user cuz i am lazy

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockAuthStatic = mockStatic(FirebaseAuth.class); // Mocks FirebaseFirestore
        mockFirestoreStatic = mockStatic(FirebaseFirestore.class); // Mocks FirebaseAuth
        mockLogStatic = mockStatic(Log.class); // Mocks the logs
        mockAuthStatic.when(FirebaseAuth::getInstance).thenReturn(mockAuth);
        mockFirestoreStatic.when(FirebaseFirestore::getInstance).thenReturn(mockDb);
    }

    @Test
    public void testDrawUsersOverCapacity() {
        billyBob = mockEntrant("billyBob");
        alice = mockEntrant("alice");
        santa = mockEntrant("santa");
        wizard = mockEntrant("wizard");
        elf67 = mockEntrant("elf67");

        event.addToEntrantList(billyBob);
        event.addToEntrantList(alice);
        event.addToEntrantList(santa);
        event.addToEntrantList(wizard);
        event.addToEntrantList(elf67);

        List<Entrant> waitList = event.getEntrantWaitingList();
        final int maxFinalListCapacity = 3;

        // There are more people on waiting list than the final list capacity
        event.setMaxFinalListCapacity(maxFinalListCapacity);

        try (MockedStatic<EventNotificationManager> mock = mockStatic(EventNotificationManager.class)) {
            mock.when(() -> EventNotificationManager.notifyInitialLotterySelection(any()))
                    .thenAnswer(invocation -> null);
            LotterySelector ls = new LotterySelector();
            List<Entrant> acceptedList;
            ls.drawAcceptedUsers(event);
            acceptedList = event.getEntrantChosenList();

            // Check that length of acceptedList is equal to maxFinalListCapacity
            assertEquals(maxFinalListCapacity, acceptedList.size());
            // Check each user in accepted list was on waiting list before
            for (Entrant entrant : acceptedList) {
                assertTrue(waitList.contains(entrant));
            }
        }
    }

    @Test
    public void testDrawUsersUnderCapacity() {
        billyBob = mockEntrant("billyBob");
        alice = mockEntrant("alice");
        santa = mockEntrant("santa");
        wizard = mockEntrant("wizard");
        elf67 = mockEntrant("elf67");

        event.addToEntrantList(billyBob);
        event.addToEntrantList(alice);
        event.addToEntrantList(santa);
        event.addToEntrantList(wizard);
        event.addToEntrantList(elf67);

        List<Entrant> waitList = event.getEntrantWaitingList();
        final int maxFinalListCapacity = 100;

        // There are less people on waiting list than the final list capacity
        event.setMaxFinalListCapacity(maxFinalListCapacity);

        try (MockedStatic<EventNotificationManager> mock = mockStatic(EventNotificationManager.class)) {
            mock.when(() -> EventNotificationManager.notifyInitialLotterySelection(any()))
                    .thenAnswer(invocation -> null);

            LotterySelector ls = new LotterySelector();
            List<Entrant> acceptedList;
            ls.drawAcceptedUsers(event);
            acceptedList = event.getEntrantChosenList();

            // Check that length of acceptedList is equal to waitingList (everyone got accepted)
            assertEquals(waitList.size(), acceptedList.size());
            // Check each user in accepted list was on waiting list before
            for (Entrant entrant : acceptedList) {
                assertTrue(waitList.contains(entrant));
            }
        }
    }

    @Test
    public void testDrawReplacementUser() {
        billyBob = mockEntrant("billyBob");
        alice = mockEntrant("alice");
        santa = mockEntrant("santa");
        wizard = mockEntrant("wizard");
        elf67 = mockEntrant("elf67");
        event.addToEntrantList(billyBob);
        event.addToEntrantList(alice);
        event.addToEntrantList(santa);
        event.addToEntrantList(wizard);
        event.addToEntrantList(elf67);

        event.addEntrantToChosenList(wizard);
        event.addEntrantToChosenList(billyBob);
        event.addEntrantToChosenList(elf67);

        event.addEntrantToCancelledList(elf67);

        List<Entrant> waitList = event.getEntrantWaitingList();
        List<Entrant> chosenList = event.getEntrantChosenList();
        final int maxFinalListCapacity = 3;
        event.setMaxFinalListCapacity(maxFinalListCapacity);

        try (MockedStatic<EventNotificationManager> mock = mockStatic(EventNotificationManager.class)) {
            mock.when(() -> EventNotificationManager.notifyLotteryReselection(any(), any()))
                    .thenAnswer(invocation -> null);

            LotterySelector ls = new LotterySelector();
            Entrant replacementUser = ls.drawReplacementUser(event, false);

            // Check that the replacement user was not in accepted list before
            assertFalse(chosenList.contains(replacementUser));
            // Check that the replacement user was in waiting list before
            assertTrue(waitList.contains(replacementUser));
        }
    }

    @Test
    public void testDrawReplacementUserImpossible() {
        // Tests that an exception will be thrown if accepted list is identical to waiting list
        // basically it's impossible to draw a unique user from waiting list that is not already on accepted list
        billyBob = mockEntrant("billyBob");
        alice = mockEntrant("alice");
        santa = mockEntrant("santa");
        event.addToEntrantList(billyBob);
        event.addToEntrantList(alice);
        event.addToEntrantList(santa);

        event.addEntrantToChosenList(billyBob);
        event.addEntrantToChosenList(alice);
        event.addEntrantToChosenList(santa);

        final int maxFinalListCapacity = 3;
        event.setMaxFinalListCapacity(maxFinalListCapacity);

        try (MockedStatic<EventNotificationManager> mock = mockStatic(EventNotificationManager.class)) {
            mock.when(() -> EventNotificationManager.notifyLotteryReselection(any(), any()))
                    .thenAnswer(invocation -> null);
            LotterySelector ls = new LotterySelector();
            assertThrows(IllegalStateException.class, () -> ls.drawReplacementUser(event, false));
        }
    }

    @After
    public void tearDown() {
        mockAuthStatic.close();
        mockFirestoreStatic.close();
        mockLogStatic.close();
    }
}