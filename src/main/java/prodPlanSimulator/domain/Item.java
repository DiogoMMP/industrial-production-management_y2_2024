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
    private LinkedHashMap<String, Integer> lowestTimes;


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

    public static LinkedHashMap<String, Double> simulateProcessUS02() {
        HashMap<Item, Workstation> ProdPlan = HashMap_Items_Machines.getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Workstation> workstations = new ArrayList<>(ProdPlan.values());
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullMachines(workstations);
        removeNullItems(items);
        fillOperationsQueue(items, operationsQueue);
        LinkedHashMap<String, Double> timeOperations = new LinkedHashMap<>();
        fillUpMachinesUS02(operationsQueue, workstations, timeOperations, items);
        for (Workstation workstation : workstations) {
            workstation.clearUpWorkstation();
        }
        for (Item item : items) {
            item.setCurrentOperationIndex(0);
            item.setLowestTimes(new LinkedHashMap<>());
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
     * Sets the lowest times of the item
     *
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
    public static LinkedHashMap<String, Double> simulateProcessUS08() {
        HashMap<Item, Workstation> ProdPlan = HashMap_Items_Machines.getProdPlan();
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

    /**
     * US6: Present average execution times per operation and corresponding waiting times.
     *
     * @return HashMap<String, Double [ ]> where String is the operation and Double[] holds:
     * [average execution time, average waiting time]
     */
    public static HashMap<String, Double[]> calculateAvgExecutionAndWaitingTimes() {
        HashMap<String, Double[]> operationTimes = new HashMap<>();
        HashMap<Item, Workstation> ProdPlan = Instances.getInstance().getHashMapItemsMachines().getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Workstation> workstations = new ArrayList<>(ProdPlan.values());
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullMachines(workstations);
        removeNullItems(items);
        // Fill the operationsQueue with items waiting for each operation
        fillOperationsQueue(items, operationsQueue);

        // Track execution and waiting times for each operation
        for (String operation : operationsQueue.keySet()) {
            double totalExecutionTime = 0.0;
            double totalWaitingTime = 0.0;
            int itemCount = 0;

            for (Item item : items) {
                for (Workstation workstation : workstations) {
                    if (workstation.getOperation().contains(operation)) {
                        totalExecutionTime += workstation.getTime();
                        // Assuming waiting time is the sum of execution times of items ahead in the queue
                        totalWaitingTime += totalExecutionTime - workstation.getTime();
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
                changeStatusMach(availableWorkstations, workstation);
                item.setCurrentOperationIndex(item.getCurrentOperationIndex() + 1);
                quantMachines--;
                String operation1 =  currentItem + " - " + " Operation: " + operation + " - Machine: " + workstation.getId() + " - Priority: " + item.getPriority() + " - Item: " + item.getId() + " - Time: " + workstation.getTime();
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
     * Changes the status of the machine
     *
     * @param workstations List of machines
     * @param workstation  Machine to change the status
     */
    private static void changeStatusMach(ArrayList<Workstation> workstations, Workstation workstation) {
        for (Workstation workstation1 : workstations) {
            if (workstation.compareTo(workstation1) == 0) {
                workstation1.setHasItem(true);
            }
        }
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
        HashMap<Item, Workstation> ProdPlan = Instances.getInstance().getHashMapItemsMachines().getProdPlan();
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullItems(items);
        for (Item item : items) {
            List<String> operations = item.getOperations();
            for (int i = 0; i < operations.size() - 1; i++) {
                String currentOperation = operations.get(i);
                String nextOperation = operations.get(i + 1);

                Workstation currentWorkstation = findMachineForOperation(ProdPlan, currentOperation);
                Workstation nextWorkstation = findMachineForOperation(ProdPlan, nextOperation);

                if (currentWorkstation != null && nextWorkstation != null) {
                    String currentMachineId = currentWorkstation.getId();
                    String nextMachineId = nextWorkstation.getId();

                    flowDependency.computeIfAbsent(currentMachineId, k -> new ArrayList<>());
                    List<Map.Entry<String, Integer>> transitions = flowDependency.get(currentMachineId);

                    updateTransitionCount(transitions, nextMachineId);
                }
            }
        }

        // Sort the flow dependency by the number of processed items in descending order
        return sortFlowDependency(flowDependency);
    }

    /**
     * Finds the machine for the operation
     *
     * @param ProdPlan  HashMap with the items and the machines
     * @param operation Operation to find the machine
     * @return Machine for the operation
     */
    private static Workstation findMachineForOperation(HashMap<Item, Workstation> ProdPlan, String operation) {
        for (Workstation workstation : ProdPlan.values()) {
            if (workstation.getOperation().contains(operation)) {
                return workstation;
            }
        }
        return null;
    }

    /**
     * Checks if there are any machines left
     *
     * @param workstations  List of machines
     * @param quantMachines Quantity of machines
     * @return Quantity of machines
     */
    private static int checkMachines(ArrayList<Workstation> workstations, int quantMachines) {
        if (quantMachines == 0) {
            for (Workstation workstation1 : workstations) {
                workstation1.clearUpWorkstation();
            }
            quantMachines = workstations.size();
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
            List<String> operations = item.getOperations();
            if (operations == null || operations.isEmpty()) {
                System.out.println("Item with ID " + item.getId() + " has no operations.");
                continue;
            }
            for (String operation : operations) {
                operationsQueue.computeIfAbsent(operation, k -> new LinkedList<>()).add(item);
            }
        }
    }


    /**
     * Calculates the total production time per item
     *
     * @return HashMap with the total production time per item
     */
    public static HashMap<Item, Double> calculateTotalProductionTimePerItem() {
        double totalProductionTime = 0.0;
        HashMap<Item, Double> totalProductionTimePerItem = new HashMap<>();

        for (Map.Entry<Item, Workstation> entry : HashMap_Items_Machines.getProdPlan().entrySet()) {
            Item item = entry.getKey();
            Workstation workstation = entry.getValue();

            for (String operation : item.getOperations()) {

                if (workstation.getOperation().equals(operation)) {
                    totalProductionTime += workstation.getTime();
                }
            }
            totalProductionTimePerItem.put(item, totalProductionTime);
            totalProductionTime = 0.0;

        }

        return totalProductionTimePerItem;
    }

    /**
     * Compares this object with the specified object to verify if they are equal.
     *
     * @param o the object to be compared.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public int compareTo(Item o) {
        return Integer.compare(this.id, o.id);
    }

}

