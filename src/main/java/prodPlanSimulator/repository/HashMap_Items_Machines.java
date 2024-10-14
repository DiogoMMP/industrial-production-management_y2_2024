package prodPlanSimulator.repository;

import prodPlanSimulator.InputFileReader ;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Machine;


import java.util.ArrayList;
import java.util.HashMap;
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

    public int calcOpTime(String operation) throws Exception {
        HashMap<Item, Machine> op = getProdPlan();
        if (op.isEmpty()) {
            throw new Exception("Operation not found");
        }
        for (Map.Entry<Item, Machine> item : op.entrySet()) {
            if (item.getValue().getOperations().equals(operation)) {
                return item.getValue().getTime();
            } else {
                throw new Exception("Operation not found");
            }
        }
        return 0;
    }
}
