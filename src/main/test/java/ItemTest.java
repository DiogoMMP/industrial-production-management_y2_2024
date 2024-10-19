import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;
import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.Instances;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    private Item item1;
    private Item item2;
    private static final String FILE_PATH_ITEMS = "test_files/articles.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations.csv";

    @BeforeEach
    void setUp() {
        item1 = new Item();
        item1.setId(10001);
        item1.setPriority(Priority.HIGH);
        List<String> operations1 = new ArrayList<>();
        operations1.add("cut");
        operations1.add("sand");
        operations1.add("paint");
        item1.setOperations(operations1);

        item2 = new Item();
        item2.setId(10002);
        item2.setPriority(Priority.LOW);
        List<String> operations2 = new ArrayList<>();
        operations2.add("drill");
        operations2.add("polish");
        item2.setOperations(operations2);
    }

    @Test
    void getId() {
        assertEquals(10001, item1.getId(), "Item ID should be 10001");
        assertEquals(10002, item2.getId(), "Item ID should be 10002");
    }

    @Test
    void setId() {
        item1.setId(11000);
        assertEquals(11000, item1.getId(), "Item ID should be updated to 11000");
    }

    @Test
    void getPriority() {
        assertEquals(Priority.HIGH, item1.getPriority(), "Priority should be HIGH for item1");
        assertEquals(Priority.LOW, item2.getPriority(), "Priority should be LOW for item2");
    }

    @Test
    void setPriority() {
        item1.setPriority(Priority.NORMAL);
        assertEquals(Priority.NORMAL, item1.getPriority(), "Priority should be updated to NORMAL");
    }

    @Test
    void getOperations() {
        assertEquals(3, item1.getOperations().size(), "Item1 should have 3 operations");
        assertEquals(List.of("cut", "sand", "paint"), item1.getOperations(), "Operations for item1 are incorrect");

        assertEquals(2, item2.getOperations().size(), "Item2 should have 2 operations");
        assertEquals(List.of("drill", "polish"), item2.getOperations(), "Operations for item2 are incorrect");
    }

    @Test
    void setOperations() {
        List<String> newOperations = new ArrayList<>();
        newOperations.add("cut");
        newOperations.add("assemble");
        item1.setOperations(newOperations);
        assertEquals(2, item1.getOperations().size(), "Item1 should now have 2 operations");
        assertEquals(List.of("cut", "assemble"), item1.getOperations(), "New operations for item1 are incorrect");
    }

    @Test
    void simulateProcessUS08() {
        HashMap<String, Double> result = Item.simulateProcessUS08(); // Assume you have a mock for this

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");

        for (String key : result.keySet()) {
            assertTrue(key.contains("Operation:"), "Result should contain 'Operation:'");
            assertTrue(key.contains("Machine:"), "Result should contain 'Machine:'");
            assertTrue(key.contains("Priority:"), "Result should contain 'Item:'");
            assertTrue(key.contains("Item:"), "Result should contain 'Item:'");
            assertTrue(key.contains("Time:"), "Result should contain 'Time:'");
        }

        for (Double value : result.values()) {
            assertTrue(value > 0, "Time should be greater than 0");
        }

        for (String key : result.keySet()) {
            System.out.println(key);
        }

    }

    // Test for calculateAvgExecutionAndWaitingTimes
    @Test

    void testCalculateAvgExecutionAndWaitingTimes() {
        // Set up data
        Item item1 = new Item();
        item1.setOperations(Arrays.asList("cut", "sand", "paint"));
        Workstation workstation1 = new Workstation();
        //workstation1.setOperation(Arrays.asList("cut", "sand", "paint"));
        HashMap<Item, Workstation> ProdPlan = new HashMap<>();
        ProdPlan.put(item1, workstation1);
        Instances.getInstance().getHashMapItemsMachines().setProdPlan(ProdPlan);


        // Call the method
        HashMap<String, Double[]> result = Item.calculateAvgExecutionAndWaitingTimes();

        // Check if the result is not null and contains entries
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");

        // Check that the result contains times for certain operations
        assertTrue(result.containsKey("cut"), "Result should contain times for 'cut' operation");
        assertTrue(result.containsKey("sand"), "Result should contain times for 'sand' operation");

        // Validate that the times for an operation have the expected format
        Double[] cutTimes = result.get("cut");
        assertNotNull(cutTimes, "Times for 'cut' operation should not be null");
        assertEquals(2, cutTimes.length, "Times array should contain 2 elements (execution and waiting time)");

        // Check that each time is valid
        assertTrue(cutTimes[0] >= 0, "Execution time for 'cut' should be 0 or greater");
        assertTrue(cutTimes[1] >= 0, "Waiting time for 'cut' should be 0 or greater");
    }

    // Test for generateWorkstationFlowDependency
    @Test
    void testGenerateWorkstationFlowDependency() {
        // Set up data
        Item item1 = new Item();
        item1.setOperations(Arrays.asList("cut", "sand", "paint"));
        Workstation workstation1 = new Workstation();
        //workstation1.setOperation(Arrays.asList("cut", "sand", "paint"));
        HashMap<Item, Workstation> ProdPlan = new HashMap<>();
        ProdPlan.put(item1, workstation1);
        Instances.getInstance().getHashMapItemsMachines().setProdPlan(ProdPlan);


        // Call the method
        Map<String, List<Map.Entry<String, Integer>>> result = Item.generateWorkstationFlowDependency();

        // Check if the result is not null and contains entries
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");

        // Check that the result contains dependencies for certain machines
        assertTrue(result.containsKey("Machine1"), "Result should contain flow for 'Machine1'");
        assertTrue(result.containsKey("Machine2"), "Result should contain flow for 'Machine2'");

        // Validate that the flow dependencies for a machine have the expected format
        List<Map.Entry<String, Integer>> machine1Flow = result.get("Machine1");
        assertNotNull(machine1Flow, "Flow for 'Machine1' should not be null");
        assertFalse(machine1Flow.isEmpty(), "Flow for 'Machine1' should not be empty");

        // Check that each entry has a valid format (machine, transition count)
        for (Map.Entry<String, Integer> entry : machine1Flow) {
            assertNotNull(entry.getKey(), "Machine name in entry should not be null");
            assertNotNull(entry.getValue(), "Transition count in entry should not be null");
            assertTrue(entry.getValue() > 0, "Transition count should be greater than 0");
        }
    }
}
