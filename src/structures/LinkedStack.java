package structures;

/**
 * Generic stack (Last In, First Out) built on linked nodes
 * (Assignment requirement 3).
 *
 * Used for:
 *   - the recent-actions history (menu option 7)
 *   - the undo feature for student records (menu option 17)
 *   - iterative Depth-First Search in the campus graph
 *
 *   top -> [newest] -> [older] -> [oldest] -> null
 *
 * Time complexity: push O(1), pop O(1), peek O(1)
 *
 * Owner: Member 2 - M.I. Thanish (23DA2-0897)
 *
 * @param <T> type of the items stored in the stack
 */
public class LinkedStack<T> {

    private static class Node<T> {
        private final T data;
        private Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> top;
    private int size;

    /** Puts an item on top of the stack. */
    public void push(T item) {
        Node<T> newNode = new Node<T>(item);
        newNode.next = top;
        top = newNode;
        size++;
    }

    /** Removes and returns the top item. */
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack underflow: the stack is empty.");
        }
        T item = top.data;
        top = top.next;
        size--;
        return item;
    }

    /** Returns the top item without removing it. */
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("The stack is empty.");
        }
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    /** Returns the items from top (most recent) to bottom (oldest). */
    public Object[] toArrayTopFirst() {
        Object[] result = new Object[size];
        Node<T> current = top;
        int i = 0;
        while (current != null) {
            result[i++] = current.data;
            current = current.next;
        }
        return result;
    }
}
