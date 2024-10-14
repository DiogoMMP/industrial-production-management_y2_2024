import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.enums.Priority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    private Item item1;
    private Item item2;

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
    void simulateProcess() {
        HashMap<String, Double> result = Item.simulateProcess(); // Assume you have a mock for this

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");

        for (String key : result.keySet()) {
            assertTrue(key.contains("Operation:"), "Result should contain 'Operation:'");
            assertTrue(key.contains("Machine:"), "Result should contain 'Machine:'");
            assertTrue(key.contains("Item:"), "Result should contain 'Item:'");
            assertTrue(key.contains("Time:"), "Result should contain 'Time:'");
        }

        double totalTime = result.values().stream().mapToDouble(Double::doubleValue).sum();
        assertTrue(totalTime > 0, "Total process time should be greater than zero");
    }
}