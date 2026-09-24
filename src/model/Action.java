package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * One entry in the action history stack.
 *
 * For student record actions it also keeps a snapshot of the record so the
 * action can be undone:
 *   ADD_STUDENT    -> undo removes the student again
 *   UPDATE_STUDENT -> undo restores the old name / programme / marks
 *   DELETE_STUDENT -> undo restores the deleted record at its old position
 *
 * Owner: Member 2 - M.I. Thanish (23DA2-0897)
 */
public class Action {

    public enum Type {
        ADD_STUDENT("Add Student"),
        UPDATE_STUDENT("Update Student"),
        DELETE_STUDENT("Delete Student"),
        UNDO("Undo"),
        ADD_REQUEST("Add Request"),
        PROCESS_REQUEST("Process Request"),
        ADD_LOCATION("Add Location"),
        REMOVE_LOCATION("Remove Location"),
        ADD_ROAD("Add Road"),
        REMOVE_ROAD("Remove Road");

        private final String label;

        Type(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final Type type;
    private final String description;
    private final String time;
    private final Student snapshot;   // copy of the record BEFORE the change (null if not needed)
    private final int listPosition;   // old position in the linked list (for undo delete)

    public Action(Type type, String description) {
        this(type, description, null, -1);
    }

    public Action(Type type, String description, Student snapshot, int listPosition) {
        this.type = type;
        this.description = description;
        this.snapshot = snapshot;
        this.listPosition = listPosition;
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public Student getSnapshot() {
        return snapshot;
    }

    public int getListPosition() {
        return listPosition;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-16s %s", time, type.getLabel(), description);
    }
}
