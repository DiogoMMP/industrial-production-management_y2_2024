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
    private final PrintStream originalOut = System.out;

    @Test
    void calcOpTime() {



    }
}