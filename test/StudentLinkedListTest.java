import model.Student;
import structures.StudentLinkedList;

/**
 * Tests for the student linked list.
 *
 * Owner: Member 1 - F.M. Asath (23DA2-0743)
 */
public final class StudentLinkedListTest {

    private StudentLinkedListTest() {
    }

    public static void run() {
        TestSupport.section("Member 1 - StudentLinkedList");
        StudentLinkedList list = new StudentLinkedList();
        TestSupport.check(list.isEmpty(), "new list is empty");
        TestSupport.check(list.removeById("X01") == null, "remove from empty list returns null");

        list.addLast(new Student("S001", "Amal", "BAIT", 70));
        list.addLast(new Student("S002", "Bimal", "BAIT", 80));
        list.addLast(new Student("S003", "Chamal", "BAIT", 90));
        TestSupport.checkEquals(3, list.size(), "size is 3 after three addLast calls");
        TestSupport.checkEquals("S002", list.findById("s002").getStudentId(), "findById is case-insensitive");
        TestSupport.check(list.findById("S999") == null, "findById returns null for missing ID");
        TestSupport.checkEquals(80.0, list.averageMarks(), "average marks = 80");
        TestSupport.checkEquals("S003 (90.00)", list.highestScorer(), "highest scorer is S003");

        // delete middle, head, tail
        TestSupport.checkEquals("S002", list.removeById("S002").getStudentId(), "remove middle node");
        TestSupport.checkEquals("head -> [S001] -> [S003] -> null", list.toChainString(), "chain after removing middle");
        list.removeById("S001");
        TestSupport.checkEquals("head -> [S003] -> null", list.toChainString(), "remove head node");
        list.removeById("S003");
        TestSupport.check(list.isEmpty(), "remove last (tail) node leaves an empty list");
        list.addLast(new Student("S004", "Dinal", "BAIT", 60));
        TestSupport.checkEquals(1, list.size(), "tail reset correctly - addLast works after emptying");

        // insertAt for undo-delete
        list.insertAt(0, new Student("S000", "Zero", "BAIT", 50));
        list.insertAt(1, new Student("S005", "Five", "BAIT", 55));
        list.insertAt(99, new Student("S006", "Six", "BAIT", 65));
        TestSupport.checkEquals("head -> [S000] -> [S005] -> [S004] -> [S006] -> null", list.toChainString(),
                "insertAt head, middle and beyond-end positions");
        TestSupport.checkEquals(2, list.indexOf("S004"), "indexOf returns 0-based position");
        list.addLast(new Student("S007", "Seven", "BAIT", 75));
        TestSupport.checkEquals("S007", list.toArray()[4].getStudentId(), "tail still correct after insertAt");
    }
}
