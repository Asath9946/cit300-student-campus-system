# University Student Record and Campus Route Management System

**Module:** CIT300 Data Structures and Algorithms
**Assessment:** Graded Practical Assignment 1 (Week 10) – 10% of the final module grade
**Institution:** SLTC Research University
**Language:** Java (console application, JDK 8 or newer, no external libraries)
**Submission deadline:** 29 September 2026

A menu-driven Java console application that manages university student records and models the campus as a graph of locations and roads. Every data structure (linked list, stack, queue, AVL tree, hash table and graph) is **implemented from scratch**. None of the `java.util` collection classes are used for the required structures.

---

## 1. Group Members

| No. | Name | Student ID | Role | Assigned Responsibility |
|---|---|---|---|---|
| 1 | **F.M. Asath** | **23DA2-0743** | Group Leader (Member 1) | Linked list implementation and student-record management; system integration and console menu |
| 2 | **M.I. Thanish** | **23DA2-0897** | Member 2 | Stack and queue implementation, service requests, action history and undo support |
| 3 | **J.F. Asma** | **23DA2-0645** | Member 3 | AVL tree implementation, hashing and student search functionality |
| 4 | **M.I.M. Naizar** | **23DA2-1052** | Member 4 | Graph implementation, campus locations and connections, BFS/DFS traversal and route finding |
| All | All members | – | – | Integration, validation, testing, debugging, documentation and GitHub collaboration |

## 2. Individual Contributions

### Member 1 – F.M. Asath (23DA2-0743) – Group Leader
- **Linked list:** `src/structures/StudentLinkedList.java` – singly linked list with head and tail pointers. Provides `addLast`, `insertAt`, `findById`, `indexOf`, `removeById` (head, middle and tail cases), `display`, and average/highest-marks summaries.
- **Student record management:** `src/service/StudentRecordService.java` – add, update, delete and search, keeping the **linked list, AVL tree and hash table in sync**. Implements the undo logic (restores a deleted record to its original position).
- **Base model and utilities:** `src/model/Student.java`, `src/util/Validator.java`, `src/util/InputHelper.java` – validation rules, safe input, `cancel` support, and a clean exit when input ends.
- **Integration:** `src/app/ConsoleMenu.java` (20-option menu), `src/app/Main.java`, `src/app/SampleData.java`, run scripts.
- **Testing:** `test/StudentLinkedListTest.java`, `test/StudentRecordServiceTest.java`, `test/TestSupport.java`, `test/TestRunner.java`.
- **Documentation and GitHub:** created the repository, reviewed and merged pull requests, wrote this README and `docs/modules/01-linked-list-and-records.md`.
- **Menu options:** 1, 2, 3, 4, 17, 20.

### Member 2 – M.I. Thanish (23DA2-0897)
- **Stack:** `src/structures/LinkedStack.java` – generic LIFO stack (`push`, `pop`, `peek`, underflow handling). Used for the action history, the undo feature and DFS.
- **Queue:** `src/structures/LinkedQueue.java` – generic FIFO queue with front and rear pointers (`enqueue`, `dequeue`, `peek`). Used for service requests, BFS and AVL level-order traversal.
- **Models and services:** `src/model/Action.java`, `src/model/ServiceRequest.java`, `src/service/ActionHistory.java`, `src/service/ServiceRequestService.java`.
- **Testing:** `test/StackQueueTest.java`; wrote `docs/TEST_CASES.md` (51 manual test cases).
- **Documentation:** `docs/modules/02-stack-and-queue.md`.
- **Menu options:** 5, 6, 7, 18 (and the stack used by 17).

### Member 3 – J.F. Asma (23DA2-0645)
- **AVL tree:** `src/structures/StudentAVLTree.java` – self-balancing BST keyed by Student ID. Handles the LL, RR, LR and RL rotations, deletion with the in-order successor, in/pre/post/level-order traversals, a tree-structure display, the search path, and a balance check.
- **Hashing:** `src/structures/StudentHashTable.java` – separate chaining, a polynomial rolling hash, load factor ≤ 0.75, and resizing to prime capacities. Reports the bucket index and number of comparisons.
- **Testing:** `test/AvlHashTest.java`.
- **Documentation:** `docs/modules/03-avl-tree-and-hashing.md`, `docs/COMPLEXITY_ANALYSIS.md`.
- **Menu options:** 8, 9.

