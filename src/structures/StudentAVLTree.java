package structures;

import model.Student;

/**
 * Self-balancing AVL tree that organises student records by Student ID
 * (Assignment requirement 5).
 *
 * Rule: for every node, |height(left) - height(right)| <= 1.
 * After each insert/delete the tree re-balances itself with rotations:
 *
 *   LL case -> single right rotation
 *   RR case -> single left rotation
 *   LR case -> left rotation on child, then right rotation
 *   RL case -> right rotation on child, then left rotation
 *
 * Because the tree stays balanced, its height is O(log n), so
 * insert, delete and search are all O(log n) - even when IDs are added in
 * sorted order (a plain BST would become a slow O(n) "linked list" then).
 *
 * In-order traversal visits IDs in ascending (sorted) order.
 *
 * Owner: Member 3 - J.F. Asma (23DA2-0645)
 */
public class StudentAVLTree {

    /** One node of the AVL tree. */
    private static class Node {
        private Student data;
        private Node left;
        private Node right;
        private int height;

        Node(Student data) {
            this.data = data;
            this.height = 1; // a new node is a leaf
        }
    }

    /** Lets callers decide what to do with each visited student (e.g. print it). */
    public interface StudentVisitor {
        void visit(Student student);
    }

    private Node root;
    private int size;
    private final StringBuilder rotationLog = new StringBuilder();

