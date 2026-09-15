package com.neueda.leap.sprint5;

// This is the END STATE of a live TDD session - see demo-guide.md for the
// red-green-refactor cycles that actually built it, one test at a time. Reading
// only this file skips the entire point of the demo.
public class ISBNValidator {

    private static final int ISBN_10_LENGTH = 10;
    private static final int ISBN_13_LENGTH = 13;
    private static final String ISBN_13_PREFIX_978 = "978";
    private static final String ISBN_13_PREFIX_979 = "979";

    public boolean isValid(String isbn) {
        // Check for null or empty
        if (isbn == null || isbn.isEmpty()) {
            return false;
        }

        // Remove hyphens and spaces for processing
        String cleanIsbn = isbn.replaceAll("[-\\s]", "");

        // Must be either 10 or 13 digits (except 10-digit can have X as check digit)
        if (cleanIsbn.length() == ISBN_10_LENGTH) {
            return isValidISBN10(cleanIsbn);
        } else if (cleanIsbn.length() == ISBN_13_LENGTH) {
            return isValidISBN13(cleanIsbn);
        }

        return false;
    }

    private boolean isValidISBN13(String isbn) {
        // All characters must be digits
        if (!isbn.matches("\\d+")) {
            return false;
        }

        // Check valid prefix (978 or 979)
        String prefix = isbn.substring(0, 3);
        if (!prefix.equals(ISBN_13_PREFIX_978) && !prefix.equals(ISBN_13_PREFIX_979)) {
            return false;
        }

        // Validate the five parts and check digit
        // Part 1: prefix (978/979) - already validated
        // Parts 2-4: registration group, registrant, publication (digits)
        // Part 5: check digit (digit)
        
        // Validate check digit using modulo 10 algorithm
        return isValidISBN13CheckDigit(isbn);
    }

    private boolean isValidISBN10(String isbn) {
        // First 9 must be digits, last can be digit or X
        if (!isbn.substring(0, 9).matches("\\d+")) {
            return false;
        }

        char lastChar = isbn.charAt(9);
        if (!Character.isDigit(lastChar) && lastChar != 'X') {
            return false;
        }

        // Validate check digit
        return isValidISBN10CheckDigit(isbn);
    }

    private boolean isValidISBN13CheckDigit(String isbn) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(isbn.charAt(12));
    }

    private boolean isValidISBN10CheckDigit(String isbn) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (i + 1) * Character.getNumericValue(isbn.charAt(i));
        }
        int checkDigit = (11 - (sum % 11)) % 11;
        char lastChar = isbn.charAt(9);
        if (checkDigit == 10) {
            return lastChar == 'X';
        }
        return checkDigit == Character.getNumericValue(lastChar);
    }
}
