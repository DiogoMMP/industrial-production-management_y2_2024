import com.kitfox.svg.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;
import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.Simulator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    private Item item1;
    private Item item2;
    private Workstation workstation1;
    private Workstation workstation2;

    @BeforeEach
    void setUp() {
        item1 = new Item();
        item1.setId(10001);
        item1.setPriority(Priority.HIGH);
        item1.setOperations(new ArrayList<>(Arrays.asList("cut", "sand", "paint")));

        item2 = new Item();
        item2.setId(10002);
        item2.setPriority(Priority.LOW);
        item2.setOperations(new ArrayList<>(Arrays.asList("drill", "polish")));

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

        Instances.getInstance().getHashMapItemsWorkstations().setProdPlan(prodPlan);
    }

    @Test
    void testItemConstructor() {
        // Setup
        int id = 10001;
        Priority priority = Priority.HIGH;
        List<String> operations = Arrays.asList("cut", "sand", "paint");

        // Execute
        Item item = new Item(id, priority, operations);

        // Verify
        assertEquals(id, item.getId(), "Item ID should be initialized correctly");
        assertEquals(priority, item.getPriority(), "Item priority should be initialized correctly");
        assertEquals(operations, item.getOperations(), "Item operations should be initialized correctly");
        assertEquals(0, item.getCurrentOperationIndex(), "Current operation index should be initialized to 0");
        assertNotNull(item.getLowestTimes(), "Lowest times should be initialized");
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
    void testCalculateTotalProductionTimePerItem() {
        // Simulate the process times
        LinkedHashMap<String, Double> timeOperations = new LinkedHashMap<>();
        timeOperations.put("Operation: cut - Item: 10001", 10.0);
        timeOperations.put("Operation: sand - Item: 10001", 20.0);
        timeOperations.put("Operation: paint - Item: 10001", 30.0);
        timeOperations.put("Operation: drill - Item: 10002", 15.0);
        timeOperations.put("Operation: polish - Item: 10002", 25.0);

        // Mock the simulator to return the simulated process times
        Simulator simulator = Instances.getInstance().getSimulator();
        simulator.setTimeOperations(timeOperations);

        // Execute the method
        HashMap<String, Double> result = Item.calculateTotalProductionTimePerItem();
        ArrayList<Integer> resultItems = new ArrayList<>();
        ArrayList<String> resultQuantity = new ArrayList<>();
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            String[] parts = entry.getKey().split(" - ");
            int item = Integer.parseInt(parts[0]);
            resultItems.add(item);
            resultQuantity.add(parts[1]);
        }
        // Verify the result
        assertNotNull(resultItems, "The result should not be null");
        assertFalse(resultItems.isEmpty(), "The result should not be empty");

        assertTrue(resultItems.contains(item1.getId()), "The result should contain item1");
        assertTrue(resultItems.contains(item2.getId()), "The result should contain item2");

        String item1ID = Integer.toString(item1.getId());
        double expectedTimeItem1 = 60.0; // 10 + 20 + 30
        double actualTimeItem1 = 0.0;
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            if (entry.getKey().contains(item1ID)) {
                actualTimeItem1 += entry.getValue();
            }
        }
        assertEquals(expectedTimeItem1, actualTimeItem1, 0.01, "The total production time for item1 is incorrect");

        double expectedTimeItem2 = 40.0; // 15 + 25
        double actualTimeItem2 = 0.0;
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            if (entry.getKey().contains(Integer.toString(item2.getId()))) {
                actualTimeItem2 += entry.getValue();
            }
        }
        assertEquals(expectedTimeItem2, actualTimeItem2, 0.01, "The total production time for item2 is incorrect");
    }

    @Test
    void testRemoveDuplicateItems() {
        HashMap<Item, Double> totalProductionTimePerItem = new HashMap<>();
        totalProductionTimePerItem.put(item1, 30.0);
        totalProductionTimePerItem.put(item2, 40.0);
        totalProductionTimePerItem.put(item1, 50.0); // Duplicate item

        HashMap<Item, Double> result = Item.removeDuplicateItems(totalProductionTimePerItem);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(50.0, result.get(item1));
        assertEquals(40.0, result.get(item2));
    }

    @Test
    void testSortById() {
        HashMap<String, Double> totalProductionTimePerItem = new HashMap<>();
        String item1ID = Integer.toString(item1.getId());
        String item2ID = Integer.toString(item2.getId());
        totalProductionTimePerItem.put(item1ID, 40.0);
        totalProductionTimePerItem.put(item2ID, 30.0);

        HashMap<String, Double> result = Item.sortById(totalProductionTimePerItem);
        assertNotNull(result);
        assertEquals(2, result.size());

        Iterator<Map.Entry<String, Double>> iterator = result.entrySet().iterator();
        assertEquals(item1, iterator.next());
        assertEquals(item2, iterator.next());
    }


    @Test
    void testCalculateAvgExecutionAndWaitingTimes() {
        // Execute the method
        HashMap<String, Double[]> result = Item.calculateAvgExecutionAndWaitingTimes();

        // Verify result is not null
        assertNotNull(result);

        // Verify all operations from both items are present
        assertTrue(result.containsKey("cut"), "Should contain 'cut' operation");
        assertTrue(result.containsKey("sand"), "Should contain 'sand' operation");
        assertTrue(result.containsKey("paint"), "Should contain 'paint' operation");
        assertTrue(result.containsKey("drill"), "Should contain 'drill' operation");
        assertTrue(result.containsKey("polish"), "Should contain 'polish' operation");

        // Check specific values for operations we know should have execution times
        Double[] cutTimes = result.get("cut");
        assertNotNull(cutTimes, "Times for 'cut' operation should not be null");
        assertEquals(10.0, cutTimes[0], 0.1, "Average execution time for 'cut' should be 10");
        assertTrue(cutTimes[1] >= 0, "Waiting time for 'cut' should be non-negative");

        Double[] sandTimes = result.get("sand");
        assertNotNull(sandTimes, "Times for 'sand' operation should not be null");
        assertEquals(20.0, sandTimes[0], 0.1, "Average execution time for 'sand' should be 20");
        assertTrue(sandTimes[1] >= 0, "Waiting time for 'sand' should be non-negative");

        // Verify all entries have valid structure
        for (Map.Entry<String, Double[]> entry : result.entrySet()) {
            String operation = entry.getKey();
            Double[] times = entry.getValue();

            assertNotNull(times, "Times array should not be null for operation " + operation);
            assertEquals(2, times.length, "Times array should have length 2 for operation " + operation);
            assertTrue(times[0] >= 0, "Execution time should be non-negative for operation " + operation);
            assertTrue(times[1] >= 0, "Waiting time should be non-negative for operation " + operation);
        }
    }



    @Test
    void testCalculateAvgExecutionAndWaitingTimesWithEmptyData() {
        // Clear the production plan
        Instances.getInstance().getHashMapItemsWorkstations().setProdPlan(new HashMap<>());

        // Execute the method
        HashMap<String, Double[]> operationTimes = Item.calculateAvgExecutionAndWaitingTimes();

        // Verify the result is not null but empty
        assertNotNull(operationTimes);
        assertTrue(operationTimes.isEmpty());
    }

    @Test
    void testGenerateWorkstationFlowDependencyWithEmptyData() {
        // Clear the production plan
        Instances.getInstance().getHashMapItemsWorkstations().setProdPlan(new HashMap<>());

        // Execute the method
        HashMap<String, List<Map.Entry<String, Integer>>> flowDependency =
                Item.generateWorkstationFlowDependency();

        // Verify the result is not null but empty
        assertNotNull(flowDependency);
        assertTrue(flowDependency.isEmpty());
    }
}
