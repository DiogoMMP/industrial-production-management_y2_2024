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
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        HashMap<String, Double> timeOperations = new HashMap<>();
        fillUpMachines(operationsQueue, machines, timeOperations, items);

        return timeOperations;
    }

    private static void sortByTime(ArrayList<Machine> machines) {
        machines.sort(Comparator.comparingInt(Machine::getTime));
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
    /**
     * US6: Present average execution times per operation and corresponding waiting times.
     *
     * @return HashMap<String, Double[]> where String is the operation and Double[] holds:
     * [average execution time, average waiting time]
     */
    public static HashMap<String, Double[]> calculateAvgExecutionAndWaitingTimes() {
        HashMap<String, Double[]> operationTimes = new HashMap<>();
        HashMap<Item, Machine> ProdPlan = Instances.getInstance().getHashMap_Items_Machines().getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Machine> machines = new ArrayList<>(ProdPlan.values());
        removeNullMachines(machines);
        removeNullItems(ProdPlan);
        // Fill the operationsQueue with items waiting for each operation
        fillOperationsQueue(ProdPlan, operationsQueue);

        // Track execution and waiting times for each operation
        for (String operation : operationsQueue.keySet()) {
            LinkedList<Item> items = operationsQueue.get(operation);
            double totalExecutionTime = 0.0;
            double totalWaitingTime = 0.0;
            int itemCount = 0;

            for (Item item : items) {
                for (Machine machine : machines) {
                    if (machine.getOperations().contains(operation)) {
                        totalExecutionTime += machine.getTime();
                        // Assuming waiting time is the sum of execution times of items ahead in the queue
                        totalWaitingTime += totalExecutionTime - machine.getTime();
                        itemCount++;
                        break;
                    }
                }
            }

            double avgExecutionTime = itemCount == 0 ? 0 : totalExecutionTime / itemCount;
            double avgWaitingTime = itemCount == 0 ? 0 : totalWaitingTime / itemCount;

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
    private static void fillUpMachines(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Machine> machines, HashMap<String, Double> timeOperations, ArrayList<Item> items) {
        int quantMachines = machines.size();
        sortByPriority(items);
        for (Item item : items) {
            for (Machine machine : machines) {
                if (item.getCurrentOperationIndex() >= item.getOperations().size()) {
                    break;
                }
                for (String operation : machine.getOperations()) {
                    quantMachines = checkIfMach(machines, quantMachines, operation);
                    if (operationsQueue.get(operation).contains(item) && (!machine.getHasItem())) {
                        quantMachines = checkMachines(machines, quantMachines);
                        machine.setHasItem(true);
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

    private static int checkIfMach(ArrayList<Machine> machines, int quantMachines, String operation) {
        boolean free = true;
        int quant = 0;
        ArrayList<Machine> Tempmachines = new ArrayList<>();
        for (Machine machine : machines) {
            if (machine.getOperations().contains(operation) && machine.getHasItem()) {
                quant++;
                Tempmachines.add(machine);
            } else if (machine.getOperations().contains(operation) && !machine.getHasItem()) {
                free = false;
            }
        }
        if (quant >= 1 && free) {
            for (Machine machine : Tempmachines) {
                machine.setHasItem(false);
            }
            quantMachines = quantMachines + quant;
        }
        return quantMachines;
    }


    /**
     * US7: Produce a listing representing the flow dependency between workstations.
     *
     * @return HashMap<String, List<Map.Entry<String, Integer>>> where String is the current workstation,
     * and the List holds entries of the next workstation and the number of transitions.
     */
    public static HashMap<String, List<Map.Entry<String, Integer>>> generateWorkstationFlowDependency() {
        HashMap<String, List<Map.Entry<String, Integer>>> flowDependency = new HashMap<>();
        HashMap<Item, Machine> ProdPlan = Instances.getInstance().getHashMap_Items_Machines().getProdPlan();
        removeNullItems(ProdPlan);
        for (Item item : ProdPlan.keySet()) {
            List<String> operations = item.getOperations();
            for (int i = 0; i < operations.size() - 1; i++) {
                String currentOperation = operations.get(i);
                String nextOperation = operations.get(i + 1);

                Machine currentMachine = findMachineForOperation(ProdPlan, currentOperation);
                Machine nextMachine = findMachineForOperation(ProdPlan, nextOperation);

                if (currentMachine != null && nextMachine != null) {
                    String currentMachineId = currentMachine.getId();
                    String nextMachineId = nextMachine.getId();

                    flowDependency.computeIfAbsent(currentMachineId, k -> new ArrayList<>());
                    List<Map.Entry<String, Integer>> transitions = flowDependency.get(currentMachineId);

                    updateTransitionCount(transitions, nextMachineId);
                }
            }
        }

        // Sort the flow dependency by the number of processed items in descending order
        return sortFlowDependency(flowDependency);
    }

    private static Machine findMachineForOperation(HashMap<Item, Machine> ProdPlan, String operation) {
        for (Machine machine : ProdPlan.values()) {
            if (machine.getOperations().contains(operation)) {
                return machine;
            }
        }
        return null;
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

    private static void updateTransitionCount(List<Map.Entry<String, Integer>> transitions, String nextMachineId) {
        for (Map.Entry<String, Integer> entry : transitions) {
            if (entry.getKey().equals(nextMachineId)) {
                entry.setValue(entry.getValue() + 1);
                return;
            }
        }
        transitions.add(new AbstractMap.SimpleEntry<>(nextMachineId, 1));
    }

    private static HashMap<String, List<Map.Entry<String, Integer>>> sortFlowDependency(
            HashMap<String, List<Map.Entry<String, Integer>>> flowDependency) {
        LinkedHashMap<String, List<Map.Entry<String, Integer>>> sortedFlowDependency = new LinkedHashMap<>();

        flowDependency.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(
                        e2.getValue().stream().mapToInt(Map.Entry::getValue).sum(),
                        e1.getValue().stream().mapToInt(Map.Entry::getValue).sum()))
                .forEachOrdered(entry -> sortedFlowDependency.put(entry.getKey(), entry.getValue()));

        return sortedFlowDependency;
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
    private static void sortByPriority(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.getPriority().compareTo(o2.getPriority());
            }
        });
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

