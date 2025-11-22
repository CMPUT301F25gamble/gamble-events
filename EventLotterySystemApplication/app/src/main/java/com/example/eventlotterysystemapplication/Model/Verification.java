package com.example.eventlotterysystemapplication.Model;

import java.util.regex.Pattern;

/**
 * This class contains methods for verifying inputs.
 * Code in validEmail() is adopted from: https://www.geeksforgeeks.org/dsa/check-given-string-valid-number-integer-floating-point-java-set-2-regular-expression-approach/
 * Regex for phone number is given by ChatGPT
 */
public class Verification {

    /**
     * Checks if an email address is valid
     * @param email A string for an email
     * @return {@code true} if email is valid, {@code false} otherwise
     */
    public static boolean validEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // Compile the regex
        Pattern p = Pattern.compile(emailRegex);
        return email != null && p.matcher(email).matches();
    }

    /**
     * Checks if a phone number is valid
     * @param phoneNumber A string for a phone number
     * @return {@code true} if email is valid, {@code false} otherwise
     */
    public static boolean validPhoneNumber(String phoneNumber) {
        String phoneRegex = "^(?:\\(\\d{3}\\)|\\d{3})[- ]?\\d{3}[- ]?\\d{4}$";
        // Compile the regex
        Pattern p = Pattern.compile(phoneRegex);
        return phoneNumber != null && p.matcher(phoneNumber).matches();
    }
}
