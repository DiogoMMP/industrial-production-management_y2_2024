package prodPlanSimulator.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {

    private HashMap_Items_Machines hashMapItemsMachines;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

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
        workstation.setOperation(null);
        ProdPlan.put(item, workstation);
        assertEquals(21, hashMapItemsMachines.calcOpTime("cut"));
    }
}