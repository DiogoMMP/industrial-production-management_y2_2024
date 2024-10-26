package prodPlanSimulator.UI;


import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.domain.Item;

import java.util.HashMap;
import java.util.Map;

public class AverageAndWaitingTimesUI implements Runnable {
    @Override
    public void run() {
        HashMap<String, Double[]> averageTimes = Item.calculateAvgExecutionAndWaitingTimes();

        for (Map.Entry<String, Double[]> entry : averageTimes.entrySet()) {
            System.out.println("Operation: " + entry.getKey());
            System.out.printf("Average Execution Time: %.2f\n", entry.getValue()[0]);
            System.out.printf("Average Waiting Time: %.2f\n", entry.getValue()[1]);
        }
        Utils.goBackAndWait();
    }
}

