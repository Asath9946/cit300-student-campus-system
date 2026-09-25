# Module 1 – Linked List and Student Record Management

**Owner:** F.M. Asath (23DA2-0743) – Group Leader
**Files:** `src/structures/StudentLinkedList.java`, `src/service/StudentRecordService.java`, `src/app/ConsoleMenu.java`, `src/app/Main.java`, `src/app/SampleData.java`, `src/model/Student.java`, `src/util/Validator.java`, `src/util/InputHelper.java`
**Menu options:** 1, 2, 3, 4, 17 (undo), 20 (sample data)

## 1. What the linked list does

`StudentLinkedList` is a **singly linked list** written from scratch (no `java.util.LinkedList`). It is the main storage for student records and keeps them in the order they were entered.

```
head -> [23DA2-0512] -> [23DA2-0233] -> [23DA2-0871] -> null
                                          ^ tail
```

Each `Node` holds one `Student` and a `next` reference. A `tail` reference is kept so that adding at the end is O(1).

| Method | What it does | Time |
|---|---|---|
| `addLast(Student)` | Adds a node after the tail | O(1) |
| `insertAt(index, Student)` | Inserts at a position (used by undo-delete) | O(n) |
| `findById(id)` | Walks from head until the ID matches | O(n) |
| `indexOf(id)` | Position of a record (remembered before delete) | O(n) |
| `removeById(id)` | Handles 3 cases: head, middle, tail | O(n) |
| `display()` | Traverses head to tail and prints a table | O(n) |
| `averageMarks()`, `highestScorer()` | Single traversal summaries | O(n) |

### Delete: the three cases
1. **Head:** `head = head.next` (and `tail = null` if the list becomes empty).
2. **Middle:** find the node *before* the target, then `previous.next = previous.next.next`.
3. **Tail:** same as middle, and also move `tail` back to `previous`.

## 2. Student record management (integration)

`StudentRecordService` keeps **three structures in sync**: the linked list, the AVL tree (Member 3) and the hash table (Member 3). All three hold **references to the same `Student` object**, so:

- **Add:** duplicate check with the hash table (O(1)), then insert into all three.
- **Update:** change the shared object once and every structure sees the new values. The Student ID cannot be changed because it is the key in the tree and the hash table.
- **Delete:** remove from all three, and push a copy onto the undo stack together with its old list position.
- **Undo (option 17):** pops the undo stack (Member 2's `LinkedStack`) and reverses the last add, update or delete. An undone delete goes back to its **original position** in the list.

## 3. Validation (requirement 14)

| Input | Rule | Error shown |
|---|---|---|
| Student ID | 3–15 letters/digits, optional single hyphens, stored upper-case | Invalid Student ID… |
| Duplicate ID | Checked with the hash table before asking other fields | Duplicate ID: … already exists |
| Name | 2–50 letters, spaces, `.` `'` `-`; no digits | Invalid name… |
| Programme | 2–60 characters, starts with a letter | Invalid programme… |
| Marks | Number 0–100, up to 2 decimals (`1e2`, `NaN`, `abc` rejected) | Marks must be a number from 0 to 100… |
| Missing record | Update/delete/request with unknown ID | Student ID … was not found |
| Menu choice | Whole number 1–20 | Invalid choice… |

Typing `cancel` at any prompt returns to the menu, and closing the input stream exits safely instead of crashing.

## 4. Tests
`test/StudentLinkedListTest.java` and `test/StudentRecordServiceTest.java` cover add, find, delete of head/middle/tail, `insertAt`, sync across the three structures, duplicate and invalid-marks rejection, and undo of add, update and delete.
