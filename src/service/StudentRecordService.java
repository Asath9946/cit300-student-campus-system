package service;

import model.Action;
import model.Student;
import structures.LinkedStack;
import structures.StudentAVLTree;
import structures.StudentHashTable;
import structures.StudentLinkedList;
import util.Validator;

/**
 * Integrates all student-record data structures and keeps them in sync.
 *
 *   StudentLinkedList  - main storage, keeps records in order of entry
 *   StudentAVLTree     - organises records by Student ID (sorted display, O(log n) search)
 *   StudentHashTable   - O(1) average lookup by Student ID, duplicate-ID checks
 *   LinkedStack        - undo stack of student record changes
 *   ActionHistory      - stack of every action performed (shared with other services)
 *
 * All three structures store REFERENCES to the same Student objects, so an
 * update to name/programme/marks is automatically visible everywhere. The
 * Student ID is the key in every structure and therefore cannot be changed.
 *
 * Owner: Member 1 - F.M. Asath (23DA2-0743) - record management and integration
 */
public class StudentRecordService {

    private final StudentLinkedList list = new StudentLinkedList();
    private final StudentAVLTree tree = new StudentAVLTree();
    private final StudentHashTable hashTable = new StudentHashTable();
    private final LinkedStack<Action> undoStack = new LinkedStack<Action>();
    private final ActionHistory history;

    public StudentRecordService(ActionHistory history) {
        this.history = history;
    }

    // ------------------------------------------------------------------
    // Add
    // ------------------------------------------------------------------

    /**
     * Validates and adds a new student to the linked list, AVL tree and hash table.
     * @throws IllegalArgumentException with a user-friendly message if anything is invalid
     */
    public Student addStudent(String studentId, String name, String programme, double marks) {
        if (!Validator.isValidStudentId(studentId)) {
            throw new IllegalArgumentException("Invalid Student ID format.");
        }
        if (!Validator.isValidName(name)) {
            throw new IllegalArgumentException("Invalid student name.");
        }
        if (!Validator.isValidProgramme(programme)) {
            throw new IllegalArgumentException("Invalid programme name.");
        }
        if (!Validator.isValidMarks(marks)) {
            throw new IllegalArgumentException("Marks must be between 0 and 100.");
        }
        String id = Validator.normalizeStudentId(studentId);
        if (hashTable.containsKey(id)) { // O(1) duplicate check using hashing
            throw new IllegalArgumentException("Duplicate ID: a student with ID " + id + " already exists.");
        }

        Student student = new Student(id, Validator.cleanText(name), Validator.cleanText(programme), marks);
        list.addLast(student);
        tree.insert(student);
        hashTable.put(student);

        undoStack.push(new Action(Action.Type.ADD_STUDENT, "Added " + id, student.copy(), list.size() - 1));
        history.record(Action.Type.ADD_STUDENT, "Added " + student);
        return student;
    }

    // ------------------------------------------------------------------
    // Update
    // ------------------------------------------------------------------

    /**
     * Updates the editable fields of a student. Pass null for a field to keep its current value.
     * @return true if something changed, false if all new values were the same as before
     * @throws IllegalArgumentException if the student does not exist or a value is invalid
     */
    public boolean updateStudent(String studentId, String newName, String newProgramme, Double newMarks) {
        Student student = findById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student ID " + studentId + " was not found.");
        }
        newName = newName == null ? null : Validator.cleanText(newName);
        newProgramme = newProgramme == null ? null : Validator.cleanText(newProgramme);
        if (newName != null && !Validator.isValidName(newName)) {
            throw new IllegalArgumentException("Invalid student name.");
        }
        if (newProgramme != null && !Validator.isValidProgramme(newProgramme)) {
            throw new IllegalArgumentException("Invalid programme name.");
        }
        if (newMarks != null && !Validator.isValidMarks(newMarks)) {
            throw new IllegalArgumentException("Marks must be between 0 and 100.");
        }

