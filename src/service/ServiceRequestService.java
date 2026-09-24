package service;

import model.Action;
import model.ServiceRequest;
import model.Student;
import structures.LinkedQueue;
import util.Validator;

/**
 * Manages student service requests with a queue so they are handled
 * strictly in order of arrival (First In, First Out).
 *
 * Owner: Member 2 - M.I. Thanish (23DA2-0897)
 */
public class ServiceRequestService {

    private final LinkedQueue<ServiceRequest> queue = new LinkedQueue<ServiceRequest>();
    private final ActionHistory history;
    private int nextRequestNumber = 1;
    private int processedCount;

    public ServiceRequestService(ActionHistory history) {
        this.history = history;
    }

    /** Creates a request for an existing student and adds it to the rear of the queue. */
    public ServiceRequest addRequest(Student student, String requestType, String details) {
        if (student == null) {
            throw new IllegalArgumentException("A service request needs an existing student.");
        }
        String requestId = String.format("SR%03d", nextRequestNumber++);
        ServiceRequest request = new ServiceRequest(requestId, student.getStudentId(),
                student.getName(), requestType, details);
        queue.enqueue(request);
        history.record(Action.Type.ADD_REQUEST, requestId + " (" + requestType + ") for "
                + student.getStudentId() + " added to queue");
        return request;
    }

    /** Removes and returns the request at the front of the queue, or null if the queue is empty. */
    public ServiceRequest processNext() {
        if (queue.isEmpty()) {
            return null;
        }
        ServiceRequest request = queue.dequeue();
        processedCount++;
        history.record(Action.Type.PROCESS_REQUEST, request.getRequestId() + " ("
                + request.getRequestType() + ") for " + request.getStudentId() + " processed");
        return request;
    }

    /** Returns the next request without removing it (null if empty). */
    public ServiceRequest peekNext() {
        return queue.isEmpty() ? null : queue.peek();
    }

    /** Prints all waiting requests from front (next) to rear (last). */
    public void displayPending() {
        if (queue.isEmpty()) {
            System.out.println("No pending service requests. The queue is empty.");
            return;
        }
        String line = Validator.repeat('-', 110);
        System.out.println("Pending service requests (FRONT = next to be processed): " + queue.size());
        System.out.println(line);
        System.out.println(String.format("%-7s | %-6s | %-12s | %-20s | %-19s | %-19s | %s",
                "Pos", "Req ID", "Student ID", "Student Name", "Request Type", "Received At", "Details"));
        System.out.println(line);
        Object[] requests = queue.toArrayFrontFirst();
        for (int i = 0; i < requests.length; i++) {
            ServiceRequest r = (ServiceRequest) requests[i];
            String position = i == 0 ? "FRONT" : (i == requests.length - 1 ? "REAR" : String.valueOf(i + 1));
            String name = r.getStudentName().length() > 20
                    ? r.getStudentName().substring(0, 17) + "..." : r.getStudentName();
            System.out.println(String.format("%-7s | %-6s | %-12s | %-20s | %-19s | %-19s | %s",
                    position, r.getRequestId(), r.getStudentId(), name,
                    r.getRequestType(), r.getCreatedAt(), r.getDetails()));
        }
        System.out.println(line);
    }

    public int pendingCount() {
        return queue.size();
    }

    public int getProcessedCount() {
        return processedCount;
    }
}
