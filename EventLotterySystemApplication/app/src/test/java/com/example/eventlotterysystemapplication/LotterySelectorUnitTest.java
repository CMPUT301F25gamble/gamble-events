package com.example.eventlotterysystemapplication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mockStatic;

import android.util.Log;

import com.example.eventlotterysystemapplication.Model.Event;
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
            5
    );

    User billyBob = new User(
            "Billy Bob <3",
            "billybob@mymail.com",
            "123-456-7890",
            "billyBobDeviceID",
            "new token"
    );

    User alice = new User(
            "Alice",
            "alice@mymail.com",
            "888-888-8888",
            "AliceDeviceID",
            "new token"
    );

    User santa = new User(
            "Santa Claus",
            "nick@north-pole.ca",
            "505-050-5067",
            "SAINT_NICK_DEVICE_ID",
            "new token"
    );

    User elf10597120397 = new User(
            "Elf #10597120397 @ North Pole",
            "elf105971203970@north-pole.ca",
            "505-048-3130",
            "10597120397_elf_device_id",
            "new token"
    );

    User elf7683989 = new User(
            "Elf #7683989 @ North Pole",
            "elf7683989@north-pole.ca",
            "Elf #7683989 @ North Pole",
            "7683989_elf_device_id",
            "new token"
    );

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
        ArrayList<User> waitingList = new ArrayList<>(
                Arrays.asList(billyBob, alice, santa, elf7683989, elf10597120397)
        );

        final int maxFinalListCapacity = 3;

        // There are more people on waiting list than the final list capacity
        event.setMaxFinalListCapacity(maxFinalListCapacity);
        event.getEntrantList().setWaiting(waitingList);

        LotterySelector ls = new LotterySelector();
        List<User> acceptedList;
        acceptedList = ls.drawAcceptedUsers(event);

        // Check that length of acceptedList is equal to maxFinalListCapacity
        assertEquals(maxFinalListCapacity, acceptedList.size());
        // Check each user in accepted list was on waiting list before
        for (User user : acceptedList) {
            assertTrue(waitingList.contains(user));
        }
    }

    @Test
    public void testDrawUsersUnderCapacity() {
        ArrayList<User> waitingList = new ArrayList<>(
                Arrays.asList(billyBob, alice, santa, elf7683989, elf10597120397)
        );

        final int maxFinalListCapacity = 100;

        // There are less people on waiting list than the final list capacity
        event.setMaxFinalListCapacity(maxFinalListCapacity);
        event.getEntrantList().setWaiting(waitingList);

        LotterySelector ls = new LotterySelector();
        List<User> acceptedList;
        acceptedList = ls.drawAcceptedUsers(event);

        // Check that length of acceptedList is equal to waitingList (everyone got accepted)
        assertEquals(waitingList.size(), acceptedList.size());
        // Check each user in accepted list was on waiting list before
        for (User user : acceptedList) {
            assertTrue(waitingList.contains(user));
        }
    }

    @Test
    public void testDrawReplacementUser() {
        ArrayList<User> waitingList = new ArrayList<>(
                Arrays.asList(billyBob, alice, santa, elf7683989, elf10597120397)
        );
        ArrayList<User> acceptedList = new ArrayList<>(
                Arrays.asList(billyBob, santa)
        );

        final int maxFinalListCapacity = 3;
        event.setMaxFinalListCapacity(maxFinalListCapacity);

        event.getEntrantList().setWaiting(waitingList);
        event.getEntrantList().setChosen(acceptedList);

        LotterySelector ls = new LotterySelector();
        User replacementUser = ls.drawReplacementUser(event);

        // Check that the replacement user was not in accepted list before
        assertFalse(acceptedList.contains(replacementUser));
        // Check that the replacement user was in waiting list before
        assertTrue(waitingList.contains(replacementUser));
    }

    @Test
    public void testDrawReplacementUserImpossible() {
        // Tests that an exception will be thrown if accepted list is identical to waiting list
        // basically it's impossible to draw a unique user from waiting list that is not already on accepted list
        ArrayList<User> waitingList = new ArrayList<>(
                Arrays.asList(billyBob, alice, santa)
        );
        ArrayList<User> acceptedList = new ArrayList<>(
                Arrays.asList(billyBob, alice, santa)
        );

        final int maxFinalListCapacity = 3;
        event.setMaxFinalListCapacity(maxFinalListCapacity);

        event.getEntrantList().setWaiting(waitingList);
        event.getEntrantList().setChosen(acceptedList);

        LotterySelector ls = new LotterySelector();
        assertThrows(IllegalStateException.class, () -> ls.drawReplacementUser(event));
    }

    @After
    public void tearDown() {
        mockAuthStatic.close();
        mockFirestoreStatic.close();
        mockLogStatic.close();
    }
}