### Member 4 – M.I.M. Naizar (23DA2-1052)
- **Graph:** `src/graph/CampusGraph.java` – undirected weighted graph using an **adjacency list** (a vertex array plus a linked list of edges for each vertex). Supports adding and removing locations and roads with full validation, and displays the network, the neighbours of a location, or an adjacency-matrix view.
- **Traversals:** BFS (queue), DFS (stack), and a fewest-stops route finder (BFS + parent array), with a connectivity check.
- **Menu handlers** for options 10–15 and 19 (written together with Member 1).
- **Testing:** `test/CampusGraphTest.java`.
- **Documentation:** `docs/modules/04-campus-graph.md`, `docs/USER_MANUAL.md`.
- **Menu options:** 10, 11, 12, 13, 14, 15, 19.

---

## 3. Requirement Traceability

| No. | Requirement | Where it is implemented | Menu |
|---|---|---|---|
| 1 | Store Student ID, Name, Programme, Marks | `model/Student.java` | 1 |
| 2 | Linked list for student records | `structures/StudentLinkedList.java` | 1–4 |
| 3 | Stack for recent actions / deleted records / undo | `structures/LinkedStack.java`, `service/ActionHistory.java`, undo in `StudentRecordService` | 7, 17 |
| 4 | Queue for service requests in order of arrival | `structures/LinkedQueue.java`, `service/ServiceRequestService.java` | 5, 6, 18 |
| 5 | BST/AVL to organise and search by Student ID | `structures/StudentAVLTree.java` | 8 |
| 6 | Hashing for efficient ID search | `structures/StudentHashTable.java` | 9 (also duplicate checks) |
| 7 | Graph of campus locations and connections | `graph/CampusGraph.java` | 10–15 |
| 8 | Adjacency list or matrix | Adjacency list (plus a matrix *view*) | 14 |
| 9 | Add/remove locations and roads | `addLocation`, `removeLocation`, `addRoad`, `removeRoad` | 10–13 |
| 10 | Display connected locations / network | `displayNetwork`, `displayNeighbours`, `displayAdjacencyMatrix` | 14 |
| 11 | BFS or DFS traversal | **Both** BFS and DFS | 15 |
| 12 | Add, update, delete, search, display records | `StudentRecordService` + `ConsoleMenu` | 1–4, 9 |
| 13 | Menu-driven interface with input validation | `app/ConsoleMenu.java`, `util/InputHelper.java`, `util/Validator.java` | all |
| 14 | Handle invalid input, duplicate IDs/locations, missing records, invalid marks, unavailable connections | See section 7 | all |

## 4. Main Menu

```
 STUDENT RECORDS (Linked List)
   1. Add Student Record
   2. Update Student Record
   3. Delete Student Record
   4. Display All Records using Linked List
 SERVICE REQUESTS & HISTORY (Queue / Stack)
   5. Add Service Request to Queue
   6. Process Next Service Request
   7. Display Recent Actions using Stack
 TREE & HASHING
   8. Display Students using AVL Tree
   9. Search Student using Hashing
 CAMPUS ROUTES (Graph - Adjacency List)
  10. Add Campus Location
  11. Remove Campus Location
  12. Add Campus Connection/Road
  13. Remove Campus Connection/Road
  14. Display Campus Connections
  15. Traverse Campus Locations using BFS or DFS
  16. Exit
 EXTRA FEATURES
  17. Undo Last Student Record Action (Stack)
  18. View Pending Service Requests (Queue)
  19. Find Route Between Two Locations (BFS - fewest stops)
  20. Load Sample Data
```
Options 1–16 follow the suggested menu in the assignment brief exactly. Options 17–20 are extra features.

