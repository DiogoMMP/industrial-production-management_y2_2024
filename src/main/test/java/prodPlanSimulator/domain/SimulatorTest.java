package prodPlanSimulator.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest {
    private static final String FILE_PATH_ITEMS = "test_files/articles.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations.csv";
    private static HashMap_Items_Machines hashMap = Instances.getInstance().getHashMapItemsWorkstations();

    @BeforeAll
    static void setUp() {
        hashMap.addAll(FILE_PATH_ITEMS, FILE_PATH_MACHINES);
    }

    @Test
    void simulateProcessUS02() {

        // Setup test data
        List<Item> items = Arrays.asList(
                new Item(23, Priority.NORMAL, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(5, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(23, Priority.NORMAL, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(6, Priority.LOW, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(19, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(24, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(8, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(11, Priority.NORMAL, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(1, Priority.LOW, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(20, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(23, Priority.NORMAL, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(10, Priority.NORMAL, Arrays.asList("CUT", "POLISH")),
                new Item(4, Priority.HIGH, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(3, Priority.LOW, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(9, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(13, Priority.NORMAL, Arrays.asList("CUT", "POLISH")),
                new Item(5, Priority.LOW, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(5, Priority.HIGH, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(2, Priority.LOW, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(13, Priority.NORMAL, Arrays.asList("CUT", "POLISH")),
                new Item(2, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(4, Priority.HIGH, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(7, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(8, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK"))
        );

        List<Workstation> workstations = Arrays.asList(
                new Workstation("ws1", "CUT", 12),
                new Workstation("ws2", "PAINT", 18),
                new Workstation("ws3", "SAND", 10),
                new Workstation("ws4", "DRILL", 15),
                new Workstation("ws5", "POLISH", 14),
                new Workstation("ws6", "GLUE", 9),
                new Workstation("ws7", "WELD", 20),
                new Workstation("ws8", "ASSEMBLE", 22),
                new Workstation("ws9", "VARNISH", 17),
                new Workstation("ws10", "INSPECT", 8),
                new Workstation("ws11", "CUT", 13),
                new Workstation("ws12", "DRILL", 16),
                new Workstation("ws13", "POLISH", 12),
                new Workstation("ws14", "PAINT", 19),
                new Workstation("ws15", "ASSEMBLE", 25),
                new Workstation("ws16", "FINISH", 11),
                new Workstation("ws17", "VARNISH", 21),
                new Workstation("ws18", "GLUE", 10),
                new Workstation("ws19", "SCREW", 11),
                new Workstation("ws20", "PACK", 23)
        );

        LinkedHashMap<String, Double> result = Item.simulateProcessUS02();
        int expectedSize = 0;
        for (Item item : items) {
            for (String operation : item.getOperations()) {
                expectedSize++;
            }
        }
        assertEquals(expectedSize, result.size());
    }

    @Test
    void simulateProcessUS08() {
        // Setup test data
        List<Item> items = Arrays.asList(
                new Item(23, Priority.NORMAL, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(5, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(23, Priority.NORMAL, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(6, Priority.LOW, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(19, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(24, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(8, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(11, Priority.NORMAL, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(1, Priority.LOW, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(20, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(23, Priority.NORMAL, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(10, Priority.NORMAL, Arrays.asList("CUT", "POLISH")),
                new Item(4, Priority.HIGH, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(3, Priority.LOW, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(9, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(13, Priority.NORMAL, Arrays.asList("CUT", "POLISH")),
                new Item(5, Priority.LOW, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(5, Priority.HIGH, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(2, Priority.LOW, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(13, Priority.NORMAL, Arrays.asList("CUT", "POLISH")),
                new Item(2, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH")),
                new Item(4, Priority.HIGH, Arrays.asList("CUT", "DRILL", "SCREW", "POLISH", "VARNISH", "PACK")),
                new Item(7, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK")),
                new Item(8, Priority.NORMAL, Arrays.asList("CUT", "POLISH", "VARNISH", "PACK"))
        );

        List<Workstation> workstations = Arrays.asList(
                new Workstation("ws1", "CUT", 12),
                new Workstation("ws2", "PAINT", 18),
                new Workstation("ws3", "SAND", 10),
                new Workstation("ws4", "DRILL", 15),
                new Workstation("ws5", "POLISH", 14),
                new Workstation("ws6", "GLUE", 9),
                new Workstation("ws7", "WELD", 20),
                new Workstation("ws8", "ASSEMBLE", 22),
                new Workstation("ws9", "VARNISH", 17),
                new Workstation("ws10", "INSPECT", 8),
                new Workstation("ws11", "CUT", 13),
                new Workstation("ws12", "DRILL", 16),
                new Workstation("ws13", "POLISH", 12),
                new Workstation("ws14", "PAINT", 19),
                new Workstation("ws15", "ASSEMBLE", 25),
                new Workstation("ws16", "FINISH", 11),
                new Workstation("ws17", "VARNISH", 21),
                new Workstation("ws18", "GLUE", 10)
        );

        LinkedHashMap<String, Double> result = Item.simulateProcessUS08();
        int expectedSize = 0;
        for (Item item : items) {
            for (String operation : item.getOperations()) {
                expectedSize++;
            }
        }

        assertEquals(expectedSize, result.size());

        // Check the priority order
        Priority previousPriority = Priority.HIGH;
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            String[] parts = entry.getKey().split(" - ");
            Priority currentPriority = Priority.valueOf(parts[3].split(": ")[1].toUpperCase());
            assertTrue(previousPriority.compareTo(currentPriority) <= 0);
            previousPriority = currentPriority;
        }
    }


}

