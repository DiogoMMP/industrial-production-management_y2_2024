package trees.ProductionTree;

import UI.Utils.Utils;
import domain.Material;
import repository.BOORepository;
import repository.Instances;
import repository.ItemsRepository;
import repository.OperationsMapRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import trees.heap.Entry;
import trees.MaterialsBST.MaterialsBST;
import trees.heap.HeapPriorityQueue;


public class ProductionTree {

    private TreeNode<String> root;
    private String mainObjectiveID;
    private Map<String, TreeNode<String>> nodesMap = new HashMap<>();
    private HeapPriorityQueue<Integer, String> qualityCheckQueue; // Priority Queue for Quality Checks
    private Map<Integer, Integer> depthPriorityMap; // Maps depth to priority
    private int nextPriority; // Counter for the next available priority

    /**
     * Constructs a production tree with the specified main objective.
     */
    public ProductionTree() {
        this.root = null;
        this.qualityCheckQueue = new HeapPriorityQueue<>();
        this.depthPriorityMap = new HashMap<>();
        this.nextPriority = 1; // Start priority from 1
    }

    /**
     * Returns the root of the production tree.
     *
     * @return the root of the production tree
     */
    public TreeNode<String> getRoot() {
        return root;
    }

    /**
     * Sets the root of the production tree.
     *
     * @param root the root of the production tree
     */
    public void setRoot(TreeNode<String> root) {
        nodesMap.clear();
        this.root = root;
    }

    /**
     * Returns the priority queue for quality checks.
     *
     * @return the priority queue for quality checks
     */
    public HeapPriorityQueue<Integer, String> getQualityCheckQueue() {
        return qualityCheckQueue;
    }

    /**
     * Sets the priority queue for quality checks.
     *
     * @param qualityCheckQueue the priority queue for quality checks
     */
    public void setQualityCheckQueue(HeapPriorityQueue<Integer, String> qualityCheckQueue) {
        this.qualityCheckQueue = qualityCheckQueue;
    }

    /**
     * Builds the production tree with the specified main objective.
     *
     * @param mainObjectiveID the ID of the main objective
     * @return the root of the production tree
     */
    public TreeNode<String> buildProductionTree(String mainObjectiveID) {
        BOORepository booRepository = Instances.getInstance().getBOORepository();
        ItemsRepository itemsRepository = Instances.getInstance().getItemsRepository();
        OperationsMapRepository operationsMapRepository = Instances.getInstance().getOperationsMapRepository();
        List<String[]> booData = booRepository.getBOORepository();
        Map<String, String> itemNames = itemsRepository.getItemsRepository();
        Map<String, String> operationDescriptions = operationsMapRepository.getOperationsMapRepository();

        String mainObjectiveOperationID = null;
        String mainObjectiveQuantity = null;

        for (String[] entry : booData) {
            if (entry[1].equals(mainObjectiveID)) { // Searching in the first column for the main objective ID
                mainObjectiveOperationID = entry[0];
                mainObjectiveQuantity = entry[2];
                break;
            }
        }

        // Check if main objective is found
        if (mainObjectiveID == null) {
            System.out.println("Main objective not found in the Bill of Operations.");
            return null;
        }

        this.mainObjectiveID = mainObjectiveID;

        String operationDescription = operationDescriptions.getOrDefault(mainObjectiveOperationID, "Unknown Operation");
        root = new TreeNode<>(operationDescription + " (Quantity: " + mainObjectiveQuantity + ")", NodeType.OPERATION);
        nodesMap.put(mainObjectiveOperationID, root);

        buildSubTree(mainObjectiveOperationID, root, booData, itemNames, operationDescriptions);

        return root; // Return the root of the production tree
    }

