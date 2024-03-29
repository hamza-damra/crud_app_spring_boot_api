package com.example.usermanager.helpers;

public class StringHelper {

    // Set Regular Expression Pattern for Email:
    public static boolean regexEmailValidationPattern(String email) {
        // Set Pattern:
        String regex = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})";

        return email.matches(regex);
    }
    // End Of Set Regular Expression Pattern for Email.

    // Method to validate if a string is not empty
    public static boolean validateNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean validateEmail(String email) {
        return regexEmailValidationPattern(email);
    }
}
