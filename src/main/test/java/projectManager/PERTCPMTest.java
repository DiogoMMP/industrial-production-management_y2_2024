package projectManager;

import domain.Activity;
import graph.Edge;
import graph.map.MapGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ActivitiesMapRepository;
import repository.Instances;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        activitiesMapRepository.addActivity("A1", new Activity("A1", "Activity 1", 5, "days", 100, "USD", List.of()));
        activitiesMapRepository.addActivity("A2", new Activity("A2", "Activity 2", 3, "days", 200, "USD", List.of("A1")));
        activitiesMapRepository.addActivity("A3", new Activity("A3", "Activity 3", 2, "days", 150, "USD", List.of("A1")));
        activitiesMapRepository.addActivity("A4", new Activity("A4", "Activity 4", 4, "days", 250, "USD", List.of("A2", "A3")));
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
        Activity activity = new Activity("A5", "Description", 5, "days", 100, "USD", Collections.emptyList());
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
        Activity activity1 = new Activity("A5", "Description", 3, "days", 100, "USD", Collections.emptyList());
        Activity activity2 = new Activity("A6", "Description", 4, "days", 100, "USD", Collections.singletonList("A5"));
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
        Activity activity = new Activity("A5", "Description", 5, "days", 100, "USD", Collections.emptyList());
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

    @AfterEach
    void tearDown() {
        activitiesMapRepository = new ActivitiesMapRepository();
        pertCPM = new PERT_CPM();
    }
}