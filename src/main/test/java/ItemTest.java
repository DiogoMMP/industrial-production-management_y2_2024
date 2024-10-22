import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;
import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.Instances;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    private Item item1;
    private Item item2;
    private Workstation workstation1;
    private Workstation workstation2;
    private static final String FILE_PATH_ITEMS = "test_files/articles.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations.csv";

    @BeforeEach
    void setUp() {
        item1 = new Item();
        item1.setId(10001);
        item1.setPriority(Priority.HIGH);
        item1.setOperations(Arrays.asList("cut", "sand", "paint"));

        item2 = new Item();
        item2.setId(10002);
        item2.setPriority(Priority.LOW);
        item2.setOperations(Arrays.asList("drill", "polish"));

        workstation1 = new Workstation();
        workstation1.setId("M1");
        workstation1.setOperation("cut");
        workstation1.setTime(10);

        workstation2 = new Workstation();
        workstation2.setId("M2");
        workstation2.setOperation("sand");
        workstation2.setTime(20);

        HashMap<Item, Workstation> prodPlan = new HashMap<>();
        prodPlan.put(item1, workstation1);
        prodPlan.put(item2, workstation2);

        Instances.getInstance().getHashMapItemsMachines().setProdPlan(prodPlan);
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
        LinkedHashMap<String, Double> result = Item.simulateProcessUS08();

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");

        for (String key : result.keySet()) {
            assertTrue(key.contains("Operation:"), "Result should contain 'Operation:'");
            assertTrue(key.contains("Machine:"), "Result should contain 'Machine:'");
            assertTrue(key.contains("Priority:"), "Result should contain 'Priority:'");
            assertTrue(key.contains("Item:"), "Result should contain 'Item:'");
            assertTrue(key.contains("Time:"), "Result should contain 'Time:'");
        }

        for (Double value : result.values()) {
            assertTrue(value > 0, "Time should be greater than 0");
        }
    }

    // Test for calculateAvgExecutionAndWaitingTimes
    @Test

    void testCalculateAvgExecutionAndWaitingTimes() {
        HashMap<String, Double[]> result = Item.calculateAvgExecutionAndWaitingTimes();

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
        Map<String, List<Map.Entry<String, Integer>>> result = Item.generateWorkstationFlowDependency();

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");

        // Check that the result contains dependencies for certain machines
        assertTrue(result.containsKey("M1"), "Result should contain flow for 'M1'");

        // Validate that the flow dependencies for a machine have the expected format
        List<Map.Entry<String, Integer>> machine1Flow = result.get("M1");
        assertNotNull(machine1Flow, "Flow for 'M1' should not be null");
        assertFalse(machine1Flow.isEmpty(), "Flow for 'M1' should not be empty");

        // Check that each entry has a valid format (machine, transition count)
        for (Map.Entry<String, Integer> entry : machine1Flow) {
            assertNotNull(entry.getKey(), "Machine name in entry should not be null");
            assertNotNull(entry.getValue(), "Transition count in entry should not be null");
            assertTrue(entry.getValue() > 0, "Transition count should be greater than 0");
        }
    }

    @Test
    void testSimulateProcessUS02() {
        LinkedHashMap<String, Double> result = Item.simulateProcessUS02();

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");

        for (String key : result.keySet()) {
            assertTrue(key.contains("Operation:"), "Result should contain 'Operation:'");
            assertTrue(key.contains("Machine:"), "Result should contain 'Machine:'");
            assertTrue(key.contains("Priority:"), "Result should contain 'Priority:'");
            assertTrue(key.contains("Item:"), "Result should contain 'Item:'");
            assertTrue(key.contains("Time:"), "Result should contain 'Time:'");
        }

        for (Double value : result.values()) {
            assertTrue(value > 0, "Time should be greater than 0");
        }
    }

    @Test
    void testCalculateTotalProductionTimePerItem() {
        HashMap<Item, Double> result = Item.calculateTotalProductionTimePerItem();
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Check the total production time for each item
        assertTrue(result.containsKey(item1));
        assertTrue(result.containsKey(item2));
    }
}
