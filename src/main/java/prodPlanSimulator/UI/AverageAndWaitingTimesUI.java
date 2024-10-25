package prodPlanSimulator.UI;


import prodPlanSimulator.domain.Item;

import java.util.HashMap;
import java.util.Map;

public class AverageAndWaitingTimesUI implements Runnable {
    @Override
    public void run() {
        HashMap<String, Double[]> averageTimes = Item.calculateAvgExecutionAndWaitingTimes();
        System.out.println("Result: " + averageTimes);
        for (Map.Entry<String, Double[]> entry : averageTimes.entrySet()) {
            System.out.println("Item: " + entry.getKey());
            for (Double value : entry.getValue()) {
                System.out.println("  Average Time: " + value);
            }
        }
    }
}