    /**
     * Builds the subtree of the production tree recursively.
     *
     * @param currentOperationID    the ID of the current operation
     * @param parent                the parent node of the current operation
     * @param booData               the data from the Bill of Operations
     * @param itemNames             the map of item IDs to item names
     * @param operationDescriptions the map of operation IDs to operation descriptions
     */
    private void buildSubTree(
            String currentOperationID,
            TreeNode<String> parent,
            List<String[]> booData,
            Map<String, String> itemNames,
            Map<String, String> operationDescriptions
    ) {
        Set<String> visitedNodes = new HashSet<>(); // Keeps track of visited nodes

        for (String[] booEntry : booData) {
            if (booEntry.length >= 2 && booEntry[0].equals(currentOperationID)) {
                String productID = booEntry[1];

                // Check if this productID has already been added
                if (visitedNodes.contains(productID)) {
                    continue; // Skip duplicate
                }
                visitedNodes.add(productID);

                String productQuantity = booEntry[2];
                String productName = itemNames.getOrDefault(productID, "Unknown Product");

                // Define node as PRODUCT
                TreeNode<String> productNode = new TreeNode<>(productName + " (Quantity: " + productQuantity + ")", NodeType.PRODUCT);
                productNode.setOperationParent(parent); // Define the parent
                parent.addChild(productNode);

                nodesMap.put(productID, productNode);

                // Add sub-operations
                int numberOperations = countOperations(booEntry);
                int k = 4;
                boolean hasOperations = false; // Flag to check if there are sub-operations
                while (k < 4 + 2 * numberOperations) {
                    String subOperationId = booEntry[k];
                    String subOperationQuantity = booEntry[k + 1];
                    k += 2;

                    String subOperationDescription = operationDescriptions.getOrDefault(subOperationId, "Unknown Operation");
                    // Define node as OPERATION
                    TreeNode<String> subOperationNode = new TreeNode<>(subOperationDescription + " (Quantity: " + subOperationQuantity + ")");
                    subOperationNode.setType(NodeType.OPERATION);
                    subOperationNode.setOperationParent(productNode); // Define the parent as the product
                    productNode.addChild(subOperationNode);

                    int depth = calculateDepth(subOperationNode);
                    int priority = getPriorityForDepth(depth);
                    qualityCheckQueue.insert(priority, subOperationDescription);

                    nodesMap.put(subOperationId, subOperationNode);

                    buildSubTree(subOperationId, subOperationNode, booData, itemNames, operationDescriptions);

                    hasOperations = true; // Set flag to true if there are sub-operations
                }

                // Add Materials
                int materialsStartIndex = findMaterialsStartIndex(booEntry);
                int numberMaterials = countMaterials(booEntry);
                for (int j = materialsStartIndex; j < materialsStartIndex + 2 * numberMaterials; j += 2) {
                    String materialId = booEntry[j];
                    String quantity = booEntry[j + 1];
                    String materialName = itemNames.getOrDefault(materialId, "Unknown Material");

                    // Check if this materialId has already been added
                    if (visitedNodes.contains(materialId)) {
                        continue; // Skip duplicate
                    }
                    visitedNodes.add(materialId);

                    // Define node as RAW_MATERIAL
                    TreeNode<String> materialNode = new TreeNode<>(materialName + " (Quantity: " + quantity + ")");
                    materialNode.setType(NodeType.RAW_MATERIAL);
                    materialNode.setOperationParent(productNode); // Define the parent as the product
                    productNode.addChild(materialNode);

                    nodesMap.put(materialId, materialNode);

                    buildSubTree(materialId, materialNode, booData, itemNames, operationDescriptions);
                }

                if (Objects.equals(productID, mainObjectiveID)) {
                    productNode.setType(NodeType.PRODUCT);
                } else if (!hasOperations && productNode.getChildren().isEmpty()) {
                    productNode.setType(NodeType.RAW_MATERIAL); // If no children, it's a raw material
                } else if (!productNode.getChildren().isEmpty()) {
                    productNode.setType(NodeType.COMPONENT); // If it has children, it's a component
                }
            }
        }
    }

    /**
     * Finds the indices of the parentheses in the input array.
     *
     * @param inputArray the array to search for parentheses
     * @param startIndex the index to start searching from in the array
     * @return an array with the indices of the opening and closing parentheses
     */
    private int[] findParenthesesIndices(String[] inputArray, int startIndex) {
        int openParenIndex = -1;
        int closeParenIndex = -1;

        // Find the index of '('
        for (int i = startIndex; i < inputArray.length; i++) {
            if (inputArray[i].contains("(")) {
                openParenIndex = i;
                break;
            }
        }

        // Find the index of ')'
        for (int i = openParenIndex + 1; i < inputArray.length; i++) {
            if (inputArray[i].contains(")")) {
                closeParenIndex = i;
                break;
            }
        }

        return new int[]{openParenIndex, closeParenIndex};
    }