    // ------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int compare(String id1, String id2) {
        return id1.compareToIgnoreCase(id2);
    }

    /**
     *        y                x
     *       / \              / \
     *      x   T3   --->    T1  y
     *     / \                  / \
     *    T1  T2               T2  T3
     */
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;
        x.right = y;
        y.left = t2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    /**
     *      x                    y
     *     / \                  / \
     *    T1  y      --->      x   T3
     *       / \              / \
     *      T2  T3           T1  T2
     */
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t2 = y.left;
        y.left = x;
        x.right = t2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    /** Checks the balance of a node and applies the correct rotation if needed. */
    private Node rebalance(Node node) {
        updateHeight(node);
        int balance = balanceFactor(node);

        if (balance > 1) { // left heavy
            if (balanceFactor(node.left) < 0) {
                logRotation("LR", node);
                node.left = rotateLeft(node.left);
            } else {
                logRotation("LL", node);
            }
            return rotateRight(node);
        }
        if (balance < -1) { // right heavy
            if (balanceFactor(node.right) > 0) {
                logRotation("RL", node);
                node.right = rotateRight(node.right);
            } else {
                logRotation("RR", node);
            }
            return rotateLeft(node);
        }
        return node; // already balanced
    }

    private void logRotation(String type, Node node) {
        if (rotationLog.length() > 0) {
            rotationLog.append(", ");
        }
        rotationLog.append(type).append(" rotation at ").append(node.data.getStudentId());
    }

    /** Describes the rotations done by the most recent insert/delete ("" if none). */
    public String getLastRotations() {
        return rotationLog.toString();
    }

    // ------------------------------------------------------------------
    // Insert
    // ------------------------------------------------------------------

    /** Inserts a student. Returns false if the Student ID already exists. */
    public boolean insert(Student student) {
        rotationLog.setLength(0);
        if (contains(student.getStudentId())) {
            return false;
        }
        root = insert(root, student);
        size++;
        return true;
    }

    private Node insert(Node node, Student student) {
        if (node == null) {
            return new Node(student);
        }
        int cmp = compare(student.getStudentId(), node.data.getStudentId());
        if (cmp < 0) {
            node.left = insert(node.left, student);
        } else {
            node.right = insert(node.right, student);
        }
        return rebalance(node);
    }

    // ------------------------------------------------------------------
    // Delete
    // ------------------------------------------------------------------

    /** Deletes a student by ID. Returns false if the ID is not in the tree. */
    public boolean delete(String studentId) {
        rotationLog.setLength(0);
        if (!contains(studentId)) {
            return false;
        }
        root = delete(root, studentId);
        size--;
        return true;
    }

    private Node delete(Node node, String studentId) {
        if (node == null) {
            return null;
        }
        int cmp = compare(studentId, node.data.getStudentId());
        if (cmp < 0) {
            node.left = delete(node.left, studentId);
        } else if (cmp > 0) {
            node.right = delete(node.right, studentId);
        } else {
            // Found the node to delete
            if (node.left == null) {
                return node.right;      // 0 or 1 child (right)
            }
            if (node.right == null) {
                return node.left;       // 1 child (left)
            }
            // 2 children: copy the in-order successor (smallest in right subtree)
            Node successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.data = successor.data;
            node.right = delete(node.right, successor.data.getStudentId());
        }
        return rebalance(node);
    }

    // ------------------------------------------------------------------
    // Search
    // ------------------------------------------------------------------

    /** Binary search by Student ID - O(log n). Returns null if not found. */
    public Student search(String studentId) {
        Node current = root;
        while (current != null) {
            int cmp = compare(studentId, current.data.getStudentId());
            if (cmp == 0) {
                return current.data;
            }
            current = cmp < 0 ? current.left : current.right;
        }
        return null;
    }

    /** Returns the IDs visited while searching, e.g. "23DA2-0500 -> 23DA2-0300 -> 23DA2-0350". */
    public String searchPath(String studentId) {
        StringBuilder path = new StringBuilder();
        Node current = root;
        while (current != null) {
            if (path.length() > 0) {
                path.append(" -> ");
            }
            path.append(current.data.getStudentId());
            int cmp = compare(studentId, current.data.getStudentId());
            if (cmp == 0) {
                return path.append("  (FOUND)").toString();
            }
            current = cmp < 0 ? current.left : current.right;
        }
        return path.append(path.length() > 0 ? " -> null  (NOT FOUND)" : "empty tree  (NOT FOUND)").toString();
    }

    public boolean contains(String studentId) {
        return search(studentId) != null;
    }

    // ------------------------------------------------------------------
    // Traversals
    // ------------------------------------------------------------------

    /** Left - Root - Right : gives students sorted by ID. */
    public void inOrder(StudentVisitor visitor) {
        inOrder(root, visitor);
    }

    private void inOrder(Node node, StudentVisitor visitor) {
        if (node != null) {
            inOrder(node.left, visitor);
            visitor.visit(node.data);
            inOrder(node.right, visitor);
        }
    }

    /** Root - Left - Right */
    public void preOrder(StudentVisitor visitor) {
        preOrder(root, visitor);
    }

    private void preOrder(Node node, StudentVisitor visitor) {
        if (node != null) {
            visitor.visit(node.data);
            preOrder(node.left, visitor);
            preOrder(node.right, visitor);
        }
    }

    /** Left - Right - Root */
    public void postOrder(StudentVisitor visitor) {
        postOrder(root, visitor);
    }

    private void postOrder(Node node, StudentVisitor visitor) {
        if (node != null) {
            postOrder(node.left, visitor);
            postOrder(node.right, visitor);
            visitor.visit(node.data);
        }
    }

    /** Level by level, top to bottom. Uses our own LinkedQueue. */
    public void levelOrder(StudentVisitor visitor) {
        if (root == null) {
            return;
        }
        LinkedQueue<Node> queue = new LinkedQueue<Node>();
        queue.enqueue(root);
        while (!queue.isEmpty()) {
            Node node = queue.dequeue();
            visitor.visit(node.data);
            if (node.left != null) {
                queue.enqueue(node.left);
            }
            if (node.right != null) {
                queue.enqueue(node.right);
            }
        }
    }

    /**
     * Prints the tree sideways (root on the left, right subtree on top).
     * Each node shows its ID and balance factor (bf).
     */
    public void printStructure() {
        if (root == null) {
            System.out.println("(empty tree)");
            return;
        }
        printStructure(root, "", true);
    }

    private void printStructure(Node node, String prefix, boolean isTail) {
        if (node.right != null) {
            printStructure(node.right, prefix + (isTail ? "|   " : "    "), false);
        }
        System.out.println(prefix + (isTail ? "\\-- " : "/-- ")
                + node.data.getStudentId() + " (bf=" + balanceFactor(node) + ")");
        if (node.left != null) {
            printStructure(node.left, prefix + (isTail ? "    " : "|   "), true);
        }
    }

    /** Verifies the AVL property and BST ordering for every node (used by tests). */
    public boolean isBalanced() {
        return checkBalanced(root, null, null);
    }

    private boolean checkBalanced(Node node, String min, String max) {
        if (node == null) {
            return true;
        }
        String id = node.data.getStudentId();
        if ((min != null && compare(id, min) <= 0) || (max != null && compare(id, max) >= 0)) {
            return false;
        }
        if (Math.abs(balanceFactor(node)) > 1) {
            return false;
        }
        return checkBalanced(node.left, min, id) && checkBalanced(node.right, id, max);
    }

    public String getRootId() {
        return root == null ? "-" : root.data.getStudentId();
    }

    public int getHeight() {
        return height(root);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
