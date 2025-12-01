package com.example.eventlotterysystemapplication;

/**
 * This class represents the data that is associated with an admin, such as whether or not they are
 * in admin mode or not, or their userID
 */
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
