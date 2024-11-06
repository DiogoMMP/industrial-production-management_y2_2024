package prodPlanSimulator.domain;

import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Simulator;

import java.util.*;
import java.util.stream.Collectors;

public class Item implements Comparable<Item> {
    private int id;
    private Priority priority;
    private List<String> operations;
    private int currentOperationIndex;
    private static HashMap_Items_Machines HashMap_Items_Workstations = Instances.getInstance().getHashMapItemsWorkstations();
    private LinkedHashMap<String, Integer> lowestTimes;
    private Map<String, Long> entryTimes = new HashMap<>();
    private Map<String, Integer> waitingTimes = new HashMap<>();
    private static Simulator simulator = Instances.getInstance().getSimulator();
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
        this.entryTimes = new HashMap<>();
        this.waitingTimes = new HashMap<>();
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

    public long getEntryTime(String operation) {
        return entryTimes.getOrDefault(operation, 0L);
    }

    public void setEntryTime(String operation, long time) {
        entryTimes.put(operation, time);
    }

    public void setWaitingTime(String operation, int time) {
        waitingTimes.put(operation, time);
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
     * Removes the null machines from the list of machines
     *
     * @param workstations List of machines
     */
    private static void removeNullMachines(ArrayList<Workstation> workstations) {
        workstations.removeIf(machine -> machine.getId().equalsIgnoreCase(""));
    }


    public LinkedHashMap<String, Integer> getLowestTimes() {
        return lowestTimes;
    }

    /**
     * Sets the lowest times of the item
     *
     * @param lowestTimes new lowest times of the item
     */
    public void setLowestTimes(LinkedHashMap<String, Integer> lowestTimes) {
        this.lowestTimes = lowestTimes;
    }


    /**
     * US6: Present average execution times per operation and corresponding waiting times.
     *
     * @return HashMap<String, Double [ ]> where String is the operation and Double[] holds:
     * [average execution time, average waiting time]
     */
    public static HashMap<String, Double[]> calculateAvgExecutionAndWaitingTimes() {
        HashMap<String, Double[]> operationTimes = new HashMap<>();
        HashMap<Item, Workstation> ProdPlan = Instances.getInstance().getHashMapItemsWorkstations().getProdPlan();
        LinkedHashMap<String, Double> timeOperations = simulator.getTimeOperations();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Workstation> machines = new ArrayList<>(ProdPlan.values());
        removeNullMachines(machines);
        removeNullItems(ProdPlan);
        // Fill the operationsQueue with items waiting for each operation
        fillOperationsQueueus06(ProdPlan, operationsQueue);
        // Track execution and waiting times for each operation
        for (String operation : operationsQueue.keySet()) {
            double totalExecutionTime = 0.0;
            double totalWaitingTime = 0.0;
            int itemCount = 0;
            ArrayList<Double> executionTimes = new ArrayList<>();
            LinkedHashMap<String, Integer> workstationsQuantityByOp = new LinkedHashMap<>();
            for (Workstation workstation : machines) {
                if (workstation.getOperation().equalsIgnoreCase(operation)) {
                    workstationsQuantityByOp.put(workstation.getOperation(), workstationsQuantityByOp.getOrDefault(workstation.getOperation(), 0) + 1);
                }
            }
            LinkedHashMap<String, Integer> temp = new LinkedHashMap<>(workstationsQuantityByOp);
            for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
                String[] parts = entry.getKey().split(" - ");
                String operationName = parts[1].split(": ")[1];
                workstationsQuantityByOp.getOrDefault(operationName, 0);
                if (operation.equals(operationName)) {
                    if (executionTimes.isEmpty()){
                        executionTimes.add(0.0);
                        workstationsQuantityByOp.put(operationName, workstationsQuantityByOp.get(operationName) - 1);
                    } else if(workstationsQuantityByOp.get(operationName) > 0){
                        executionTimes.add(executionTimes.get(executionTimes.size() - 1));
                        workstationsQuantityByOp.put(operationName, workstationsQuantityByOp.get(operationName) - 1);
                    } else if (workstationsQuantityByOp.get(operationName) == 0){
                        executionTimes.add(entry.getValue() + executionTimes.get(executionTimes.size() - 1));
                        workstationsQuantityByOp.put(operationName, temp.get(operationName));
                    }
                    totalExecutionTime += entry.getValue();
                    executionTimes.add(entry.getValue());
                    itemCount++;
                }
            }
            for (Double time : executionTimes) {
                totalWaitingTime += time;
            }
            // Calculate averages
            double avgExecutionTime = itemCount > 0 ? totalExecutionTime / itemCount : 0;
            double avgWaitingTime = itemCount > 0 ? totalWaitingTime / itemCount : 0;
            operationTimes.put(operation, new Double[]{avgExecutionTime, avgWaitingTime});
        }

        return operationTimes;
    }


