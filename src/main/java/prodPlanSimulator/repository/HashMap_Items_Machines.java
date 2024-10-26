package prodPlanSimulator.repository;

import prodPlanSimulator.InputFileReader;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;


import java.io.FileNotFoundException;
import java.util.*;

public class HashMap_Items_Machines {
    private HashMap<Item, Workstation> ProdPlan;

    public HashMap_Items_Machines() {
        this.ProdPlan = new HashMap<>();
    }

    /**
     * Constructor with ProdPlan
     *
     * @param ProdPlan production plan
     */
    public HashMap_Items_Machines(HashMap<Item, Workstation> ProdPlan) {
        this.ProdPlan = ProdPlan;
    }

    /**
     * Add all items and machines to the map
     *
     * @param itemsPath   path to items
     * @param machinesPath path to machines
     */
    public void addAll(String itemsPath, String machinesPath) throws FileNotFoundException {
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
     * @param items   items
     * @param machines machines
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
     */
    public HashMap<String, Double> calcOpTime(LinkedHashMap<String, Double> timeOperations) {
        try {
            HashMap<String, Double> OpTime = new HashMap<>();
            for (String operation : timeOperations.keySet()) {
                String[] parts = operation.split(" - ");
                String operationName = parts[1].split(": ")[1];
                OpTime.putIfAbsent(operationName, 0.0);
                OpTime.put(operationName, OpTime.get(operationName) + timeOperations.get(operation));
            }
            return OpTime;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }


    /**
     * List workstations by ascending order
     */
    public void listWorkstationsByAscOrder(LinkedHashMap<String, Double> timeOperations) {
        int totalExecutionTime = 0;

        // Calculate total execution time
        for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
            Double time = entry.getValue();
            totalExecutionTime += time;
        }

        // Store workstations with total time
        HashMap<String, Double> workstations = new HashMap<>();
        for (String workstation : timeOperations.keySet()) {
            double timeWkStation = 0;
            Workstation ws = new Workstation();
            String[] parts = workstation.split(" - ");
            String workstationName = parts[2].split(": ")[1];
            timeWkStation += timeOperations.get(workstation);
            ws.setId(workstationName);
            if (workstations.containsKey(workstationName)) {
                workstations.put(workstationName, workstations.get(workstationName) + timeWkStation);
            } else {
                workstations.put(workstationName, timeWkStation);
            }
        }

// Calculate the percentage of total time for each workstation
        HashMap<String, Double> workstationPercentages = new HashMap<>();
        for (Map.Entry<String, Double> entry : workstations.entrySet()) {
            double time = entry.getValue();
            double percentage = time / totalExecutionTime * 100;
            workstationPercentages.put(entry.getKey(), percentage);
        }

// Print sorted workstations with total time and percentage
        workstationPercentages.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.printf("%s - Total time: %.0f - Percentage: %.2f%%\n", entry.getKey(), workstations.get(entry.getKey()), entry.getValue()));
    }

    /**
     * Set the production plan
     *
     * @param prodPlan production plan
     */
    public void setProdPlan(HashMap<Item, Workstation> prodPlan) {
        this.ProdPlan = prodPlan;
    }
}
