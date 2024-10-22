package prodPlanSimulator.repository;

import prodPlanSimulator.InputFileReader;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMap_Items_Machines {
    private HashMap<Item, Workstation> ProdPlan;

    public HashMap_Items_Machines() {
        this.ProdPlan = new HashMap<>();
    }

    /**
     * Constructor with ProdPlan
     *
     * @param ProdPlan
     */
    public HashMap_Items_Machines(HashMap<Item, Workstation> ProdPlan) {
        this.ProdPlan = ProdPlan;
    }

    /**
     * Add all items and machines to the map
     *
     * @param itemsPath
     * @param machinesPath
     */
    public void addAll(String itemsPath, String machinesPath) {
        Map<Integer, Item> items = InputFileReader.readItems(itemsPath);
        Map<Integer, Workstation> machines = InputFileReader.readMachines(machinesPath);
        try {
            if (items.isEmpty() || machines.isEmpty()) {
                throw new Exception("Items or Machines not found");
            }
            fillMap(items, machines);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    /**
     * Fill the map with items and machines
     *
     * @param items
     * @param machines
     */
    public void fillMap(Map<Integer, Item> items, Map<Integer, Workstation> machines) {
        int size = Math.max(items.size(), machines.size());
        Item item = new Item();
        Workstation workstation = new Workstation();
        for (int i = 1; i <= size; i++) {
            if (items.get(i) != null) {
                item = items.get(i);
            } else if (items.get(i) == null) {
                item = new Item();
            }
            if (machines.get(i) != null) {
                workstation = machines.get(i);
            } else if (machines.get(i) == null) {
                workstation = new Workstation();
            }
            ProdPlan.put(item, workstation);
        }
    }

    /**
     * Get the production plan
     *
     * @return production plan
     */
    public HashMap<Item, Workstation> getProdPlan() {
        return ProdPlan;
    }

    /**
     * Calculate the time of a specific operation
     *
     * @return time of the operation
     * @throws Exception if operation not found
     */
    public HashMap<String, Double> calcOpTime(String operation) throws Exception {
        HashMap<Item, Workstation> op = getProdPlan();
        HashMap<String, Double> OpTime = new HashMap<>();
        double totalTime = 0.0;

        for (Map.Entry<Item, Workstation> entry : op.entrySet()) {
            Item item = entry.getKey();
            Workstation workstation = entry.getValue();

            // Check if the workstation performs the specified operation
            if (workstation.getOperation().equals(operation)) {
                totalTime += workstation.getTime();
            }

            // Check if the item has the specified operation
            if (item.getOperations().contains(operation)) {
                totalTime += workstation.getTime();
            }
        }

        OpTime.put(operation, totalTime);
        return OpTime;
    }


    /**
     * List workstations by ascending order
     */
    public void listWorkstationsByAscOrder() {
        HashMap<Item, Workstation> op = getProdPlan();
        int totalExecutionTime = 0;

        // Calculate total execution time
        for (Map.Entry<Item, Workstation> entry : op.entrySet()) {
            Workstation workstation = entry.getValue();

            totalExecutionTime += workstation.getTime();

        }

        // Store workstations with total time and percentage
        List<Map.Entry<Workstation, Double>> workstations = new ArrayList<>();
        for (Map.Entry<Item, Workstation> entry : op.entrySet()) {
            Workstation workstation = entry.getValue();
            int totalTime = 0;
            totalTime += workstation.getTime();
            double percentage = (double) totalTime / totalExecutionTime * 100;
            workstations.add(Map.entry(workstation, percentage));
        }

        // Sort workstations by percentage in ascending order
        workstations.sort(Map.Entry.comparingByValue());

        // Print sorted workstations with total time and percentage
        for (Map.Entry<Workstation, Double> entry : workstations) {
            Workstation workstation = entry.getKey();
            double percentage = entry.getValue();
            System.out.println("Workstation ID: " + workstation.getId() + ", Total Time: " + workstation.getTime() + ", Percentage: " + String.format("%.2f", percentage) + "%");
        }
    }

    /**
     * Set the production plan
     *
     * @param prodPlan
     */
    public void setProdPlan(HashMap<Item, Workstation> prodPlan) {
        this.ProdPlan = prodPlan;
    }
}
