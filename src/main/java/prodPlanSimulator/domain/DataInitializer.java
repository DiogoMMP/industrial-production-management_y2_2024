package prodPlanSimulator.domain;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataInitializer {

    private HashMap_Items_Machines hashMapItemsMachines;
    public DataInitializer() {
        this.hashMapItemsMachines = Instances.getInstance().getHashMapItemsMachines();
    }

    public void initializeData() {
        // Use InputFileReader to read Items and Machines
        hashMapItemsMachines.addAll("articles.csv", "workstations.csv");
    }

    public void runItemMethods() {
        // Assuming that the Item class has a method named 'calculateAvgExecutionAndWaitingTimes'
        Map<String, Map<String, Double>> result = Item.calculateAverageTimesUS06();
        HashMap<String, Double> result2 = Item.simulateProcessUS08();
        HashMap<String, Double> result3 = Item.simulateProcessUS02();

        for (Map.Entry<String, Map<String, Double>> entry : result.entrySet()) {
            System.out.println("Item: " + entry.getKey());
            for (Map.Entry<String, Double> subEntry : entry.getValue().entrySet()) {
                System.out.println("  Operation: " + subEntry.getKey() + ", Average Time: " + subEntry.getValue());
            }
        }

        // Generate workstation flow dependency
        Map<String, Map<String, Integer>> flowDependency = Item.calculateFlowDependencyUS07();

        // Print the flow dependency
        for (Map.Entry<String, Map<String, Integer>> entry : flowDependency.entrySet()) {
            System.out.println("Workstation: " + entry.getKey());
            for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
                System.out.println("  Next Workstation: " + subEntry.getKey() + ", Count: " + subEntry.getValue());
            }
        }

        // Print the simulation results
        for (Map.Entry<String, Double> entry : result3.entrySet()) {
            System.out.println("Item: " + entry.getKey());
        }
    }

    public static void main(String[] args) {
        DataInitializer dataInitializer = new DataInitializer();
        dataInitializer.initializeData();
        dataInitializer.runItemMethods();
    }
}