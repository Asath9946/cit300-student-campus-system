package model;

/**
 * Represents one university student record.
 *
 * Fields required by the assignment: Student ID, Name, Programme and Marks.
 * The Student ID is the unique key used by the linked list, the AVL tree and
 * the hash table, so it is final and can never be changed after creation.
 *
 * Owner: Base model shared by all members (created by the group leader).
 */
public class Student {

    private final String studentId;
    private String name;
    private String programme;
    private double marks;

    public Student(String studentId, String name, String programme, double marks) {
        this.studentId = studentId;
        this.name = name;
        this.programme = programme;
        this.marks = marks;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    /**
     * Converts marks into a letter grade.
     * A: 75-100, B: 65-74, C: 55-64, S: 40-54, F: below 40
     */
    public String getGrade() {
        if (marks >= 75) {
            return "A";
        } else if (marks >= 65) {
            return "B";
        } else if (marks >= 55) {
            return "C";
        } else if (marks >= 40) {
            return "S";
        }
        return "F";
    }

    /** Creates an independent copy. Used by the undo feature to remember old values. */
    public Student copy() {
        return new Student(studentId, name, programme, marks);
    }

    /** Copies the editable fields (name, programme, marks) from another record. */
    public void restoreFrom(Student snapshot) {
        this.name = snapshot.name;
        this.programme = snapshot.programme;
        this.marks = snapshot.marks;
    }

    /** Header line that matches {@link #toTableRow()}. */
    public static String tableHeader() {
        return String.format("%-12s | %-24s | %-30s | %6s | %-5s",
                "Student ID", "Name", "Programme", "Marks", "Grade");
    }

    public String toTableRow() {
        return String.format("%-12s | %-24s | %-30s | %6.2f | %-5s",
                studentId, shorten(name, 24), shorten(programme, 30), marks, getGrade());
    }

    private static String shorten(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 3) + "...";
    }

    @Override
    public String toString() {
        return studentId + " - " + name + " (" + programme + ", "
                + String.format("%.2f", marks) + ", Grade " + getGrade() + ")";
    }
}
