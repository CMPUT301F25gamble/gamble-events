package com.example.eventlotterysystemapplication;

import org.junit.Test;

public class EventUnitTest {
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
    public void testParseTimestamps(){

    }

    @Test
    public void testGetEventID(){

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
    public void
}
