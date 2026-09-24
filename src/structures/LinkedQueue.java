package structures;

/**
 * Generic queue (First In, First Out) built on linked nodes
 * (Assignment requirement 4).
 *
 * Used for:
 *   - student service requests, processed in order of arrival (options 5, 6, 18)
 *   - Breadth-First Search in the campus graph
 *   - level-order traversal of the AVL tree
 *
 *   front -> [first in] -> [second] -> [last in] <- rear
 *
 * Time complexity: enqueue O(1), dequeue O(1), peek O(1)
 *
 * Owner: Member 2 - M.I. Thanish (23DA2-0897)
 *
 * @param <T> type of the items stored in the queue
 */
public class LinkedQueue<T> {

    private static class Node<T> {
        private final T data;
        private Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> front;
    private Node<T> rear;
    private int size;

    /** Adds an item at the rear of the queue. */
    public void enqueue(T item) {
        Node<T> newNode = new Node<T>(item);
        if (rear == null) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    /** Removes and returns the item at the front of the queue. */
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue underflow: the queue is empty.");
        }
        T item = front.data;
        front = front.next;
        if (front == null) {
            rear = null; // queue became empty
        }
        size--;
        return item;
    }

    /** Returns the front item without removing it. */
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("The queue is empty.");
        }
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    /** Returns the items from front (next to be served) to rear. */
    public Object[] toArrayFrontFirst() {
        Object[] result = new Object[size];
        Node<T> current = front;
        int i = 0;
        while (current != null) {
            result[i++] = current.data;
            current = current.next;
        }
        return result;
    }
}
