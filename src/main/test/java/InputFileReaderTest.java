import org.junit.jupiter.api.Test;
import prodPlanSimulator.InputFileReader;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Operation;
import prodPlanSimulator.domain.Workstation;
import prodPlanSimulator.enums.Priority;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InputFileReaderTest {

    private static final String FILE_PATH_ITEMS = "test_files/articles.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations.csv";

    @Test
    public void testReadArticles() throws FileNotFoundException {
        /*
        Map<Integer, Item> items = InputFileReader.readArticles(FILE_PATH_ITEMS);

        assertNotNull(items, "The items list should not be null.");
        assertEquals(24, items.size(), "The number of items read from the file should be 24.");

        Item item1 = items.get(10);
        assertNotNull(item1, "Item 20 should exist.");
        assertEquals(Priority.NORMAL, item1.getPriority(), "The priority of item 20 should be NORMAL.");
        List<Operation> operationsItem1 = item1.getOperations();
        assertEquals(List.of("CUT", "POLISH", "VARNISH", "PACK"), operationsItem1, "The operations for item 20 are incorrect.");
            */
       }


    @Test
    public void testReadMachines() throws FileNotFoundException {
        /*
        Map<Integer, Workstation> machines = InputFileReader.readMachines(FILE_PATH_MACHINES);

        assertNotNull(machines, "The machines list should not be null.");
        assertEquals(20, machines.size(), "The number of machines read from the file should be 20.");

        Workstation machine1 = machines.get(10);
        assertNotNull(machine1, "Workstation ws10 should exist.");
        assertEquals("INSPECT", machine1.getOperation(), "The operation for machine ws10 are incorrect.");
        assertEquals(8, machine1.getTime(), "The total time for machine ws10 is incorrect.");

         */
    }
}
