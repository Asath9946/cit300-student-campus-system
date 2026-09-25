package structures;

import model.Student;
import util.Validator;

/**
 * Singly linked list that stores and manages student records
 * (Assignment requirement 2).
 *
 * Each node holds one Student and a reference to the next node.
 * A tail reference makes adding at the end O(1).
 *
 *   head -> [S1] -> [S2] -> [S3] -> null
 *                            ^ tail
 *
 * Time complexity:
 *   addLast          O(1)
 *   insertAt(index)  O(n)
 *   findById         O(n)
 *   removeById       O(n)
 *   display          O(n)
 *
 * Owner: Member 1 - F.M. Asath (23DA2-0743)
 */
public class StudentLinkedList {

    /** One node of the list. */
    private static class Node {
        private final Student data;
        private Node next;

        Node(Student data) {
            this.data = data;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    /** Adds a student at the end of the list. */
    public void addLast(Student student) {
        Node newNode = new Node(student);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     * Inserts a student at a given position (0 = first).
     * Used by "undo delete" so a restored record goes back to its old position.
     * If the index is larger than the size, the student is added at the end.
     */
    public void insertAt(int index, Student student) {
        if (index <= 0 || head == null) {
            Node newNode = new Node(student);
            newNode.next = head;
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
            size++;
            return;
        }
        if (index >= size) {
            addLast(student);
            return;
        }
        Node previous = head;
        for (int i = 0; i < index - 1; i++) {
            previous = previous.next;
        }
        Node newNode = new Node(student);
        newNode.next = previous.next;
        previous.next = newNode;
        size++;
    }

    /** Linear search from head to tail. Returns null when the ID is not found. */
    public Student findById(String studentId) {
        Node current = head;
        while (current != null) {
            if (current.data.getStudentId().equalsIgnoreCase(studentId)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    public boolean containsId(String studentId) {
        return findById(studentId) != null;
    }

    /** Returns the position of a student (0-based) or -1 if not found. */
    public int indexOf(String studentId) {
        Node current = head;
        int index = 0;
        while (current != null) {
            if (current.data.getStudentId().equalsIgnoreCase(studentId)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    /**
     * Removes a student by ID and returns the removed record (null if not found).
     * Handles three cases: removing the head, a middle node, and the tail.
     */
    public Student removeById(String studentId) {
        if (head == null) {
            return null;
        }
        // Case 1: the node to delete is the head
        if (head.data.getStudentId().equalsIgnoreCase(studentId)) {
            Student removed = head.data;
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return removed;
        }
        // Case 2 and 3: find the node BEFORE the one to delete
        Node previous = head;
        while (previous.next != null
                && !previous.next.data.getStudentId().equalsIgnoreCase(studentId)) {
            previous = previous.next;
        }
        if (previous.next == null) {
            return null; // not found
        }
        Student removed = previous.next.data;
        if (previous.next == tail) {
            tail = previous; // deleting the last node
        }
        previous.next = previous.next.next;
        size--;
        return removed;
    }

    /** Traverses the list from head to tail and prints every record as a table. */
    public void display() {
        if (isEmpty()) {
            System.out.println("No student records available. The linked list is empty.");
            return;
        }
        String line = Validator.repeat('-', 92);
        System.out.println(line);
        System.out.println(String.format("%-4s | ", "No.") + Student.tableHeader());
        System.out.println(line);
        Node current = head;
        int number = 1;
        while (current != null) {
            System.out.println(String.format("%-4d | ", number) + current.data.toTableRow());
            current = current.next;
            number++;
        }
        System.out.println(line);
        System.out.println(String.format("Total records: %d | Average marks: %.2f | Highest: %s",
                size, averageMarks(), highestScorer()));
    }

    /** Shows the node chain visually: head -> ID -> ID -> null */
    public String toChainString() {
        StringBuilder builder = new StringBuilder("head -> ");
        Node current = head;
        while (current != null) {
            builder.append("[").append(current.data.getStudentId()).append("] -> ");
            current = current.next;
        }
        return builder.append("null").toString();
    }

    public double averageMarks() {
        if (isEmpty()) {
            return 0.0;
        }
        double total = 0;
        Node current = head;
        while (current != null) {
            total += current.data.getMarks();
            current = current.next;
        }
        return total / size;
    }

    /** Returns "ID (marks)" of the highest scorer, or "-" if the list is empty. */
    public String highestScorer() {
        if (isEmpty()) {
            return "-";
        }
        Student best = head.data;
        Node current = head.next;
        while (current != null) {
            if (current.data.getMarks() > best.getMarks()) {
                best = current.data;
            }
            current = current.next;
        }
        return best.getStudentId() + " (" + String.format("%.2f", best.getMarks()) + ")";
    }

    /** Copies the list into an array (used by tests). */
    public Student[] toArray() {
        Student[] result = new Student[size];
        Node current = head;
        int i = 0;
        while (current != null) {
            result[i++] = current.data;
            current = current.next;
        }
        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