    /**
     * Removes null items from the production plan
     */
    private static void removeNullItems(HashMap<Item, Workstation> prodPlan) {
        prodPlan.keySet().removeIf(Objects::isNull);
    }


    /**
     * US7: Produce a listing representing the flow dependency between workstations.
     *
     * @return HashMap<String, List < Map.Entry < String, Integer>>> where String is the current workstation,
     * and the List holds entries of the next workstation and the number of transitions.
     */


    public static HashMap<String, List<Map.Entry<String, Integer>>> generateWorkstationFlowDependency() {
        HashMap<String, List<Map.Entry<String, Integer>>> flowDependency = new HashMap<>();
        HashMap<Item, Workstation> prodPlan = Instances.getInstance().getHashMapItemsWorkstations().getProdPlan();
        ArrayList<Workstation> machines = new ArrayList<>(prodPlan.values());

        // Remove null entries and initialize flowDependency
        removeNullMachines(machines);
        removeNullItems(prodPlan);
        LinkedHashMap<String, Double> timeOperations = simulator.getTimeOperations();

        for (Workstation machine : machines) {
            flowDependency.put(machine.getId(), new ArrayList<>());
        }

        List<String> entries = new ArrayList<>(timeOperations.keySet());
        ArrayList<String> itemsID = timeOperations.keySet().stream()
                .map(entry -> entry.split(" - ")[4].split(": ")[1])
                .collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < entries.size() - 1; i++) {
            String currentEntry = entries.get(i);
            String nextEntry = entries.get(i + 1);
            String currentItemID = itemsID.get(i);
            String nextItemID = itemsID.get(i + 1);

            String[] currentParts = currentEntry.split(" - ");
            String currentMachineId = currentParts[2].split(": ")[1];

            String[] nextParts = nextEntry.split(" - ");
            String nextMachineId = nextParts[2].split(": ")[1];

            Workstation fromWorkstation = machines.stream()
                    .filter(machine -> machine.getId().equalsIgnoreCase(currentMachineId))
                    .findFirst()
                    .orElse(null);

            Workstation toMachine = machines.stream()
                    .filter(machine -> machine.getId().equalsIgnoreCase(nextMachineId))
                    .findFirst()
                    .orElse(null);

            if (fromWorkstation != null && toMachine != null && currentItemID.equals(nextItemID)) {
                updateTransitions(flowDependency, fromWorkstation.getId(), toMachine.getId());
            }
        }

