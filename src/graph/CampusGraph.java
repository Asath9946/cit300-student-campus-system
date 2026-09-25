package graph;

import structures.LinkedQueue;
import structures.LinkedStack;
import util.Validator;

/**
 * Campus map as an UNDIRECTED, WEIGHTED graph using an ADJACENCY LIST
 * (Assignment requirements 7 - 11).
 *
 *   Vertices = campus locations (Library, Cafeteria, ...)
 *   Edges    = roads/paths between two locations, weighted by distance (metres)
 *
 * Storage: an array of vertices. Every vertex owns a linked list of edge
 * nodes (its neighbours). Because roads are two-way, each road is stored
 * twice: A -> B in A's list and B -> A in B's list.
 *
 *   [0] Main Gate      -> Admin Building(120m) -> Car Park(80m) -> null
 *   [1] Admin Building -> Main Gate(120m) -> Library(150m) -> null
 *   ...
 *
 * Why an adjacency list? A campus is a SPARSE graph (each place connects to
 * only a few others), so a list uses O(V + E) memory instead of the O(V^2)
 * needed by an adjacency matrix.
 *
 * Location names are matched case-insensitively ("library" == "Library").
 *
 * Time complexity (V = locations, E = roads):
 *   addLocation      O(V)      (duplicate check)
 *   removeLocation   O(V + E)  (must delete every road pointing to it)
 *   addRoad          O(V + deg)
 *   removeRoad       O(V + deg)
 *   BFS / DFS        O(V + E)
 *
 * Owner: Member 4 - M.I.M. Naizar (23DA2-1052)
 */
public class CampusGraph {

    /** One neighbour in a location's adjacency list (a road to another location). */
    private static class EdgeNode {
        private final String destination;
        private final int distance;
        private EdgeNode next;

        EdgeNode(String destination, int distance) {
            this.destination = destination;
            this.distance = distance;
        }
    }

    /** One campus location and the head of its adjacency list. */
    private static class Vertex {
        private final String name;
        private EdgeNode head;
        private int degree;

        Vertex(String name) {
            this.name = name;
        }
    }

    private Vertex[] vertices = new Vertex[10];
    private int vertexCount;
    private int roadCount;

    // ------------------------------------------------------------------
    // Locations (vertices)
    // ------------------------------------------------------------------

