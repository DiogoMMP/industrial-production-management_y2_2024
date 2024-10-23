package prodPlanSimulator.UI.Domain.US04;

import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;
import java.util.HashMap;
import java.util.Map;
public class ListExecutionTimeAllOpUI implements Runnable {
    HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();
    @Override
    public void run() {
        System.out.println("\n\n--- Execution Times by Operation  ------------");
        HashMap<String, Double> execTimes = map.calcOpTime();
        for (Map.Entry<String, Double> entry : execTimes.entrySet()) {
            System.out.println("Total time of the operatiom " + entry.getKey() + " : " + entry.getValue());
        }
        Utils.goBackAndWait();
    }
}
