package prodPlanSimulator.UI;

import prodPlanSimulator.domain.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowDependencyUI implements Runnable {
    @Override
    public void run() {
        HashMap<String, List<Map.Entry<String, Integer>>> flowDependency = Item.generateWorkstationFlowDependency();
        for (Map.Entry<String, List<Map.Entry<String, Integer>>> entry : flowDependency.entrySet()) {
            String workstation = entry.getKey();
            List<Map.Entry<String, Integer>> dependencies = entry.getValue();

            // Sort dependencies in descending order of processed items
            dependencies.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            // Format the output
            StringBuilder sb = new StringBuilder();
            sb.append(workstation).append(" : [");
            for (int i = 0; i < dependencies.size(); i++) {
                Map.Entry<String, Integer> subEntry = dependencies.get(i);
                sb.append("(").append(subEntry.getKey()).append(",").append(subEntry.getValue()).append(")");
                if (i < dependencies.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            System.out.println(sb.toString());
        }
    }
}

