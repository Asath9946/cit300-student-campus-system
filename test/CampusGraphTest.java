import graph.CampusGraph;

/**
 * Tests for the campus graph (adjacency list, BFS, DFS, route finding).
 *
 * Owner: Member 4 - M.I.M. Naizar (23DA2-1052)
 */
public final class CampusGraphTest {

    private CampusGraphTest() {
    }

    private static String join(String[] items) {
        return items == null ? "null" : String.join(",", items);
    }

    public static void run() {
        TestSupport.section("Member 4 - CampusGraph");
        CampusGraph g = new CampusGraph();
        TestSupport.check(g.isEmpty(), "new graph is empty");
        TestSupport.check(g.addLocation("Gate"), "add location Gate");
        TestSupport.check(!g.addLocation("gate"), "duplicate location rejected (case-insensitive)");
        g.addLocation("Library");
        g.addLocation("Lab");
        g.addLocation("Cafe");
        g.addLocation("Hostel");
        g.addLocation("Island"); // not connected to anything
        TestSupport.checkEquals(6, g.getLocationCount(), "6 locations");

        TestSupport.check(g.addRoad("Gate", "Library", 100), "add road Gate-Library");
        g.addRoad("Gate", "Lab", 50);
        g.addRoad("Library", "Cafe", 70);
        g.addRoad("Lab", "Cafe", 40);
        g.addRoad("Cafe", "Hostel", 200);
        TestSupport.checkEquals(5, g.getRoadCount(), "5 roads");
        TestSupport.check(!g.addRoad("library", "GATE", 10), "duplicate road rejected (either direction)");
        TestSupport.check(!g.addRoad("Gate", "Gate", 10), "self-loop rejected");
        TestSupport.check(!g.addRoad("Gate", "Mars", 10), "road to missing location rejected");
        TestSupport.check(g.hasRoad("Library", "Gate"), "roads are two-way (undirected)");
        TestSupport.checkEquals(100, g.getDistance("Library", "Gate"), "distance stored both ways");

        TestSupport.checkEquals("Gate,Library,Lab,Cafe,Hostel", join(g.bfs("Gate")), "BFS order level by level");
        TestSupport.checkEquals("Gate,Library,Cafe,Lab,Hostel", join(g.dfs("Gate")), "DFS order goes deep first");
        TestSupport.check(g.bfs("Mars") == null, "BFS from missing location returns null");
        TestSupport.checkEquals("Island", join(g.bfs("Island")), "BFS from isolated location visits only itself");
        TestSupport.check(!g.isFullyConnected(), "graph with an isolated location is not fully connected");

        String[] route = g.findRoute("Gate", "Hostel");
        TestSupport.checkEquals("Gate,Library,Cafe,Hostel", join(route), "route with fewest stops found by BFS");
        TestSupport.checkEquals(370, g.pathDistance(route), "route distance = 100 + 70 + 200");
        TestSupport.check(g.findRoute("Gate", "Island") == null, "no route to an unconnected location");

        TestSupport.check(g.removeRoad("Cafe", "Library"), "remove road Cafe-Library");
        TestSupport.check(!g.hasRoad("Library", "Cafe"), "removed in both directions");
        TestSupport.check(!g.removeRoad("Cafe", "Library"), "removing a missing road returns false");
        TestSupport.checkEquals(4, g.getRoadCount(), "4 roads left");

        TestSupport.checkEquals(2, g.removeLocation("Cafe"), "removing Cafe also removes its 2 connected roads");
        TestSupport.check(!g.hasLocation("Cafe"), "Cafe no longer exists");
        TestSupport.check(!g.hasRoad("Lab", "Cafe") && !g.hasRoad("Hostel", "Cafe"), "no road still points to Cafe");
        TestSupport.checkEquals(2, g.getRoadCount(), "road count updated (Gate-Library, Gate-Lab remain)");
        TestSupport.checkEquals(-1, g.removeLocation("Cafe"), "removing a missing location returns -1");

        // growth beyond the initial array size (10)
        CampusGraph big = new CampusGraph();
        for (int i = 0; i < 25; i++) {
            big.addLocation("Block " + i);
            if (i > 0) {
                big.addRoad("Block " + (i - 1), "Block " + i, 10);
            }
        }
        TestSupport.checkEquals(25, big.bfs("Block 0").length, "vertex array grows; BFS reaches all 25 locations");
        TestSupport.check(big.isFullyConnected(), "chain of 25 blocks is fully connected");
    }
}
