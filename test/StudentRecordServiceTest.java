import model.Student;
import service.ActionHistory;
import service.StudentRecordService;
import util.Validator;

/**
 * Integration tests: add / update / delete / undo keep the linked list,
 * AVL tree and hash table in sync, and invalid input is rejected.
 *
 * Owner: Member 1 - F.M. Asath (23DA2-0743)
 */
public final class StudentRecordServiceTest {

    private StudentRecordServiceTest() {
    }

    public static void run() {
        TestSupport.section("Member 1 - Validator");
        TestSupport.check(Validator.isValidStudentId("23DA2-0743"), "valid ID 23DA2-0743");
        TestSupport.check(!Validator.isValidStudentId("23DA2--0743"), "double hyphen rejected");
        TestSupport.check(!Validator.isValidStudentId("AB"), "too short ID rejected");
        TestSupport.check(!Validator.isValidStudentId("ID 001"), "ID with space rejected");
        TestSupport.check(Validator.isValidName("M.I.M. Naizar"), "name with dots accepted");
        TestSupport.check(!Validator.isValidName("John3"), "name with digit rejected");
        TestSupport.check(!Validator.isValidMarks(100.5) && !Validator.isValidMarks(-1), "marks outside 0-100 rejected");
        TestSupport.check(Validator.isValidMarks(0) && Validator.isValidMarks(100), "boundary marks 0 and 100 accepted");

        TestSupport.section("Member 1 - StudentRecordService (integration + undo)");
        final StudentRecordService service = new StudentRecordService(new ActionHistory());
        service.addStudent("23da2-0500", "Kamal Perera", "BAIT", 72);
        service.addStudent("23DA2-0300", "Nuha Ahamed", "BSc (Hons) Software Engineering", 81.5);
        service.addStudent("23DA2-0700", "Ravi Kumar", "BAIT", 45);
        TestSupport.checkEquals("23DA2-0500", service.findById("23DA2-0500").getStudentId(), "ID stored in upper case");
        TestSupport.check(service.isConsistent() && service.count() == 3, "list, AVL and hash all hold 3 records");

        TestSupport.checkThrows(IllegalArgumentException.class, new Runnable() {
            public void run() {
                service.addStudent("23DA2-0300", "Someone Else", "BAIT", 50);
            }
        }, "duplicate Student ID rejected");
        TestSupport.checkThrows(IllegalArgumentException.class, new Runnable() {
            public void run() {
                service.addStudent("23DA2-0999", "Bad Marks", "BAIT", 120);
            }
        }, "invalid marks (120) rejected");
        TestSupport.checkThrows(IllegalArgumentException.class, new Runnable() {
            public void run() {
                service.updateStudent("NOPE-000", "Name", null, null);
            }
        }, "updating a missing record rejected");
        TestSupport.checkThrows(IllegalArgumentException.class, new Runnable() {
            public void run() {
                service.deleteStudent("NOPE-000");
            }
        }, "deleting a missing record rejected");
        TestSupport.check(service.count() == 3, "failed operations did not change the data");

        // update is visible in every structure (shared object)
        TestSupport.check(service.updateStudent("23DA2-0700", null, null, 55.0), "update marks");
        TestSupport.checkEquals(55.0, service.getTree().search("23DA2-0700").getMarks(), "AVL sees updated marks");
        TestSupport.checkEquals(55.0, service.getList().findById("23DA2-0700").getMarks(), "list sees updated marks");
        TestSupport.check(!service.updateStudent("23DA2-0700", null, null, 55.0), "same value -> no change reported");

        // delete middle record, then undo restores it at the same position
        service.deleteStudent("23DA2-0300");
        TestSupport.check(service.findById("23DA2-0300") == null && service.isConsistent(), "delete removes from all structures");
        String undoDelete = service.undoLastAction();
        TestSupport.check(undoDelete.startsWith("Undo delete"), "undo delete message");
        Student[] order = service.getList().toArray();
        TestSupport.checkEquals("23DA2-0300", order[1].getStudentId(), "restored record back at its old position (2nd)");
        TestSupport.check(service.getTree().contains("23DA2-0300") && service.isConsistent(), "restored in AVL and hash");

        // undo update
        service.undoLastAction();
        TestSupport.checkEquals(45.0, service.findById("23DA2-0700").getMarks(), "undo update restores old marks (45)");

        // undo the three adds (LIFO)
        service.undoLastAction();
        TestSupport.check(service.findById("23DA2-0700") == null, "undo add removes the last added student first");
        service.undoLastAction();
        service.undoLastAction();
        TestSupport.check(service.count() == 0 && service.isConsistent(), "all adds undone - system empty");
        TestSupport.checkThrows(IllegalStateException.class, new Runnable() {
            public void run() {
                service.undoLastAction();
            }
        }, "undo with empty stack throws");
    }
}
