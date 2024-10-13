import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import org.junit.jupiter.api.Assertions;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Machine ;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {
    private static HashMap<Item, Machine> ProdPlan = new HashMap<>();
    private static HashMap_Items_Machines hashMapItemsMachines = Instances.getInstance().getHashMap_Items_Machines();
    private static Item item1;

    @BeforeAll
    static void setUp() {
        ProdPlan = hashMapItemsMachines.getProdPlan();
        ProdPlan.keySet().forEach(item -> item.setCurrentOperationIndex(0));
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        items.get(0).setCurrentOperationIndex(0);
        Collections.sort(items);
        item1 = items.get(0);

        item1.setCurrentOperationIndex(0);
    }

    @AfterEach
    void tearDown() {
        item1 = new Item();
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        Collections.sort(items);
        item1 = items.get(0);

        item1.setCurrentOperationIndex(0);
    }

    @Test
    void getId() {
        assertEquals(10001, item1.getId());
    }

    @Test
    void setId() {
        item1.setId(11000);
        assertEquals(11000, item1.getId());
    }

    @Test
    void getPriority() {
        Item item = new Item();
        item.setPriority(Priority.HIGH);
        assertEquals(Priority.HIGH, item.getPriority());
    }

    @Test
    void setPriority() {
        item1.setPriority(Priority.LOW);
        assertEquals(Priority.LOW, item1.getPriority());
    }

    @Test
    void getOperations() {
        Assertions.assertNotNull(item1.getOperations()); // Ensure getOperations does not return null
        assertEquals(4, item1.getOperations().size());
    }

    @Test
    void setOperations() {
        ArrayList<String> operations = new ArrayList<>();
        operations.add("Corte");
        operations.add("Dobra");
        operations.add("Furacao");
        operations.add("Pintura");
        operations.add("Montagem");
        item1.setOperations(operations);
        assertEquals(5, item1.getOperations().size());
    }

    @Test
    void getCurrentOperationIndex() {
        assertEquals(0, item1.getCurrentOperationIndex());
    }

    @Test
    void setCurrentOperationIndex() {
        item1.setCurrentOperationIndex(1);
        assertEquals(1, item1.getCurrentOperationIndex());
    }

    @Test
    void simulateProcess() {
        HashMap <String, Double> result = Item.simulateProcess();
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());

        // Check if the result contains expected operations
        for (String key : result.keySet()) {
            Assertions.assertTrue(key.contains("Operation:"));
            Assertions.assertTrue(key.contains("Machine:"));
            Assertions.assertTrue(key.contains("Item:"));
            Assertions.assertTrue(key.contains("Time:"));
        }

        // Check if the times are correctly calculated
        double totalTime = result.values().stream().mapToDouble(Double::doubleValue).sum();
        Assertions.assertTrue(totalTime > 0);
    }
}