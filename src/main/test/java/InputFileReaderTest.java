import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import importer_and_exporter.InputFileReader;
import domain.Item;
import domain.Operation;
import domain.Workstation;
import enums.Priority;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class InputFileReaderTest {

    private static final String FILE_PATH_ITEMS = "test_files/articles2.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations2.csv";
    private static final String FILE_PATH_OPERATIONS = "test_files/operations.csv";
    private static final String FILE_PATH_ITEMS_LIST = "test_files/items.csv";

    private List<Operation> operationsList;
    private Map<String, String> itemsList;

    @BeforeEach
    public void setUp() {
        operationsList = List.of(
                new Operation("CUT", "Cutting", 0.0),
                new Operation("POLISH", "Polishing", 0.0),
                new Operation("VARNISH", "Varnishing", 0.0),
                new Operation("PACK", "Packing", 0.0)
        );

        itemsList = Map.of(
                "10", "Item 10 Description",
                "20", "Item 20 Description"
        );
    }

    @Test
    public void testReadArticles() throws FileNotFoundException {
        Map<Integer, Item> items = InputFileReader.readArticles(FILE_PATH_ITEMS, operationsList, itemsList);

        assertNotNull(items, "The items list should not be null.");
        assertEquals(2, items.size(), "The number of items read from the file should be 2.");

        Item item1 = items.get(1);
        assertNotNull(item1, "Item 1 should exist.");
        assertEquals(Priority.NORMAL, item1.getPriority(), "The priority of item 1 should be NORMAL.");
        assertEquals("Item 10 Description", item1.getDescription(), "The description of item 1 is incorrect.");

        item1.setOperationsRequired(operationsList);
        List<Operation> operationsItem1 = item1.getOperationsRequired();

        List<String> operationDescriptions = operationsItem1.stream()
                .map(Operation::getDescription)
                .collect(Collectors.toList());
        assertEquals(List.of("Cutting", "Polishing", "Varnishing", "Packing"), operationDescriptions, "The operations for item 1 are incorrect.");
    }

    @Test
    public void testReadMachines() throws FileNotFoundException {
        Map<Integer, Workstation> machines = InputFileReader.readMachines(FILE_PATH_MACHINES, operationsList);

        assertNotNull(machines, "The machines list should not be null.");
        assertEquals(2, machines.size(), "The number of machines read from the file should be 2.");

        Workstation machine1 = machines.get(1);

        machine1.setOperation(operationsList.get(0));

        assertNotNull(machine1, "Workstation 1 should exist.");
        assertEquals("CUT", machine1.getOperation().getId(), "The operation for machine 1 is incorrect.");
        assertEquals(8, machine1.getTime(), "The total time for machine 1 is incorrect.");
    }

    @Test
    public void testReadListOperations() {
        List<Operation> operations = InputFileReader.readListOperations(FILE_PATH_OPERATIONS);

        assertNotNull(operations, "The operations list should not be null.");
        assertEquals(4, operations.size(), "The number of operations read from the file should be 4.");

        Operation operation1 = operations.get(0);
        assertEquals("CUT", operation1.getId(), "The ID of the first operation is incorrect.");
        assertEquals("Cutting", operation1.getDescription(), "The description of the first operation is incorrect.");
    }

    @Test
    public void testReadItems() {
        Map<String, String> items = InputFileReader.readItems(FILE_PATH_ITEMS_LIST);

        assertNotNull(items, "The items map should not be null.");
        assertEquals(2, items.size(), "The number of items read from the file should be 2.");

        assertEquals("Item 10 Description", items.get("10"), "The description for item 10 is incorrect.");
    }

    @Test
    public void testReadOperations() {
        Map<String, String> operations = InputFileReader.readOperations(FILE_PATH_OPERATIONS);

        assertNotNull(operations, "The operations map should not be null.");
        assertEquals(4, operations.size(), "The number of operations read from the file should be 4.");

        assertEquals("Cutting", operations.get("CUT"), "The description for operation CUT is incorrect.");
    }
}