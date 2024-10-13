package prodPlanSimulator.domain;

import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.HashMap_Items_Machines;

import java.util.*;

public class Item implements  Comparable<Item> {
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
        machines.sort(Comparator.comparing(Machine::getTime));

        // AC1 - Create the operationsQueue with the list of the items for each operation
        fillOperationsQueue(ProdPlan, operationsQueue);
        // AC2 - Assign the items to the machines
        HashMap<String, Double> timeOperations = new HashMap<>();
        fillUpMachines(operationsQueue, machines, timeOperations);

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
     * @param ProdPlan HashMap with the items and the machines
     */

    private static void removeNullItems(HashMap<Item, Machine> ProdPlan) {
        ProdPlan.entrySet().removeIf(entry -> entry.getKey().getId() == 0);
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
                            String operation1 = "Operation: " + operation + " - Machine: " + machine.getId() + " - Item: " + item.getId() + " - Time: " + machine.getTime();
                            timeOperations.put(operation1, timeOperations.getOrDefault(operation, 0.0) + machine.getTime());
                            break;
                        }
                    }
                }
            }
        }
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

    public  void calculateAverageTimes() {
        HashMap<Item, Machine> ProdPlan = HashMap_Items_Machines.getProdPlan();
        HashMap<String, Double> totalTimesPerOperation = new HashMap<>();
        HashMap<String, Integer> operationCount = new HashMap<>();
        HashMap<String, Double> waitingTimesPerOperation = new HashMap<>();

        // Simulating the process
        HashMap<String, Double> timeOperations = simulateProcess();

        // Calculate total execution times and count for average
        for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
            String operation = entry.getKey().split(" - ")[0];  // Extracting operation name
            totalTimesPerOperation.put(operation, totalTimesPerOperation.getOrDefault(operation, 0.0) + entry.getValue());
            operationCount.put(operation, operationCount.getOrDefault(operation, 0) + 1);
        }

        // Calculate average execution time per operation
        System.out.println("Average Execution Times:");
        for (String operation : totalTimesPerOperation.keySet()) {
            double totalExecutionTime = totalTimesPerOperation.get(operation);
            int count = operationCount.get(operation);
            double averageTime = totalExecutionTime / count;
            System.out.println(operation + " : Average Time = " + averageTime);
        }

        // Simulating machine availability to calculate waiting times
        for (Machine machine : ProdPlan.values()) {
            double totalWaitingTime = 0.0;
            for (Item item : ProdPlan.keySet()) {
                // If machine is busy, increase waiting time
                if (machine.getHasItem()) {
                    totalWaitingTime += machine.getTime();
                }
            }
            // Add waiting time for the current machine's operation
            String machineOperation = machine.getOperations().get(0);
            waitingTimesPerOperation.put(machineOperation, waitingTimesPerOperation.getOrDefault(machineOperation, 0.0) + totalWaitingTime);
        }

        // Print waiting times per operation
        System.out.println("Average Waiting Times:");
        for (String operation : waitingTimesPerOperation.keySet()) {
            double totalWaitingTime = waitingTimesPerOperation.get(operation);
            int count = operationCount.get(operation); // Use the same count as execution
            double averageWaitingTime = totalWaitingTime / count;
            System.out.println(operation + " : Average Waiting Time = " + averageWaitingTime);
        }
    }



    public void generateMachineFlowReport() {
        HashMap<Item, Machine> ProdPlan = HashMap_Items_Machines.getProdPlan();
        Map<String, Map<String, Integer>> machineFlow = new HashMap<>();

        // Iterate over the ProdPlan to build the flow between machines
        for (Item item : ProdPlan.keySet()) {
            List<String> operations = item.getOperations();
            Machine lastMachine = null;

            // Simulate processing of each item and track its flow between machines
            for (String operation : operations) {
                Machine currentMachine = ProdPlan.get(item);  // Get current machine handling the item

                if (lastMachine != null) {
                    String lastMachineId = lastMachine.getId();
                    String currentMachineId = currentMachine.getId();

                    // Update the flow map
                    machineFlow.putIfAbsent(lastMachineId, new HashMap<>());
                    Map<String, Integer> transitions = machineFlow.get(lastMachineId);
                    transitions.put(currentMachineId, transitions.getOrDefault(currentMachineId, 0) + 1);
                }

                lastMachine = currentMachine;  // Move to next machine
            }
        }

        // Print the machine flow report in descending order of processed items
        System.out.println("Machine Flow Report:");
        for (String machine : machineFlow.keySet()) {
            System.out.print(machine + " : ");
            Map<String, Integer> flows = machineFlow.get(machine);

            // Sort the flows by number of items processed
            flows.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(entry -> {
                        System.out.print("(" + entry.getKey() + "," + entry.getValue() + ") ");
                    });
            System.out.println();
        }
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
        List<Item> itemsList = new ArrayList<>(items);
        itemsList.sort(Comparator.comparing(Item::getPriority));
        items.clear();
        items.addAll(itemsList);
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

