/**
 * Tiny assertion helper so the project can be tested without JUnit or any
 * external library. Each check prints PASS or FAIL.
 *
 * Owner: Member 1 - F.M. Asath (23DA2-0743) - test integration
 */
public final class TestSupport {

    private static int passed;
    private static int failed;

    private TestSupport() {
    }

    public static void section(String title) {
        System.out.println();
        System.out.println("== " + title + " ==");
    }

    public static void check(boolean condition, String description) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + description);
        } else {
            failed++;
            System.out.println("  FAIL  " + description);
        }
    }

    public static void checkEquals(Object expected, Object actual, String description) {
        boolean same = expected == null ? actual == null : expected.equals(actual);
        check(same, description + (same ? "" : "  (expected: " + expected + ", actual: " + actual + ")"));
    }

    /** Passes if the code throws the expected exception type. */
    public static void checkThrows(Class<? extends Throwable> type, Runnable code, String description) {
        try {
            code.run();
            check(false, description + "  (no exception thrown)");
        } catch (Throwable t) {
            check(type.isInstance(t), description + (type.isInstance(t) ? "" : "  (threw " + t + ")"));
        }
    }

    public static int getPassed() {
        return passed;
    }

    public static int getFailed() {
        return failed;
    }
}
