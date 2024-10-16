package prodPlanSimulator.domain;
import prodPlanSimulator.InputFileReader;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Machine;
import prodPlanSimulator.repository.HashMap_Items_Machines;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataInitializer {

    private HashMap_Items_Machines hashMapItemsMachines;

    public DataInitializer() {
        this.hashMapItemsMachines = new HashMap_Items_Machines();
    }

    public void initializeData() {
        // Use InputFileReader to read Items and Machines
        Map<Integer, Item> items = InputFileReader.readItems("articles.csv");
        Map<String, Machine> machines = InputFileReader.readMachines("workstations.csv");

        // Add them to ProdPlan
        HashMap<Item, Machine> ProdPlan = new HashMap<>();
        for (Map.Entry<Integer, Item> itemEntry : items.entrySet()) {
            Machine machine = machines.get(itemEntry.getKey().toString());
            if (machine != null) {
                ProdPlan.put(itemEntry.getValue(), machine);
            }
        }

        hashMapItemsMachines.setProdPlan(ProdPlan);
    }

    public void runItemMethods() {
        // Assuming that the Item class has a method named 'calculateAvgExecutionAndWaitingTimes'
        HashMap<String, Double[]> result = Item.calculateAvgExecutionAndWaitingTimes();
        HashMap<String, Double> result2 = Item.simulateProcess();

        for (Map.Entry<String, Double[]> entry : result.entrySet()) {
            System.out.println("Item: " + entry.getKey());
            System.out.println("Execution Time: " + entry.getValue()[0]);
            System.out.println("Waiting Time: " + entry.getValue()[1]);
        }

        // Generate workstation flow dependency
        HashMap<String, List<Map.Entry<String, Integer>>> flowDependency = Item.generateWorkstationFlowDependency();

        // Print the flow dependency
        for (Map.Entry<String, List<Map.Entry<String, Integer>>> entry : flowDependency.entrySet()) {
            System.out.println("Workstation: " + entry.getKey());
            for (Map.Entry<String, Integer> transition : entry.getValue()) {
                System.out.println("Next Workstation: " + transition.getKey() + ", Transitions: " + transition.getValue());
            }
        }

        // Print the simulation results
        for (Map.Entry<String, Double> entry : result2.entrySet()) {
            System.out.println("Item: " + entry.getKey());
        }
    }

    public static void main(String[] args) {
        DataInitializer dataInitializer = new DataInitializer();
        dataInitializer.initializeData();
        dataInitializer.runItemMethods();
    }
}