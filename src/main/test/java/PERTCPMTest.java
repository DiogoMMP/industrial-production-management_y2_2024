import domain.Activity;
import graph.map.MapGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import projectManager.PERT_CPM;
import repository.ActivitiesMapRepository;


import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PERT_CPMTest {

    private PERT_CPM pertCPM;

    @BeforeEach
    void setUp() {
        // Initialize the PERT_CPM instance before each test
        ActivitiesMapRepository activitiesMapRepository = new ActivitiesMapRepository();
        activitiesMapRepository.addActivities("activities.csv");
        pertCPM = new PERT_CPM();
    }

    @Test
    void testBuildPERT_CPM() {
        MapGraph<String, Integer> graph = pertCPM.getPert_CPM();
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
    void testAddDependency() {
        Activity activity1 = new Activity("A1", "Description", 3, "days", 100, "USD", Collections.emptyList());
        Activity activity2 = new Activity("A2", "Description", 4, "days", 100, "USD", Collections.singletonList("A1"));
        pertCPM.addActivity(activity1);
        pertCPM.addActivity(activity2);
        pertCPM.addDependency("A2", "A1");
        assertTrue(pertCPM.getPert_CPM().outgoingEdges("A1 (3)").stream()
                .anyMatch(edge -> edge.getVDest().equals("A2 (4)")));
    }

    @Test
    void testRemoveActivity() {
        Activity activity = new Activity("A1", "Description", 3, "days", 100, "USD", Collections.emptyList());
        pertCPM.addActivity(activity);
        pertCPM.removeActivity("A1");
        assertFalse(pertCPM.containsActivity("A1"));
    }

    @Test
    void testRemoveDependency() {
        Activity activity1 = new Activity("A1", "Description", 3, "days", 100, "USD", Collections.emptyList());
        Activity activity2 = new Activity("A2", "Description", 4, "days", 100, "USD", Collections.singletonList("A1"));
        pertCPM.addActivity(activity1);
        pertCPM.addActivity(activity2);
        pertCPM.addDependency("A2", "A1");
        pertCPM.removeDependency("A2", "A1");
        assertFalse(pertCPM.getPert_CPM().outgoingEdges("A1 (3)").stream()
                .anyMatch(edge -> edge.getVDest().equals("A2 (4)")));
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