        return sortWorkstationsByTransitions(flowDependency);
    }




    private static void updateTransitions(HashMap<String, List<Map.Entry<String, Integer>>> flowDependency, String fromMachine, String toMachine) {

        // Check if the transition to `toMachine` already exists
        boolean found = false;
        for (Map.Entry<String, Integer> entry : flowDependency.get(fromMachine)) {
            if (entry.getKey().equals(toMachine)) {
                // Increment the existing transition count
                entry.setValue(entry.getValue() + 1);
                found = true;
                break;
            }
        }
        // If the transition to `toMachine` was not found, add a new entry with a count of 1
        if (!found) {
            flowDependency.get(fromMachine).add(new AbstractMap.SimpleEntry<>(toMachine, 1));
        }
    }


    private static HashMap<String, List<Map.Entry<String, Integer>>> sortWorkstationsByTransitions(
            HashMap<String, List<Map.Entry<String, Integer>>> flowDependency) {

        // Calculate total transitions for each workstation
        Map<String, Integer> totalTransitions = new HashMap<>();
        for (Map.Entry<String, List<Map.Entry<String, Integer>>> entry : flowDependency.entrySet()) {
            int total = entry.getValue().stream()
                    .mapToInt(Map.Entry::getValue)
                    .sum();
            totalTransitions.put(entry.getKey(), total);
        }

        // Create sorted result
        LinkedHashMap<String, List<Map.Entry<String, Integer>>> sortedFlow = new LinkedHashMap<>();

        // Sort workstations by total transitions (descending)
        totalTransitions.entrySet().stream()
                .sorted((e1, e2) -> {
                    int compare = e2.getValue().compareTo(e1.getValue());
                    if (compare == 0) {
                        // If transition counts are equal, sort by workstation ID
                        return e1.getKey().compareTo(e2.getKey());
                    }
                    return compare;
                })
                .forEach(entry -> {
                    String workstationId = entry.getKey();
                    List<Map.Entry<String, Integer>> transitions = flowDependency.get(workstationId);

                    // Sort transitions by count (descending) and then by destination workstation ID
                    transitions.sort((a, b) -> {
                        int compareCount = b.getValue().compareTo(a.getValue());
                        if (compareCount == 0) {
                            return a.getKey().compareTo(b.getKey());
                        }
                        return compareCount;
                    });

                    sortedFlow.put(workstationId, transitions);
                });

        return sortedFlow;
    }


    /**
     * Fills the operationsQueue with the list of the items for each operation
     *
     * @param ProdPlan        HashMap with the items and the machines
     * @param operationsQueue HashMap with the operations and the list of items
     */
    private static void fillOperationsQueueus06(HashMap<Item, Workstation> ProdPlan, HashMap<String, LinkedList<Item>> operationsQueue) {
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
     * Calculates the total production time per item
     *
     * @return HashMap with the total production time per item
     */
    public static TreeMap<Item, Double> calculateTotalProductionTimePerItem() {
        HashMap<Item, Double> totalProductionTimePerItem = new HashMap<>();
        LinkedHashMap<String, Double> timeOperations = simulator.getTimeOperations();

        HashMap<Item, Workstation> ProdPlan = HashMap_Items_Workstations.getProdPlan();
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());

        for (Item item : items) {
            double totalProductionTime = 0.0;
            for (String operation : item.getOperations()) {
                for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
                    if (entry.getKey().contains(operation) && entry.getKey().contains("Item: " + item.getId())) {
                        totalProductionTime += entry.getValue();
                    }
                }
            }
            totalProductionTimePerItem.put(item, totalProductionTime);
        }
        return sortById(removeDuplicateItems(totalProductionTimePerItem));
    }

    /**
     * Calculates the total time of each operation
     * @return HashMap with the total time of each operation
     */
    public static HashMap<String, Double> calcOpTime() {
        LinkedHashMap<String, Double> timeOperations = simulator.getTimeOperations();
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
     * Removes duplicate items from the HashMap
     *
     * @param totalProductionTimePerItem HashMap with the total production time per item
     * @return HashMap with the unique items
     */
    public static HashMap<Item, Double> removeDuplicateItems(HashMap<Item, Double> totalProductionTimePerItem) {
        HashMap<Item, Double> uniqueTotalProductionTimePerItem = new HashMap<>();
        Set<Integer> uniqueIds = new HashSet<>();

        for (Map.Entry<Item, Double> entry : totalProductionTimePerItem.entrySet()) {
            Item item = entry.getKey();
            int itemId = item.getId();

            if (!uniqueIds.contains(itemId)) {
                uniqueTotalProductionTimePerItem.put(item, entry.getValue());
                uniqueIds.add(itemId);
            }
        }

        return uniqueTotalProductionTimePerItem;
    }

    /**
     * Sorts the HashMap by the item ID
     *
     * @param totalProductionTimePerItem HashMap with the total production time per item
     * @return TreeMap sorted by the item ID
     */
    public static TreeMap<Item, Double> sortById(HashMap<Item, Double> totalProductionTimePerItem) {
        TreeMap<Item, Double> sortedMap = new TreeMap<>(Comparator.comparingInt(Item::getId));
        sortedMap.putAll(totalProductionTimePerItem);
        return sortedMap;
    }


    /**
     * Compares this object with the specified object to verify if they are equal.
     *
     * @param o the object to be compared.
     * @return 0 if the objects are equal, 1 if the object is greater than the specified object, -1 if the object is less than the specified object.
     */
    @Override
    public int compareTo(Item o) {
        return Integer.compare(this.id, o.id);
    }

}

