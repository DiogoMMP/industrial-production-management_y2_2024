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

    public void initializeData() throws FileNotFoundException {
        // Use InputFileReader to read Items and Machines
        hashMapItemsMachines.addAll("articles.csv", "workstations.csv");
    }


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

        // Print the flow dependency
        for (Map.Entry<String, List<Map.Entry<String, Integer>>> entry : flowDependency.entrySet()) {
            System.out.println("Workstation: " + entry.getKey());
            for (Map.Entry<String, Integer> subEntry : entry.getValue()) {
                System.out.println("  Next Workstation: " + subEntry.getKey() + ", Count: " + subEntry.getValue());
            }
        }
        // Print the simulation results
        for (Map.Entry<String, Double> entry : result3.entrySet()) {
            System.out.println("Item: " + entry.getKey());
        }
    }


    public static void main(String[] args) throws FileNotFoundException {
        DataInitializer dataInitializer = new DataInitializer();
        dataInitializer.initializeData();
        dataInitializer.runItemMethods();
    }
}