import model.Student;
import structures.StudentAVLTree;
import structures.StudentHashTable;

/**
 * Tests for the AVL tree and the hash table.
 *
 * Owner: Member 3 - J.F. Asma (23DA2-0645)
 */
public final class AvlHashTest {

    private AvlHashTest() {
    }

    private static Student s(String id) {
        return new Student(id, "Student " + id, "BAIT", 60);
    }

    public static void run() {
        TestSupport.section("Member 3 - StudentAVLTree");
        StudentAVLTree tree = new StudentAVLTree();

        // Sorted insertion would make a plain BST a straight line; AVL must stay balanced.
        for (int i = 1; i <= 15; i++) {
            tree.insert(s(String.format("ID%03d", i)));
            if (!tree.isBalanced()) {
                TestSupport.check(false, "tree balanced after inserting ID" + i);
            }
        }
        TestSupport.check(tree.isBalanced(), "15 sorted inserts: tree stays balanced");
        TestSupport.checkEquals(4, tree.getHeight(), "height of 15 nodes is 4 (perfectly balanced)");
        TestSupport.checkEquals("ID008", tree.getRootId(), "root is the middle ID (ID008)");
        TestSupport.check(!tree.insert(s("ID005")), "duplicate ID rejected by AVL tree");

        final StringBuilder inOrder = new StringBuilder();
        tree.inOrder(student -> inOrder.append(student.getStudentId()).append(' '));
        TestSupport.check(inOrder.toString().startsWith("ID001 ID002 ID003")
                && inOrder.toString().trim().endsWith("ID015"), "in-order traversal is sorted");

        final StringBuilder level = new StringBuilder();
        tree.levelOrder(student -> level.append(student.getStudentId()).append(' '));
        TestSupport.check(level.toString().startsWith("ID008 ID004 ID012"), "level-order starts root, left, right");

        // specific rotation cases
        StudentAVLTree ll = new StudentAVLTree();
        ll.insert(s("C"));
        ll.insert(s("B"));
        ll.insert(s("A"));
        TestSupport.check(ll.getLastRotations().startsWith("LL"), "LL case detected -> right rotation");
        TestSupport.checkEquals("B", ll.getRootId(), "LL case: B becomes root");

        StudentAVLTree lr = new StudentAVLTree();
        lr.insert(s("C"));
        lr.insert(s("A"));
        lr.insert(s("B"));
        TestSupport.check(lr.getLastRotations().startsWith("LR"), "LR case detected -> double rotation");
        TestSupport.checkEquals("B", lr.getRootId(), "LR case: B becomes root");

        StudentAVLTree rl = new StudentAVLTree();
        rl.insert(s("A"));
        rl.insert(s("C"));
        rl.insert(s("B"));
        TestSupport.check(rl.getLastRotations().startsWith("RL"), "RL case detected -> double rotation");

        // delete leaf, one-child and two-children nodes
        TestSupport.check(tree.delete("ID001"), "delete leaf node");
        TestSupport.check(tree.delete("ID008"), "delete root with two children");
        TestSupport.check(!tree.contains("ID008"), "deleted ID no longer found");
        TestSupport.check(tree.isBalanced(), "tree balanced after deletions");
        TestSupport.check(!tree.delete("ID999"), "deleting a missing ID returns false");
        TestSupport.checkEquals(13, tree.size(), "size after 2 deletions is 13");
        for (int i = 2; i <= 15; i++) {
            tree.delete(String.format("ID%03d", i));
        }
        TestSupport.check(tree.isEmpty() && tree.isBalanced(), "tree empty after deleting everything");
        TestSupport.check(tree.searchPath("X").contains("NOT FOUND"), "search path on empty tree says NOT FOUND");

        TestSupport.section("Member 3 - StudentHashTable");
        StudentHashTable table = new StudentHashTable(5);
        boolean allAdded = true;
        for (int i = 1; i <= 20; i++) {
            allAdded = table.put(s(String.format("23DA2-%04d", i))) && allAdded;
        }
        TestSupport.check(allAdded, "put 20 unique records");
        TestSupport.checkEquals(20, table.size(), "20 records stored");
        TestSupport.check(table.getLoadFactor() <= 0.75, "resizing keeps load factor <= 0.75");
        TestSupport.check(table.getCapacity() > 5, "table grew from its initial capacity of 5");
        TestSupport.check(!table.put(s("23DA2-0007")), "duplicate ID rejected by hash table");
        TestSupport.checkEquals("23DA2-0013", table.get("23da2-0013").getStudentId(), "get is case-insensitive");
        StudentHashTable.SearchResult result = table.search("23DA2-0010");
        TestSupport.check(result.getStudent() != null && result.getBucketIndex() == table.hash("23DA2-0010"),
                "search reports the correct bucket");
        TestSupport.check(table.get("NOPE-1") == null, "missing ID returns null");
        TestSupport.checkEquals("23DA2-0005", table.remove("23DA2-0005").getStudentId(), "remove returns removed record");
        TestSupport.check(table.remove("23DA2-0005") == null, "removing again returns null");
        TestSupport.checkEquals(19, table.size(), "size is 19 after removal");

        // collisions: keys that land in the same bucket must all stay retrievable
        StudentHashTable small = new StudentHashTable(11);
        String first = "C100";
        String collider = null;
        for (int i = 101; i < 999 && collider == null; i++) {
            if (small.hash("C" + i) == small.hash(first)) {
                collider = "C" + i;
            }
        }
        small.put(s(first));
        small.put(s(collider));
        TestSupport.checkEquals(1, small.countCollisionBuckets(), first + " and " + collider + " collide in one bucket");
        TestSupport.check(small.get(first) != null && small.get(collider) != null,
                "both colliding records found via chaining");
        small.remove(first);
        TestSupport.check(small.get(collider) != null, "chain intact after removing one colliding record");
    }
}
