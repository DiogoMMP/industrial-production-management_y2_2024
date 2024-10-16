import org.junit.jupiter.api.Test;
import prodPlanSimulator.InputFileReader;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Machine;
import prodPlanSimulator.enums.Priority;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InputFileReaderTest {

    private static final String FILE_PATH_ITEMS = "test_files/articles.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations.csv";

    @Test
    public void testReadItems() {
        Map<Integer, Item> items = InputFileReader.readItems(FILE_PATH_ITEMS);

        assertNotNull(items, "The items list should not be null.");
        assertEquals(15, items.size(), "The number of items read from the file should be 15.");

        Item item1 = items.get(10001);
        assertNotNull(item1, "Item 10001 should exist.");
        assertEquals(Priority.HIGH, item1.getPriority(), "The priority of item 10001 should be HIGH.");
        List<String> operationsItem1 = item1.getOperations();
        assertEquals(List.of("CUT", "SAND", "PAINT", "INSPECT"), operationsItem1, "The operations for item 10001 are incorrect.");
    }

    @Test
    public void testReadMachines() {
        Map<String, Machine> machines = InputFileReader.readMachines(FILE_PATH_MACHINES);

        assertNotNull(machines, "The machines list should not be null.");
        assertEquals(9, machines.size(), "The number of machines read from the file should be 9.");

        Machine machine1 = machines.get("ws1");
        assertNotNull(machine1, "Machine ws1 should exist.");
        assertEquals(List.of("CUT", "PAINT"), machine1.getOperations(), "The operations for machine ws1 are incorrect.");
        assertEquals(30, machine1.getTime(), "The total time for machine ws1 is incorrect.");
    }
}
