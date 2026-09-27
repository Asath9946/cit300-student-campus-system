package structures;

import model.Student;

/**
 * Hash table for fast Student ID searching (Assignment requirement 6).
 *
 * Technique: SEPARATE CHAINING
 *   - an array of buckets; each bucket is a small linked list of entries
 *   - students whose IDs hash to the same index (a collision) share a bucket
 *
 * Hash function (polynomial rolling hash, like Java's String.hashCode):
 *   h = 0
 *   for each character c in the ID:  h = 31 * h + c
 *   index = |h| mod capacity
 *
 * The table grows (rehashes into a larger prime-sized array) when the
 * load factor (size / capacity) goes above 0.75, which keeps chains short.
 *
 * Average time complexity: put O(1), get O(1), remove O(1)
 * Worst case (every key in one bucket): O(n)
 *
 * Owner: Member 3 - J.F. Asma (23DA2-0645)
 */
public class StudentHashTable {

    private static final int DEFAULT_CAPACITY = 11;
    private static final double MAX_LOAD_FACTOR = 0.75;

    /** One entry in a bucket chain. */
    private static class Entry {
        private final String key;
        private final Student value;
        private Entry next;

        Entry(String key, Student value) {
            this.key = key;
            this.value = value;
        }
    }

    /** Result of a detailed search - shows how hashing found the record. */
    public static class SearchResult {
        private final Student student;
        private final int bucketIndex;
        private final int comparisons;

        SearchResult(Student student, int bucketIndex, int comparisons) {
            this.student = student;
            this.bucketIndex = bucketIndex;
            this.comparisons = comparisons;
        }

        public Student getStudent() {
            return student;
        }

        public int getBucketIndex() {
            return bucketIndex;
        }

        public int getComparisons() {
            return comparisons;
        }
    }

    private Entry[] buckets;
    private int size;
    private int resizeCount;

    public StudentHashTable() {
        this(DEFAULT_CAPACITY);
    }

    public StudentHashTable(int initialCapacity) {
        buckets = new Entry[Math.max(3, initialCapacity)];
    }

    /** Converts a Student ID into a bucket index. Keys are compared in upper case. */
    public int hash(String key) {
        String normalized = key.toUpperCase();
        int h = 0;
        for (int i = 0; i < normalized.length(); i++) {
            h = 31 * h + normalized.charAt(i);
        }
        return (h & 0x7fffffff) % buckets.length; // remove sign bit, then mod
    }

    /** Adds a student. Returns false if the ID already exists (duplicates are rejected). */
    public boolean put(Student student) {
        String key = student.getStudentId().toUpperCase();
        if (containsKey(key)) {
            return false;
        }
        if ((double) (size + 1) / buckets.length > MAX_LOAD_FACTOR) {
            resize();
        }
        int index = hash(key);
        Entry entry = new Entry(key, student);
        entry.next = buckets[index]; // insert at the front of the chain
        buckets[index] = entry;
        size++;
        return true;
    }

    /** Returns the student with this ID, or null if not found. */
    public Student get(String studentId) {
        return search(studentId).getStudent();
    }

    /** Search that also reports the bucket index and number of comparisons. */
    public SearchResult search(String studentId) {
        String key = studentId.toUpperCase();
        int index = hash(key);
        int comparisons = 0;
        Entry current = buckets[index];
        while (current != null) {
            comparisons++;
            if (current.key.equals(key)) {
                return new SearchResult(current.value, index, comparisons);
            }
            current = current.next;
        }
        return new SearchResult(null, index, comparisons);
    }

    public boolean containsKey(String studentId) {
        return get(studentId) != null;
    }

    /** Removes a student by ID. Returns the removed student, or null if not found. */
    public Student remove(String studentId) {
        String key = studentId.toUpperCase();
        int index = hash(key);
        Entry current = buckets[index];
        Entry previous = null;
        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    /** Doubles the capacity (to the next prime) and re-inserts every entry. */
    private void resize() {
        Entry[] oldBuckets = buckets;
        buckets = new Entry[nextPrime(oldBuckets.length * 2)];
        for (Entry head : oldBuckets) {
            Entry current = head;
            while (current != null) {
                Entry next = current.next;
                int index = hash(current.key);
                current.next = buckets[index];
                buckets[index] = current;
                current = next;
            }
        }
        resizeCount++;
    }

    private static int nextPrime(int number) {
        int candidate = Math.max(2, number);
        while (!isPrime(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        for (int i = 2; (long) i * i <= number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /** Prints every bucket and its chain, e.g. [ 3] -> 23DA2-0101 -> 23DA2-0412 */
    public void printTable() {
        System.out.println("Capacity: " + buckets.length + " | Records: " + size
                + " | Load factor: " + String.format("%.2f", getLoadFactor())
                + " | Collisions (buckets with >1 record): " + countCollisionBuckets()
                + " | Resizes: " + resizeCount);
        for (int i = 0; i < buckets.length; i++) {
            StringBuilder line = new StringBuilder(String.format("[%2d]", i));
            Entry current = buckets[i];
            if (current == null) {
                line.append(" -> (empty)");
            }
            while (current != null) {
                line.append(" -> ").append(current.key);
                current = current.next;
            }
            System.out.println(line);
        }
    }

    public int countCollisionBuckets() {
        int count = 0;
        for (Entry head : buckets) {
            if (head != null && head.next != null) {
                count++;
            }
        }
        return count;
    }

    public double getLoadFactor() {
        return (double) size / buckets.length;
    }

    public int getCapacity() {
        return buckets.length;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
