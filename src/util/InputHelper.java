package util;

import java.util.Scanner;

/**
 * Safe console input. Every read method keeps asking until the user types a
 * valid value, so the program never crashes on bad input
 * (e.g. letters where a number is expected).
 *
 * Typing "cancel" at any prompt abandons the current operation and returns to
 * the main menu. If the input stream ends (Ctrl+D / Ctrl+Z) the program exits
 * cleanly instead of throwing an exception.
 *
 * Owner: Base utility shared by all members (created by the group leader).
 */
public class InputHelper {

    public static final String CANCEL_WORD = "cancel";

    /** Thrown when the user types "cancel". The menu catches it and returns. */
    public static class OperationCancelledException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public OperationCancelledException() {
            super("Operation cancelled by user.");
        }
    }

    /** Thrown when there is no more input (end of stream). */
    public static class InputClosedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public InputClosedException() {
            super("Input stream closed.");
        }
    }

    /** Checks one text value. Used with Java 8 lambdas, e.g. Validator::isValidName */
    public interface TextRule {
        boolean isValid(String value);
    }

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Reads one raw line (trimmed). Handles end-of-input and the cancel word. */
    public String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        String line = scanner.nextLine().trim();
        if (line.equalsIgnoreCase(CANCEL_WORD)) {
            throw new OperationCancelledException();
        }
        return line;
    }

    /** Reads a menu choice. Returns -1 if the value is not a whole number. */
    public int readMenuChoice(String prompt) {
        String line = readLine(prompt);
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Keeps asking until a non-empty value that passes the rule is entered. */
    public String readText(String prompt, TextRule rule, String errorMessage) {
        while (true) {
            String value = readLine(prompt);
            if (value.isEmpty()) {
                printError("This field cannot be empty.");
            } else if (!rule.isValid(value)) {
                printError(errorMessage);
            } else {
                return Validator.cleanText(value);
            }
        }
    }

    /**
     * Used by "update": pressing Enter keeps the current value (returns null),
     * otherwise the new value must pass the rule.
     */
    public String readOptionalText(String prompt, TextRule rule, String errorMessage) {
        while (true) {
            String value = readLine(prompt);
            if (value.isEmpty()) {
                return null;
            }
            if (rule.isValid(value)) {
                return Validator.cleanText(value);
            }
            printError(errorMessage);
        }
    }

    /** Keeps asking until a whole number between min and max (inclusive) is entered. */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readLine(prompt);
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                }
                printError("Please enter a whole number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("'" + value + "' is not a valid whole number.");
            }
        }
    }

    /** Reads marks between 0 and 100. */
    public double readMarks(String prompt) {
        while (true) {
            String value = readLine(prompt);
            Double marks = parseMarks(value);
            if (marks != null) {
                return marks;
            }
            printError("Marks must be a number from 0 to 100 with up to 2 decimals (e.g. 78.5).");
        }
    }

    /** Update version of {@link #readMarks}: Enter keeps the current value (returns null). */
    public Double readOptionalMarks(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value.isEmpty()) {
                return null;
            }
            Double marks = parseMarks(value);
            if (marks != null) {
                return marks;
            }
            printError("Marks must be a number from 0 to 100 with up to 2 decimals (e.g. 78.5).");
        }
    }

    private Double parseMarks(String value) {
        // only plain decimal numbers such as 78, 78.5 or 100.00 (rejects "1e2", "NaN", "78d" ...)
        if (!value.matches("\\d{1,3}(\\.\\d{1,2})?")) {
            return null;
        }
        try {
            double marks = Double.parseDouble(value);
            if (Validator.isValidMarks(marks) && !Double.isInfinite(marks)) {
                return marks;
            }
        } catch (NumberFormatException e) {
            // fall through to return null
        }
        return null;
    }

    /** Reads Y/N. */
    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt + " (Y/N): ");
            if (value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")) {
                return true;
            }
            if (value.equalsIgnoreCase("n") || value.equalsIgnoreCase("no")) {
                return false;
            }
            printError("Please type Y or N.");
        }
    }

    public void pause() {
        readLine("\nPress Enter to return to the menu...");
    }

    public static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }
}
