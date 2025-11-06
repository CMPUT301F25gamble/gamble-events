package com.example.eventlotterysystemapplication;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserUnitTest {
    private User user = new User("John Doe", "johndoe@gmail.com", "00000", "deviceID");

    @Test
    public void testUpdateAllUserInfo() {
        user.updateUserInfo(user, "Midterm Wizard", "wizard@gmail.com", "67-67-67");
        assertEquals("Midterm Wizard", user.getName());
        assertEquals("wizard@gmail.com", user.getEmail());
        assertEquals("67-67-67", user.getPhoneNumber());
        assertEquals("deviceID", user.getDeviceID());
    }

    @Test
    public void testUpdateNameOnly() {
        user.updateUserInfo(user, "Henry Tang", null, "");
        assertEquals("Henry Tang", user.getName());
        assertEquals("johndoe@gmail.com", user.getEmail());
        assertEquals("00000", user.getPhoneNumber());
        assertEquals("deviceID", user.getDeviceID());
    }

    @Test
    public void testUpdatePhoneAndEmail() {
        user.updateUserInfo(user, null, "johndough@gmail.com", "12345");
        assertEquals("John Doe", user.getName());
        assertEquals("johndough@gmail.com", user.getEmail());
        assertEquals("12345", user.getPhoneNumber());
        assertEquals("deviceID", user.getDeviceID());
    }

    @Test
    public void testUpdateNothing() {
        user.updateUserInfo(user, null, null, null);
        assertEquals("John Doe", user.getName());
        assertEquals("johndoe@gmail.com", user.getEmail());
        assertEquals("00000", user.getPhoneNumber());
        assertEquals("deviceID", user.getDeviceID());
    }
}
