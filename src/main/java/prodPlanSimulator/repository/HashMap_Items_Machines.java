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

    public void addAll() {
        Map<Integer, Item> items = InputFileReader.readItems("articles.csv");
        Map<String, Machine> machines = InputFileReader.readMachines("workstations.csv");
        try {
            if (items.isEmpty() || machines.isEmpty()) {
                throw new Exception("Items or Machines not found");
            }
            verifySize(items, machines);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private void verifySize(Map<Integer, Item> items, Map<String, Machine> machines) {
        if (items.size() > machines.size()) {
            ifMoreItems(items, machines);
        } else if (items.size() < machines.size()) {
            ifMoreMachines(items, machines);
        } else {
            ifEqual(items, machines);
        }
    }

    private void ifEqual(Map<Integer, Item> items, Map<String, Machine> machines) {
        int i = 0;
        for (Map.Entry<Integer, Item> item : items.entrySet()) {
            Machine machine = machines.get(item.getValue().getOperations().get(i));
            ProdPlan.put(item.getValue(), machine);
            i++;
        }
    }

    private void ifMoreMachines(Map<Integer, Item> items, Map<String, Machine> machines) {
        int i = 0;
        for (Map.Entry<String, Machine> machine : machines.entrySet()) {
            Item item = items.get(i);
            if (item != null) {
                ProdPlan.put(item, machine.getValue());
                i++;
            } else {
                ProdPlan.put(new Item(), machine.getValue());
            }
        }
    }

    private void ifMoreItems(Map<Integer, Item> items, Map<String, Machine> machines) {
        int i = 0;
        ArrayList<Machine> listMachines = new ArrayList<>(machines.values());
        for (Map.Entry<Integer, Item> item : items.entrySet()) {

            if (listMachines.size() > i) {
                Machine machine = listMachines.get(i);
                ProdPlan.put(item.getValue(), machine);
                i++;
            } else {
                ProdPlan.put(item.getValue(), new Machine());
            }
        }
    }


    public HashMap<Item, Machine> getProdPlan() {
        return ProdPlan;
    }

    /**
     * Calculate the time of a specific operation
     *
     * @param operation
     * @return time of the operation
     * @throws Exception
     */
    public int calcOpTime(String operation) throws Exception {
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
