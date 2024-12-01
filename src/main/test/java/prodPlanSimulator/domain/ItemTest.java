package prodPlanSimulator.domain;

import domain.Item;
import domain.Operation;
import domain.Workstation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import enums.Priority;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.Simulator;

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
        item1.setId("10001");
        item1.setPriority(Priority.HIGH);
        Operation operation = new Operation();
        operation.setDescription("cut");
        Operation operation1 = new Operation();
        operation1.setDescription("sand");
        Operation operation2 = new Operation();
        operation2.setDescription("paint");
        item1.setOperationsRequired(new ArrayList<>(Arrays.asList(operation, operation1, operation2)));

        item2 = new Item();
        item2.setId("10002");
        item2.setPriority(Priority.LOW);
        operation1.setDescription("drill");
        operation2.setDescription("polish");
        item2.setOperationsRequired(new ArrayList<>(Arrays.asList(operation1, operation2)));

        workstation1 = new Workstation();
        workstation1.setId("M1");
        operation1.setDescription("cut");
        workstation1.setOperation(operation1);
        workstation1.setTime(10);

        workstation2 = new Workstation();
        workstation2.setId("M2");
        operation2.setDescription("sand");
        workstation2.setOperation(operation2);
        workstation2.setTime(20);

        HashMap<Item, Workstation> prodPlan = new HashMap<>();
        prodPlan.put(item1, workstation1);
        prodPlan.put(item2, workstation2);

        Instances.getInstance().getHashMapItemsWorkstations().setProdPlan(prodPlan);


    }

    @Test
    void testItemConstructor() {

        // Setup
        String id = "10001";
        Priority priority = Priority.HIGH;
        Operation operation = new Operation();
        operation.setDescription("cut");
        Operation operation1 = new Operation();
        operation1.setDescription("sand");
        Operation operation2 = new Operation();
        operation2.setDescription("paint");
        List<Operation> operations = Arrays.asList(operation, operation1, operation2);

        // Execute
        Item item = new Item(id, priority, operations);

        // Verify
        assertEquals(id, item.getId(), "Item ID should be initialized correctly");
        assertEquals(priority, item.getPriority(), "Item priority should be initialized correctly");
        assertEquals(operations, item.getOperationsRequired(), "Item operations should be initialized correctly");
        assertEquals(0, item.getCurrentOperationIndex(), "Current operation index should be initialized to 0");
        assertNotNull(item.getLowestTimes(), "Lowest times should be initialized");


    }

    @Test
    void getId() {
        assertEquals("10001", item1.getId(), "Item ID should be 10001");
        assertEquals("10002", item2.getId(), "Item ID should be 10002");
    }

    @Test
    void setId() {
        item1.setId("11000");
        assertEquals("11000", item1.getId(), "Item ID should be updated to 11000");

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

        Operation operation = new Operation();
        operation.setDescription("cut");
        Operation operation1 = new Operation();
        operation1.setDescription("sand");
        Operation operation2 = new Operation();
        operation2.setDescription("paint");
        item1.setOperationsRequired(new ArrayList<>(Arrays.asList(operation, operation1, operation2)));
        assertEquals(3, item1.getOperationsRequired().size(), "Item1 should have 3 operations");
        assertEquals(List.of(operation,operation1,operation2), item1.getOperationsRequired(), "Operations for item1 are incorrect");

        Operation operation3 = new Operation();
        operation3.setDescription("drill");
        Operation operation4 = new Operation();
        operation4.setDescription("polish");
        item2.setOperationsRequired(new ArrayList<>(Arrays.asList(operation3, operation4)));
        assertEquals(2, item2.getOperationsRequired().size(), "Item2 should have 2 operations");
        assertEquals(List.of(operation3,operation4), item2.getOperationsRequired(), "Operations for item2 are incorrect");
    }

    @Test
    void setOperations() {
        List<Operation> newOperations = new ArrayList<>();
        Operation operation = new Operation();
        operation.setDescription("cut");
        Operation operation1 = new Operation();
        operation1.setDescription("assemble");
        newOperations.add(operation);
        newOperations.add(operation1);
        item1.setOperationsRequired(newOperations);
        assertEquals(2, item1.getOperationsRequired().size(), "Item1 should now have 2 operations");
        assertEquals(List.of(operation, operation1), item1.getOperationsRequired(), "New operations for item1 are incorrect");
    }


    @Test
    void testCalculateTotalProductionTimePerItem() {
        // Simulate the process times
        LinkedHashMap<String, Double> timeOperations = new LinkedHashMap<>();
        timeOperations.put("1 -  Operation: CUT - Machine: ws11 - Priority: normal - Item: 1001 - Time: 10 - Quantity: 1", 10.0);
        timeOperations.put("2 -  Operation: SAND - Machine: ws12 - Priority: normal - Item: 1001 - Time: 20 - Quantity: 1", 20.0);
        timeOperations.put("3 -  Operation: PAINT - Machine: ws13 - Priority: normal - Item: 1001 - Time: 30 - Quantity: 1", 30.0);
        timeOperations.put("4 -  Operation: DRILL - Machine: ws21 - Priority: normal - Item: 1002 - Time: 15 - Quantity: 1", 15.0);
        timeOperations.put("5 -  Operation: POLISH - Machine: ws22 - Priority: normal - Item: 1002 - Time: 25 - Quantity: 1", 25.0);

        // Mock the simulator to return the simulated process times
        Simulator simulator = Instances.getInstance().getSimulator();
        simulator.setTimeOperations(timeOperations);

        // Execute the method
        HashMap<String, Double> result = Item.calculateTotalProductionTimePerItem();

        // Verify the result
        assertNotNull(result, "The result should not be null");
        assertFalse(result.isEmpty(), "The result should not be empty");

        String item1Key = "1001 - 1";
        String item2Key = "1002 - 1";

        assertTrue(result.containsKey(item1Key), "The result should contain item1");
        assertTrue(result.containsKey(item2Key), "The result should contain item2");

        double expectedTimeItem1 = 60.0; // 10 + 20 + 30
        assertEquals(expectedTimeItem1, result.get(item1Key), 0.01, "The total production time for item1 is incorrect");

        double expectedTimeItem2 = 40.0; // 15 + 25
        assertEquals(expectedTimeItem2, result.get(item2Key), 0.01, "The total production time for item2 is incorrect");
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