    /**
     * Counts the number of elements between parentheses in the input array.
     *
     * @param inputArray the array to count the elements from between parentheses
     * @param startIndex the index to start counting from in the array
     * @return the number of elements between parentheses
     */
    private int countElementsBetweenParentheses(String[] inputArray, int startIndex) {
        int[] indices = findParenthesesIndices(inputArray, startIndex);
        int openParenIndex = indices[0];
        int closeParenIndex = indices[1];

        if (openParenIndex == -1 || closeParenIndex == -1) {
            return 0; // Brackets not found
        }

        // Count the elements between the brackets
        int count = 0;
        for (int i = openParenIndex + 1; i < closeParenIndex; i++) {
            String element = inputArray[i].trim();
            if (!element.isEmpty()) {
                count++;
            }
        }

        // Divide by 2 (each element consists of ID and quantity)
        return count / 2;
    }

    /**
     * Finds the index of the start of the materials section in the input array.
     *
     * @param inputArray the array to search for the materials section
     * @return the index of the start of the materials section
     */
    private int findStartIndexAfterSecondParentheses(String[] inputArray) {
        int[] firstPair = findParenthesesIndices(inputArray, 0);
        int[] secondPair = findParenthesesIndices(inputArray, firstPair[1] + 1);

        return (secondPair[0] != -1 && secondPair[0] + 1 < inputArray.length)
                ? secondPair[0] + 1
                : -1;
    }

    /**
     * Counts the number of operations in the input array.
     *
     * @param inputArray the array to count the operations from
     * @return the number of operations in the input array
     */
    private int countOperations(String[] inputArray) {
        return countElementsBetweenParentheses(inputArray, 0);
    }

    /**
     * Counts the number of materials in the input array.
     *
     * @param inputArray the array to count the materials from
     * @return the number of materials in the input array
     */
    private int countMaterials(String[] inputArray) {
        int[] firstPair = findParenthesesIndices(inputArray, 0);
        return countElementsBetweenParentheses(inputArray, firstPair[1] + 1);
    }

    /**
     * Finds the index of the start of the materials section in the input array.
     *
     * @param inputArray the array to search for the materials section
     * @return the index of the start of the materials section
     */
    private int findMaterialsStartIndex(String[] inputArray) {
        return findStartIndexAfterSecondParentheses(inputArray);
    }

    /**
     * Searches for a node in the production tree by its ID or name.
     *
     * @param id the ID of the operation or material to search for
     * @return a map with details such as type, quantity (for materials), and parent operation if applicable
     */
    public Map<String, String> searchNodeByID(String id) {
        Map<String, String> result = new HashMap<>();

        // Find the node with the specified ID or name
        TreeNode<String> node = nodesMap.get(id);
        if (node == null) {
            result.put("Error", "Leaf not found on Production Tree.");
            return result;
        }

        String value = node.getValue();
        NodeType type = node.getType();

        // Determines the type based on NodeType
        if (type == NodeType.OPERATION) {
            result.put("Type", "Operation");
            result.put("Description", value);

            // Find the parent operation directly
            TreeNode<String> parentOperation = node.getOperationParent();
            if (parentOperation != null) {
                result.put("Parent Operation", parentOperation.getValue());
            } else {
                result.put("Parent Operation", "None");
            }

        } else if (type == NodeType.PRODUCT || type == NodeType.COMPONENT || type == NodeType.RAW_MATERIAL) {
            result.put("Type", type.toString());
            result.put("Description", value);

            // Extract the quantity from the material
            String quantity = extractQuantityFromMaterial(value);
            result.put("Quantity", quantity);

            // Find the material's parent operation
            TreeNode<String> parentOperation = node.getOperationParent();
            if (parentOperation != null) {
                result.put("Parent Operation", parentOperation.getValue());
            }
        }

        return result;
    }

    /**
     * Searches for a node in the production tree by its ID or name.
     *
     * @param name the name of the operation or material to search for
     * @return a map with details such as type, quantity (for materials), and parent operation if applicable
     */
    public Map<String, String> searchNodeByName(String name) {

        for (Map.Entry<String, TreeNode<String>> entry : nodesMap.entrySet()) {
            TreeNode<String> node = entry.getValue();
            if (node.getValue().contains(name)) {
                String id = entry.getKey();
                return searchNodeByID(id);
            }
        }
        return null;
    }

