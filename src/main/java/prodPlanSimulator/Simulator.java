package prodPlanSimulator;

import domain.Activity;
import domain.Item;
import domain.Material;
import domain.Workstation;
import repository.HashMap_Items_Machines;
import repository.Instances;
import repository.ItemsRepository;
import repository.WorkstationRepository;
import trees.AVL_BST.AVL;
import trees.AVL_BST.BOO;
import trees.ProductionTree.NodeType;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;
import projectManager.PERT_CPM;
import projectManager.CalculateTimes;


import java.util.*;

public class Simulator {
    private LinkedHashMap<String, Double> timeOperations;
    private AVL<BOO> bombooTree;

    /**
     * Constructor for the Simulator
     */
    public Simulator() {
        this.timeOperations = new LinkedHashMap<>();
    }

    /**
     * Simulate the process of all the items present in the system. And this method is used to reset everything when used by the simulator.
     * @return LinkedHashMap<String, Double> where String is the operation and Double is the time taken to complete the operation.
     */
    public LinkedHashMap<String, Double> simulateProcessUS02() {
        timeOperations = new LinkedHashMap<>();
        HashMap_Items_Machines HashMap_Items_Workstations = Instances.getInstance().getHashMapItemsWorkstations();
        HashMap<Item, Workstation> ProdPlan = HashMap_Items_Workstations.getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Workstation> workstations = new ArrayList<>(ProdPlan.values());
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullMachines(workstations);
        removeNullItems(items);
        // AC1 - Create the operationsQueue with the list of the items for each operation
        fillOperationsQueue(items, operationsQueue);
        // AC2 - Assign the items to the machines
        fillUpMachinesUS02(operationsQueue, workstations, items);
        return SimulatorReset(workstations, items);
    }

    /**
     * Simulate the process of all the items present in the system. And this method is used to reset everything when used by the simulator.
     * @param workstations List of machines
     * @param items List of items
     * @return LinkedHashMap<String, Double> where String is the operation and Double is the time taken to complete the operation.
     */
    private LinkedHashMap<String, Double> SimulatorReset(ArrayList<Workstation> workstations, ArrayList<Item> items) {
        for (Workstation workstation : workstations) {
            workstation.clearUpWorkstation();
        }
        for (Item item : items) {
            item.setCurrentOperationIndex(0);
            item.setLowestTimes(new LinkedHashMap<>());
            // Calculate and set waiting times for each operation
            for (String operation : item.getOperationsString()) {
                long entryTime = item.getEntryTime(operation);
                long currentTime = System.currentTimeMillis();
                int waitTime = (int) (currentTime - entryTime);
                item.setWaitingTime(operation, waitTime);
            }
        }
        return timeOperations;
    }

