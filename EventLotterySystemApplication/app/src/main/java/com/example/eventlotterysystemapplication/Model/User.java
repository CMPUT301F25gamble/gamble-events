package com.example.eventlotterysystemapplication.Model;

import java.util.NoSuchElementException;
import java.util.Objects;

import android.util.Log;

/**
 * An instance of this class represents a single user
 */
public class User{
    private String name;
    private String email;
    private String phoneNumber;
    private String deviceID;
    private String deviceToken;
    private String userID;
    private boolean admin;
    private boolean optOutLotteryStatusNotifications;
    private boolean optOutSpecificNotifications;
    private boolean optOutAdminNotifications;

    /**
     * A blank constructor, useful for when we want to create our user object by manually parsing it
     * from Firebase
     */
    public User() {
        // Empty constructor used by Firebase to deserialize documents into User object
    }

    /**
     * A constructor for the User object that contains the attributes that can be set as we create
     * the user object
     * @param name The name of the user
     * @param email The email of the user
     * @param phoneNumber The phone number of the user
     * @param deviceID The deviceID of the user
     * @param deviceToken The Firebase Messaging Service device token used to send notifications
     */
    public User(String name, String email, String phoneNumber , String deviceID, String deviceToken) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceID = deviceID;
        this.deviceToken = deviceToken;
        admin = false;
        this.optOutLotteryStatusNotifications = false;
        this.optOutSpecificNotifications = false;
        this.optOutAdminNotifications = false;
    }
    /**
     * A constructor for the User object that contains the attributes that can be set as we create
     * the user object
     * @param name The name of the user
     * @param email The email of the user
     * @param phoneNumber The phone number of the user
     * @param deviceID The deviceID of the user
     * @param deviceToken The Firebase Messaging Service device token used to send notifications
     * @param optOutLotteryStatusNotifications Whether user would like to receive lottery status notification or not
     * @param optOutSpecificNotifications Whether user would like to receive specific notification or not
     * @param optOutAdminNotifications Whether user would like to receive admin notification or not
     */


    public User(String name, String email, String phoneNumber , String deviceID, String deviceToken,
                boolean optOutLotteryStatusNotifications, boolean optOutSpecificNotifications,
                boolean optOutAdminNotifications) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceID = deviceID;
        this.deviceToken = deviceToken;
        admin = false;
        this.optOutLotteryStatusNotifications = optOutLotteryStatusNotifications;
        this.optOutSpecificNotifications = optOutSpecificNotifications;
        this.optOutAdminNotifications = optOutAdminNotifications;
    }
    /**
     * Gets the userID of the current user object
     * @return The userID string set by the database
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets the userID of the current user object
     * @param userID The userID string set by the database
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Gets the user's name
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name
     * @param name The user's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's email
     * @return The user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email
     * @param email The user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's phone number
     * @return The user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number
     * @param phoneNumber The user's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the deviceID of the user
     * @return A string of the deviceID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Sets the deviceID of the user
     * @param deviceID A string of the deviceID
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * Gets the deviceToken of the user
     * @return A string of the deviceToken
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * Sets the deviceToken of the user
     * @param deviceToken A string of the deviceToken
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**
     * Returns the admin status of the user
     * @return A boolean that represents whether or not the user is an admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Sets the admin status of the user
     * @param admin A boolean that represents whether or not the user is an admin
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Getter for optOutLotteryStatusNotifications
     * @return True if we opt out, false otherwise
     */

    public boolean isOptOutLotteryStatusNotifications() {
        return optOutLotteryStatusNotifications;
    }

    /**
     * Setter for optOutLotteryStatusNotifications
     * @param optOutLotteryStatusNotifications True if we opt out, false otherwise
     */

    public void setOptOutLotteryStatusNotifications(boolean optOutLotteryStatusNotifications) {
        this.optOutLotteryStatusNotifications = optOutLotteryStatusNotifications;
    }

    /**
     * Getter for optOutSpecificNotifications
     * @return True if we opt out, false otherwise
     */

    public boolean isOptOutSpecificNotifications() {
        return optOutSpecificNotifications;
    }

    /**
     * Setter for optOutSpecificNotifications
     * @param optOutSpecificNotifications True if we opt out, false otherwise
     */

    public void setOptOutSpecificNotifications(boolean optOutSpecificNotifications) {
        this.optOutSpecificNotifications = optOutSpecificNotifications;
    }

    /**
     * Check if a user object is equivalent to another user object
     * @param o An object that we are trying to test if this object is equal to
     * @return True if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        } else if (this == o) {
            return true;
        } else {
            User user2 = (User) o;
            return Objects.equals(this.userID, user2.userID);
        }
    }

    /**
     * Modify one or more user profile info
     * @param user The user profile
     * @param name The user name, or {@code null} if it doesn't need to be updated
     * @param email The user email, or {@code null} if it doesn't need to be updated
     * @param phoneNumber The user phone number, or {@code null} if it doesn't need to be updated
     */
    public void updateUserInfo(User user, String name, String email, String phoneNumber) {
        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }
        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            user.setPhoneNumber(phoneNumber);
        }

        // Will need to comment these out when running UserUnitTest
        Database db = Database.getDatabase();
        db.modifyUser(this, task -> {
            if (!task.isSuccessful()) {
                Log.e("Database", "Cannot modify user");
            }
        });
    }

    /**
     * Getter for optOutSpecificNotifications
     * @return True if we opt out, false otherwise
     */

    public boolean isOptOutAdminNotifications() {
        return optOutAdminNotifications;
    }

    /**
     * Setter for optOutLotteryStatusNotifications
     * @param optOutAdminNotifications True if we opt out, false otherwise
     */
    public void setOptOutAdminNotifications(boolean optOutAdminNotifications) {
        this.optOutAdminNotifications = optOutAdminNotifications;
    }
}