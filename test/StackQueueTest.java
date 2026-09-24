import model.Student;
import service.ActionHistory;
import service.ServiceRequestService;
import structures.LinkedQueue;
import structures.LinkedStack;

/**
 * Tests for the stack, the queue and the service request queue.
 *
 * Owner: Member 2 - M.I. Thanish (23DA2-0897)
 */
public final class StackQueueTest {

    private StackQueueTest() {
    }

    public static void run() {
        TestSupport.section("Member 2 - LinkedStack (LIFO)");
        final LinkedStack<String> stack = new LinkedStack<String>();
        TestSupport.check(stack.isEmpty(), "new stack is empty");
        stack.push("A");
        stack.push("B");
        stack.push("C");
        TestSupport.checkEquals("C", stack.peek(), "peek returns last pushed item");
        TestSupport.checkEquals(3, stack.size(), "size is 3");
        TestSupport.checkEquals("C", stack.pop(), "pop returns C first (LIFO)");
        TestSupport.checkEquals("B", stack.pop(), "then B");
        TestSupport.checkEquals("A", stack.pop(), "then A");
        TestSupport.checkThrows(IllegalStateException.class, new Runnable() {
            public void run() {
                stack.pop();
            }
        }, "pop on empty stack throws (stack underflow)");

        TestSupport.section("Member 2 - LinkedQueue (FIFO)");
        final LinkedQueue<Integer> queue = new LinkedQueue<Integer>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        TestSupport.checkEquals(1, queue.peek(), "peek returns first item");
        TestSupport.checkEquals(1, queue.dequeue(), "dequeue returns 1 first (FIFO)");
        queue.enqueue(4);
        TestSupport.checkEquals(2, queue.dequeue(), "then 2");
        TestSupport.checkEquals(3, queue.dequeue(), "then 3");
        TestSupport.checkEquals(4, queue.dequeue(), "then 4");
        TestSupport.check(queue.isEmpty(), "queue empty after all dequeues");
        queue.enqueue(5);
        TestSupport.checkEquals(5, queue.peek(), "rear reset correctly after emptying");
        queue.dequeue();
        TestSupport.checkThrows(IllegalStateException.class, new Runnable() {
            public void run() {
                queue.dequeue();
            }
        }, "dequeue on empty queue throws (queue underflow)");

        TestSupport.section("Member 2 - ServiceRequestService and ActionHistory");
        ActionHistory history = new ActionHistory();
        ServiceRequestService requests = new ServiceRequestService(history);
        Student s1 = new Student("S001", "Amal", "BAIT", 70);
        Student s2 = new Student("S002", "Bimal", "BAIT", 80);
        requests.addRequest(s1, "Transcript Request", "first");
        requests.addRequest(s2, "Student ID Card", "second");
        TestSupport.checkEquals(2, requests.pendingCount(), "two requests pending");
        TestSupport.checkEquals("SR001", requests.processNext().getRequestId(), "first request in is processed first");
        TestSupport.checkEquals("SR002", requests.processNext().getRequestId(), "second request processed next");
        TestSupport.check(requests.processNext() == null, "processing an empty queue returns null");
        TestSupport.checkEquals(4, history.size(), "history stack recorded 2 adds + 2 processes");
        TestSupport.check(history.latest().getDescription().contains("SR002"), "top of history is the latest action");
    }
}
