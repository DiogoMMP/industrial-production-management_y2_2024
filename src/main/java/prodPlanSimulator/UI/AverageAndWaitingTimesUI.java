package prodPlanSimulator.UI;


import prodPlanSimulator.domain.Item;

import java.util.Map;

public class AverageAndWaitingTimesUI implements Runnable {
    @Override
    public void run() {
        Map<String, Map<String, Double>> averageTimes = Item.calculateAverageTimesUS06();
        for (Map.Entry<String, Map<String, Double>> entry : averageTimes.entrySet()) {
            System.out.println("Item: " + entry.getKey());
            for (Map.Entry<String, Double> time : entry.getValue().entrySet()) {
                System.out.println("Workstation: " + time.getKey() + ", Time: " + time.getValue());
            }
        }
    }
}
