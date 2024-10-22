package prodPlanSimulator.UI;

import prodPlanSimulator.domain.Item;

import java.util.Map;

public class FlowDependencyUI implements Runnable {
    @Override
    public void run() {
        Map<String, Map<String, Integer>> flowDependency = Item.calculateFlowDependencyUS07();
        for (Map.Entry<String, Map<String, Integer>> entry : flowDependency.entrySet()) {
            System.out.print(entry.getKey() + " : ");
            for (Map.Entry<String, Integer> dependency : entry.getValue().entrySet()) {
                System.out.print("(" + dependency.getKey() + "," + dependency.getValue() + "), ");
            }
            System.out.println();
        }
    }
}
