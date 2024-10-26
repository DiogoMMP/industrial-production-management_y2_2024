package prodPlanSimulator.domain;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataInitializer {

    private HashMap_Items_Machines hashMapItemsMachines;
    public DataInitializer() {
        this.hashMapItemsMachines = Instances.getInstance().getHashMapItemsWorkstations();
    }

    /**
     * Initialize data by reading Items and Machines from CSV files
     */
    public void initializeData() throws FileNotFoundException {
        // Use InputFileReader to read Items and Machines
        hashMapItemsMachines.addAll("articles.csv", "workstations.csv");
    }


    /**
     * Run methods from Item class
     */
    public void runItemMethods() {
        // Assuming that the Item class has a method named 'calculateAvgExecutionAndWaitingTimes'
        HashMap<String, Double[]> result = Item.calculateAvgExecutionAndWaitingTimes();
        HashMap<String, Double> result2 = Item.simulateProcessUS08();
        HashMap<String, Double> result3 = Item.simulateProcessUS02();

        System.out.println("Result: " + result);
        for (Map.Entry<String, Double[]> entry : result.entrySet()) {
            System.out.println("Item: " + entry.getKey());
            for (Double value : entry.getValue()) {
                System.out.println("  Average Time: " + value);
            }
        }
        // Generate workstation flow dependency
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


    public static void main(String[] args) throws FileNotFoundException {
        DataInitializer dataInitializer = new DataInitializer();
        dataInitializer.initializeData();
        dataInitializer.runItemMethods();
    }
}