## 5. How to Run

**Requirement:** Java JDK 8 or newer (`javac -version` should work in a terminal).

| Platform | Run the system | Run the automated tests |
|---|---|---|
| Windows | double-click `run.bat` | double-click `test.bat` |
| macOS / Linux | `sh run.sh` | `sh test.sh` |

**Manual compile (any OS):**
```bash
javac -d out -sourcepath src src/app/Main.java
java -cp out app.Main
```

**IDE (IntelliJ IDEA / NetBeans / VS Code / Eclipse):** open the project folder, mark `src` as the *Sources Root* (and `test` as *Test Sources*), then run `app.Main`.

At start-up, answer **Y** to load sample data: 10 students, 10 campus locations, 12 roads and 3 service requests.

## 6. Project Structure

```
cit300-student-campus-system/
├── README.md
├── run.bat / run.sh              # compile and run
├── test.bat / test.sh            # compile and run all tests
├── src/
│   ├── app/
│   │   ├── Main.java             # entry point                         (Member 1)
│   │   ├── ConsoleMenu.java      # menu and input handling              (Member 1 + 4)
│   │   └── SampleData.java       # demo data                           (Member 1)
│   ├── model/
│   │   ├── Student.java          # student record                      (Member 1)
│   │   ├── Action.java           # history/undo entry                  (Member 2)
│   │   └── ServiceRequest.java   # queued request                      (Member 2)
│   ├── structures/
│   │   ├── StudentLinkedList.java                                      (Member 1)
│   │   ├── LinkedStack.java                                            (Member 2)
│   │   ├── LinkedQueue.java                                            (Member 2)
│   │   ├── StudentAVLTree.java                                         (Member 3)
│   │   └── StudentHashTable.java                                       (Member 3)
│   ├── graph/
│   │   └── CampusGraph.java                                            (Member 4)
│   ├── service/
│   │   ├── StudentRecordService.java  # keeps list/AVL/hash in sync    (Member 1)
│   │   ├── ActionHistory.java                                          (Member 2)
│   │   └── ServiceRequestService.java                                  (Member 2)
│   └── util/
│       ├── Validator.java        # validation rules                    (Member 1)
│       └── InputHelper.java      # safe console input                  (Member 1)
├── test/                         # 126 automated checks, no JUnit needed
│   ├── TestRunner.java, TestSupport.java                               (Member 1)
│   ├── StudentLinkedListTest.java, StudentRecordServiceTest.java       (Member 1)
│   ├── StackQueueTest.java                                             (Member 2)
│   ├── AvlHashTest.java                                                (Member 3)
│   └── CampusGraphTest.java                                            (Member 4)
└── docs/
    ├── modules/01..04-*.md       # one design note per member
    ├── TEST_CASES.md             # 51 manual test cases                (Member 2)
    ├── COMPLEXITY_ANALYSIS.md                                          (Member 3)
    └── USER_MANUAL.md                                                  (Member 4)
```

## 7. Input Validation and Error Handling

| Situation | System response |
|---|---|
| Non-numeric or out-of-range menu choice | `[ERROR] Invalid choice. Please enter a number from 1 to 20.` |
| Invalid Student ID format | Error, then asked again (3–15 letters/digits, optional hyphens) |
| **Duplicate Student ID** | Rejected immediately (O(1) hash check) and the existing record is shown |
| Invalid name / programme | Error, then asked again |
| **Invalid marks** (`abc`, `-5`, `150`, `1e2`) | Error, then asked again (0–100, up to 2 decimals) |
| **Missing record** (update / delete / search / request) | `[ERROR] Student ID ... was not found.` |
| Service request for an unknown student | Rejected. Only registered students can make requests |
| Processing an empty queue / undo with an empty stack | `[INFO]` message, no crash |
| **Duplicate location** (case-insensitive) | `[ERROR] Duplicate location` |
| Unknown location | Error, then asked again (the user can type the number or the name) |
| Road to the same location / **duplicate road** | Rejected with a clear message |
| Invalid distance (≤ 0, non-numeric, > 100000) | Error, then asked again |
| **Unavailable connection** (removing a road that does not exist) | `[ERROR] Connection not available: there is no direct road between ...` |
| No route between two locations | `[ERROR] No route available: ... are not connected.` |
| Unreachable locations during BFS/DFS | Listed as not reachable |
| User wants to abort | Type `cancel` at any prompt |
| Input stream closed (Ctrl+Z / Ctrl+D) | Program closes safely without a stack trace |