    /**
     * US16: Simulate the process of all the items present in the system. And this method is used to reset everything when used by the simulator.
     *
     * @return LinkedHashMap<String, Double> where String is the operation and Double is the time taken to complete the operation.
     */
    private LinkedHashMap<String, Double> SimulatorResetUS16(ArrayList<Workstation> workstations) {
        for (Workstation workstation : workstations) {
            workstation.clearUpWorkstation();
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
        items.removeIf(item -> item.getId().equalsIgnoreCase(""));
    }

    /**
     * Fills the operationsQueue with the list of the items for each operation
     *
     * @param items           List with the items
     * @param operationsQueue HashMap with the operations and the list of items
     */
    private static void fillOperationsQueue(ArrayList<Item> items, HashMap<String, LinkedList<Item>> operationsQueue) {
        for (Item item : items) {
            ArrayList<String> operations = (ArrayList<String>) item.getOperationsString();
            if (operations.isEmpty()) {
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

    /**
     * Fills the operationsQueue with the list of the items for each operation
     *
     * @param materials       List with the materials
     * @param operationsQueue HashMap with the operations and the list of items
     * @param postOrderElements List with the post order elements
     */
    private static void fillOperationsQueue(ArrayList<Material> materials, LinkedHashMap<String, LinkedList<Material>> operationsQueue, List<BOO> postOrderElements) {
        for (BOO boo : postOrderElements) {
            String operation = boo.getOperation();
            for (String itemName : boo.getItems()) {
                for (Material material : materials) {
                    if (material.getName().equalsIgnoreCase(itemName)) {
                        if (!operationsQueue.containsKey(operation)) {
                            operationsQueue.put(operation, new LinkedList<>());
                        }
                        operationsQueue.get(operation).add(material);
                    }
                }
            }
        }
    }

    /**
     * Assigns the items to the machines
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param workstations    List of machines
     * @param items           List of items
     */
    private void fillUpMachinesUS02(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Workstation> workstations, ArrayList<Item> items) {
        int quantMachines = workstations.size();
        sortMachinesByTime(workstations);
        sortItemsByTime(items, workstations);
        addAllItems(operationsQueue, workstations, items, quantMachines);
    }
    /**
     * Assigns the items to the machines
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param workstations    List of machines
     * @param materials       List of materials
     */
    private void fillUpMachinesUS16(LinkedHashMap<String, LinkedList<Material>> operationsQueue, ArrayList<Workstation> workstations, ArrayList<Material> materials) {
        int quantMachines = workstations.size();
        sortMachinesByTime(workstations);
        addAllItemsWithSteps(operationsQueue, workstations, materials, quantMachines);
    }

    /**
     * Sorts the items by priority
     *
     * @param items List of items
     */
    private static void sortItemsByPriority(ArrayList<Item> items) {
        items.sort(Comparator.comparing(Item::getPriority));
    }

    /**
     * Sorts the machines by time
     *
     * @param workstations List of machines
     */
    private static void sortMachinesByTime(ArrayList<Workstation> workstations) {
        workstations.sort(Comparator.comparingInt(Workstation::getTime));
    }

    /**
     * Assigns the items to the machines
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param workstations    List of machines
     * @param items           List of items
     */
    private void fillUpMachinesUS08(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Workstation> workstations, ArrayList<Item> items) {
        int quantMachines = workstations.size();
        sortMachinesByTime(workstations);
        sortItemsByPriorityAndTime(items, workstations);
        addAllItems(operationsQueue, workstations, items, quantMachines);
    }


    /**
     * Adds all the items to the machines
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param workstations    List of machines
     * @param items           List of items
     * @param quantMachines   Quantity of machines
     */
    private void addAllItems(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Workstation> workstations, ArrayList<Item> items, int quantMachines) {
        for (Item item : items) {
            if (!item.getId().equalsIgnoreCase("")) {
                quantMachines = addOperations(operationsQueue, workstations, item, quantMachines);
            }
        }
    }

    /**
     * Adds all the items to the machines
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param workstations    List of machines
     * @param materials       List of materials
     * @param quantMachines   Quantity of machines
     */
    private void addAllItemsWithSteps(HashMap<String, LinkedList<Material>> operationsQueue, ArrayList<Workstation> workstations, ArrayList<Material> materials, int quantMachines) {
        for (String operation : operationsQueue.keySet()) {
            for (Material material : materials) {
                if (!material.getID().equalsIgnoreCase("") && operationsQueue.get(operation).contains(material)) {
                    quantMachines = addOperationsWithSteps(operation, workstations, material, quantMachines, operationsQueue);
                }
            }
        }
    }


    /**
     * Adds the operations to the machines for each item
     *
     * @param operationsQueue HashMap with the operations and the list of items
     * @param workstations    List of machines
     * @param item            Item to add the operations
     * @param quantMachines   Quantity of machines
     * @return Quantity of machines
     */
    private int addOperations(HashMap<String, LinkedList<Item>> operationsQueue, ArrayList<Workstation> workstations, Item item, int quantMachines) {
        for (String operation : item.getOperationsString()) {
            ArrayList<Workstation> availableWorkstations = new ArrayList<>();
            for (Workstation workstation : workstations) {
                if (workstation.getOperationName().equalsIgnoreCase(operation)) {
                    availableWorkstations.add(workstation);
                }
            }
            if (quantMachines == 0) {
                quantMachines = checkMachines(workstations, quantMachines);
            } else {
                quantMachines = checkMachinesWithOperation(availableWorkstations, quantMachines, operation);
            }
            quantMachines = addItem(operationsQueue, item, operation, availableWorkstations, quantMachines);
        }
        return quantMachines;
    }

    /**
     * Adds the operations to the machines for each item
     *
     * @param operation         Operation to add the item
     * @param workstations      List of machines
     * @param material          Material to add to the machine
     * @param quantMachines     Quantity of machines
     * @param operationsQueue   HashMap with the operations and the list of items
     * @return Quantity of machines
     */
    private int addOperationsWithSteps(String operation, ArrayList<Workstation> workstations, Material material, int quantMachines, HashMap<String, LinkedList<Material>> operationsQueue) {
        ArrayList<Workstation> availableWorkstations = new ArrayList<>();
        for (Workstation workstation : workstations) {
            if (workstation.getOperationName().equalsIgnoreCase(operation)) {
                availableWorkstations.add(workstation);
            }
        }
        if (quantMachines == 0) {
            quantMachines = checkMachines(workstations, quantMachines);
        } else {
            quantMachines = checkMachinesWithOperation(availableWorkstations, quantMachines, operation);
        }
        quantMachines = addItemsWithSteps(operationsQueue, material, operation, availableWorkstations, quantMachines);
        return quantMachines;
    }

    /**
     * Adds the item to the machine for the corresponding operation
     *
     * @param operationsQueue       HashMap with the operations and the list of items
     * @param item                  Item to add to the machine
     * @param operation             Operation to add the item
     * @param availableWorkstations List of available machines
     * @param quantMachines         Quantity of machines
     * @return Quantity of machines
     */
    private int addItem(HashMap<String, LinkedList<Item>> operationsQueue, Item item, String operation, ArrayList<Workstation> availableWorkstations, int quantMachines) {
        for (Workstation workstation : availableWorkstations) {
            if (item.getCurrentOperationIndex() > item.getOperationsRequired().size()) {
                return quantMachines;
            }
            if ((operationsQueue.get(workstation.getOperation().getDescription()).contains(item) && workstation.getOperationName().equalsIgnoreCase(operation)) && (!workstation.getHasItem())) {
                int currentItem = timeOperations.size() + 1;
                workstation.setHasItem(true);
                item.setCurrentOperationIndex(item.getCurrentOperationIndex() + 1);
                quantMachines--;
                String operation1 = currentItem + " - " + " Operation: " + operation + " - Machine: " + workstation.getId() + " - Priority: " + item.getPriority() + " - Item: " + item.getId() + " - Time: " + workstation.getTime() + " - Quantity: " + item.getQuantity();
                timeOperations.put(operation1, (double) workstation.getTime());
                operationsQueue.get(workstation.getOperation().getDescription()).remove(item);
                return quantMachines;
            }
        }
        return quantMachines;
    }

    /**
     * Adds the item to the machine for the corresponding operation
     *
     * @param operationsQueue       HashMap with the operations and the list of items
     * @param material              Material to add to the machine
     * @param operation             Operation to add the item
     * @param availableWorkstations List of available machines
     * @param quantMachines         Quantity of machines
     * @return Quantity of machines
     */
    private int addItemsWithSteps(HashMap<String, LinkedList<Material>> operationsQueue, Material material, String operation, ArrayList<Workstation> availableWorkstations, int quantMachines) {
        for (Workstation workstation : availableWorkstations) {
            if ((operationsQueue.get(workstation.getOperation().getDescription()).contains(material) && workstation.getOperationName().equalsIgnoreCase(operation)) && (!workstation.getHasItem())) {
                int currentItem = timeOperations.size() + 1;
                workstation.setHasItem(true);
                quantMachines--;
                String operation1 = currentItem + " - " + "Operation: " + operation + " - Machine: " + workstation.getId() + " - Item: " + material.getName() + " - Time: " + workstation.getTime() + " - Quantity: " + material.getQuantity();
                timeOperations.put(operation1, (double) workstation.getTime());
                operationsQueue.get(workstation.getOperation().getDescription()).remove(material);
                return quantMachines;
            }
        }
        return quantMachines;
    }

    /**
     * Sorts the machines by time
     *
     * @param workstations List of machines
     */
    private static void sortItemsByTime(ArrayList<Item> items, ArrayList<Workstation> workstations) {
        addTimes(items, workstations);
        swapOperations(items);
        items.sort(Simulator::sortForItemsLowestTime);
    }


    /**
     * Sorts the items by the lowest time
     *
     * @param item1 Item 1
     * @param item2 Item 2
     * @return Comparison between the two items
     */
    private static int sortForItemsLowestTime(Item item1, Item item2) {
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
    }


    /**
     * Swaps the operations
     *
     * @param items List of items
     */
    private static void swapOperations(ArrayList<Item> items) {
        for (Item item : items) {
            List<String> operations = item.getOperationsString();
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

    /**
     * Adds the times for each operation
     *
     * @param items        List of items
     * @param workstations List of machines
     */
    private static void addTimes(ArrayList<Item> items, ArrayList<Workstation> workstations) {
        for (Item item : items) {
            LinkedHashMap<String, Integer> operationTimes = new LinkedHashMap<>();
            for (String operation : item.getOperationsString()) {
                int minTime = Integer.MAX_VALUE;
                for (Workstation workstation : workstations) {
                    if (workstation.getOperationName().equalsIgnoreCase(operation)) {
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

    /**
     * Sorts the items by priority and time
     *
     * @param items        List of items
     * @param workstations List of machines
     */
    private static void sortItemsByPriorityAndTime(ArrayList<Item> items, ArrayList<Workstation> workstations) {
        sortItemsByPriority(items);
        addTimes(items, workstations);
        swapOperations(items);
        items.sort((item1, item2) -> {
            int priorityComparison = item1.getPriority().compareTo(item2.getPriority());
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            return sortForItemsLowestTime(item1, item2);
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
            if (workstation.getOperationName().contains(operation) && workstation.getHasItem()) {
                quant++;
                tempMachines.add(workstation);
            }
            if (workstation.getOperationName().equalsIgnoreCase(operation) && !workstation.getHasItem()) {
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
     * Checks if there are any machines left
     *
     * @param machines List of machines
     * @param quantMachines Quantity of machines
     * @return Quantity of machines
     */
    private static int checkMachines(ArrayList<Workstation> machines, int quantMachines) {
        if (quantMachines == 0) {
            for (Workstation machine1 : machines) {
                machine1.clearUpWorkstation();
            }
            quantMachines = machines.size();
        }
        return quantMachines;
    }

    /**
     * Simulates the process of all the items present in the system
     */
    public LinkedHashMap<String, Double> simulateProcessUS08() {
        timeOperations = new LinkedHashMap<>();
        HashMap_Items_Machines HashMap_Items_Workstations = Instances.getInstance().getHashMapItemsWorkstations();
        HashMap<Item, Workstation> ProdPlan = HashMap_Items_Workstations.getProdPlan();
        HashMap<String, LinkedList<Item>> operationsQueue = new HashMap<>();
        ArrayList<Workstation> workstations = new ArrayList<>(ProdPlan.values());
        ArrayList<Item> items = new ArrayList<>(ProdPlan.keySet());
        removeNullMachines(workstations);
        removeNullItems(items);
        fillOperationsQueue(items, operationsQueue);
        fillUpMachinesUS08(operationsQueue, workstations, items);
        return SimulatorReset(workstations, items);
    }

    /**
     * Simulates the process of all the items present in the system
     */
    public LinkedHashMap<String, Double> simulateBOMBOO() {
        ProductionTree productionTree = Instances.getInstance().getProductionTree();
        TreeNode<String> root = productionTree.getRoot();
        bombooTree = new AVL<>();
        LinkedHashMap<Integer, BOO> materials = new LinkedHashMap<>();
        List<BOO> postOrderElements = createBOMBOOTree(root, materials);
        timeOperations = new LinkedHashMap<>();
        WorkstationRepository workstationRepository = Instances.getInstance().getWorkstationRepository();
        HashMap<Integer, Workstation> workstationsMap = (HashMap<Integer, Workstation>) workstationRepository.getWorkstations();
        LinkedHashMap<String, LinkedList<Material>> operationsQueue = new LinkedHashMap<>();
        ArrayList<Workstation> workstations = new ArrayList<>(workstationsMap.values());
        removeNullMachines(workstations);
        ArrayList<Material> filteredMaterials = new ArrayList<>();
        fillItemsWithMaterials(materials, filteredMaterials);
        fillOperationsQueue(filteredMaterials, operationsQueue, postOrderElements);
        fillUpMachinesUS16(operationsQueue, workstations, filteredMaterials);
        return SimulatorResetUS16(workstations);
    }

    /**
     * Fills the operationsQueue with the list of the items for each operation
     * @param materials List with the materials
     * @param filteredMaterials List with the filtered materials
     */
    private void fillItemsWithMaterials(LinkedHashMap<Integer, BOO> materials, ArrayList<Material> filteredMaterials) {
        ItemsRepository itemsRepository = Instances.getInstance().getItemsRepository();
        List<String> itemsIDs = new ArrayList<>(itemsRepository.getItemsRepository().keySet());
        for (Map.Entry<Integer, BOO> entry : materials.entrySet()) {
            BOO boo = entry.getValue();
            List<String> materialNames = boo.getItems();
            for (String materialName : materialNames) {
                for (String id : itemsIDs) {
                    if (itemsRepository.getItemValue(id).equalsIgnoreCase(materialName)) {
                        Double quantity = boo.getQuantityItems().get(boo.getItemPosition(materialName)); // Get the quantity of the material
                        String quantityString = String.valueOf(quantity);
                        Material material = new Material(id, materialName, quantityString);
                        filteredMaterials.add(material);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Creates the BOMBOO tree
     * @param node TreeNode<String> with the root of the tree
     * @param materials LinkedHashMap<Integer, BOO> with the materials
     * @return List<BOO> with the post order elements
     */
    private List<BOO> createBOMBOOTree(TreeNode<String> node,LinkedHashMap<Integer, BOO> materials) {
        BOO root = new BOO();
        createBOMBOOTree(node, root);
        List<BOO> postOrder = bombooTree.getAllNodes();
        Iterable<BOO> postOrderElements = changeOrder(postOrder);
        fillMaterials(materials, postOrderElements);
        List<BOO> postOrderList = new ArrayList<>();
        postOrderElements.forEach(postOrderList::add);
        return postOrderList;
    }

    /**
     * Changes the order of the post order elements
     * @param postOrder List<BOO> with the post order elements
     * @return Iterable<BOO> with the post order elements
     */
    private Iterable<BOO> changeOrder(List<BOO> postOrder) {
        List<BOO> postOrderElements = new ArrayList<>();
        for (int i = postOrder.size() - 1; i >= 0; i--) {
            postOrderElements.add(postOrder.get(i));
        }
        return postOrderElements;
    }

    /**
     * Fills the materials
     * @param materials LinkedHashMap<Integer, BOO> with the materials
     * @param postOrderElements Iterable<BOO> with the post order elements
     */
    private void fillMaterials(LinkedHashMap<Integer, BOO> materials, Iterable<BOO> postOrderElements) {
        int index = 0;
        for (BOO boo : postOrderElements) {
            materials.put(index++, boo);
        }
    }

    /**
     * Creates the BOMBOO tree
     * @param node TreeNode<String> with the root of the tree
     */
    private void createBOMBOOTree(TreeNode<String> node, BOO firstElement) {
        if (node == null) {
            return;
        }
        // Process the current node
        String value = node.getValue();
        int startIndex = value.indexOf("(Quantity: ");
        int endIndex = value.indexOf(')', startIndex);

        if (startIndex != -1 && endIndex != -1) {
            String name = value.substring(0, startIndex).trim();
            String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
            double quantity = Double.parseDouble(quantityStr);
            if (node.getType() == NodeType.OPERATION) {
                if (bombooTree.getLatestInsertedNode() != null) {
                    BOO operation = new BOO(quantity, name);
                    bombooTree.insert(operation);
                    BOO insertedOperation = bombooTree.search(operation);
                    insertedOperation.setType(NodeType.OPERATION);
                } else {
                    BOO operation = new BOO(quantity, name);
                    bombooTree.insert(operation);
                    BOO insertedOperation = bombooTree.search(operation);
                    insertedOperation.setType(NodeType.OPERATION);
                    insertedOperation.setItems(firstElement.getItems());
                    insertedOperation.setQuantityItems(firstElement.getQuantityItems());
                }
            } else if (node.getType() == NodeType.PRODUCT || node.getType() == NodeType.COMPONENT || node.getType() == NodeType.RAW_MATERIAL) {
                if (bombooTree.getLatestInsertedNode() != null) {
                    BOO latestOperation = bombooTree.search(bombooTree.getElem(bombooTree.getLatestInsertedNode()));
                    if (latestOperation != null) {
                        latestOperation.addItems(name);
                        latestOperation.addQuantity(quantity);
                    }
                } else {
                    firstElement.addItems(name);
                    firstElement.addQuantity(quantity);
                }
            }
        }

        List<TreeNode<String>> materialChildren = new ArrayList<>();
        List<TreeNode<String>> operationChildren = new ArrayList<>();

        for (TreeNode<String> child : node.getChildren()) {
            if (child.getType() == NodeType.PRODUCT || child.getType() == NodeType.COMPONENT || child.getType() == NodeType.RAW_MATERIAL) {
                materialChildren.add(child);
            } else if (child.getType() == NodeType.OPERATION) {
                operationChildren.add(child);
            }
        }

        for (TreeNode<String> materialChild : materialChildren) {
            createBOMBOOTree(materialChild, firstElement);
        }

        for (TreeNode<String> operationChild : operationChildren) {
            createBOMBOOTree(operationChild, firstElement);
        }
    }


    /**
     * Gets the timeOperations
     *
     * @return LinkedHashMap<String, Double> where String is the operation and Double is the time taken to complete the operation.
     */

    public LinkedHashMap<String, Double> getTimeOperations() {
        return timeOperations;
    }

    /**
     * Sets the timeOperations
     * @param timeOperations LinkedHashMap<String, Double> where String is the operation and Double is the time taken to complete the operation.
     */
    public void setTimeOperations(LinkedHashMap<String, Double> timeOperations) {
        this.timeOperations = timeOperations;
    }
}

