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
    private static HashMap_Items_Machines HashMap_Items_Machines = Instances.getInstance().getHashMapItemsMachines();
    private LinkedHashMap<String,Integer> lowestTimes;
    ;

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
        this.lowestTimes = new LinkedHashMap<>();
    }

    /**
     * Empty Item Builder
     */
    public Item() {
        this.id = 0;
        this.priority = null;
        this.operations = new ArrayList<>();
        this.currentOperationIndex = 0;
        this.lowestTimes = new LinkedHashMap<>();
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
     * Sets the lowest times of the item
     * @param lowestTimes new lowest times of the item
     */
    public void setLowestTimes(LinkedHashMap<String, Integer> lowestTimes) {
        this.lowestTimes = lowestTimes;
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
     * Gets the lowest times of the item
     *
     * @return lowest times of the item
     */
    public LinkedHashMap<String, Integer> getLowestTimes() {
        return lowestTimes;
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
    public static LinkedHashMap<String, Double> simulateProcess() {
        HashMap<Item, Machine> ProdPlan = HashMap_Items_Machines.getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Machine> machines = new ArrayList<>(ProdPlan.values());
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullMachines(machines);
        removeNullItems(items);
        // AC1 - Create the operationsQueue with the list of the items for each operation
        fillOperationsQueue(items, operationsQueue);
        // AC2 - Assign the items to the machines
        LinkedHashMap<String, Double> timeOperations = new LinkedHashMap<>();
        fillUpMachines(operationsQueue, machines, timeOperations, items);

        return timeOperations;
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
     * @param items list with the items
     */

    private static void removeNullItems(ArrayList<Item> items) {
        items.removeIf(item -> item.getId() == 0);
    }

    /**
     * US6: Present average execution times per operation and corresponding waiting times.
     *
     * @return HashMap<String, Double [ ]> where String is the operation and Double[] holds:
     * [average execution time, average waiting time]
     */
    public static HashMap<String, Double[]> calculateAvgExecutionAndWaitingTimes() {
        HashMap<String, Double[]> operationTimes = new HashMap<>();
        HashMap<Item, Machine> ProdPlan = Instances.getInstance().getHashMapItemsMachines().getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Machine> machines = new ArrayList<>(ProdPlan.values());
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullMachines(machines);
        removeNullItems(items);
        // Fill the operationsQueue with items waiting for each operation
        fillOperationsQueue(items, operationsQueue);

        // Track execution and waiting times for each operation
        for (String operation : operationsQueue.keySet()) {
            double totalExecutionTime = 0.0;
            double totalWaitingTime = 0.0;
            int itemCount = 0;

            for (Item item : items) {
                for (Machine machine : machines) {
                    if (machine.getOperation().contains(operation)) {
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


    private static void sortItemsByPriority(ArrayList<Item> items) {
        items.sort(Comparator.comparing(Item::getPriority));
    }

    private static void sortMachinesByTime(ArrayList<Machine> machines) {
        machines.sort(Comparator.comparingInt(Machine::getTime));
    }

    /**
     * Assigns the items to the machines
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param machines        List of machines
     * @param timeOperations  HashMap with the time of each operation
     * @param items           List of items
     */
    private static void fillUpMachines(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Machine> machines, LinkedHashMap<String, Double> timeOperations, ArrayList<Item> items) {
        int quantMachines = machines.size();
        sortMachinesByTime(machines);
        sortItemsByPriorityAndTime(items, machines);
        for (Item item : items) {
            for (String operation : item.getOperations()) {
                List<Machine> availableMachines = new ArrayList<>();
                for (Machine machine : machines) {
                    if (machine.getOperation().equalsIgnoreCase(operation) && !machine.getHasItem()) {
                        availableMachines.add(machine);
                    }
                }
                availableMachines.sort(Comparator.comparingInt(Machine::getTime));
                checkMachinesWithOperation(machines, quantMachines, operation);
                for (Machine machine : availableMachines) {
                    if (item.getCurrentOperationIndex() >= item.getOperations().size()) {
                        break;
                    }
                    quantMachines = checkMachinesWithOperation(machines, quantMachines, machine.getOperation());
                    if ((operationsQueue.get(machine.getOperation()).contains(item) && machine.getOperation().equalsIgnoreCase(operation)) && (!machine.getHasItem())) {
                        quantMachines = checkMachines(machines, quantMachines);
                        changeStatusMach(machines, machine);
                        item.setCurrentOperationIndex(item.getCurrentOperationIndex() + 1);
                        quantMachines--;
                        String operation1 = "Operation: " + operation + " - Machine: " + machine.getId() + " - Priority: " + item.getPriority() + " - Item: " + item.getId() + " - Time: " + machine.getTime();
                        timeOperations.put(operation1, timeOperations.getOrDefault(machine.getOperation(), 0.0) + machine.getTime());
                        break;
                    }
                }
            }
        }
    }

    private static void sortItemsByPriorityAndTime(ArrayList<Item> items, ArrayList<Machine> machines) {
        sortItemsByPriority(items);
        addTimes(items, machines);
        swapOperations(items);
        items.sort((item1, item2) -> {
            int priorityComparison = item1.getPriority().compareTo(item2.getPriority());
            if (priorityComparison != 0) {
                return priorityComparison;
            }

            // Sort the operations by time for both items
            LinkedHashMap<String, Integer> sortedTimes1 = item1.getLowestTimes().entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);

            LinkedHashMap<String, Integer> sortedTimes2 = item2.getLowestTimes().entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);

            Iterator<Map.Entry<String, Integer>> it1 = sortedTimes1.entrySet().iterator();
            Iterator<Map.Entry<String, Integer>> it2 = sortedTimes2.entrySet().iterator();

            while (it1.hasNext() && it2.hasNext()) {
                Map.Entry<String, Integer> entry1 = it1.next();
                Map.Entry<String, Integer> entry2 = it2.next();
                int timeComparison = Integer.compare(entry1.getValue(), entry2.getValue());
                if (timeComparison != 0) {
                    return timeComparison;
                }
            }
            return 0;
        });
    }

    private static void swapOperations(ArrayList<Item> items) {
        for (Item item : items) {
            List<String> operations = item.getOperations();
            LinkedHashMap<String, Integer> lowestTimes = item.getLowestTimes();
            boolean swapped;
            do {
                swapped = false;
                for (int i = 0; i < operations.size() - 1; i++) {
                    String operation1 = operations.get(i);
                    String operation2 = operations.get(i + 1);
                    if (lowestTimes.get(operation1) > lowestTimes.get(operation2)) {
                        Collections.swap(operations, i, i + 1);
                        swapped = true;
                    }
                }
            } while (swapped);
        }
    }

    private static void addTimes(ArrayList<Item> items, ArrayList<Machine> machines) {
        for (Item item : items) {
            LinkedHashMap<String, Integer> operationTimes = new LinkedHashMap<>();
            for (String operation : item.getOperations()) {
                int minTime = Integer.MAX_VALUE;
                for (Machine machine : machines) {
                    if (machine.getOperation().equalsIgnoreCase(operation)) {
                        minTime = Math.min(minTime, machine.getTime());
                    }
                }
                operationTimes.put(operation, minTime);
            }
            item.setLowestTimes(operationTimes);
        }
    }


    /**
     * Changes the status of the machine
     *
     * @param machines List of machines
     * @param machine  Machine to change the status
     */
    private static void changeStatusMach(ArrayList<Machine> machines, Machine machine) {
        for (Machine machine1 : machines) {
            if (machine.compareTo(machine1) == 0) {
                machine1.setHasItem(true);
            }
        }
    }

    private static int checkMachinesWithOperation(ArrayList<Machine> machines, int quantMachines, String
            operation) {
        boolean free = true;
        int quant = 0;
        ArrayList<Machine> Tempmachines = new ArrayList<>();
        for (Machine machine : machines) {
            if (machine.getOperation().contains(operation) && machine.getHasItem()) {
                quant++;
                Tempmachines.add(machine);
            } else if (machine.getOperation().contains(operation) && !machine.getHasItem()) {
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
     * @return HashMap<String, List < Map.Entry < String, Integer>>> where String is the current workstation,
     * and the List holds entries of the next workstation and the number of transitions.
     */
    public static HashMap<String, List<Map.Entry<String, Integer>>> generateWorkstationFlowDependency() {
        HashMap<String, List<Map.Entry<String, Integer>>> flowDependency = new HashMap<>();
        HashMap<Item, Machine> ProdPlan = Instances.getInstance().getHashMapItemsMachines().getProdPlan();
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullItems(items);
        for (Item item : items) {
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
            if (machine.getOperation().contains(operation)) {
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

    private static void updateTransitionCount(List<Map.Entry<String, Integer>> transitions, String
            nextMachineId) {
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
     * @param items           List with the items
     * @param operationsQueue HashMap with the operations and the list of items
     */
    private static void fillOperationsQueue(ArrayList<Item> items, HashMap<String, LinkedList<Item>> operationsQueue) {
        for (Item item : items) {
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

                if (machine.getOperation().equals(operation)) {
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

