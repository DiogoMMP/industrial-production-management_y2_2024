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
}
