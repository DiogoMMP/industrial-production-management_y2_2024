package prodPlanSimulator.UI;

import prodPlanSimulator.domain.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowDependencyUI implements Runnable {
    @Override
    public void run() {
        HashMap<String, List<Map.Entry<String, Integer>>> flowDependency = Item.generateWorkstationFlowDependency();

        // Print the flow dependency
        for (Map.Entry<String, List<Map.Entry<String, Integer>>> entry : flowDependency.entrySet()) {
            System.out.println("Workstation: " + entry.getKey());
            for (Map.Entry<String, Integer> subEntry : entry.getValue()) {
                System.out.println("  Next Workstation: " + subEntry.getKey() + ", Count: " + subEntry.getValue());
            }
        }
    }
}

