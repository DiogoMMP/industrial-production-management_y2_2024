package repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import domain.Item;
import domain.Operation;
import domain.Workstation;
import enums.Priority;
import prodPlanSimulator.Simulator;
import repository.HashMap_Items_Machines;
import repository.Instances;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {
    private HashMap_Items_Machines hashMapItemsMachines;
    private Item item1;
    private Item item2;
    private Workstation workstation1;
    private Workstation workstation2;

    private static final String FILE_PATH_ITEMS = "test_files/articles.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations.csv";
    private static final String FILE_PATH_OPERATIONS = "test_files/operations.csv";
    private static final String FILE_PATH_ITEMS_LIST = "test_files/items.csv";

    @BeforeEach
    void setUp() {
        hashMapItemsMachines = new HashMap_Items_Machines();

        item1 = new Item();
        item1.setId("10001");
        item1.setPriority(Priority.HIGH);
        Operation operation1 = new Operation();
        operation1.setDescription("cut");
        Operation operation2 = new Operation();
        operation2.setDescription("sand");
        item1.setOperationsRequired(new ArrayList<>(Arrays.asList(operation1, operation2)));

        item2 = new Item();
        item2.setId("10002");
        item2.setPriority(Priority.LOW);
        Operation operation3 = new Operation();
        operation3.setDescription("drill");
        Operation operation4 = new Operation();
        operation4.setDescription("polish");
        item2.setOperationsRequired(new ArrayList<>(Arrays.asList(operation3, operation4)));

        workstation1 = new Workstation();
        workstation1.setId("M1");
        workstation1.setOperation(operation1);
        workstation1.setTime(10);

        workstation2 = new Workstation();
        workstation2.setId("M2");
        workstation2.setOperation(operation2);
        workstation2.setTime(20);
    }

    @Test
    void testAddAll() throws FileNotFoundException {
        // Mock the InputFileReader methods
        List<Operation> operations = Arrays.asList(new Operation("CUT", "cut", 12.0), new Operation("SAND", "sand", 23.0));
        Map<String, String> items = Map.of("10001", "Item 1", "10002", "Item 2");
        Map<Integer, Workstation> workstations = Map.of(1, workstation1, 2, workstation2);

        // Mock the Instances methods
        Instances.getInstance().getWorkstationRepository().setWorkstations(workstations);
        Instances.getInstance().getHashMapItemsWorkstationsSprint1().setProdPlan(new HashMap<>(Map.of(item1, workstation1, item2, workstation2)));

        // Execute the method
        hashMapItemsMachines.addAll(FILE_PATH_OPERATIONS, FILE_PATH_ITEMS_LIST, FILE_PATH_MACHINES);

        // Verify the result
        HashMap<Item, Workstation> prodPlan = hashMapItemsMachines.getProdPlan();
        assertNotNull(prodPlan, "The production plan should not be null");
        assertEquals(2, prodPlan.size(), "The production plan should contain 2 entries");
    }

    @Test
    void testFillMap() {
        Map<Integer, Item> items = Map.of(1, item1, 2, item2);
        Map<Integer, Workstation> machines = Map.of(1, workstation1, 2, workstation2);

        // Execute the method
        hashMapItemsMachines.fillMap(items, machines);

        // Verify the result
        HashMap<Item, Workstation> prodPlan = hashMapItemsMachines.getProdPlan();
        assertNotNull(prodPlan, "The production plan should not be null");
        assertEquals(2, prodPlan.size(), "The production plan should contain 2 entries");
        assertEquals(workstation1, prodPlan.get(item1), "The workstation for item1 is incorrect");
        assertEquals(workstation2, prodPlan.get(item2), "The workstation for item2 is incorrect");
    }

    @Test
    void testGetProdPlan() {
        HashMap<Item, Workstation> prodPlan = new HashMap<>(Map.of(item1, workstation1, item2, workstation2));
        hashMapItemsMachines.setProdPlan(prodPlan);

        // Execute the method
        HashMap<Item, Workstation> result = hashMapItemsMachines.getProdPlan();

        // Verify the result
        assertEquals(prodPlan, result, "The production plan should be returned correctly");
    }

    @Test
    public void testListWorkstationsByAscOrder() {

        Simulator simulator = Instances.getInstance().getSimulator();
        Map<String, Double> timeOperations = simulator.getTimeOperations();


        LinkedHashMap<String, Double> sortedOperations = timeOperations.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));


        double totalTime = sortedOperations.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();


        sortedOperations.forEach((key, time) -> {
            double percentage = (time / totalTime) * 100;
            System.out.printf("%s - Total time: %.0f - Percentage: %.2f%%\n", key, time, percentage);
        });
    }


    @Test
    void testSetProdPlan() {
        HashMap<Item, Workstation> prodPlan = new HashMap<>(Map.of(item1, workstation1, item2, workstation2));

        // Execute the method
        hashMapItemsMachines.setProdPlan(prodPlan);

        // Verify the result
        assertEquals(prodPlan, hashMapItemsMachines.getProdPlan(), "The production plan should be set correctly");
    }
}