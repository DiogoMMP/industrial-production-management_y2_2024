package prodPlanSimulator.repository;

import prodPlanSimulator.InputFileReader;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Machine;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HashMap_Items_Machines {
    private HashMap<Item, Machine> ProdPlan;
    public HashMap_Items_Machines() {
        this.ProdPlan = new HashMap<>();
    }

    public HashMap_Items_Machines(HashMap<Item, Machine> ProdPlan) {
        this.ProdPlan = ProdPlan;
    }

    public void addAll(String itemsPath, String machinesPath) {
        Map<Integer, Item> items = InputFileReader.readItems(itemsPath);
        Map<Integer, Machine> machines = InputFileReader.readMachines(machinesPath);
        try {
            if (items.isEmpty() || machines.isEmpty()) {
                throw new Exception("Items or Machines not found");
            }
            fillMap(items, machines);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public void fillMap(Map<Integer, Item> items, Map<Integer, Machine> machines) {
        int size = Math.max(items.size(), machines.size());
        Item item = new Item();
        Machine machine = new Machine();
        for (int i = 1; i <= size; i++) {
            if (items.get(i) != null){
                item = items.get(i);
            } else if (items.get(i) == null){
                item = new Item();
            }
            if (machines.get(i) != null) {
                machine = machines.get(i);
            } else if (machines.get(i) == null) {
                machine = new Machine();
            }
            ProdPlan.put(item, machine);
        }
    }


    public HashMap<Item, Machine> getProdPlan() {
        return ProdPlan;
    }

    /**
     * Calculate the time of a specific operation
     *
     * @param operation operation to calculate time
     * @return time of the operation
     * @throws Exception if operation not found
     */
    public int calcOpTime(String operation) throws Exception {
        try {
            HashMap<Item, Machine> op = getProdPlan();
            if (op.isEmpty()) {
                throw new Exception("Operation not found");
            }
            for (Map.Entry<Item, Machine> item : op.entrySet()) {
                if (item.getValue().getOperation().equals(operation)) {
                    return item.getValue().getTime();
                } else {
                    throw new Exception("Operation not found");
                }
            }
            return 0;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    public void listWorkstationsByAscOrder() {
        HashMap<Item, Machine> op = getProdPlan();
        int totalExecutionTime = 0;

        // Calculate total execution time
        for (Map.Entry<Item, Machine> entry : op.entrySet()) {
            Machine machine = entry.getValue();

            totalExecutionTime += machine.getTime();

        }

        // Store workstations with total time and percentage
        List<Map.Entry<Machine, Double>> workstations = new ArrayList<>();
        for (Map.Entry<Item, Machine> entry : op.entrySet()) {
            Machine machine = entry.getValue();
            int totalTime = 0;
            totalTime += machine.getTime();
            double percentage = (double) totalTime / totalExecutionTime * 100;
            workstations.add(Map.entry(machine, percentage));
        }

        // Sort workstations by percentage in ascending order
        workstations.sort(Map.Entry.comparingByValue());

        // Print sorted workstations with total time and percentage
        for (Map.Entry<Machine, Double> entry : workstations) {
            Machine machine = entry.getKey();
            double percentage = entry.getValue();
            System.out.println("Workstation ID: " + machine.getId() + ", Total Time: " + machine.getTime() + ", Percentage: " + String.format("%.2f", percentage) + "%");
        }
    }

    public void setProdPlan(HashMap<Item, Machine> prodPlan) {
        this.ProdPlan = prodPlan;
    }
}