## 8. Data Structures Summary

| Structure | Implementation | Key operations | Complexity |
|---|---|---|---|
| Linked list | Singly linked, head and tail pointers | addLast, find, remove, insertAt | O(1) add, O(n) search/delete |
| Stack | Linked nodes, `top` | push, pop, peek | O(1) |
| Queue | Linked nodes, `front` and `rear` | enqueue, dequeue, peek | O(1) |
| AVL tree | Self-balancing BST keyed by Student ID, 4 rotation cases | insert, delete, search, 4 traversals | O(log n) |
| Hash table | Separate chaining, polynomial hash, load factor 0.75, prime resize | put, get, remove | O(1) average |
| Graph | Adjacency list (vertex array + edge linked lists), undirected, weighted | add/remove vertex and edge, BFS, DFS, route | O(V + E) traversal |

Full analysis: [`docs/COMPLEXITY_ANALYSIS.md`](docs/COMPLEXITY_ANALYSIS.md)

## 9. Testing

- **Automated:** `test.bat` / `sh test.sh` runs **126 checks** across five test classes (one or more per member). Expected output: `RESULT: 126 passed, 0 failed`.
- **Manual:** 51 console test cases covering every menu option and every error situation: [`docs/TEST_CASES.md`](docs/TEST_CASES.md)

## 10. GitHub Collaboration

Each member developed their component on a **feature branch** and opened a **pull request**. Another member reviewed it before the leader merged it into `main`.

| Branch | Member | Content |
|---|---|---|
| `main` (initial setup) | F.M. Asath | Project skeleton, `Student` model, validation and input utilities, test helper |
| `feature/stack-queue` | M.I. Thanish | LinkedStack, LinkedQueue, Action, ServiceRequest, history and request services, tests |
| `feature/linked-list` | F.M. Asath | StudentLinkedList and tests |
| `feature/avl-hashing` | J.F. Asma | StudentAVLTree, StudentHashTable and tests |
| `feature/campus-graph` | M.I.M. Naizar | CampusGraph (BFS/DFS/route) and tests |
| `feature/integration` | F.M. Asath | StudentRecordService, ConsoleMenu, Main, sample data, run scripts |
| `docs/*` | All members | Module notes, test cases, complexity analysis, user manual, README |

Repository: `https://github.com/Asath9946/cit300-student-campus-system`

## 11. Demonstration Video

Merged demonstration video (under 15 minutes, all members' faces visible): `<add video link here>`

| Time | Presenter | Content |
|---|---|---|
| 00:00 – 01:00 | All members | Introduction (names, IDs, responsibilities) |
| 01:00 – 04:00 | F.M. Asath | Linked list, add/update/delete/display, validation, undo |
| 04:00 – 06:45 | M.I. Thanish | Queue (service requests), stack (history, undo) |
| 06:45 – 09:30 | J.F. Asma | AVL tree traversals, rotations, hashing search |
| 09:30 – 12:30 | M.I.M. Naizar | Graph operations, BFS, DFS, route finder |
| 12:30 – 13:30 | F.M. Asath | Automated tests, GitHub history, conclusion |

## 12. Declaration

We declare that this project is our own group work for CIT300 Data Structures and Algorithms. Each member can explain and demonstrate their own contribution as listed above.

| Name | Student ID |
|---|---|
| F.M. Asath | 23DA2-0743 |
| M.I. Thanish | 23DA2-0897 |
| J.F. Asma | 23DA2-0645 |
| M.I.M. Naizar | 23DA2-1052 |
