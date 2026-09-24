package util;

/**
 * Central place for all input validation rules.
 * Every rule returns true/false so that it can be reused by the console
 * menu, the services and the tests.
 *
 * Owner: Base utility shared by all members (created by the group leader).
 */
public final class Validator {

    public static final double MIN_MARKS = 0.0;
    public static final double MAX_MARKS = 100.0;
    public static final int MIN_DISTANCE = 1;
    public static final int MAX_DISTANCE = 100000;

    private Validator() {
        // utility class - no objects needed
    }

    /**
     * Student ID: 3-15 characters, letters/digits, groups may be separated by a
     * single hyphen. Examples: 23DA2-0743, IT2023001, S001
     */
    public static boolean isValidStudentId(String id) {
        if (id == null) {
            return false;
        }
        String value = id.trim();
        return value.length() >= 3 && value.length() <= 15
                && value.matches("[A-Za-z0-9]+(-[A-Za-z0-9]+)*");
    }

    /** Stores all IDs in upper case so "23da2-0743" and "23DA2-0743" are the same key. */
    public static String normalizeStudentId(String id) {
        return id.trim().toUpperCase();
    }

    /** Name: 2-50 characters, must start with a letter, letters/spaces/dots/apostrophes/hyphens only. */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        String value = name.trim();
        return value.length() >= 2 && value.length() <= 50
                && value.matches("[A-Za-z][A-Za-z .'-]*");
    }

    /** Programme: 2-60 characters, letters, digits, spaces and ( ) & . , - / */
    public static boolean isValidProgramme(String programme) {
        if (programme == null) {
            return false;
        }
        String value = programme.trim();
        return value.length() >= 2 && value.length() <= 60
                && value.matches("[A-Za-z][A-Za-z0-9 ()&.,/-]*");
    }

    public static boolean isValidMarks(double marks) {
        return !Double.isNaN(marks) && marks >= MIN_MARKS && marks <= MAX_MARKS;
    }

    /** Location name: 2-40 characters, starts with a letter or digit. */
    public static boolean isValidLocationName(String name) {
        if (name == null) {
            return false;
        }
        String value = name.trim();
        return value.length() >= 2 && value.length() <= 40
                && value.matches("[A-Za-z0-9][A-Za-z0-9 &'().-]*");
    }

    public static boolean isValidDistance(int distance) {
        return distance >= MIN_DISTANCE && distance <= MAX_DISTANCE;
    }

    /** Removes leading/trailing spaces and collapses repeated inner spaces. */
    public static String cleanText(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    /** Java 8 compatible replacement for String.repeat(). */
    public static String repeat(char ch, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(ch);
        }
        return builder.toString();
    }
}
