package app;

import graph.CampusGraph;
import model.Action;
import model.ServiceRequest;
import model.Student;
import service.ActionHistory;
import service.ServiceRequestService;
import service.StudentRecordService;
import structures.StudentAVLTree;
import structures.StudentHashTable;
import util.InputHelper;
import util.Validator;

/**
 * Menu-driven console interface (Assignment requirement 13).
 *
 * Options 1-16 follow the suggested menu in the assignment brief exactly.
 * Options 17-20 are extra features (undo, pending queue view, route finder,
 * sample data).
 *
 * Every handler validates its input and prints a clear [ERROR] message for
 * invalid input, duplicate IDs/locations, missing records, invalid marks and
 * unavailable connections (Assignment requirement 14).
 *
 * Owner: Member 1 - F.M. Asath (23DA2-0743) - integration.
 * Graph handlers (options 10-15, 19) written together with Member 4.
 */
public class ConsoleMenu {

    private static final int EXIT_OPTION = 16;
    private static final int LAST_OPTION = 20;

    private final InputHelper input;
    private final ActionHistory history = new ActionHistory();
    private final StudentRecordService students = new StudentRecordService(history);
    private final ServiceRequestService requests = new ServiceRequestService(history);
    private final CampusGraph campus = new CampusGraph();

    public ConsoleMenu(InputHelper input) {
        this.input = input;
    }

