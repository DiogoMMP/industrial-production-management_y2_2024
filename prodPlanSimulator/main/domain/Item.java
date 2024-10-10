package main.domain;

import main.enums.Priority;
import main.interfaces.Simulator;
import main.repository.HashMap_Items_Machines;
import main.repository.Instances;

import java.util.*;

public class Item implements Simulator {
    private int id;
    private Priority priority;
    private List<String> operations;
    private int currentOperationIndex;
    private HashMap_Items_Machines HashMap_Items_Machines = Instances.getInstance().getHashMap_Items_Machines();


    /**
     * Item Builder
     *
     * @param id         Item ID
     * @param priority   Item priority
     * @param operations Item operations
     */
    public Item(int id, Priority priority, List<String> operations) {
        this.id = id;
        this.priority = priority;
        this.operations = operations;
        this.currentOperationIndex = 0;
    }

    /**
     * Empty Item Builder
     */
    public Item() {
        this.id = 0;
        this.priority = null;
        this.operations = null;
        this.currentOperationIndex = 0;
    }

    // Getters e Setters

    /**
     * Gets the ID of the product
     *
     * @return ID of the product
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the product
     *
     * @param id new ID of the product
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the priority of the item
     *
     * @return priority of the item
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the item
     *
     * @param priority new priority of the item
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Gets the operations of the item
     *
     * @return operations of the item
     */
    public List<String> getOperations() {
        return operations;
    }

    /**
     * Sets the operations of the item
     *
     * @param operations new operations of the item
     */
    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    /**
     * Gets the current operation index of the item
     *
     * @return current operation index of the item
     */
    public int getCurrentOperationIndex() {
        return currentOperationIndex;
    }

    /**
     * Sets the current operation index of the item
     *
     * @param currentOperationIndex new current operation index of the item
     */
    public void setCurrentOperationIndex(int currentOperationIndex) {
        this.currentOperationIndex = currentOperationIndex;
    }

    /**
     * Simulates the process of all the items present in the system
     */
    @Override
    public void simulateProcess() {
        HashMap<Item, Machine> ProdPlan = HashMap_Items_Machines.getProdPlan();
        HashMap<String, Queue<Item>> operationsQueue = new HashMap<>();
        ArrayList<Machine> machines = new ArrayList<>(ProdPlan.values());
        machines.sort(Comparator.comparing(Machine::getTime));
        // AC01 - Create the operationsQueue with the list of the items for each operation
        fillOperationsQueue(ProdPlan, operationsQueue);

        // AC02 - Assign the items to the machines
        // Importante temos de ver se é a melhor maneira de fazer isto
        fillUpMachines(operationsQueue, machines);
    }

    private static void fillUpMachines(HashMap<String, Queue<Item>> operationsQueue, ArrayList<Machine> machines) {
        for (String operation : operationsQueue.keySet()) {
            Queue<Item> items = operationsQueue.get(operation);
            for (Item item : items) {
                for (Machine machine : machines) {
                    if (item.getCurrentOperationIndex() >= item.getOperations().size()) {
                        break;
                    }
                    if (machine.getOperation().equalsIgnoreCase(operation) && machine.getItem() == null) {
                        machine.setItem(item);
                        item.setCurrentOperationIndex(item.getCurrentOperationIndex() + 1);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Fills the operationsQueue with the list of the items for each operation
     *
     * @param ProdPlan        HashMap with the items and the machines
     * @param operationsQueue HashMap with the operations and the list of items
     */
    private static void fillOperationsQueue(HashMap<Item, Machine> ProdPlan, HashMap<String, Queue<Item>> operationsQueue) {
        for (Item item : ProdPlan.keySet()) {
            ArrayList<String> operations = (ArrayList<String>) item.getOperations();
            for (String operation : operations) {
                if (!operationsQueue.containsKey(operation)) {
                    operationsQueue.put(operation, new LinkedList<>());
                    operationsQueue.get(operation).add(item);
                } else if (!operationsQueue.get(operation).contains(item)) {
                    operationsQueue.get(operation).add(item);
                }
            }
        }
    }
}

