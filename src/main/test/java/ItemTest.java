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
        TreeMap<Item, Double> result = Item.calculateTotalProductionTimePerItem();
        assertNotNull(result, "The result should not be null");
        assertFalse(result.isEmpty(), "The result should not be empty");

        assertTrue(result.containsKey(item1), "The result should contain item1");
        assertTrue(result.containsKey(item2), "The result should contain item2");

        double expectedTimeItem1 = 30.0;
        assertEquals(expectedTimeItem1, result.get(item1), 0.01, "The total production time for item1 is incorrect");


        double expectedTimeItem2 = 0.0;
        assertEquals(expectedTimeItem2, result.get(item2), 0.01, "The total production time for item2 is incorrect");
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
        HashMap<Item, Double> totalProductionTimePerItem = new HashMap<>();
        totalProductionTimePerItem.put(item2, 40.0);
        totalProductionTimePerItem.put(item1, 30.0);

        TreeMap<Item, Double> result = Item.sortById(totalProductionTimePerItem);
        assertNotNull(result);
        assertEquals(2, result.size());

        Iterator<Item> iterator = result.keySet().iterator();
        assertEquals(item1, iterator.next());
        assertEquals(item2, iterator.next());
    }


    @Test
    void testGenerateWorkstationFlowDependency() {
        // Execute the method
        HashMap<String, List<Map.Entry<String, Integer>>> flowDependency = Item.generateWorkstationFlowDependency();

        // Verify the result is not null
        assertNotNull(flowDependency);

        // Verify M1 and M2 are in the results
        assertTrue(flowDependency.containsKey("M1"));
        assertTrue(flowDependency.containsKey("M2"));

        // Get transitions from M1
        List<Map.Entry<String, Integer>> m1Transitions = flowDependency.get("M1");

        // Since M1 (cut) comes before M2 (sand) in item1's operations,
        // there should be a transition from M1 to M2
        boolean foundTransition = false;
        for (Map.Entry<String, Integer> transition : m1Transitions) {
            if (transition.getKey().equals("M2")) {
                foundTransition = true;
                assertEquals(1, transition.getValue().intValue(),
                        "Should have 1 transition from M1 to M2");
                break;
            }
        }
        assertTrue(foundTransition, "Should have found a transition from M1 to M2");

        // M2 should have no transitions since it's the last workstation in our test setup
        assertTrue(flowDependency.get("M2").isEmpty(),
                "M2 should have no outgoing transitions");
    }

    @Test
    void testCalculateAvgExecutionAndWaitingTimes() {
        // Execute the method
        HashMap<String, Double[]> operationTimes = Item.calculateAvgExecutionAndWaitingTimes();

        // Verify the result is not null
        assertNotNull(operationTimes);

        // Test cut operation times
        Double[] cutTimes = operationTimes.get("cut");
        assertNotNull(cutTimes);
        assertEquals(2, cutTimes.length);
        assertEquals(10.0, cutTimes[0], 0.01, "Cut operation execution time should be 10");
        assertEquals(0.0, cutTimes[1], 0.01, "First operation should have no waiting time");

        // Test sand operation times
        Double[] sandTimes = operationTimes.get("sand");
        assertNotNull(sandTimes);
        assertEquals(2, sandTimes.length);
        assertEquals(20.0, sandTimes[0], 0.01, "Sand operation execution time should be 20");
        assertTrue(sandTimes[1] >= 0.0, "Waiting time should be non-negative");

        // Operations not assigned to any workstation should not be in the results
        assertNull(operationTimes.get("paint"),
                "Paint operation should not be in results as no workstation handles it");
        assertNull(operationTimes.get("drill"),
                "Drill operation should not be in results as no workstation handles it");
        assertNull(operationTimes.get("polish"),
                "Polish operation should not be in results as no workstation handles it");
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