    /** Index of a location in the vertex array, or -1 if it does not exist. */
    private int indexOf(String name) {
        for (int i = 0; i < vertexCount; i++) {
            if (vertices[i].name.equalsIgnoreCase(name.trim())) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasLocation(String name) {
        return indexOf(name) != -1;
    }

    /** Returns the stored spelling of a location (e.g. "library" -> "Library"). */
    public String getLocationName(String name) {
        int index = indexOf(name);
        return index == -1 ? null : vertices[index].name;
    }

    /** Adds a new location. Returns false if it already exists. */
    public boolean addLocation(String name) {
        String cleanName = Validator.cleanText(name);
        if (hasLocation(cleanName)) {
            return false;
        }
        if (vertexCount == vertices.length) {
            Vertex[] bigger = new Vertex[vertices.length * 2];
            System.arraycopy(vertices, 0, bigger, 0, vertexCount);
            vertices = bigger;
        }
        vertices[vertexCount++] = new Vertex(cleanName);
        return true;
    }

    /**
     * Removes a location and every road connected to it.
     * Returns the number of roads that were removed, or -1 if the location does not exist.
     */
    public int removeLocation(String name) {
        int index = indexOf(name);
        if (index == -1) {
            return -1;
        }
        Vertex target = vertices[index];
        int removedRoads = target.degree;

        // 1) remove the reverse edge (neighbour -> target) from every neighbour
        EdgeNode edge = target.head;
        while (edge != null) {
            Vertex neighbour = vertices[indexOf(edge.destination)];
            removeEdgeFromList(neighbour, target.name);
            edge = edge.next;
        }
        roadCount -= removedRoads;

        // 2) remove the vertex itself by shifting the array left
        for (int i = index; i < vertexCount - 1; i++) {
            vertices[i] = vertices[i + 1];
        }
        vertices[--vertexCount] = null;
        return removedRoads;
    }

    // ------------------------------------------------------------------
    // Roads (edges)
    // ------------------------------------------------------------------

    public boolean hasRoad(String from, String to) {
        int index = indexOf(from);
        if (index == -1) {
            return false;
        }
        return findEdge(vertices[index], to) != null;
    }

    /** Distance of the road between two locations, or -1 if there is no road. */
    public int getDistance(String from, String to) {
        int index = indexOf(from);
        if (index == -1) {
            return -1;
        }
        EdgeNode edge = findEdge(vertices[index], to);
        return edge == null ? -1 : edge.distance;
    }

    /**
     * Adds a two-way road. Returns false if either location is missing,
     * both are the same location, or the road already exists.
     */
    public boolean addRoad(String from, String to, int distance) {
        int a = indexOf(from);
        int b = indexOf(to);
        if (a == -1 || b == -1 || a == b || findEdge(vertices[a], to) != null) {
            return false;
        }
        addEdgeToList(vertices[a], vertices[b].name, distance);
        addEdgeToList(vertices[b], vertices[a].name, distance);
        roadCount++;
        return true;
    }

    /** Removes a two-way road. Returns false if the road does not exist. */
    public boolean removeRoad(String from, String to) {
        int a = indexOf(from);
        int b = indexOf(to);
        if (a == -1 || b == -1 || findEdge(vertices[a], to) == null) {
            return false;
        }
        removeEdgeFromList(vertices[a], vertices[b].name);
        removeEdgeFromList(vertices[b], vertices[a].name);
        roadCount--;
        return true;
    }

    /** Adds an edge node at the END of a vertex's list (keeps insertion order for display). */
    private void addEdgeToList(Vertex vertex, String destination, int distance) {
        EdgeNode newEdge = new EdgeNode(destination, distance);
        if (vertex.head == null) {
            vertex.head = newEdge;
        } else {
            EdgeNode current = vertex.head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newEdge;
        }
        vertex.degree++;
    }

    private void removeEdgeFromList(Vertex vertex, String destination) {
        EdgeNode current = vertex.head;
        EdgeNode previous = null;
        while (current != null) {
            if (current.destination.equalsIgnoreCase(destination)) {
                if (previous == null) {
                    vertex.head = current.next;
                } else {
                    previous.next = current.next;
                }
                vertex.degree--;
                return;
            }
            previous = current;
            current = current.next;
        }
    }

    private EdgeNode findEdge(Vertex vertex, String destination) {
        EdgeNode current = vertex.head;
        while (current != null) {
            if (current.destination.equalsIgnoreCase(destination.trim())) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Display
    // ------------------------------------------------------------------

    /** Prints the whole adjacency list. */
    public void displayNetwork() {
        if (vertexCount == 0) {
            System.out.println("The campus network is empty. Add locations first.");
            return;
        }
        System.out.println("Campus network (adjacency list) - " + vertexCount
                + " locations, " + roadCount + " roads");
        System.out.println(Validator.repeat('-', 70));
        for (int i = 0; i < vertexCount; i++) {
            System.out.println(String.format("[%2d] %-22s -> %s",
                    i, vertices[i].name, neighboursToString(vertices[i])));
        }
        System.out.println(Validator.repeat('-', 70));
    }

    /** Prints the neighbours of one location. Returns false if the location does not exist. */
    public boolean displayNeighbours(String name) {
        int index = indexOf(name);
        if (index == -1) {
            return false;
        }
        Vertex vertex = vertices[index];
        System.out.println("Locations directly connected to " + vertex.name
                + " (" + vertex.degree + " road" + (vertex.degree == 1 ? "" : "s") + "):");
        if (vertex.head == null) {
            System.out.println("  (none - this location is not connected to any other location)");
        }
        EdgeNode edge = vertex.head;
        while (edge != null) {
            System.out.println("  - " + edge.destination + "  (" + edge.distance + " m)");
            edge = edge.next;
        }
        return true;
    }

    /** Prints the same graph as an adjacency matrix (0 = no road, otherwise distance). */
    public void displayAdjacencyMatrix() {
        if (vertexCount == 0) {
            System.out.println("The campus network is empty.");
            return;
        }
        System.out.println("Adjacency matrix view (values are distances in metres, 0 = no direct road)");
        StringBuilder header = new StringBuilder(String.format("%-6s", ""));
        for (int i = 0; i < vertexCount; i++) {
            header.append(String.format("%6s", "[" + i + "]"));
        }
        System.out.println(header);
        for (int i = 0; i < vertexCount; i++) {
            StringBuilder row = new StringBuilder(String.format("%-6s", "[" + i + "]"));
            for (int j = 0; j < vertexCount; j++) {
                int distance = i == j ? 0 : Math.max(0, getDistance(vertices[i].name, vertices[j].name));
                row.append(String.format("%6d", distance));
            }
            System.out.println(row);
        }
        for (int i = 0; i < vertexCount; i++) {
            System.out.println("  [" + i + "] = " + vertices[i].name);
        }
    }

    private String neighboursToString(Vertex vertex) {
        if (vertex.head == null) {
            return "(no connections)";
        }
        StringBuilder builder = new StringBuilder();
        EdgeNode edge = vertex.head;
        while (edge != null) {
            builder.append(edge.destination).append("(").append(edge.distance).append("m)");
            if (edge.next != null) {
                builder.append(" -> ");
            }
            edge = edge.next;
        }
        return builder.toString();
    }

    // ------------------------------------------------------------------
    // Traversals
    // ------------------------------------------------------------------

    /**
     * Breadth-First Search: visits the start, then all neighbours at distance 1,
     * then distance 2, and so on. Uses our own LinkedQueue (FIFO).
     * Returns the visit order, or null if the start location does not exist.
     */
    public String[] bfs(String start) {
        int startIndex = indexOf(start);
        if (startIndex == -1) {
            return null;
        }
        boolean[] visited = new boolean[vertexCount];
        String[] order = new String[vertexCount];
        int count = 0;

        LinkedQueue<Integer> queue = new LinkedQueue<Integer>();
        visited[startIndex] = true;
        queue.enqueue(startIndex);

        while (!queue.isEmpty()) {
            int current = queue.dequeue();
            order[count++] = vertices[current].name;
            EdgeNode edge = vertices[current].head;
            while (edge != null) {
                int neighbour = indexOf(edge.destination);
                if (!visited[neighbour]) {
                    visited[neighbour] = true; // mark when enqueued so it is never added twice
                    queue.enqueue(neighbour);
                }
                edge = edge.next;
            }
        }
        return trim(order, count);
    }

    /**
     * Depth-First Search: goes as deep as possible along one path before
     * backtracking. Uses our own LinkedStack (LIFO) instead of recursion.
     * Neighbours are pushed in reverse so they are visited in list order.
     * Returns the visit order, or null if the start location does not exist.
     */
    public String[] dfs(String start) {
        int startIndex = indexOf(start);
        if (startIndex == -1) {
            return null;
        }
        boolean[] visited = new boolean[vertexCount];
        String[] order = new String[vertexCount];
        int count = 0;

        LinkedStack<Integer> stack = new LinkedStack<Integer>();
        stack.push(startIndex);

        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (visited[current]) {
                continue;
            }
            visited[current] = true;
            order[count++] = vertices[current].name;

            // collect neighbours, then push them last-to-first
            int[] neighbours = new int[vertices[current].degree];
            int n = 0;
            EdgeNode edge = vertices[current].head;
            while (edge != null) {
                neighbours[n++] = indexOf(edge.destination);
                edge = edge.next;
            }
            for (int i = n - 1; i >= 0; i--) {
                if (!visited[neighbours[i]]) {
                    stack.push(neighbours[i]);
                }
            }
        }
        return trim(order, count);
    }

    /**
     * Finds the route with the FEWEST STOPS between two locations using BFS
     * and a parent array. Returns the path (start ... end), or null if the
     * locations are not connected. Throws if a location does not exist.
     */
    public String[] findRoute(String from, String to) {
        int start = indexOf(from);
        int end = indexOf(to);
        if (start == -1 || end == -1) {
            throw new IllegalArgumentException("Both locations must exist.");
        }
        int[] parent = new int[vertexCount];
        boolean[] visited = new boolean[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            parent[i] = -1;
        }
        LinkedQueue<Integer> queue = new LinkedQueue<Integer>();
        visited[start] = true;
        queue.enqueue(start);
        while (!queue.isEmpty()) {
            int current = queue.dequeue();
            if (current == end) {
                break;
            }
            EdgeNode edge = vertices[current].head;
            while (edge != null) {
                int neighbour = indexOf(edge.destination);
                if (!visited[neighbour]) {
                    visited[neighbour] = true;
                    parent[neighbour] = current;
                    queue.enqueue(neighbour);
                }
                edge = edge.next;
            }
        }
        if (!visited[end]) {
            return null; // no path
        }
        // walk back from end to start using the parent array (a stack reverses it)
        LinkedStack<String> reversed = new LinkedStack<String>();
        for (int v = end; v != -1; v = parent[v]) {
            reversed.push(vertices[v].name);
        }
        String[] path = new String[reversed.size()];
        for (int i = 0; i < path.length; i++) {
            path[i] = reversed.pop();
        }
        return path;
    }

    /** Sum of road distances along a path returned by {@link #findRoute}. */
    public int pathDistance(String[] path) {
        int total = 0;
        for (int i = 0; i < path.length - 1; i++) {
            total += getDistance(path[i], path[i + 1]);
        }
        return total;
    }

    /** True if every location can be reached from every other location. */
    public boolean isFullyConnected() {
        if (vertexCount <= 1) {
            return true;
        }
        return bfs(vertices[0].name).length == vertexCount;
    }

    private String[] trim(String[] array, int count) {
        String[] result = new String[count];
        System.arraycopy(array, 0, result, 0, count);
        return result;
    }

    /** All location names in insertion order. */
    public String[] getLocations() {
        String[] names = new String[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            names[i] = vertices[i].name;
        }
        return names;
    }

    public int getLocationCount() {
        return vertexCount;
    }

    public int getRoadCount() {
        return roadCount;
    }

    public boolean isEmpty() {
        return vertexCount == 0;
    }
}
