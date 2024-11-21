package prodPlanSimulator.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class HashMapTest {

    private HashMap_Items_Machines hashMap;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        hashMap = new HashMap_Items_Machines();

        // Redirect the console output to capture the printed result
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testCalcOpTime() {
        /*
        // Provide a predefined set of values instead of mocking
        LinkedHashMap<String, Double> timeOperations = new LinkedHashMap<>();
        timeOperations.put("Step 1 - Op: A - Workstation 1: Workstation 1", 5.0);
        timeOperations.put("Step 2 - Op: B - Workstation 2: Workstation 2", 10.0);
        timeOperations.put("Step 3 - Op: A - Workstation 1: Workstation 1", 3.0);

        // Run the method
        HashMap<String, Double> execTimes = hashMap.calcOpTime();

        // Expected result
        HashMap<String, Double> expectedExecTimes = new HashMap<>();
        expectedExecTimes.put("A", 8.0);
        expectedExecTimes.put("B", 10.0);

        // Verify the return value
        assertEquals(expectedExecTimes, execTimes);

         */
    }

    @Test
    public void testListWorkstationsByAscOrder() {
        // Provide a predefined set of values instead of mocking
        LinkedHashMap<String, Double> timeOperations = new LinkedHashMap<>();
        timeOperations.put("Step 1 - Machine A - Workstation 1: Workstation 1", 5.0);
        timeOperations.put("Step 2 - Machine B - Workstation 2: Workstation 2", 10.0);
        timeOperations.put("Step 3 - Machine C - Workstation 1: Workstation 1", 3.0);

        // Run the method
        hashMap.listWorkstationsByAscOrder();

        // Capture and verify the output
        String expectedOutput = "Workstation 1 - Total time: 8 - Percentage: 44.44%\n"
                + "Workstation 2 - Total time: 10 - Percentage: 55.56%\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}