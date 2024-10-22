package prodPlanSimulator.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.InputFileReader;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;
import prodPlanSimulator.enums.Priority;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {

    private HashMap_Items_Machines hashMapItemsMachines;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static final String FILE_PATH_ITEMS = "test_files/articles.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations.csv";

    @BeforeEach
    void setUp() {
        hashMapItemsMachines = new HashMap_Items_Machines();
        System.setOut(new PrintStream(outContent));
        Map<Integer, Item> items = InputFileReader.readItems(FILE_PATH_ITEMS);
        Map<Integer, Workstation> machines = InputFileReader.readMachines(FILE_PATH_MACHINES);
        hashMapItemsMachines.fillMap(items, machines);
    }

    @Test
    void calcOpTime() throws Exception {
        HashMap<Item, Workstation> ProdPlan = new HashMap<>();
        Item item = new Item();
        Item item2 = new Item();
        Item item3 = new Item();

        item.setId(10001);
        item2.setId(10002);
        item3.setId(10003);

        Workstation workstation = new Workstation();
        Workstation workstation2 = new Workstation();
        Workstation workstation3 = new Workstation();

        workstation.setId("1");
        workstation.setOperation("cut");
        workstation.setTime(21);
        workstation2.setId("2");
        workstation2.setOperation("cut");
        workstation2.setTime(10);
        workstation3.setId("3");
        workstation3.setOperation("sand");
        workstation3.setTime(15);
        ProdPlan.put(item, workstation);
        ProdPlan.put(item2, workstation2);
        ProdPlan.put(item3, workstation3);

        hashMapItemsMachines.setProdPlan(ProdPlan);
        assertEquals(31, hashMapItemsMachines.calcOpTime("cut"));
        assertEquals(15, hashMapItemsMachines.calcOpTime("sand"));

    }
    @Test
    void listWorkstationsByAscOrder() {
        // Mock data
        Item item1 = new Item();
        Item item2 = new Item();
        Item item3 = new Item();
        Item item4 = new Item();
        Item item5 = new Item();
        Workstation workstation1 = new Workstation();
        Workstation workstation2 = new Workstation();
        Workstation workstation3 = new Workstation();
        Workstation workstation4 = new Workstation();
        Workstation workstation5 = new Workstation();


        item1.setId(10001);
        item1.setPriority(Priority.HIGH);
        item1.setOperations(List.of(new String[]{"cut", "sand", "paint"}));

        item2.setId(10002);
        item2.setPriority(Priority.LOW);
        item2.setOperations(List.of(new String[]{"drill", "polish"}));

        item3.setId(10003);
        item3.setPriority(Priority.NORMAL);
        item3.setOperations(List.of(new String[]{"cut", "polish"}));

        item4.setId(10004);
        item4.setPriority(Priority.NORMAL);
        item4.setOperations(List.of(new String[]{"cut", "paint"}));

        item5.setId(10005);
        item5.setPriority(Priority.HIGH);
        item5.setOperations(List.of(new String[]{"drill", "sand"}));

        workstation1.setId("Workstation1");
        workstation1.setOperation("cut");
        workstation1.setTime(20);

        workstation2.setId("Workstation2");
        workstation2.setOperation("sand");
        workstation2.setTime(10);

        workstation3.setId("Workstation3");
        workstation3.setOperation("cut");
        workstation3.setTime(25);

        workstation4.setId("Workstation4");
        workstation4.setOperation("paint");
        workstation4.setTime(15);

        workstation5.setId("Workstation5");
        workstation5.setOperation("drill");
        workstation5.setTime(30);

        HashMap<Item, Workstation> prodPlan = new HashMap<>();
        prodPlan.put(item1, workstation1);
        prodPlan.put(item2, workstation2);
        prodPlan.put(item3, workstation3);
        prodPlan.put(item4, workstation4);
        prodPlan.put(item5, workstation5);

        hashMapItemsMachines.setProdPlan(prodPlan);

        // Call the method
        hashMapItemsMachines.listWorkstationsByAscOrder();

        // Verify the output
        String expectedOutput = "Workstation ID: Workstation2, Total Time: 10, Percentage: 10.00%\n" +
                "Workstation ID: Workstation4, Total Time: 15, Percentage: 15.00%\n" +
                "Workstation ID: Workstation1, Total Time: 20, Percentage: 20.00%\n" +
                "Workstation ID: Workstation3, Total Time: 25, Percentage: 25.00%\n" +
                "Workstation ID: Workstation5, Total Time: 30, Percentage: 30.00%\n";

        assertEquals(expectedOutput, outContent.toString());

        // Reset the output stream
        System.setOut(originalOut);
    }
}
