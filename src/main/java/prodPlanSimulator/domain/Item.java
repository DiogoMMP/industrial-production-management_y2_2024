package prodPlanSimulator.domain;

import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.HashMap_Items_Machines;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Item implements Comparable<Item> {
    public Callable<LinkedHashMap<String, Double>> simulateProcessUS02;
    private int id;
    private Priority priority;
    private List<String> operations;
    private int currentOperationIndex;
    private static HashMap_Items_Machines HashMap_Items_Workstations = Instances.getInstance().getHashMapItemsWorkstations();
    private LinkedHashMap<String, Integer> lowestTimes;
    private Map<String, Long> entryTimes = new HashMap<>();
    private Map<String, Integer> waitingTimes = new HashMap<>();

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

    // New methods to resolve errors
    public long getEntryTime(String operation) {
        return entryTimes.getOrDefault(operation, 0L);
    }

    public void setEntryTime(String operation, long time) {
        entryTimes.put(operation, time);
    }

    public void setWaitingTime(String operation, int time) {
        waitingTimes.put(operation, time);
    }


    public static LinkedHashMap<String, Double> simulateProcessUS02() {
        HashMap<Item, Workstation> ProdPlan = HashMap_Items_Workstations.getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Workstation> workstations = new ArrayList<>(ProdPlan.values());
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullMachines(workstations);
        removeNullItems(items);
        // AC1 - Create the operationsQueue with the list of the items for each operation
        fillOperationsQueue(items, operationsQueue);
        // AC2 - Assign the items to the machines
        LinkedHashMap<String, Double> timeOperations = new LinkedHashMap<>();
        fillUpMachinesUS02(operationsQueue, workstations, timeOperations, items);
        for (Workstation workstation : workstations) {
            workstation.clearUpWorkstation();
        }
        for (Item item : items) {
            item.setCurrentOperationIndex(0);
            item.setLowestTimes(new LinkedHashMap<>());
            // Calculate and set waiting times for each operation
            for (String operation : item.getOperations()) {
                long entryTime = item.getEntryTime(operation);
                long currentTime = System.currentTimeMillis();
                int waitTime = (int) (currentTime - entryTime);
                item.setWaitingTime(operation, waitTime);
            }
        }
        return timeOperations;
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
    public static LinkedHashMap<String, Double> simulateProcessUS08() {
        HashMap<Item, Workstation> ProdPlan = HashMap_Items_Workstations.getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Workstation> workstations = new ArrayList<>(ProdPlan.values());
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullMachines(workstations);
        removeNullItems(items);
        fillOperationsQueue(items, operationsQueue);
        LinkedHashMap<String, Double> timeOperations = new LinkedHashMap<>();
        fillUpMachinesUS08(operationsQueue, workstations, timeOperations, items);
        for (Workstation workstation : workstations) {
            workstation.clearUpWorkstation();
        }
        for (Item item : items) {
            item.setCurrentOperationIndex(0);
            item.setLowestTimes(new LinkedHashMap<>());
        }
        return timeOperations;
    }


    /**
     * Removes the null machines from the list of machines
     *
     * @param workstations List of machines
     */
    private static void removeNullMachines(ArrayList<Workstation> workstations) {
        workstations.removeIf(machine -> machine.getId().equalsIgnoreCase(""));
    }

    /**
     * Removes the null items from the list of items
     *
     * @param items list with the items
     */

    private static void removeNullItems(ArrayList<Item> items) {
        items.removeIf(item -> item.getId() == 0);
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
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Workstation> machines = new ArrayList<>(ProdPlan.values());
        removeNullMachines(machines);
        removeNullItems(ProdPlan);

        // Fill the operationsQueue with items waiting for each operation
        fillOperationsQueueus06(ProdPlan, operationsQueue);

        // Track execution and waiting times for each operation
        for (String operation : operationsQueue.keySet()) {
            LinkedList<Item> items = operationsQueue.get(operation);
            double totalExecutionTime = 0.0;
            double totalWaitingTime = 0.0;
            int itemCount = 0;

            // Find the fastest machine for this operation
            Workstation fastestMachine = null;
            double fastestTime = Double.MAX_VALUE;
            for (Workstation machine : machines) {
                if (machine.getOperation().contains(operation) && machine.getTime() < fastestTime) {
                    fastestMachine = machine;
                    fastestTime = machine.getTime();
                }
            }

            if (fastestMachine != null) {
                // Calculate waiting time for each item in the queue
                int position = 0;
                for (Item item : items) {
                    // Execution time is the time of the fastest machine
                    totalExecutionTime += fastestTime;

                    // Waiting time for this item is the execution time of all items ahead in the queue
                    totalWaitingTime += position * fastestTime;

                    position++;
                    itemCount++;
                }

                // Calculate averages
                double avgExecutionTime = itemCount > 0 ? totalExecutionTime / itemCount : 0;
                double avgWaitingTime = itemCount > 0 ? totalWaitingTime / itemCount : 0;

                operationTimes.put(operation, new Double[]{avgExecutionTime, avgWaitingTime});
            }
        }

        return operationTimes;
    }


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
     * Removes null entries from the workstation list
     */
    private static void removeNullWorkstations(ArrayList<Workstation> workstations) {
        workstations.removeIf(Objects::isNull);
    }

    /**
     * Removes null items from the production plan
     */
    private static void removeNullItems(HashMap<Item, Workstation> prodPlan) {
        prodPlan.keySet().removeIf(Objects::isNull);
    }

    /**
     * Fills the operationsQueue with items for each operation
     */
    private static void fillOperationsQueue(HashMap<Item, Workstation> prodPlan, HashMap<String, LinkedList<Item>> operationsQueue) {
        for (Item item : prodPlan.keySet()) {
            if (item.getOperations() != null && !item.getOperations().isEmpty()) {
                String currentOperation = item.getOperations().get(item.getCurrentOperationIndex());
                operationsQueue.computeIfAbsent(currentOperation, k -> new LinkedList<>()).add(item);
            }
        }
    }

    private static void sortItemsByPriority(ArrayList<Item> items) {
        items.sort(Comparator.comparing(Item::getPriority));
    }

    private static void sortMachinesByTime(ArrayList<Workstation> workstations) {
        workstations.sort(Comparator.comparingInt(Workstation::getTime));
    }

    /**
     * Assigns the items to the machines
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param workstations    List of machines
     * @param timeOperations  HashMap with the time of each operation
     * @param items           List of items
     */
    private static void fillUpMachinesUS08(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Workstation> workstations, LinkedHashMap<String, Double> timeOperations, ArrayList<Item> items) {
        int quantMachines = workstations.size();
        sortMachinesByTime(workstations);
        sortItemsByPriorityAndTime(items, workstations);
        addAllItems(operationsQueue, workstations, timeOperations, items, quantMachines);
    }

    private static void fillUpMachinesUS02(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Workstation> workstations, LinkedHashMap<String, Double> timeOperations, ArrayList<Item> items) {
        int quantMachines = workstations.size();
        sortMachinesByTime(workstations);
        sortItemsByTime(items, workstations);
        addAllItems(operationsQueue, workstations, timeOperations, items, quantMachines);
    }

    private static void addAllItems(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Workstation> workstations, LinkedHashMap<String, Double> timeOperations, ArrayList<Item> items, int quantMachines) {
        for (Item item : items) {
            if (item.getId() != 0) {
                quantMachines = addOperations(operationsQueue, workstations, timeOperations, item, quantMachines);
            }
        }
    }


    /**
     * Adds the operations to the machines for each item
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param workstations    List of machines
     * @param timeOperations  HashMap with the time of each operation
     * @param item            Item to add the operations
     * @param quantMachines   Quantity of machines
     * @return Quantity of machines
     */
    private static int addOperations(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Workstation> workstations, LinkedHashMap<String, Double> timeOperations, Item item, int quantMachines) {
        for (String operation : item.getOperations()) {
            ArrayList<Workstation> availableWorkstations = new ArrayList<>();
            for (Workstation workstation : workstations) {
                if (workstation.getOperation().equalsIgnoreCase(operation)) {
                    availableWorkstations.add(workstation);
                }
            }
            if (quantMachines == 0) {
                quantMachines = checkMachines(workstations, quantMachines);
            } else {
                quantMachines = checkMachinesWithOperation(availableWorkstations, quantMachines, operation);
            }
            quantMachines = addItem(operationsQueue, timeOperations, item, operation, availableWorkstations, quantMachines);
        }
        return quantMachines;
    }

    /**
     * Adds the item to the machine for the corresponding operation
     *
     * @param operationsQueue       HashMap with the operations and the list of items
     * @param timeOperations        HashMap with the time of each operation
     * @param item                  Item to add to the machine
     * @param operation             Operation to add the item
     * @param availableWorkstations List of available machines
     * @param quantMachines         Quantity of machines
     * @return Quantity of machines
     */
    private static int addItem(HashMap<String, LinkedList<Item>> operationsQueue, LinkedHashMap<String, Double> timeOperations, Item item, String operation, ArrayList<Workstation> availableWorkstations, int quantMachines) {
        for (Workstation workstation : availableWorkstations) {
            if (item.getCurrentOperationIndex() > item.getOperations().size()) {
                return quantMachines;
            }
            if ((operationsQueue.get(workstation.getOperation()).contains(item) && workstation.getOperation().equalsIgnoreCase(operation)) && (!workstation.getHasItem())) {
                int currentItem = timeOperations.size() + 1;
                workstation.setHasItem(true);
                item.setCurrentOperationIndex(item.getCurrentOperationIndex() + 1);
                quantMachines--;
                String operation1 = currentItem + " - " + " Operation: " + operation + " - Machine: " + workstation.getId() + " - Priority: " + item.getPriority() + " - Item: " + item.getId() + " - Time: " + workstation.getTime();
                timeOperations.put(operation1, timeOperations.getOrDefault(workstation.getOperation(), 0.0) + workstation.getTime());
                operationsQueue.get(workstation.getOperation()).remove(item);
                return quantMachines;
            }
        }
        return quantMachines;
    }


    private static void sortItemsByTime(ArrayList<Item> items, ArrayList<Workstation> workstations) {
        addTimes(items, workstations);
        swapOperations(items);
        items.sort((item1, item2) -> {
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

    private static void addTimes(ArrayList<Item> items, ArrayList<Workstation> workstations) {
        for (Item item : items) {
            LinkedHashMap<String, Integer> operationTimes = new LinkedHashMap<>();
            for (String operation : item.getOperations()) {
                int minTime = Integer.MAX_VALUE;
                for (Workstation workstation : workstations) {
                    if (workstation.getOperation().equalsIgnoreCase(operation)) {
                        minTime = Math.min(minTime, workstation.getTime());
                    }
                }
                if (minTime == Integer.MAX_VALUE) {
                    System.out.println("Warning: No workstation found for operation: " + operation);
                }
                operationTimes.put(operation, minTime);
            }
            item.setLowestTimes(operationTimes);
        }
    }

    private static void sortItemsByPriorityAndTime(ArrayList<Item> items, ArrayList<Workstation> workstations) {
        sortItemsByPriority(items);
        addTimes(items, workstations);
        swapOperations(items);
        items.sort((item1, item2) -> {
            int priorityComparison = item1.getPriority().compareTo(item2.getPriority());
            if (priorityComparison != 0) {
                return priorityComparison;
            }
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


    /**
     * Checks if there are any machines left with the operation
     *
     * @param workstations  List of machines
     * @param quantMachines Quantity of machines
     * @param operation     Operation to check
     * @return Quantity of machines
     */
    private static int checkMachinesWithOperation(ArrayList<Workstation> workstations, int quantMachines, String operation) {
        boolean notFree = true;
        int quant = 0;
        ArrayList<Workstation> tempMachines = new ArrayList<>();
        for (Workstation workstation : workstations) {
            if (workstation.getOperation().contains(operation) && workstation.getHasItem()) {
                quant++;
                tempMachines.add(workstation);
            }
            if (workstation.getOperation().equalsIgnoreCase(operation) && !workstation.getHasItem()) {
                notFree = false;
            }
        }
        if (quant >= 1 && notFree) {
            for (Workstation workstation : tempMachines) {
                workstation.clearUpWorkstation();
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
        HashMap<Item, Workstation> prodPlan = Instances.getInstance().getHashMapItemsWorkstations().getProdPlan();
        ArrayList<Workstation> machines = new ArrayList<>(prodPlan.values());

        // Remove null entries and initialize flowDependency
        removeNullMachines(machines);
        removeNullItems(prodPlan);
        LinkedHashMap<String, Double> timeOperations = simulateProcessUS02();

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

    private static LinkedHashMap<Workstation, String> listMachinesOperations(LinkedHashMap<String, Double> timeOperations, HashMap<Item, Workstation> prodPlan) {
        LinkedHashMap<Workstation, String> machinesOps = new LinkedHashMap<>();
        for (String entry : timeOperations.keySet()) {
            // Extract the machine and operation from the string
            String[] parts = entry.split(" - ");
            String operation = parts[1].split(": ")[1];
            String machineId = parts[2].split(": ")[1];

            // Find the corresponding workstation
            Workstation workstation = null;
            for (Workstation ws : prodPlan.values()) {
                if (ws.getId().equals(machineId)) {
                    workstation = ws;
                    break;
                }
            }

            if (workstation != null) {
                machinesOps.put(workstation, operation);
            }
        }
        return machinesOps;
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


    private static int checkMachines(ArrayList<Workstation> machines, int quantMachines) {
        if (quantMachines == 0) {
            for (Workstation machine1 : machines) {
                machine1.clearUpWorkstation();
            }
            quantMachines = machines.size();
        }
        return quantMachines;
    }

    private static Workstation findMachineForOperation(HashMap<Item, Workstation> ProdPlan, String operation) {
        for (Workstation machine : ProdPlan.values()) {
            if (machine.getOperation().contains(operation)) {
                return machine;
            }
        }
        return null;
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
     * /**
     * Fills the operationsQueue with the list of the items for each operation
     *
     * @param items           List with the items
     * @param operationsQueue HashMap with the operations and the list of items
     */
    private static void fillOperationsQueue(ArrayList<Item> items, HashMap<String, LinkedList<Item>> operationsQueue) {
        for (Item item : items) {
            ArrayList<String> operations = (ArrayList<String>) item.getOperations();
            if (operations.isEmpty()) {
                System.out.println("Item with ID " + item.getId() + " has no operations.");
                continue;
            }
            for (String operation : operations) {
                if (!operationsQueue.containsKey(operation)) {
                    operationsQueue.put(operation, new LinkedList<>());
                }
                operationsQueue.get(operation).add(item);
                // Set entry time for the operation
                item.setEntryTime(operation, System.currentTimeMillis());
            }
        }
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
     * Calculates the total production time per item
     *
     * @return HashMap with the total production time per item
     */
    public static TreeMap<Item, Double> calculateTotalProductionTimePerItem() {
        HashMap<Item, Double> totalProductionTimePerItem = new HashMap<>();
        LinkedHashMap<String, Double> timeOperations = simulateProcessUS02();

        HashMap<Item, Workstation> ProdPlan = HashMap_Items_Workstations.getProdPlan();
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());

        for (Item item : items) {
            double totalProductionTime = 0.0;
            for (String operation : item.getOperations()) {
                for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
                    if (entry.getKey().contains(operation)) {
                        totalProductionTime += entry.getValue();
                    }
                }
            }
            totalProductionTimePerItem.put(item, totalProductionTime);
        }
        return sortById(removeDuplicateItems(totalProductionTimePerItem));
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

