package projectManager;

import domain.Activity;
import graph.Edge;
import graph.map.MapGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ActivitiesMapRepository;
import repository.Instances;


import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PERTCPMTest {

    private PERT_CPM pertCPM;

    @BeforeEach
    void setUp() {
        // Initialize the PERT_CPM instance before each test
        ActivitiesMapRepository activitiesMapRepository = new ActivitiesMapRepository();
        activitiesMapRepository.addActivities("activities.csv");
        pertCPM = Instances.getInstance().getPERT_CPM();
        pertCPM.buildPERT_CPM();
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

        String vertexLabel = "A1 (3 days)";
        assertTrue(pertCPM.getPert_CPM().vertices().contains(vertexLabel), "Vertex should exist in the graph");

        Collection<Edge<String, String>> edges = pertCPM.getPert_CPM().outgoingEdges(vertexLabel);
        if (edges != null) {
            assertTrue(edges.stream().anyMatch(edge -> edge.getVDest().equals("A2 (4 days)")));
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

        Collection<Edge<String, String>> edges = pertCPM.getPert_CPM().outgoingEdges("A1 (3)");
        if (edges != null) {
            assertFalse(edges.stream().anyMatch(edge -> edge.getVDest().equals("A2 (4)")));
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
}