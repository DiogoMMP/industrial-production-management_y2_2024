package repository;

import importer_and_exporter.InputFileReader;
import domain.Item;
import domain.Operation;
import domain.Workstation;
import prodPlanSimulator.Simulator;


import java.io.FileNotFoundException;
import java.util.*;

public class HashMap_Items_Machines {
    private HashMap<Item, Workstation> ProdPlan;
    private static Simulator simulator = Instances.getInstance().getSimulator();
    private WorkstationRepository workstationRepository;
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




    public void addAll(String operationsPath, String itemsPath, String workstationsPath) throws FileNotFoundException {
        List<Operation> operations = InputFileReader.readListOperations(operationsPath);
        Map<String, String> items = InputFileReader.readItems(itemsPath);
        Map<Integer,Workstation> workstations = InputFileReader.readMachines(workstationsPath, operations);
        workstationRepository = Instances.getInstance().getWorkstationRepository();
        workstationRepository.setWorkstations(workstations);
        HashMap_Items_Machines_Sprint1 hashMapItemsWorkstationsSprint1 = Instances.getInstance().getHashMapItemsWorkstationsSprint1();
        ProdPlan = hashMapItemsWorkstationsSprint1.getProdPlan();
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





    /**
     * Set the production plan
     *
     * @param prodPlan production plan
     */
    public void setProdPlan(HashMap<Item, Workstation> prodPlan) {
        this.ProdPlan = prodPlan;
    }
}
