package main.repository;

import main.InputFileReader;
import main.domain.Item;
import main.domain.Machine;


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
        Map<Integer, Item> items = InputFileReader.readItems();
        Map<String, Machine> machines = InputFileReader.readMachines();
        for (Map.Entry<Integer, Item> item : items.entrySet()) {
            ProdPlan.put(item.getValue(), machines.get(item.getValue().getOperations().get(0)));
        }
    }

    public HashMap<Item, Machine> getProdPlan() {
        return ProdPlan;
    }
}
