package prodPlanSimulator.repository;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Machine;

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
        hashMapItemsMachines.addAll();
    }

    @Test
    void calcOpTime() throws Exception {
        HashMap<Item, Machine> ProdPlan = new HashMap<>();
        Item item = new Item();
        item.setId(10001);
        Machine machine = new Machine();
        machine.setId("1");
        machine.setOperations(null);
        machine.setTime(10);
        ProdPlan.put(item, machine);
        assertEquals(21, hashMapItemsMachines.calcOpTime("cut"));
    }
}