    /** Main loop: show the menu, read a choice, run it, repeat until Exit. */
    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            try {
                int choice = input.readMenuChoice("Enter your choice (1-" + LAST_OPTION + "): ");
                if (choice < 1 || choice > LAST_OPTION) {
                    InputHelper.printError("Invalid choice. Please enter a number from 1 to " + LAST_OPTION + ".");
                    continue;
                }
                if (choice == EXIT_OPTION) {
                    running = false;
                    printExitSummary();
                    continue;
                }
                handleChoice(choice);
                input.pause();
            } catch (InputHelper.OperationCancelledException e) {
                InputHelper.printInfo("Operation cancelled. Returning to the main menu.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                InputHelper.printError(e.getMessage());
            }
        }
    }

    /** Asks at start-up whether demo data should be loaded. */
    public void offerSampleData() {
        try {
            if (input.readYesNo("Load sample data (10 students, 10 campus locations, 12 roads, 3 requests)?")) {
                InputHelper.printSuccess(SampleData.load(students, requests, campus));
            } else {
                InputHelper.printInfo("Starting with an empty system. You can load sample data later (option 20).");
            }
        } catch (InputHelper.OperationCancelledException e) {
            InputHelper.printInfo("Starting with an empty system.");
        }
    }

    private void printMenu() {
        String line = Validator.repeat('=', 66);
        System.out.println();
        System.out.println(line);
        System.out.println("   UNIVERSITY STUDENT RECORD & CAMPUS ROUTE MANAGEMENT SYSTEM");
        System.out.println(line);
        System.out.println(String.format("   Students: %d | Pending requests: %d | Locations: %d | Roads: %d",
                students.count(), requests.pendingCount(), campus.getLocationCount(), campus.getRoadCount()));
        System.out.println(Validator.repeat('-', 66));
        System.out.println(" STUDENT RECORDS (Linked List)");
        System.out.println("   1. Add Student Record");
        System.out.println("   2. Update Student Record");
        System.out.println("   3. Delete Student Record");
        System.out.println("   4. Display All Records using Linked List");
        System.out.println(" SERVICE REQUESTS & HISTORY (Queue / Stack)");
        System.out.println("   5. Add Service Request to Queue");
        System.out.println("   6. Process Next Service Request");
        System.out.println("   7. Display Recent Actions using Stack");
        System.out.println(" TREE & HASHING");
        System.out.println("   8. Display Students using AVL Tree");
        System.out.println("   9. Search Student using Hashing");
        System.out.println(" CAMPUS ROUTES (Graph - Adjacency List)");
        System.out.println("  10. Add Campus Location");
        System.out.println("  11. Remove Campus Location");
        System.out.println("  12. Add Campus Connection/Road");
        System.out.println("  13. Remove Campus Connection/Road");
        System.out.println("  14. Display Campus Connections");
        System.out.println("  15. Traverse Campus Locations using BFS or DFS");
        System.out.println("  16. Exit");
        System.out.println(" EXTRA FEATURES");
        System.out.println("  17. Undo Last Student Record Action (Stack)");
        System.out.println("  18. View Pending Service Requests (Queue)");
        System.out.println("  19. Find Route Between Two Locations (BFS - fewest stops)");
        System.out.println("  20. Load Sample Data");
        System.out.println(line);
        System.out.println(" Tip: type 'cancel' at any prompt to return to this menu.");
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1: addStudent(); break;
            case 2: updateStudent(); break;
            case 3: deleteStudent(); break;
            case 4: displayAllStudents(); break;
            case 5: addServiceRequest(); break;
            case 6: processNextRequest(); break;
            case 7: displayRecentActions(); break;
            case 8: displayAvlTree(); break;
            case 9: searchWithHashing(); break;
            case 10: addLocation(); break;
            case 11: removeLocation(); break;
            case 12: addRoad(); break;
            case 13: removeRoad(); break;
            case 14: displayConnections(); break;
            case 15: traverseCampus(); break;
            case 17: undoLastAction(); break;
            case 18: viewPendingRequests(); break;
            case 19: findRoute(); break;
            case 20: loadSampleData(); break;
            default: InputHelper.printError("Invalid choice.");
        }
    }

    private void printTitle(String title) {
        System.out.println();
        System.out.println("---- " + title + " " + Validator.repeat('-', Math.max(3, 60 - title.length())));
    }

    // ==================================================================
    // 1-4 STUDENT RECORDS
    // ==================================================================

    private String readStudentId(String prompt) {
        String id = input.readText(prompt, Validator::isValidStudentId,
                "Invalid Student ID. Use 3-15 letters/digits, optionally separated by '-' (e.g. 23DA2-0743).");
        return Validator.normalizeStudentId(id);
    }

    private String readName(String prompt) {
        return input.readText(prompt, Validator::isValidName,
                "Invalid name. Use 2-50 letters (spaces, dots, apostrophes and hyphens allowed, no digits).");
    }

    private String readProgramme(String prompt) {
        return input.readText(prompt, Validator::isValidProgramme,
                "Invalid programme. Use 2-60 characters starting with a letter (e.g. BSc (Hons) Software Engineering).");
    }

    /** Option 1 */
    private void addStudent() {
        printTitle("ADD STUDENT RECORD");
        String id = readStudentId("Student ID (e.g. 23DA2-0743): ");
        if (students.findById(id) != null) {
            InputHelper.printError("Duplicate ID: a student with ID " + id + " already exists.");
            System.out.println("        Existing record: " + students.findById(id));
            return;
        }
        String name = readName("Full name: ");
        String programme = readProgramme("Programme: ");
        double marks = input.readMarks("Marks (0-100): ");

        Student student = students.addStudent(id, name, programme, marks);
        InputHelper.printSuccess("Student added: " + student);
        StudentAVLTree tree = students.getTree();
        StudentHashTable.SearchResult hashInfo = students.searchWithHashing(id);
        System.out.println("  Linked list : added at position " + students.getList().size() + " (tail)");
        System.out.println("  AVL tree    : inserted, tree height = " + tree.getHeight()
                + ", root = " + tree.getRootId()
                + (tree.getLastRotations().isEmpty() ? " (no rotation needed)"
                        : " | rebalanced: " + tree.getLastRotations()));
        System.out.println("  Hash table  : stored in bucket [" + hashInfo.getBucketIndex() + "]");
    }

    /** Option 2 */
    private void updateStudent() {
        printTitle("UPDATE STUDENT RECORD");
        if (students.count() == 0) {
            InputHelper.printInfo("There are no student records to update.");
            return;
        }
        String id = readStudentId("Student ID to update: ");
        Student student = students.findById(id);
        if (student == null) {
            InputHelper.printError("Student ID " + id + " was not found.");
            return;
        }
        System.out.println("Current record: " + student);
        System.out.println("(Press Enter to keep the current value. Student ID cannot be changed because it is the key.)");
        String name = input.readOptionalText("New name [" + student.getName() + "]: ", Validator::isValidName,
                "Invalid name. Use 2-50 letters (spaces, dots, apostrophes and hyphens allowed, no digits).");
        String programme = input.readOptionalText("New programme [" + student.getProgramme() + "]: ",
                Validator::isValidProgramme, "Invalid programme. Use 2-60 characters starting with a letter.");
        Double marks = input.readOptionalMarks("New marks [" + String.format("%.2f", student.getMarks()) + "]: ");

        if (students.updateStudent(id, name, programme, marks)) {
            InputHelper.printSuccess("Record updated: " + students.findById(id));
            System.out.println("  The change is visible in the linked list, AVL tree and hash table"
                    + " (they share the same object).");
        } else {
            InputHelper.printInfo("No changes were made.");
        }
    }

    /** Option 3 */
    private void deleteStudent() {
        printTitle("DELETE STUDENT RECORD");
        if (students.count() == 0) {
            InputHelper.printInfo("There are no student records to delete.");
            return;
        }
        String id = readStudentId("Student ID to delete: ");
        Student student = students.findById(id);
        if (student == null) {
            InputHelper.printError("Student ID " + id + " was not found.");
            return;
        }
        System.out.println("Record: " + student);
        if (!input.readYesNo("Are you sure you want to delete this record?")) {
            InputHelper.printInfo("Delete cancelled.");
            return;
        }
        Student removed = students.deleteStudent(id);
        InputHelper.printSuccess("Deleted " + removed.getStudentId()
                + " from the linked list, AVL tree and hash table.");
        if (students.getTree().getLastRotations().length() > 0) {
            System.out.println("  AVL tree rebalanced: " + students.getTree().getLastRotations());
        }
        System.out.println("  The deleted record was pushed onto the undo stack (use option 17 to restore it).");
    }

    /** Option 4 */
    private void displayAllStudents() {
        printTitle("ALL STUDENT RECORDS (LINKED LIST - order of entry)");
        students.getList().display();
        if (!students.getList().isEmpty()) {
            System.out.println("Linked list nodes: " + students.getList().toChainString());
        }
    }

    // ==================================================================
    // 5-7 QUEUE AND STACK
    // ==================================================================

    /** Option 5 */
    private void addServiceRequest() {
        printTitle("ADD SERVICE REQUEST TO QUEUE");
        if (students.count() == 0) {
            InputHelper.printError("No student records exist. Add a student before creating a service request.");
            return;
        }
        String id = readStudentId("Student ID making the request: ");
        Student student = students.findById(id);
        if (student == null) {
            InputHelper.printError("Student ID " + id + " was not found. Requests can only be made by registered students.");
            return;
        }
        System.out.println("Student: " + student.getName());
        System.out.println("Request types:");
        for (int i = 0; i < ServiceRequest.REQUEST_TYPES.length; i++) {
            System.out.println("  " + (i + 1) + ". " + ServiceRequest.REQUEST_TYPES[i]);
        }
        int type = input.readInt("Choose request type (1-" + ServiceRequest.REQUEST_TYPES.length + "): ",
                1, ServiceRequest.REQUEST_TYPES.length);
        String details = input.readText("Short description (max 80 characters): ",
                value -> value.length() <= 80, "Description must be 80 characters or fewer.");

        ServiceRequest request = requests.addRequest(student, ServiceRequest.REQUEST_TYPES[type - 1], details);
        InputHelper.printSuccess("Request " + request.getRequestId() + " added to the REAR of the queue.");
        System.out.println("  Position in queue: " + requests.pendingCount()
                + " (requests are served in order of arrival - FIFO)");
    }

    /** Option 6 */
    private void processNextRequest() {
        printTitle("PROCESS NEXT SERVICE REQUEST");
        ServiceRequest request = requests.processNext();
        if (request == null) {
            InputHelper.printInfo("No pending service requests. The queue is empty.");
            return;
        }
        InputHelper.printSuccess("Processed request from the FRONT of the queue:");
        System.out.println("  Request ID  : " + request.getRequestId());
        System.out.println("  Student     : " + request.getStudentId() + " - " + request.getStudentName());
        System.out.println("  Type        : " + request.getRequestType());
        System.out.println("  Details     : " + request.getDetails());
        System.out.println("  Received at : " + request.getCreatedAt());
        if (students.findById(request.getStudentId()) == null) {
            InputHelper.printInfo("Note: this student's record has since been deleted.");
        }
        ServiceRequest next = requests.peekNext();
        System.out.println("  Remaining in queue: " + requests.pendingCount()
                + (next == null ? "" : " | Next: " + next.getRequestId() + " (" + next.getStudentId() + ")"));
    }

    /** Option 7 */
    private void displayRecentActions() {
        printTitle("RECENT ACTIONS (STACK - Last In, First Out)");
        history.display(15);
        System.out.println();
        String nextUndo = students.peekUndo();
        System.out.println("Undo stack: " + students.undoCount() + " student record action(s) can be undone."
                + (nextUndo == null ? "" : " Next undo -> " + nextUndo));
    }

    // ==================================================================
    // 8-9 AVL TREE AND HASHING
    // ==================================================================

    /** Option 8 */
    private void displayAvlTree() {
        printTitle("STUDENTS USING AVL TREE (key = Student ID)");
        final StudentAVLTree tree = students.getTree();
        if (tree.isEmpty()) {
            InputHelper.printInfo("The AVL tree is empty. Add student records first.");
            return;
        }
        System.out.println("Root: " + tree.getRootId() + " | Height: " + tree.getHeight()
                + " | Nodes: " + tree.size() + " | Balanced: " + (tree.isBalanced() ? "Yes" : "No"));
        System.out.println("  1. In-order traversal (sorted by Student ID)");
        System.out.println("  2. Pre-order traversal");
        System.out.println("  3. Post-order traversal");
        System.out.println("  4. Level-order traversal (uses the queue)");
        System.out.println("  5. Show tree structure");
        System.out.println("  6. Search a Student ID in the AVL tree (show path)");
        int option = input.readInt("Choose (1-6): ", 1, 6);
        final String line = Validator.repeat('-', 92);
        final int[] counter = {0};
        StudentAVLTree.StudentVisitor printer = student -> {
            counter[0]++;
            System.out.println(String.format("%-4d | ", counter[0]) + student.toTableRow());
        };
        switch (option) {
            case 1:
            case 2:
            case 3:
            case 4:
                String[] names = {"", "IN-ORDER (Left-Root-Right, sorted by ID)", "PRE-ORDER (Root-Left-Right)",
                    "POST-ORDER (Left-Right-Root)", "LEVEL-ORDER (top to bottom)"};
                System.out.println(names[option]);
                System.out.println(line);
                System.out.println(String.format("%-4s | ", "No.") + Student.tableHeader());
                System.out.println(line);
                if (option == 1) {
                    tree.inOrder(printer);
                } else if (option == 2) {
                    tree.preOrder(printer);
                } else if (option == 3) {
                    tree.postOrder(printer);
                } else {
                    tree.levelOrder(printer);
                }
                System.out.println(line);
                break;
            case 5:
                System.out.println("Tree structure (rotate your head left: root is on the left, bf = balance factor)");
                tree.printStructure();
                break;
            default:
                String id = readStudentId("Student ID to search: ");
                System.out.println("Search path: " + tree.searchPath(id));
                Student found = tree.search(id);
                if (found == null) {
                    InputHelper.printError("Student ID " + id + " was not found in the AVL tree.");
                } else {
                    InputHelper.printSuccess("Found: " + found);
                }
        }
    }

    /** Option 9 */
    private void searchWithHashing() {
        printTitle("SEARCH STUDENT USING HASHING");
        System.out.println("  1. Search by Student ID");
        System.out.println("  2. View hash table buckets");
        int option = input.readInt("Choose (1-2): ", 1, 2);
        if (option == 2) {
            students.getHashTable().printTable();
            return;
        }
        if (students.count() == 0) {
            InputHelper.printInfo("There are no student records to search.");
            return;
        }
        String id = readStudentId("Student ID to search: ");
        StudentHashTable.SearchResult result = students.searchWithHashing(id);
        System.out.println("  hash(\"" + id + "\") -> bucket [" + result.getBucketIndex() + "], "
                + result.getComparisons() + " key comparison(s) in that bucket");
        if (result.getStudent() == null) {
            InputHelper.printError("Student ID " + id + " was not found.");
            return;
        }
        Student s = result.getStudent();
        InputHelper.printSuccess("Student found:");
        System.out.println("  Student ID : " + s.getStudentId());
        System.out.println("  Name       : " + s.getName());
        System.out.println("  Programme  : " + s.getProgramme());
        System.out.println("  Marks      : " + String.format("%.2f", s.getMarks()) + " (Grade " + s.getGrade() + ")");
        System.out.println("  A linked-list search would need up to " + students.count()
                + " comparisons; hashing needed " + result.getComparisons() + ".");
    }

    // ==================================================================
    // 10-15 CAMPUS GRAPH
    // ==================================================================

    private void printLocations() {
        String[] names = campus.getLocations();
        StringBuilder builder = new StringBuilder("Locations: ");
        for (int i = 0; i < names.length; i++) {
            builder.append(i + 1).append(". ").append(names[i]);
            if (i < names.length - 1) {
                builder.append(" | ");
            }
        }
        System.out.println(builder);
    }

    /** Accepts either the location's number from the list or its name. Re-asks if it does not exist. */
    private String readExistingLocation(String prompt) {
        while (true) {
            String value = input.readLine(prompt);
            if (value.isEmpty()) {
                InputHelper.printError("This field cannot be empty.");
                continue;
            }
            String[] names = campus.getLocations();
            try {
                int number = Integer.parseInt(value);
                if (number >= 1 && number <= names.length) {
                    return names[number - 1];
                }
                InputHelper.printError("There is no location number " + number + ".");
                continue;
            } catch (NumberFormatException e) {
                // not a number - treat it as a name
            }
            String stored = campus.getLocationName(Validator.cleanText(value));
            if (stored != null) {
                return stored;
            }
            InputHelper.printError("Location '" + value + "' does not exist. Enter a number or name from the list.");
        }
    }

    /** Option 10 */
    private void addLocation() {
        printTitle("ADD CAMPUS LOCATION");
        String name = input.readText("Location name (e.g. Library): ", Validator::isValidLocationName,
                "Invalid location name. Use 2-40 characters: letters, digits, spaces and & ' ( ) . -");
        if (!campus.addLocation(name)) {
            InputHelper.printError("Duplicate location: '" + campus.getLocationName(name) + "' already exists.");
            return;
        }
        history.record(Action.Type.ADD_LOCATION, "Added location " + name);
        InputHelper.printSuccess("Location '" + name + "' added as a new vertex. Total locations: "
                + campus.getLocationCount());
    }

    /** Option 11 */
    private void removeLocation() {
        printTitle("REMOVE CAMPUS LOCATION");
        if (campus.isEmpty()) {
            InputHelper.printInfo("There are no campus locations to remove.");
            return;
        }
        printLocations();
        String name = readExistingLocation("Location to remove (number or name): ");
        campus.displayNeighbours(name);
        if (!input.readYesNo("Remove '" + name + "' and all of its roads?")) {
            InputHelper.printInfo("Remove cancelled.");
            return;
        }
        int removedRoads = campus.removeLocation(name);
        history.record(Action.Type.REMOVE_LOCATION, "Removed location " + name + " and " + removedRoads + " road(s)");
        InputHelper.printSuccess("Location '" + name + "' removed together with " + removedRoads + " connected road(s).");
    }

    /** Option 12 */
    private void addRoad() {
        printTitle("ADD CAMPUS CONNECTION/ROAD");
        if (campus.getLocationCount() < 2) {
            InputHelper.printError("At least two locations are needed to add a road. Add locations first (option 10).");
            return;
        }
        printLocations();
        String from = readExistingLocation("From location (number or name): ");
        String to = readExistingLocation("To location (number or name): ");
        if (from.equalsIgnoreCase(to)) {
            InputHelper.printError("A road must connect two different locations.");
            return;
        }
        if (campus.hasRoad(from, to)) {
            InputHelper.printError("Duplicate road: " + from + " and " + to + " are already connected ("
                    + campus.getDistance(from, to) + " m).");
            return;
        }
        int distance = input.readInt("Distance in metres (" + Validator.MIN_DISTANCE + "-"
                + Validator.MAX_DISTANCE + "): ", Validator.MIN_DISTANCE, Validator.MAX_DISTANCE);
        campus.addRoad(from, to, distance);
        history.record(Action.Type.ADD_ROAD, "Added road " + from + " <-> " + to + " (" + distance + " m)");
        InputHelper.printSuccess("Two-way road added: " + from + " <-> " + to + " (" + distance + " m).");
    }

    /** Option 13 */
    private void removeRoad() {
        printTitle("REMOVE CAMPUS CONNECTION/ROAD");
        if (campus.getRoadCount() == 0) {
            InputHelper.printInfo("There are no roads to remove.");
            return;
        }
        printLocations();
        String from = readExistingLocation("From location (number or name): ");
        String to = readExistingLocation("To location (number or name): ");
        if (!campus.hasRoad(from, to)) {
            InputHelper.printError("Connection not available: there is no direct road between "
                    + from + " and " + to + ".");
            return;
        }
        campus.removeRoad(from, to);
        history.record(Action.Type.REMOVE_ROAD, "Removed road " + from + " <-> " + to);
        InputHelper.printSuccess("Road between " + from + " and " + to + " removed.");
    }

    /** Option 14 */
    private void displayConnections() {
        printTitle("DISPLAY CAMPUS CONNECTIONS");
        if (campus.isEmpty()) {
            InputHelper.printInfo("The campus network is empty. Add locations first (option 10 or 20).");
            return;
        }
        System.out.println("  1. Full campus network (adjacency list)");
        System.out.println("  2. Neighbours of one location");
        System.out.println("  3. Adjacency matrix view");
        int option = input.readInt("Choose (1-3): ", 1, 3);
        if (option == 1) {
            campus.displayNetwork();
            System.out.println("Every location reachable from every other: "
                    + (campus.isFullyConnected() ? "Yes" : "No"));
        } else if (option == 2) {
            printLocations();
            campus.displayNeighbours(readExistingLocation("Location (number or name): "));
        } else {
            campus.displayAdjacencyMatrix();
        }
    }

    /** Option 15 */
    private void traverseCampus() {
        printTitle("TRAVERSE CAMPUS LOCATIONS");
        if (campus.isEmpty()) {
            InputHelper.printInfo("The campus network is empty. Add locations first (option 10 or 20).");
            return;
        }
        System.out.println("  1. Breadth-First Search (BFS) - uses a queue, visits level by level");
        System.out.println("  2. Depth-First Search (DFS)   - uses a stack, goes deep first");
        System.out.println("  3. Both (compare)");
        int option = input.readInt("Choose (1-3): ", 1, 3);
        printLocations();
        String start = readExistingLocation("Start location (number or name): ");
        if (option == 1 || option == 3) {
            printTraversal("BFS", campus.bfs(start));
        }
        if (option == 2 || option == 3) {
            printTraversal("DFS", campus.dfs(start));
        }
    }

    private void printTraversal(String type, String[] order) {
        System.out.println(type + " order (" + order.length + " of " + campus.getLocationCount() + " visited):");
        System.out.println("  " + String.join(" -> ", order));
        if (order.length < campus.getLocationCount()) {
            StringBuilder unreachable = new StringBuilder();
            for (String location : campus.getLocations()) {
                boolean visited = false;
                for (String v : order) {
                    if (v.equals(location)) {
                        visited = true;
                        break;
                    }
                }
                if (!visited) {
                    unreachable.append(unreachable.length() == 0 ? "" : ", ").append(location);
                }
            }
            InputHelper.printInfo("Not reachable from the start location: " + unreachable);
        }
    }

    // ==================================================================
    // 16-20 EXIT AND EXTRA FEATURES
    // ==================================================================

    /** Option 17 */
    private void undoLastAction() {
        printTitle("UNDO LAST STUDENT RECORD ACTION");
        String next = students.peekUndo();
        if (next == null) {
            InputHelper.printInfo("Nothing to undo. The undo stack is empty.");
            return;
        }
        System.out.println("Top of undo stack: " + next);
        if (!input.readYesNo("Undo this action?")) {
            InputHelper.printInfo("Undo cancelled.");
            return;
        }
        InputHelper.printSuccess(students.undoLastAction());
    }

    /** Option 18 */
    private void viewPendingRequests() {
        printTitle("PENDING SERVICE REQUESTS (QUEUE)");
        requests.displayPending();
        System.out.println("Requests processed so far: " + requests.getProcessedCount());
    }

    /** Option 19 */
    private void findRoute() {
        printTitle("FIND ROUTE BETWEEN TWO LOCATIONS");
        if (campus.getLocationCount() < 2) {
            InputHelper.printError("At least two locations are needed. Add locations first.");
            return;
        }
        printLocations();
        String from = readExistingLocation("From location (number or name): ");
        String to = readExistingLocation("To location (number or name): ");
        if (from.equalsIgnoreCase(to)) {
            InputHelper.printInfo("You are already at " + from + ".");
            return;
        }
        String[] path = campus.findRoute(from, to);
        if (path == null) {
            InputHelper.printError("No route available: " + from + " and " + to + " are not connected.");
            return;
        }
        InputHelper.printSuccess("Route with the fewest stops (" + (path.length - 1) + " road(s)):");
        for (int i = 0; i < path.length - 1; i++) {
            System.out.println(String.format("  %d. %s -> %s (%d m)", i + 1, path[i], path[i + 1],
                    campus.getDistance(path[i], path[i + 1])));
        }
        System.out.println("  Total distance: " + campus.pathDistance(path) + " m");
    }

    /** Option 20 */
    private void loadSampleData() {
        printTitle("LOAD SAMPLE DATA");
        InputHelper.printSuccess(SampleData.load(students, requests, campus));
    }

    private void printExitSummary() {
        System.out.println();
        System.out.println("Session summary: " + students.count() + " students | "
                + requests.pendingCount() + " pending requests | " + requests.getProcessedCount()
                + " processed | " + campus.getLocationCount() + " locations | "
                + campus.getRoadCount() + " roads | " + history.size() + " actions recorded");
        System.out.println("Thank you for using the University Student Record & Campus Route Management System. Goodbye!");
    }
}
