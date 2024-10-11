
package main.domain;

import main.enums.Priority;
import main.repository.HashMap_Items_Machines;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    private static HashMap<Item, Machine> ProdPlan = new HashMap<>();
    private static HashMap_Items_Machines HashMap = new HashMap_Items_Machines(ProdPlan);
    private static Item item;
    private static Item item1;

    @BeforeAll
    static void setUp() {
        HashMap.addAll();
        ProdPlan = HashMap.getProdPlan();
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        item = items.get(0); // Ensure item is initialized properly
        item1 = items.get(0); // Ensure item1 is initialized properly
    }

    @AfterEach
    void tearDown() {
        item1 = new Item();
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        item1 = items.get(0);
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
        assertEquals(Priority.HIGH, item1.getPriority());
    }

    @Test
    void setPriority() {
        item1.setPriority(Priority.LOW);
        assertEquals(Priority.LOW, item1.getPriority());
    }

    @Test
    void getOperations() {
        assertNotNull(item1.getOperations()); // Ensure getOperations does not return null
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
        HashMap<String, Double> result = item.simulateProcess();
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Check if the result contains expected operations
        for (String key : result.keySet()) {
            assertTrue(key.contains("Operation:"));
            assertTrue(key.contains("Machine:"));
            assertTrue(key.contains("Item:"));
            assertTrue(key.contains("Time:"));
        }

        // Check if the times are correctly calculated
        double totalTime = result.values().stream().mapToDouble(Double::doubleValue).sum();
        assertTrue(totalTime > 0);
    }
}