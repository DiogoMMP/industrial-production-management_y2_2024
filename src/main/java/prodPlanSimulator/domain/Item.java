package prodPlanSimulator.domain;

import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.HashMap_Items_Machines;

import java.util.*;

public class Item implements Comparable<Item> {
    private int id;
    private Priority priority;
    private List<String> operations;
    private int currentOperationIndex;
    private static HashMap_Items_Machines HashMap_Items_Machines = Instances.getInstance().getHashMap_Items_Machines();


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

    public static HashMap<String, Double> simulateProcess() {
        HashMap<Item, Machine> ProdPlan = HashMap_Items_Machines.getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Machine> machines = new ArrayList<>(ProdPlan.values());
        removeNullMachines(machines);
        removeNullItems(ProdPlan);
        sortByTime(machines);
        // AC1 - Create the operationsQueue with the list of the items for each operation
        fillOperationsQueue(ProdPlan, operationsQueue);
        // AC2 - Assign the items to the machines
        HashMap<String, Double> timeOperations = new HashMap<>();
        fillUpMachines(operationsQueue, machines, timeOperations);

        return timeOperations;
    }

    private static void sortByTime(ArrayList<Machine> machines) {
        for (int i = 0; i < machines.size() - 1; i++) {
            if (machines.get(i).getTime() > machines.get(i + 1).getTime()) {
                Machine temp = machines.get(i);
                machines.set(i, machines.get(i + 1));
                machines.set(i + 1, temp);
            }
        }
    }

    /**
     * Removes the null machines from the list of machines
     *
     * @param machines List of machines
     */
    private static void removeNullMachines(ArrayList<Machine> machines) {
        machines.removeIf(machine -> machine.getId().equalsIgnoreCase(""));
    }

    /**
     * Removes the null items from the list of items
     *
     * @param ProdPlan HashMap with the items and the machines
     */

    private static void removeNullItems(HashMap<Item, Machine> ProdPlan) {
        ProdPlan.entrySet().removeIf(entry -> entry.getKey().getId() == 0);
    }

    /**
     * US6: Present average execution times per operation and corresponding waiting times.
     *
     * @return HashMap<String, Double [ ]> where String is the operation and Double[] holds:
     * [average execution time, average waiting time]
     */
    public static HashMap<String, Double[]> calculateAvgExecutionAndWaitingTimes() {

        HashMap<String, Double[]> operationTimes = new HashMap<>();
        HashMap<String, Double[]> waitingTimes = new HashMap<>();
        HashMap<Item, Machine> ProdPlan = HashMap_Items_Machines.getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Machine> machines = new ArrayList<>(ProdPlan.values());

        // Fill the operationsQueue with items waiting for each operation
        fillOperationsQueue(ProdPlan, operationsQueue);

        // Track execution and waiting times for each operation
        for (String operation : operationsQueue.keySet()) {
            LinkedList<Item> items = operationsQueue.get(operation);
            double totalExecutionTime = 0.0;
            double totalWaitingTime = 0.0;

            for (Item item : items) {
                double executionTime = 0.0;
                double waitingTime = 0.0;

                for (Machine machine : machines) {
                    if (machine.getOperations().contains(operation)) {
                        executionTime = machine.getTime();
                        totalExecutionTime += executionTime;

                        // Assuming waiting time is calculated as the time an item spent in the queue before being processed
                        waitingTime = machine.getTime();  // Assume each machine tracks its own waiting time
                        totalWaitingTime += waitingTime;
                    }
                }
            }

            int numItems = items.size();
            double avgExecutionTime = numItems == 0 ? 0 : totalExecutionTime / numItems;
            double avgWaitingTime = numItems == 0 ? 0 : totalWaitingTime / numItems;

            operationTimes.put(operation, new Double[]{avgExecutionTime, avgWaitingTime});
        }

        return operationTimes;
    }

