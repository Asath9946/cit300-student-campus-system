/**
 * Runs every test class and prints a summary.
 * Exit code 0 = all tests passed, 1 = at least one failure.
 *
 * Owner: Member 1 - F.M. Asath (23DA2-0743) - test integration
 */
public final class TestRunner {

    private TestRunner() {
    }

    public static void main(String[] args) {
        System.out.println("CIT300 Assignment 1 - automated tests");
        StudentLinkedListTest.run();
        StackQueueTest.run();
        AvlHashTest.run();
        CampusGraphTest.run();
        StudentRecordServiceTest.run();

        System.out.println();
        System.out.println("==================================================");
        System.out.println(" RESULT: " + TestSupport.getPassed() + " passed, " + TestSupport.getFailed() + " failed");
        System.out.println("==================================================");
        System.exit(TestSupport.getFailed() == 0 ? 0 : 1);
    }
}
