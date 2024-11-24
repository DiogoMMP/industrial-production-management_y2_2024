package UI.Domain.US04;

import UI.Utils.Utils;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.domain.Item;

import java.util.HashMap;
import java.util.Map;
public class ListExecutionTimeAllOpUI implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();

    /**
     * This method calculates the total time of each operation and prints it on the screen.
     */
    @Override
    public void run() {
        System.out.println("\n\n--- Execution Times by Operation  ------------");
        HashMap<String, Double> execTimes = Item.calcOpTime();
        for (Map.Entry<String, Double> entry : execTimes.entrySet()) {
            System.out.println("Total time of the operatiom " + entry.getKey() + " : " + entry.getValue());
        }
        Utils.goBackAndWait();
    }
}