    /**
     * Fills the machines with the items
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param machines        List of machines
     */
    private static void fillUpMachines(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Machine> machines, HashMap<String, Double> timeOperations) {
        int quantMachines = machines.size();
        for (String operation : operationsQueue.keySet()) {
            LinkedList<Item> items = operationsQueue.get(operation);
            sortByPriority(items);
            for (Item item : items) {
                for (Machine machine : machines) {
                    if (item.getCurrentOperationIndex() >= item.getOperations().size()) {
                        break;
                    }
                    for (String operationMachine : machine.getOperations()) {
                        if (operationMachine.equalsIgnoreCase(operation) && !machine.getHasItem()) {
                            quantMachines = checkMachines(machines, quantMachines);
                            machine.setHasItem(false);
                            item.setCurrentOperationIndex(item.getCurrentOperationIndex() + 1);
                            quantMachines--;
                            String operation1 = "Operation: " + operation + " - Machine: " + machine.getId() + " - Priority: " + item.getPriority() + " - Item: " + item.getId() + " - Time: " + machine.getTime();
                            timeOperations.put(operation1, timeOperations.getOrDefault(operation, 0.0) + machine.getTime());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * US7: Produce a listing representing the flow dependency between workstations.
     *
     * @return HashMap<String, List < Tuple < String, Integer>>> where String is the current workstation,
     * and the List holds tuples of the next workstation and the number of transitions.
     */
    public static HashMap<String, List<Tuple<String, Integer>>> generateWorkstationFlowDependency() {
        HashMap<String, List<Tuple<String, Integer>>> flowDependency = new HashMap<>();
        HashMap<Item, Machine> ProdPlan = HashMap_Items_Machines.getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Machine> machines = new ArrayList<>(ProdPlan.values());

        // Create a map to track transitions between machines
        for (Item item : ProdPlan.keySet()) {
            List<String> operations = item.getOperations();
            Machine prevMachine = null;

            for (int i = 0; i < operations.size(); i++) {
                String operation = operations.get(i);
                Machine currMachine = null;

                // Find the machine that performs the current operation
                for (Machine machine : machines) {
                    if (machine.getOperations().contains(operation)) {
                        currMachine = machine;
                        break;
                    }
                }

                // If there's a previous machine, record the transition
                if (prevMachine != null && currMachine != null) {
                    String prevMachineId = prevMachine.getId();
                    String currMachineId = currMachine.getId();

                    if (!flowDependency.containsKey(prevMachineId)) {
                        flowDependency.put(prevMachineId, new ArrayList<>());
                    }

                    List<Tuple<String, Integer>> transitions = flowDependency.get(prevMachineId);
                    boolean found = false;
                    for (Tuple<String, Integer> transition : transitions) {
                        if (transition.getFirst().equals(currMachineId)) {
                            transition.setSecond(transition.getSecond() + 1);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        transitions.add(new Tuple<>(currMachineId, 1));
                    }
                }

                prevMachine = currMachine;  // Move to the next machine
            }
        }

        return flowDependency;
    }

    private static int checkMachines(ArrayList<Machine> machines, int quantMachines) {
        if (quantMachines == 0) {
            for (Machine machine1 : machines) {
                machine1.clearUpMachine();
            }
            quantMachines = machines.size();
        }
        return quantMachines;
    }


    /**
     * Fills the operationsQueue with the list of the items for each operation
     *
     * @param ProdPlan        HashMap with the items and the machines
     * @param operationsQueue HashMap with the operations and the list of items
     */
    private static void fillOperationsQueue(HashMap<Item, Machine> ProdPlan, HashMap<String, LinkedList<Item>> operationsQueue) {
        for (Item item : ProdPlan.keySet()) {
            ArrayList<String> operations = (ArrayList<String>) item.getOperations();
            for (String operation : operations) {
                if (!operationsQueue.containsKey(operation)) {
                    operationsQueue.put(operation, new LinkedList<>());
                }
                operationsQueue.get(operation).add(item);
            }
        }
    }

    /**
     * Sorts items by their priority (high, normal, low)
     *
     * @param items Queue of items to be sorted
     */
    private static void sortByPriority(LinkedList<Item> items) {
        ArrayList<Item> itemsList = new ArrayList<>(items);
        sortHigh(itemsList, items);
        sortNormal(itemsList, items);
        sortLow(itemsList, items);
        items.clear();
        items.addAll(itemsList);
    }

    private static void sortHigh(ArrayList<Item> itemsList, LinkedList<Item> items) {
        for (Item item : items) {
            if (item.getPriority().toString().equalsIgnoreCase("HIGH")) {
                itemsList.add(item);
            }
        }
    }

    private static void sortNormal(ArrayList<Item> items, LinkedList<Item> itemsList) {
        for (Item item : items) {
            if (item.getPriority().toString().equalsIgnoreCase("NORMAL")) {
                itemsList.add(item);
            }
        }
    }

    private static void sortLow(ArrayList<Item> items, LinkedList<Item> itemsList) {
        for (Item item : items) {
            if (item.getPriority().toString().equalsIgnoreCase("LOW")) {
                itemsList.add(item);
            }
        }
    }


    /**
     * Calculates the total production time per item
     *
     * @return HashMap with the total production time per item
     */
    public HashMap<Item, Double> calculateTotalProductionTimePerItem() {
        double totalProductionTime = 0.0;
        HashMap<Item, Double> totalProductionTimePerItem = new HashMap<>();

        for (Map.Entry<Item, Machine> entry : HashMap_Items_Machines.getProdPlan().entrySet()) {
            Item item = entry.getKey();
            Machine machine = entry.getValue();

            for (String operation : item.getOperations()) {

                if (machine.getOperations().equals(operation)) {
                    totalProductionTime += machine.getTime();
                }
            }
            totalProductionTimePerItem.put(item, totalProductionTime);
            totalProductionTime = 0.0;

        }

        return totalProductionTimePerItem;
    }

    @Override
    public int compareTo(Item o) {
        return Integer.compare(this.id, o.id);
    }

}