        Student before = student.copy();
        StringBuilder changes = new StringBuilder();
        if (newName != null && !newName.equals(student.getName())) {
            changes.append("name '").append(student.getName()).append("' -> '").append(newName).append("'; ");
            student.setName(newName);
        }
        if (newProgramme != null && !newProgramme.equals(student.getProgramme())) {
            changes.append("programme '").append(student.getProgramme()).append("' -> '")
                    .append(newProgramme).append("'; ");
            student.setProgramme(newProgramme);
        }
        if (newMarks != null && Double.compare(newMarks, student.getMarks()) != 0) {
            changes.append(String.format("marks %.2f -> %.2f; ", student.getMarks(), newMarks));
            student.setMarks(newMarks);
        }
        if (changes.length() == 0) {
            return false;
        }
        String description = "Updated " + student.getStudentId() + ": " + changes.toString().trim();
        undoStack.push(new Action(Action.Type.UPDATE_STUDENT, description, before, -1));
        history.record(Action.Type.UPDATE_STUDENT, description);
        return true;
    }

    // ------------------------------------------------------------------
    // Delete
    // ------------------------------------------------------------------

    /**
     * Deletes a student from all three structures.
     * The deleted record is pushed onto the undo stack so it can be restored.
     * @throws IllegalArgumentException if the student does not exist
     */
    public Student deleteStudent(String studentId) {
        String id = Validator.normalizeStudentId(studentId);
        if (!hashTable.containsKey(id)) {
            throw new IllegalArgumentException("Student ID " + id + " was not found.");
        }
        int position = list.indexOf(id);
        Student removed = list.removeById(id);
        tree.delete(id);
        hashTable.remove(id);

        undoStack.push(new Action(Action.Type.DELETE_STUDENT, "Deleted " + id, removed.copy(), position));
        history.record(Action.Type.DELETE_STUDENT, "Deleted " + removed);
        return removed;
    }

    // ------------------------------------------------------------------
    // Undo
    // ------------------------------------------------------------------

    /**
     * Reverses the most recent add / update / delete (LIFO order).
     * @return a message describing what was undone
     * @throws IllegalStateException if there is nothing to undo
     */
    public String undoLastAction() {
        if (undoStack.isEmpty()) {
            throw new IllegalStateException("Nothing to undo. The undo stack is empty.");
        }
        Action action = undoStack.pop();
        Student snapshot = action.getSnapshot();
        String id = snapshot.getStudentId();
        String message;

        switch (action.getType()) {
            case ADD_STUDENT:
                list.removeById(id);
                tree.delete(id);
                hashTable.remove(id);
                message = "Undo add: student " + id + " was removed.";
                break;
            case UPDATE_STUDENT:
                Student current = hashTable.get(id);
                current.restoreFrom(snapshot);
                message = "Undo update: student " + id + " restored to " + current;
                break;
            case DELETE_STUDENT:
                Student restored = snapshot.copy();
                list.insertAt(action.getListPosition(), restored);
                tree.insert(restored);
                hashTable.put(restored);
                message = "Undo delete: student " + restored + " was restored.";
                break;
            default:
                message = "This action cannot be undone.";
        }
        history.record(Action.Type.UNDO, message);
        return message;
    }

    /** What the next undo would reverse, or null if nothing. */
    public String peekUndo() {
        return undoStack.isEmpty() ? null : undoStack.peek().getDescription();
    }

    public int undoCount() {
        return undoStack.size();
    }

    /** Empties the undo stack (used after loading sample data). */
    public void clearUndoStack() {
        undoStack.clear();
    }

    // ------------------------------------------------------------------
    // Search and access
    // ------------------------------------------------------------------

    /** O(1) average lookup using the hash table. */
    public Student findById(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return null;
        }
        return hashTable.get(Validator.normalizeStudentId(studentId));
    }

    public StudentHashTable.SearchResult searchWithHashing(String studentId) {
        return hashTable.search(Validator.normalizeStudentId(studentId));
    }

    public StudentLinkedList getList() {
        return list;
    }

    public StudentAVLTree getTree() {
        return tree;
    }

    public StudentHashTable getHashTable() {
        return hashTable;
    }

    public int count() {
        return list.size();
    }

    /** True when the list, tree and hash table all hold the same number of records. */
    public boolean isConsistent() {
        return list.size() == tree.size() && tree.size() == hashTable.size();
    }
}
