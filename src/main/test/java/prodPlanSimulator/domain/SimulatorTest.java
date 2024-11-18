package prodPlanSimulator.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.Simulator;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest {
    private static final String FILE_PATH_ITEMS = "test_files/articles.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations.csv";
    private static HashMap_Items_Machines hashMap = Instances.getInstance().getHashMapItemsWorkstations();
    private static Simulator simulator = Instances.getInstance().getSimulator();
    private static Operation CUT, SCREW, PAINT, DRILL, POLISH, VARNISH, PACK, SAND, GLUE, WELD, ASSEMBLE, INSPECT, FINISH;

    @BeforeAll
    static void setUp() throws FileNotFoundException {
        /*
        hashMap.addAll(FILE_PATH_ITEMS, FILE_PATH_MACHINES);

        // Define all operations used in tests
        CUT = new Operation("CUT1", "cut", 10.0);
        SCREW = new Operation("SCREW2", "screw", 20.0);
        PAINT = new Operation("PAINT3", "paint", 30.0);
        DRILL = new Operation("DRILL4", "drill", 40.0);
        POLISH = new Operation("POLISH5", "polish", 50.0);
        VARNISH = new Operation("VARNISH6", "varnish", 60.0);
        PACK = new Operation("PACK7", "pack", 70.0);
        SAND = new Operation("SAND1", "sand", 10.0);
        GLUE = new Operation("GLUE1", "glue", 9.0);
        WELD = new Operation("WELD1", "weld", 20.0);
        ASSEMBLE = new Operation("ASSEMBLE1", "assemble", 22.0);
        INSPECT = new Operation("INSPECT1", "inspect", 8.0);
        FINISH = new Operation("FINISH1", "finish", 11.0);

         */
    }

    @Test
    void simulateProcessUS02() {
        // Setup test data for items with predefined operations
        List<Item> items = Arrays.asList(
                /*
                new Item(23, Priority.NORMAL, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(5, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(23, Priority.NORMAL, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(6, Priority.LOW, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(19, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(24, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(8, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(11, Priority.NORMAL, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(1, Priority.LOW, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(20, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(23, Priority.NORMAL, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(10, Priority.NORMAL, Arrays.asList(CUT, POLISH)),
                new Item(4, Priority.HIGH, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(3, Priority.LOW, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(9, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(13, Priority.NORMAL, Arrays.asList(CUT, POLISH)),
                new Item(5, Priority.LOW, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(5, Priority.HIGH, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(2, Priority.LOW, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(13, Priority.NORMAL, Arrays.asList(CUT, POLISH)),
                new Item(2, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(4, Priority.HIGH, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(7, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(8, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK))

                 */
        );

        List<Workstation> workstations = Arrays.asList(
                new Workstation("ws1", CUT, 12),
                new Workstation("ws2", PAINT, 18),
                new Workstation("ws3", SAND, 10),
                new Workstation("ws4", DRILL, 15),
                new Workstation("ws5", POLISH, 14),
                new Workstation("ws6", GLUE, 9),
                new Workstation("ws7", WELD, 20),
                new Workstation("ws8", ASSEMBLE, 22),
                new Workstation("ws9", VARNISH, 17),
                new Workstation("ws10", INSPECT, 8),
                new Workstation("ws11", CUT, 13),
                new Workstation("ws12", DRILL, 16),
                new Workstation("ws13", POLISH, 12),
                new Workstation("ws14", PAINT, 19),
                new Workstation("ws15", ASSEMBLE, 25),
                new Workstation("ws16", FINISH, 11),
                new Workstation("ws17", VARNISH, 21),
                new Workstation("ws18", GLUE, 10),
                new Workstation("ws19", SCREW, 11),
                new Workstation("ws20", PACK, 23)
        );

        LinkedHashMap<String, Double> result = simulator.simulateProcessUS02();
        int expectedSize = 0;
        for (Item item : items) {
            for (Operation operation : item.getOperationsRequired()) {
                expectedSize++;
            }
        }
        assertEquals(expectedSize, result.size());
    }

    @Test
    void simulateProcessUS08() {
        /*
        // Setup test data
        List<Item> items = Arrays.asList(
                new Item(23, Priority.NORMAL, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(5, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(23, Priority.NORMAL, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(6, Priority.LOW, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(19, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(24, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(8, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(11, Priority.NORMAL, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(1, Priority.LOW, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(20, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(23, Priority.NORMAL, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(10, Priority.NORMAL, Arrays.asList(CUT, POLISH)),
                new Item(4, Priority.HIGH, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(3, Priority.LOW, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(9, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(13, Priority.NORMAL, Arrays.asList(CUT, POLISH)),
                new Item(5, Priority.LOW, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(5, Priority.HIGH, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(2, Priority.LOW, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(13, Priority.NORMAL, Arrays.asList(CUT, POLISH)),
                new Item(2, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH)),
                new Item(4, Priority.HIGH, Arrays.asList(CUT, DRILL, SCREW, POLISH, VARNISH, PACK)),
                new Item(7, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK)),
                new Item(8, Priority.NORMAL, Arrays.asList(CUT, POLISH, VARNISH, PACK))
        );

        List<Workstation> workstations = Arrays.asList(
                new Workstation("ws1", CUT, 12),
                new Workstation("ws2", PAINT, 18),
                new Workstation("ws3", SAND, 10),
                new Workstation("ws4", DRILL, 15),
                new Workstation("ws5", POLISH, 14),
                new Workstation("ws6", GLUE, 9),
                new Workstation("ws7", WELD, 20),
                new Workstation("ws8", ASSEMBLE, 22),
                new Workstation("ws9", VARNISH, 17),
                new Workstation("ws10", INSPECT, 8),
                new Workstation("ws11", CUT, 13),
                new Workstation("ws12", DRILL, 16),
                new Workstation("ws13", POLISH, 12),
                new Workstation("ws14", PAINT, 19),
                new Workstation("ws15", ASSEMBLE, 25),
                new Workstation("ws16", FINISH, 11),
                new Workstation("ws17", VARNISH, 21),
                new Workstation("ws18", GLUE, 10)
        );

        LinkedHashMap<String, Double> result = simulator.simulateProcessUS08();

        // Validate result size matches expected size
        int expectedSize = 0;
        for (Item item : items) {
            for (Operation operation : item.getOperationsRequired()) {
                expectedSize++;
            }
        }
        assertEquals(expectedSize, result.size());

         */
    }
}


