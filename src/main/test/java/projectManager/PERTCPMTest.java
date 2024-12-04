package projectManager;

import domain.Activity;
import graph.Edge;
import graph.map.MapGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ActivitiesMapRepository;
import repository.Instances;


import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PERTCPMTest {

    private PERT_CPM pertCPM;
    private ActivitiesMapRepository activitiesMapRepository;
    @BeforeEach
    void setUp() {
        // Initialize the PERT_CPM instance before each test
        activitiesMapRepository = Instances.getInstance().getActivitiesMapRepository();
        activitiesMapRepository.addActivities("activities.csv");
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
        Activity activity = new Activity("A3", "Description", 5, "days", 100, "USD", Collections.emptyList());
        pertCPM.addActivity(activity);
        assertTrue(pertCPM.containsActivity("A3"));
    }



    @Test
    void testRemoveActivity() {
        Activity activity = new Activity("A1", "Description", 3, "days", 100, "USD", Collections.emptyList());
        pertCPM.addActivity(activity);
        pertCPM.removeActivity("A1");
        assertFalse(pertCPM.containsActivity("A1"));
    }

    @Test
    void testAddDependency() {
        Activity activity1 = new Activity("A1", "Description", 3, "days", 100, "USD", Collections.emptyList());
        Activity activity2 = new Activity("A2", "Description", 4, "days", 100, "USD", Collections.singletonList("A1"));
        pertCPM.addActivity(activity1);
        pertCPM.addActivity(activity2);
        pertCPM.addDependency("A2", "A1");

        String vertexLabel1 = "A1 (3 days)";
        String vertexLabel2 = "A2 (4 days)";
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
        Activity activity1 = new Activity("A1", "Description", 3, "days", 100, "USD", Collections.emptyList());
        Activity activity2 = new Activity("A2", "Description", 4, "days", 100, "USD", Collections.singletonList("A1"));
        pertCPM.addActivity(activity1);
        pertCPM.addActivity(activity2);
        pertCPM.addDependency("A2", "A1");
        pertCPM.removeDependency("A2", "A1");

        String vertexLabel1 = "A1 (3 days)";
        String vertexLabel2 = "A2 (4 days)";
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
        Activity activity = new Activity("A3", "Description", 5, "days", 100, "USD", Collections.emptyList());
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
    @AfterEach
    void tearDown() {
        activitiesMapRepository = new ActivitiesMapRepository();
        pertCPM = new PERT_CPM();
    }
}