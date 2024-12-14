package projectManager;

import domain.Activity;
import graph.Edge;
import graph.map.MapGraph;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ActivitiesMapRepository;
import repository.Instances;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PERTCPMTest {

    private PERT_CPM pertCPM;
    private ActivitiesMapRepository activitiesMapRepository;

    @BeforeEach
    void setUp() {
        // Initialize the PERT_CPM instance before each test
        activitiesMapRepository = Instances.getInstance().getActivitiesMapRepository();
        activitiesMapRepository.addActivity("A1", new Activity("A1", "Activity 1", 5, "days", 100, List.of()));
        activitiesMapRepository.addActivity("A2", new Activity("A2", "Activity 2", 3, "days", 200, List.of("A1")));
        activitiesMapRepository.addActivity("A3", new Activity("A3", "Activity 3", 2, "days", 150, List.of("A1")));
        activitiesMapRepository.addActivity("A4", new Activity("A4", "Activity 4", 4, "days", 250, List.of("A2", "A3")));
        pertCPM = Instances.getInstance().getPERT_CPM();
        pertCPM.buildPERT_CPM();
        CalculateTimes calculateTimes = new CalculateTimes();
        calculateTimes.calculateTimes();
    }

    @Test
    void testBuildPERT_CPM() {
        MapGraph<String, String> graph = pertCPM.getPert_CPM();
        assertNotNull(graph);
        assertTrue(graph.vertices().contains("START"));
        assertTrue(graph.vertices().contains("END"));
    }

    @Test
    void testAddActivity() {
        Activity activity = new Activity("A5", "Description", 5, "days", 100, Collections.emptyList());
        pertCPM.addActivity(activity);
        assertTrue(pertCPM.containsActivity("A5"));
    }

    @Test
    void testRemoveActivity() {
        pertCPM.removeActivity("A1");
        assertFalse(pertCPM.containsActivity("A1"));
    }

    @Test
    void testAddDependency() {
        Activity activity1 = new Activity("A5", "Description", 3, "days", 100, Collections.emptyList());
        Activity activity2 = new Activity("A6", "Description", 4, "days", 100, Collections.singletonList("A5"));
        pertCPM.addActivity(activity1);
        pertCPM.addActivity(activity2);
        pertCPM.addDependency("A6", "A5");

        String vertexLabel1 = "A5 (3 days)";
        String vertexLabel2 = "A6 (4 days)";
        assertTrue(pertCPM.getPert_CPM().vertices().contains(vertexLabel1), "Vertex should exist in the graph");

        Collection<Edge<String, String>> edges = pertCPM.getPert_CPM().outgoingEdges(vertexLabel1);
        if (edges != null) {
            assertTrue(edges.stream().anyMatch(edge -> edge.getVDest().equals(vertexLabel2)));
        } else {
            fail("Outgoing edges should not be null");
        }
    }

    @Test
    void testRemoveDependency() {
        pertCPM.addDependency("A2", "A1");

        pertCPM.removeDependency("A2", "A1");

        String vertexLabel1 = "A1 (5 days)";
        String vertexLabel2 = "A2 (3 days)";
        Collection<Edge<String, String>> edges = pertCPM.getPert_CPM().outgoingEdges(vertexLabel1);
        if (edges != null) {
            assertFalse(edges.stream().anyMatch(edge -> edge.getVDest().equals(vertexLabel2)));
        } else {
            assertTrue(true); // No outgoing edges, so the dependency was removed
        }
    }

    @Test
    void testIsEmpty() {
        assertFalse(pertCPM.isEmpty());
        pertCPM.getActivities().clear();
        assertTrue(pertCPM.isEmpty());
    }

    @Test
    void testSize() {
        int initialSize = pertCPM.size();
        Activity activity = new Activity("A5", "Description", 5, "days", 100, Collections.emptyList());
        pertCPM.addActivity(activity);
        assertEquals(initialSize + 1, pertCPM.size());
    }

    @Test
    void testFindCriticalPaths() {
        LinkedHashMap<Integer, List<Activity>> criticalPaths = pertCPM.findCriticalPaths();
        assertNotNull(criticalPaths, "Critical paths should not be null");
        assertFalse(criticalPaths.isEmpty(), "Critical paths should not be empty");

        for (List<Activity> path : criticalPaths.values()) {
            assertFalse(path.isEmpty(), "Each critical path should not be empty");
            for (Activity activity : path) {
                assertNotNull(activity, "Activity in critical path should not be null");
                assertEquals(0.0, activity.getSlack(), "Activity slack should be 0.0");
            }
        }
    }

    @Test
    void testGetPert_CPMWithoutDuration() {
        MapGraph<String, String> pertCpmWithoutDuration = pertCPM.getPert_CPMWithoutDuration();
        assertNotNull(pertCpmWithoutDuration);
        assertTrue(pertCpmWithoutDuration.vertices().containsAll(List.of("START", "A1", "A2", "A3", "A4", "END")));
        assertTrue(pertCpmWithoutDuration.validEdge("START", "A1"));
        assertTrue(pertCpmWithoutDuration.validEdge("A1", "A2"));
        assertTrue(pertCpmWithoutDuration.validEdge("A1", "A3"));
        assertTrue(pertCpmWithoutDuration.validEdge("A2", "A4"));
        assertTrue(pertCpmWithoutDuration.validEdge("A3", "A4"));
        assertTrue(pertCpmWithoutDuration.validEdge("A4", "END"));
    }

    @Test
    void testExportScheduleToCSV() throws IOException {
        File outputFile = new File("test_schedule.csv");
        pertCPM.exportScheduleToCSV(outputFile);

        assertTrue(outputFile.exists());

        try (FileReader reader = new FileReader(outputFile)) {
            char[] buffer = new char[1024];
            int read = reader.read(buffer);
            String content = new String(buffer, 0, read);

            assertTrue(content.contains("act_id;cost;duration;es;ls;ef;lf;prev_act_id1;...;prev_act_idN"));
            assertTrue(content.contains("A1;100;5;"));
            assertTrue(content.contains("A2;200;3;"));
            assertTrue(content.contains("A3;150;2;"));
            assertTrue(content.contains("A4;250;4;"));
        } finally {
            outputFile.delete();
        }
    }

    @Test
    void testHasCircularDependencies() {
        // Case 1: No circular dependencies
        assertFalse(pertCPM.hasCircularDependencies(), "Graph should not have circular dependencies.");

        // Case 2: Introduce circular dependencies (A1 -> A2 -> A1)
        pertCPM.addDependency("A1", "A2");
        pertCPM.addDependency("A2", "A1"); // Creates a circular dependency
        assertTrue(pertCPM.hasCircularDependencies(), "Graph should have circular dependencies.");

        // Remove the circular dependency to restore state
        pertCPM.removeDependency("A2", "A1");
    }
    @Test
    void testTopologicalSort() {
        // Get topological sort order
        List<String> sortedOrder = pertCPM.topologicalSort();

        // Expected topological order for the setup graph
        List<String> expectedOrder = List.of("START", "A1 (5 days)", "A3 (2 days)", "A2 (3 days)", "A4 (4 days)", "END");
        assertEquals(expectedOrder, sortedOrder, "Topological order should match expected order.");

        // Case: Adding a cycle and ensuring topological sort fails
        pertCPM.addDependency("A1", "A3"); // Creates a cycle
        assertThrows(IllegalStateException.class, pertCPM::topologicalSort, "Topological sort should fail due to cycle.");

        // Remove the cycle to restore state
        pertCPM.removeDependency("A1", "A3");
    }


    @AfterEach
    void tearDown() {
        activitiesMapRepository = new ActivitiesMapRepository();
        pertCPM = new PERT_CPM();
    }

    @Test
    void testGetBottleneckActivities() {
        List<Activity> bottleneckActivities = pertCPM.getBottleneckActivities();
        assertNotNull(bottleneckActivities, "Bottleneck activities should not be null");
        assertFalse(bottleneckActivities.isEmpty(), "Bottleneck activities should not be empty");
        // Assuming "D" is the expected bottleneck activity based on the setup
        assertTrue(bottleneckActivities.stream().anyMatch(activity -> "A4".equals(activity.getActId())), "Bottleneck activities should contain activity D");
    }

    @Test
    void testSimulateDelaysAndRecalculate() {
        LinkedHashMap<String, Integer> delays = new LinkedHashMap<>();
        delays.put("A2", 2); // Delay activity A2 by 2 days
        delays.put("A3", 1); // Delay activity A3 by 1 day

        pertCPM.simulateDelaysAndRecalculate(delays);

        // Verify the new durations
        assertEquals(5, pertCPM.getActivities().get("A1").getDuration());
        assertEquals(5, pertCPM.getActivities().get("A2").getDuration());
        assertEquals(3, pertCPM.getActivities().get("A3").getDuration());
        assertEquals(4, pertCPM.getActivities().get("A4").getDuration());

        // Verify the recalculated total project duration
        double totalProjectDuration = pertCPM.calculateTotalProjectDuration();
        assertEquals(14.0, totalProjectDuration);
    }

    @Test
    void testCalculateTotalProjectDuration() {
        // Calculate the total project duration without any delays
        double totalProjectDuration = pertCPM.calculateTotalProjectDuration();
        assertEquals(12.0, totalProjectDuration);
    }

    @AfterAll
    static void tearDownAll() {
        Instances.getInstance().clear();
    }
}