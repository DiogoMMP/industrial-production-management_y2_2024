package prodPlanSimulator.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;
import prodPlanSimulator.enums.Priority;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {

    private HashMap_Items_Machines hashMapItemsMachines;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        hashMapItemsMachines = new HashMap_Items_Machines();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void calcOpTime() throws Exception {
        HashMap<Item, Workstation> ProdPlan = new HashMap<>();
        Item item = new Item();
        item.setId(10001);
        Workstation workstation = new Workstation();
        workstation.setId("1");
        workstation.setOperation("cut");
        workstation.setTime(21);
        ProdPlan.put(item, workstation);
        hashMapItemsMachines.setProdPlan(ProdPlan);
        assertEquals(21, hashMapItemsMachines.calcOpTime("cut"));
    }
    @Test
    void listWorkstationsByAscOrder() {
        // Mock data
        Item item1 = new Item();
        Item item2 = new Item();
        Workstation workstation1 = new Workstation();
        Workstation workstation2 = new Workstation();

        item1.setId(10001);
        item1.setPriority(Priority.HIGH);
        item1.setOperations(List.of(new String[]{"cut", "sand", "paint"}));

        item2.setId(10002);
        item2.setPriority(Priority.LOW);
        item2.setOperations(List.of(new String[]{"drill", "polish"}));

        workstation1.setId("Workstation1");
        workstation1.setOperation("cut");
        workstation1.setTime(10);

        workstation2.setId("Workstation2");
        workstation2.setOperation("sand");
        workstation2.setTime(20);

        HashMap<Item, Workstation> prodPlan = new HashMap<>();
        prodPlan.put(item1, workstation1);
        prodPlan.put(item2, workstation2);

        hashMapItemsMachines.setProdPlan(prodPlan);

        // Call the method
        hashMapItemsMachines.listWorkstationsByAscOrder();

        // Verify the output
        String expectedOutput = "Workstation ID: Workstation1, Total Time: 10, Percentage: 33.33%\n" +
                "Workstation ID: Workstation2, Total Time: 20, Percentage: 66.67%\n";
        assertEquals(expectedOutput, outContent.toString());

        // Reset the output stream
        System.setOut(originalOut);
    }
}
