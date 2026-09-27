package app;

import graph.CampusGraph;
import model.ServiceRequest;
import service.ServiceRequestService;
import service.StudentRecordService;

/**
 * Demo data so the system can be shown quickly in the video / viva.
 * Records that already exist are skipped, so loading twice is safe.
 *
 * Student IDs are deliberately NOT in sorted order, so the AVL tree has to
 * perform rotations while they are inserted.
 *
 * Owner: Member 1 - F.M. Asath (23DA2-0743) - integration
 */
public final class SampleData {

    private SampleData() {
    }

    private static final Object[][] STUDENTS = {
        {"23DA2-0512", "Nimal Perera", "BAIT", 78.5},
        {"23DA2-0233", "Fathima Rizna", "BSc (Hons) Software Engineering", 84.0},
        {"23DA2-0871", "Kavindu Silva", "BEng (Hons) Civil Engineering", 62.5},
        {"23DA2-0105", "Tharushi Fernando", "BAIT", 91.0},
        {"23DA2-0344", "Mohamed Rifkhan", "BSc (Hons) Computer Science", 55.0},
        {"23DA2-0690", "Sanduni Jayasinghe", "BSc (Hons) Data Science", 71.25},
        {"23DA2-0958", "Arjun Kumar", "BEng (Hons) Electrical Engineering", 38.0},
        {"23DA2-0421", "Aysha Nuha", "BAIT", 67.0},
        {"23DA2-0777", "Dinuka Rajapaksha", "BSc (Hons) Software Engineering", 49.5},
        {"23DA2-0150", "Priya Shanmugam", "BSc (Hons) Computer Science", 88.0}
    };

    private static final String[] LOCATIONS = {
        "Main Gate", "Admin Building", "Library", "Lecture Hall Complex", "Computer Lab",
        "Cafeteria", "Auditorium", "Student Hostel", "Sports Ground", "Car Park"
    };

    /** {from, to, distance in metres} */
    private static final Object[][] ROADS = {
        {"Main Gate", "Car Park", 80},
        {"Main Gate", "Admin Building", 120},
        {"Admin Building", "Library", 150},
        {"Admin Building", "Lecture Hall Complex", 200},
        {"Library", "Computer Lab", 90},
        {"Library", "Cafeteria", 160},
        {"Lecture Hall Complex", "Computer Lab", 110},
        {"Lecture Hall Complex", "Cafeteria", 130},
        {"Cafeteria", "Auditorium", 100},
        {"Cafeteria", "Student Hostel", 250},
        {"Student Hostel", "Sports Ground", 180},
        {"Auditorium", "Sports Ground", 220}
    };

    /** Loads all demo data and returns a short summary. */
    public static String load(StudentRecordService students, ServiceRequestService requests,
                              CampusGraph campus) {
        int addedStudents = 0;
        for (Object[] s : STUDENTS) {
            if (students.findById((String) s[0]) == null) {
                students.addStudent((String) s[0], (String) s[1], (String) s[2], (Double) s[3]);
                addedStudents++;
            }
        }
        // Sample records are the starting state, so they should not be "undone".
        students.clearUndoStack();

        int addedLocations = 0;
        for (String location : LOCATIONS) {
            if (campus.addLocation(location)) {
                addedLocations++;
            }
        }

        int addedRoads = 0;
        for (Object[] r : ROADS) {
            if (campus.addRoad((String) r[0], (String) r[1], (Integer) r[2])) {
                addedRoads++;
            }
        }

        int addedRequests = 0;
        if (requests.pendingCount() == 0 && addedStudents > 0) {
            requests.addRequest(students.findById("23DA2-0105"), ServiceRequest.REQUEST_TYPES[0],
                    "Official transcript for internship application");
            requests.addRequest(students.findById("23DA2-0344"), ServiceRequest.REQUEST_TYPES[2],
                    "Re-correction for CIT300 mid-exam");
            requests.addRequest(students.findById("23DA2-0690"), ServiceRequest.REQUEST_TYPES[1],
                    "Lost student ID card");
            addedRequests = 3;
        }

        return addedStudents + " students, " + addedLocations + " locations, "
                + addedRoads + " roads and " + addedRequests + " service requests loaded.";
    }
}
