package UI.Domain.USEI.US07;

import UI.Utils.Utils;
import domain.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowDependencyUI implements Runnable {
    @Override
    public void run() {
        HashMap<String, List<Map.Entry<String, Integer>>> flowDependency = Item.generateWorkstationFlowDependency();

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Workstation Flow Dependencies ------------------------\n" + Utils.RESET);

        System.out.printf(Utils.BOLD + "%-20s %-100s%n", "Operation", "Dependencies");
        System.out.println("-".repeat(120) + Utils.RESET);

        for (Map.Entry<String, List<Map.Entry<String, Integer>>> entry : flowDependency.entrySet()) {
            String workstation = entry.getKey();
            List<Map.Entry<String, Integer>> dependencies = entry.getValue();

            dependencies.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            StringBuilder sb = new StringBuilder();

            sb.append(String.format("%-20s", workstation));

            for (int i = 0; i < dependencies.size(); i++) {
                Map.Entry<String, Integer> subEntry = dependencies.get(i);
                sb.append(String.format("(%s, %d)", subEntry.getKey(), subEntry.getValue()));

                if (i < dependencies.size() - 1) {
                    sb.append(", ");
                }
            }

            System.out.println(sb);
        }

        Utils.goBackAndWait();
    }


}