    /**
     * Extracts the quantity from a material string.
     *
     * @param material the material string to extract the quantity from
     * @return the quantity of the material
     */
    private String extractQuantityFromMaterial(String material) {
        String[] parts = material.split(" ");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        return "Unknown quantity";
    }

    /**
     * Displays the production tree in a human-readable format.
     *
     * @param node the current node in the production tree
     * @return the string representation of the production tree
     */
    public int calculateDepth(TreeNode<String> node) {
        int depth = 0;
        while (node != null) {
            depth++;
            node = node.getParent();
        }
        return depth;
    }

    /**
     * Calculates the depth of a node in the production tree.
     *
     * @param depth the depth of the node
     * @return the priority for the depth
     */
    private int getPriorityForDepth(int depth) {
        // Assign a unique priority to each depth level
        if (!depthPriorityMap.containsKey(depth)) {
            depthPriorityMap.put(depth, nextPriority++);
        }
        return depthPriorityMap.get(depth);
    }

    /**
     * Displays the production tree in a human-readable format.
     */
    public void viewQualityChecksInOrder() {
        HeapPriorityQueue<Integer, String> tempQueue = qualityCheckQueue.clone();

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "Quality Checks in Order of Priority:\n" + Utils.RESET);

        while (!tempQueue.isEmpty()) {
            var check = tempQueue.removeMin();
            System.out.println(Utils.GREEN + "Quality Check: " + Utils.RESET + check.getValue() + " [Priority: " + check.getKey() + "]");
        }
    }

    /**
     * Allows the user to perform quality checks one at a time interactively.
     */
    public void performQualityChecksInteractively() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "Starting Interactive Quality Checks:" + Utils.RESET);
        while (!qualityCheckQueue.isEmpty()) {
            var nextCheck = qualityCheckQueue.removeMin();
            System.out.println("\nNext Quality Check: " + nextCheck.getValue() +
                    " [Priority: " + nextCheck.getKey() + "]");

            System.out.print(Utils.YELLOW + "Perform this quality check? (Y/N): " + Utils.RESET);

            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                System.out.println("\n" + Utils.GREEN + "Performing Quality Check: " + nextCheck.getValue() + Utils.RESET);
            } else if (input.equals("n")) {
                System.out.println("\n" + Utils.RED + "Skipping Quality Check: " + nextCheck.getValue() + Utils.RESET);
            } else {
                System.err.println("Invalid input. Skipping Quality Check.");
            }

            System.out.print("\n\n" + Utils.BOLD + "Do you want to continue with the next quality check? (Y/N): " + Utils.RESET);
            input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("n")) {
                System.out.println("\n" + Utils.RED + "Stopping Quality Checks." + Utils.RESET);
                break;
            }
        }

        if (qualityCheckQueue.isEmpty()) {
            System.out.println(Utils.GREEN + "All Quality Checks have been completed." + Utils.RESET);
        }


    }

    /**
     * Prioritizes the critical path of the production tree based on the depth of operations.
     *
     * @param root the root of the production tree
     */
    public List<String> prioritizeCriticalPath(TreeNode<String> root) {
        if (root == null) {
            return new ArrayList<>();
        }

        List<String> criticalPaths = new ArrayList<>();

        // Priority Queue to store operations by depth
        HeapPriorityQueue<Integer, TreeNode<String>> criticalPathQueue = new HeapPriorityQueue<>();

        // Recursive function to traverse and calculate depth
        traverseAndAddToHeap(root, criticalPathQueue);

        // Find the operations with the maximum depth
        int maxDepth = Integer.MIN_VALUE;
        List<TreeNode<String>> maxDepthNodes = new ArrayList<>();

        while (!criticalPathQueue.isEmpty()) {
            Entry<Integer, TreeNode<String>> entry = criticalPathQueue.removeMin();
            int depth = -entry.getKey();
            TreeNode<String> node = entry.getValue();

            // If the depth is greater than the maximum, update the maximum and clear the list
            if (depth > maxDepth) {
                maxDepth = depth;
                maxDepthNodes.clear();
                maxDepthNodes.add(node);
            }
            // If the depth is equal to the maximum, add it to the list
            else if (depth == maxDepth) {
                maxDepthNodes.add(node);
            }
        }

        // Adds the operations with the maximum depth to the criticalPaths list
        for (TreeNode<String> node : maxDepthNodes) {
            criticalPaths.add(node.getValue() + " (Depth: " + maxDepth + ")");
        }

        return criticalPaths;
    }


    /**
     * Traverses the production tree and adds operations to a priority queue based on depth.
     *
     * @param node  the current node in the production tree
     * @param queue the priority queue to store operations by depth
     */
    private void traverseAndAddToHeap(TreeNode<String> node, HeapPriorityQueue<Integer, TreeNode<String>> queue) {
        if (node.getType() == NodeType.OPERATION) {
            int depth = calculateDepth(node);
            // Use negative depth to simulate max-heap behavior
            queue.insert(-depth, node);
        }
        for (TreeNode<String> child : node.getChildren()) {
            traverseAndAddToHeap(child, queue);
        }
    }

    /**
     * Displays the critical path of the production tree in sequence.
     *
     * @param root the root of the production tree
     */
    public void displayCriticalPathInSequence(TreeNode<String> root) {
        System.out.println("Critical Path Sequence:");
        traverseCriticalPath(root);
    }

    /**
     * Traverses the critical path of the production tree in reverse order.
     *
     * @param node the current node in the production tree
     */
    public void traverseCriticalPath(TreeNode<String> node) {
        if (node == null) return;

        // Perform a reverse traversal of the children first
        for (TreeNode<String> child : node.getChildren()) {
            traverseCriticalPath(child);
        }

        // Visit the current node
        if (node.getType() == NodeType.OPERATION) {
            System.out.println(node.getValue());
        }
    }

    /**
     * Calculate the total quantity of materials and time needed for the production.
     *
     * @return a map containing the total quantity of materials and time needed
     */
    public Map<String, Map<String,BigDecimal>> calculateTotalMaterialsAndOperations(TreeNode<String> root) {
        Map<String, BigDecimal> materialQuantities = new HashMap<>();
        Map<String, BigDecimal> operationTimes = new HashMap<>();
        calculateTotals(materialQuantities, operationTimes, root);

        Map<String, Map<String,BigDecimal>> result = new HashMap<>();
        result.put("materialQuantities", materialQuantities);
        result.put("operationTimes", operationTimes);
        return result;
    }

    /**
     * Calculates the total quantity of materials and time needed for the production.
     *
     * @param materialQuantities  the map to store the total quantity of materials
     * @param operationQuantities the map to store the total time needed for operations
     * @param root                the root of the production tree
     */
    public void calculateTotals(Map<String, BigDecimal> materialQuantities, Map<String, BigDecimal> operationQuantities, TreeNode<String> root) {
        traverseTree(root, materialQuantities, operationQuantities);
    }

    /**
     * Traverses the production tree and calculates the total quantity of materials and time needed.
     *
     * @param node                the current node in the production tree
     * @param materialQuantities  the map to store the total quantity of materials
     * @param operationQuantities the map to store the total time needed for operations
     */
    public void traverseTree(TreeNode<String> node, Map<String, BigDecimal> materialQuantities, Map<String, BigDecimal> operationQuantities) {
        if (node == null) {
            return;
        }

        String value = node.getValue();
        if (node.getType().equals(NodeType.PRODUCT) || node.getType().equals(NodeType.COMPONENT) ||
                node.getType().equals(NodeType.RAW_MATERIAL)) {
            int startIndex = value.indexOf("(Quantity: ");
            int endIndex = value.indexOf(')', startIndex);
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                String materialName = value.substring(0, startIndex).trim();
                String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
                String numericQuantityStr = quantityStr.replaceAll("[^\\d.]", ""); // Extract numeric part
                BigDecimal quantity = new BigDecimal(numericQuantityStr);
                materialQuantities.put(materialName, materialQuantities.getOrDefault(materialName, BigDecimal.ZERO).add(quantity));
            }
        } else if (node.getType().equals(NodeType.OPERATION)) {
            int startIndex = value.indexOf("(Quantity: ");
            int endIndex = value.indexOf(')', startIndex);
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                String operationName = value.substring(0, startIndex).trim();
                String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
                String numericQuantityStr = quantityStr.replaceAll("[^\\d.]", ""); // Extract numeric part
                BigDecimal quantity = new BigDecimal(numericQuantityStr);
                operationQuantities.put(operationName, operationQuantities.getOrDefault(operationName, BigDecimal.ZERO).add(quantity));
            }
        }

        for (TreeNode<String> child : node.getChildren()) {
            traverseTree(child, materialQuantities, operationQuantities);
        }
    }

    public List<Map.Entry<Material, BigDecimal>> getMaterialQuantityPairs() {
        List<Map.Entry<Material, BigDecimal>> materialQuantityPairs = new ArrayList<>();
        for (Map.Entry<String, TreeNode<String>> entry : nodesMap.entrySet()) {
            TreeNode<String> node = entry.getValue();
            if (node.getType() == NodeType.PRODUCT || node.getType() == NodeType.COMPONENT || node.getType() == NodeType.RAW_MATERIAL) {
                String value = node.getValue();
                int startIndex = value.indexOf("(Quantity: ");
                int endIndex = value.indexOf(')', startIndex);
                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    String materialID = entry.getKey();
                    String materialName = value.substring(0, startIndex).trim();
                    String quantityWithUnit = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
                    String numericQuantityStr = quantityWithUnit.replaceAll("[^\\d.]", ""); // Extract numeric part
                    BigDecimal quantity = new BigDecimal(numericQuantityStr);
                    Material material = new Material(materialID, materialName, numericQuantityStr);
                    boolean found = false;
                    for (Map.Entry<Material, BigDecimal> pair : materialQuantityPairs) {
                        if (pair.getKey().equals(material)) {
                            pair.setValue(pair.getValue().add(quantity));
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        materialQuantityPairs.add(new AbstractMap.SimpleEntry<>(material, quantity));
                    }
                }
            }
        }
        return materialQuantityPairs;
    }

    /**
     * Updates the quantities of materials in the production tree.
     *
     * @param materialID  the ID of the material to update
     * @param newQuantity the new quantity of the material
     */
    public void updateQuantities(String materialID, double newQuantity) {
        TreeNode<String> node = nodesMap.get(materialID);
        if (node == null) {
            System.err.println("Material not found in the production tree.");
            return;
        }

        String value = node.getValue();
        int startIndex = value.indexOf("(Quantity: ");
        int endIndex = value.indexOf(')', startIndex);
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String materialName = value.substring(0, startIndex).trim();
            String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
            String numericQuantityStr = quantityStr.replaceAll("[^\\d.]", ""); // Extract numeric part
            BigDecimal oldQuantity = new BigDecimal(numericQuantityStr);
            value = materialName + " (Quantity: " + new BigDecimal(newQuantity).setScale(3, RoundingMode.HALF_UP).toString() + ")";
            node.setValue(value);
            updateChildrenQuantities(materialID, newQuantity);
        }
    }

    public void updateChildrenQuantities(String materialID, double parentNewQuantity) {
        TreeNode<String> node = nodesMap.get(materialID);
        if (node == null) {
            System.err.println("Material not found in the production tree.");
            return;
        }
        updateChildrenQuantitiesRecursive(node, parentNewQuantity);
    }

    private void updateChildrenQuantitiesRecursive(TreeNode<String> node, double parentNewQuantity) {
        if (node == null) {
            return;
        }

        for (TreeNode<String> child : node.getChildren()) {
            String value = child.getValue();
            int startIndex = value.indexOf("(Quantity: ");
            int endIndex = value.indexOf(')', startIndex);
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                String childName = value.substring(0, startIndex).trim();
                String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
                String numericQuantityStr = quantityStr.replaceAll("[^\\d.]", ""); // Extract numeric part
                BigDecimal oldQuantity = new BigDecimal(numericQuantityStr);
                BigDecimal newQuantity = oldQuantity.multiply(BigDecimal.valueOf(parentNewQuantity));
                newQuantity = newQuantity.setScale(3, RoundingMode.HALF_UP); // Set scale to 3 decimal places
                value = childName + " (Quantity: " + newQuantity.toString() + ")";
                child.setValue(value);

                // Recursively update the quantities of the child's children
                updateChildrenQuantitiesRecursive(child, parentNewQuantity);
            }
        }
    }
}