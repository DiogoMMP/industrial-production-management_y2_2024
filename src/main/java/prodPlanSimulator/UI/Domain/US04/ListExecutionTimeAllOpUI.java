package prodPlanSimulator.UI.Domain.US04;

import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.Simulator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
public class ListExecutionTimeAllOpUI implements Runnable {
    HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();
    Simulator simulator = Instances.getInstance().getSimulator();

    /**
     * This method calculates the total time of each operation and prints it on the screen.
     */
    @Override
    public void run() {
        System.out.println("\n\n--- Execution Times by Operation  ------------");
        LinkedHashMap<String, Double> timeOperations = simulator.getTimeOperations();
        HashMap<String, Double> execTimes = map.calcOpTime(timeOperations);
        for (Map.Entry<String, Double> entry : execTimes.entrySet()) {
            System.out.println("Total time of the operatiom " + entry.getKey() + " : " + entry.getValue());
        }
        Utils.goBackAndWait();
    }
}
