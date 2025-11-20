package com.example.eventlotterysystemapplication;

public class AdminSession {
    private static boolean isAdminMode = false;
    private static String selectedUserId = null;

    public static boolean getAdminMode() {
        return isAdminMode;
    }

    public static void setAdminMode(boolean mode) {
        isAdminMode = mode;
    }

    public static String getSelectedUserId() {
        return selectedUserId;
    }

    public static void setSelectedUserId(String id) {
        selectedUserId = id;
    }
}
