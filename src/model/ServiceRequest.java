package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A student service request waiting in the queue
 * (e.g. transcript, ID card, exam re-correction).
 *
 * Owner: Member 2 - M.I. Thanish (23DA2-0897)
 */
public class ServiceRequest {

    /** The request categories offered in the menu. */
    public static final String[] REQUEST_TYPES = {
        "Transcript Request",
        "Student ID Card",
        "Exam Re-correction",
        "Hostel Facility",
        "Library Clearance",
        "Other"
    };

    private final String requestId;
    private final String studentId;
    private final String studentName;
    private final String requestType;
    private final String details;
    private final String createdAt;

    public ServiceRequest(String requestId, String studentId, String studentName,
                          String requestType, String details) {
        this.requestId = requestId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.requestType = requestType;
        this.details = details;
        this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public String getRequestId() {
        return requestId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getDetails() {
        return details;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-12s | %-20s | %-19s | %s | %s",
                requestId, studentId, studentName, requestType, createdAt, details);
    }
}
