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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class HashMapTest {

    private HashMap_Items_Machines hashMapItemsMachines;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static final String FILE_PATH_ITEMS = "test_files/articles.csv";
    private static final String FILE_PATH_MACHINES = "test_files/workstations.csv";

    @BeforeEach
    void setUp() {
        hashMapItemsMachines = new HashMap_Items_Machines();

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
        assertEquals(31, hashMapItemsMachines.calcOpTime());

    }

    @Test
    void listWorkstationsByAscOrder() {   // Prepare the simulated date

    }
}


