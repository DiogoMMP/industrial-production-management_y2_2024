package trees.ProductionTree;

import domain.Material;
import prodPlanSimulator.repository.BOORepository;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.ItemsRepository;
import prodPlanSimulator.repository.OperationsMapRepository;
import java.util.*;

import trees.heap.Entry;
import trees.MaterialsBST.MaterialsBST;
import trees.heap.HeapPriorityQueue;


public class ProductionTree {

    private TreeNode<String> root;
    private Map<String, TreeNode<String>> nodesMap = new HashMap<>();
    private HeapPriorityQueue<Integer, String> qualityCheckQueue; // Priority Queue for Quality Checks
    private Map<Integer, Integer> depthPriorityMap; // Maps depth to priority
    private int nextPriority; // Counter for the next available priority

    /**
     * Constructs a production tree with the specified main objective.
     *
     */
    public ProductionTree() {
        this.root = null;
        this.qualityCheckQueue = new HeapPriorityQueue<>();
        this.depthPriorityMap = new HashMap<>();
        this.nextPriority = 1; // Start priority from 1
    }

    /**
     * Returns the root of the production tree.
     * @return the root of the production tree
     */
    public TreeNode<String> getRoot() {
        return root;
    }

    /**
     * Sets the root of the production tree.
     * @param root the root of the production tree
     */
    public void setRoot(TreeNode<String> root) {
        this.root = root;
    }

    /**
     * Returns the priority queue for quality checks.
     * @return the priority queue for quality checks
     */
    public HeapPriorityQueue<Integer, String> getQualityCheckQueue() {
        return qualityCheckQueue;
    }

    /**
     * Sets the priority queue for quality checks.
     * @param qualityCheckQueue the priority queue for quality checks
     */
    public void setQualityCheckQueue(HeapPriorityQueue<Integer, String> qualityCheckQueue) {
        this.qualityCheckQueue = qualityCheckQueue;
    }

    /**
     * Builds the production tree with the specified main objective.
     * @param mainObjectiveID the ID of the main objective
     */
    public TreeNode<String> buildProductionTree(String mainObjectiveID) {
        BOORepository booRepository = Instances.getInstance().getBOORepository();
        ItemsRepository itemsRepository = Instances.getInstance().getItemsRepository();
        OperationsMapRepository operationsMapRepository = Instances.getInstance().getOperationsMapRepository();
        List<String[]> booData = booRepository.getBOORepository();
        Map<String, String> itemNames = itemsRepository.getItemsRepository();
        Map<String, String> operationDescriptions = operationsMapRepository.getOperationsMapRepository();

        // Locate the root material
        String rootMaterialID = null;
        String rootMaterialQuantity = null;
        for (String[] entry : booData) {
            if (entry[1].equals(mainObjectiveID)) {
                rootMaterialID = entry[1];
                rootMaterialQuantity = entry[2];
                break;
            }
        }

        if (rootMaterialID == null) {
            System.out.println("Main objective not found in the Bill of Operations.");
            return null;
        }

        // Create the root material node
        String rootMaterialName = itemNames.getOrDefault(rootMaterialID, "Unknown Material");
        root = new TreeNode<>(rootMaterialName + " (Quantity: " + rootMaterialQuantity + ")", NodeType.MATERIAL);

        // Locate and attach the first operation
        String varnishOperationID = null;
        String varnishQuantity = null;
        for (String[] entry : booData) {
            if (entry.length >= 2 && entry[1].equals(rootMaterialID)) {
                varnishOperationID = entry[0];
                varnishQuantity = entry[2];
                break;
            }
        }

        if (varnishOperationID != null) {
            String varnishDescription = operationDescriptions.getOrDefault(varnishOperationID, "Unknown Operation");
            TreeNode<String> varnishOperationNode = new TreeNode<>(
                    varnishDescription + " (Quantity: " + varnishQuantity + ")", NodeType.OPERATION
            );
            int depth = calculateDepth(varnishOperationNode);
            int priority = getPriorityForDepth(depth);
            qualityCheckQueue.insert(priority, varnishDescription);
            root.addChild(varnishOperationNode);
            buildSubTree(varnishOperationID, varnishOperationNode, booData, itemNames, operationDescriptions);
        }

        return root;
    }


    /**
     * Builds the subtree of the production tree recursively.
     * @param currentOperationID the ID of the current operation
     * @param parent the parent node of the current operation
     * @param booData the data from the Bill of Operations
     * @param itemNames the map of item IDs to item names
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

                TreeNode<String> productNode = new TreeNode<>(productName + " (Quantity: " + productQuantity + ")", NodeType.MATERIAL);
                productNode.setOperationParent(parent); // Define the parent
                parent.addChild(productNode);

                nodesMap.put(productID, productNode);

                // Add sub-operations
                int numberOperations = countOperations(booEntry);
                int k = 4;
                while (k < 4 + 2 * numberOperations) {
                    String subOperationId = booEntry[k];
                    String subOperationQuantity = booEntry[k + 1];
                    k += 2;

                    String subOperationDescription = operationDescriptions.getOrDefault(subOperationId, "Unknown Operation");
                    TreeNode<String> subOperationNode = new TreeNode<>(subOperationDescription + " (Quantity: " + subOperationQuantity + ")");
                    subOperationNode.setType(NodeType.OPERATION);
                    subOperationNode.setOperationParent(parent); // Define the parent
                    productNode.addChild(subOperationNode);

                    int depth = calculateDepth(subOperationNode);
                    int priority = getPriorityForDepth(depth);
                    qualityCheckQueue.insert(priority, subOperationDescription);

                    nodesMap.put(subOperationId, subOperationNode);

                    buildSubTree(subOperationId, subOperationNode, booData, itemNames, operationDescriptions);
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

                    TreeNode<String> materialNode = new TreeNode<>(materialName + " (Quantity: " + quantity + ")");
                    materialNode.setType(NodeType.MATERIAL);
                    materialNode.setOperationParent(parent); // Define the parent
                    productNode.addChild(materialNode);

                    nodesMap.put(materialId, materialNode);

                    buildSubTree(materialId, materialNode, booData, itemNames, operationDescriptions);
                }
            }
        }
    }

    /**
     * Finds the indices of the parentheses in the input array.
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
     * @param inputArray the array to count the operations from
     * @return the number of operations in the input array
     */
    private int countOperations(String[] inputArray) {
        return countElementsBetweenParentheses(inputArray, 0);
    }

    /**
     * Counts the number of materials in the input array.
     * @param inputArray the array to count the materials from
     * @return the number of materials in the input array
     */
    private int countMaterials(String[] inputArray) {
        int[] firstPair = findParenthesesIndices(inputArray, 0);
        return countElementsBetweenParentheses(inputArray, firstPair[1] + 1);
    }

    /**
     * Finds the index of the start of the materials section in the input array.
     * @param inputArray the array to search for the materials section
     * @return the index of the start of the materials section
     */
    private int findMaterialsStartIndex(String[] inputArray) {
        return findStartIndexAfterSecondParentheses(inputArray);
    }

    /**
     * Searches for a node in the production tree by its ID or name.
     * @param idOrName the ID or name of the operation or material to search for
     * @return a map with details such as type, quantity (for materials), and parent operation if applicable
     */
    public Map<String, String> searchNode(String idOrName) {
        Map<String, String> result = new HashMap<>();

        // Find the node with the specified ID or name
        TreeNode<String> node = nodesMap.get(idOrName);
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
            }else {
                result.put("Parent Operation", "None");
            }

        } else if (type == NodeType.MATERIAL) {
            result.put("Type", "Material");
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
     * Extracts the quantity from a material string.
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
        System.out.println("Quality Checks in Order of Priority:");
        while (!tempQueue.isEmpty()) {
            var check = tempQueue.removeMin();
            System.out.println("Quality Check: " + check.getValue() + " [Priority: " + check.getKey() + "]");
        }
    }

    /**
     * Allows the user to perform quality checks one at a time interactively.
     */
    public void performQualityChecksInteractively() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Starting Interactive Quality Checks:");
        while (!qualityCheckQueue.isEmpty()) {
            var nextCheck = qualityCheckQueue.removeMin();
            System.out.println("Next Quality Check: " + nextCheck.getValue() + " [Priority: " + nextCheck.getKey() + "]");
            System.out.print("Perform this quality check? (yes/no): ");

            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes")) {
                System.out.println("Performing Quality Check: " + nextCheck.getValue());
            } else if (input.equals("no")) {
                System.out.println("Skipping Quality Check: " + nextCheck.getValue());
            } else {
                System.out.println("Invalid input. Skipping Quality Check.");
            }

            System.out.print("Do you want to continue with the next quality check? (yes/no): ");
            input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("no")) {
                System.out.println("Stopping Quality Checks.");
                break;
            }
        }

        if (qualityCheckQueue.isEmpty()) {
            System.out.println("All Quality Checks have been completed.");
        }


    }

    /**
     * Prioritizes the critical path of the production tree based on the depth of operations.
     * @param root the root of the production tree
     */
    public void prioritizeCriticalPath(TreeNode<String> root) {
        if (root == null) {
            System.out.println("Production tree is empty.");
            return;
        }

        // Priority Queue to store operations by depth
        HeapPriorityQueue<Integer, TreeNode<String>> criticalPathQueue = new HeapPriorityQueue<>();

        // Recursive function to traverse and calculate depth
        traverseAndAddToHeap(root, criticalPathQueue);

        // Display the critical path in order
        System.out.println("Critical Path (in order of the most important to the least important):");
        while (!criticalPathQueue.isEmpty()) {
            Entry<Integer, TreeNode<String>> entry = criticalPathQueue.removeMin();
            TreeNode<String> node = entry.getValue();
            System.out.println("Operation: " + node.getValue() + " (Depth: " + -entry.getKey() + ")");
        }
    }

    /**
     * Traverses the production tree and adds operations to a priority queue based on depth.
     * @param node the current node in the production tree
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
     * @param root the root of the production tree
     */
    public void displayCriticalPathInSequence(TreeNode<String> root) {
        System.out.println("Critical Path Sequence:");
        traverseCriticalPath(root);
    }

    /**
     * Traverses the critical path of the production tree in reverse order.
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
    public Map<String, Object> calculateTotalMaterialsAndOperations(TreeNode<String> root) {
        Map<String, Double> materialQuantities = new HashMap<>();
        Map<String, Double> operationTimes = new HashMap<>();
        calculateTotals(materialQuantities, operationTimes, root);

        Map<String, Object> result = new HashMap<>();
        result.put("materialQuantities", materialQuantities);
        result.put("operationTimes", operationTimes);
        return result;
    }

    /**
     * Calculates the total quantity of materials and time needed for the production.
     * @param materialQuantities the map to store the total quantity of materials
     * @param operationQuantities the map to store the total time needed for operations
     * @param root the root of the production tree
     */
    public void calculateTotals(Map<String, Double> materialQuantities, Map<String, Double> operationQuantities, TreeNode<String> root) {
        traverseTree(root, materialQuantities, operationQuantities);
    }

    /**
     * Traverses the production tree and calculates the total quantity of materials and time needed.
     * @param node the current node in the production tree
     * @param materialQuantities the map to store the total quantity of materials
     * @param operationQuantities the map to store the total time needed for operations
     */
    public void traverseTree(TreeNode<String> node, Map<String, Double> materialQuantities, Map<String, Double> operationQuantities) {
        if (node == null) {
            return;
        }

        String value = node.getValue();
        if (node.getType().equals(NodeType.MATERIAL)) {
            int startIndex = value.indexOf("(Quantity: ");
            int endIndex = value.indexOf(')', startIndex);
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                String materialName = value.substring(0, startIndex).trim();
                String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
                double quantity = Double.parseDouble(quantityStr);
                materialQuantities.put(materialName, materialQuantities.getOrDefault(materialName, 0.0) + quantity);
            }
        } else if (node.getType().equals(NodeType.OPERATION)) {
            int startIndex = value.indexOf("(Quantity: ");
            int endIndex = value.indexOf(')', startIndex);
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                String operationName = value.substring(0, startIndex).trim();
                String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
                double quantity = Double.parseDouble(quantityStr);
                operationQuantities.put(operationName, operationQuantities.getOrDefault(operationName, 0.0) + quantity);
            }
        }

        for (TreeNode<String> child : node.getChildren()) {
            traverseTree(child, materialQuantities, operationQuantities);
        }
    }

    public List<Map.Entry<Material, Double>> getMaterialQuantityPairs() {
        List<Map.Entry<Material, Double>> materialQuantityPairs = new ArrayList<>();
        for (Map.Entry<String, TreeNode<String>> entry : nodesMap.entrySet()) {
            TreeNode<String> node = entry.getValue();
            if (node.getType() == NodeType.MATERIAL) {
                String value = node.getValue();
                int startIndex = value.indexOf("(Quantity: ");
                int endIndex = value.indexOf(')', startIndex);
                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    String materialID = entry.getKey();
                    String materialName = value.substring(0, startIndex).trim();
                    String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
                    double quantity = Double.parseDouble(quantityStr);
                    Material material = new Material(materialID, materialName, quantityStr);
                    if (materialQuantityPairs.contains(material)) {
                        for (Map.Entry<Material, Double> pair : materialQuantityPairs) {
                            if (pair.getKey().equals(material)) {
                                pair.setValue(pair.getValue() + quantity);
                            }
                        }
                    } else {
                        materialQuantityPairs.add(new AbstractMap.SimpleEntry<>(material, quantity));
                    }
                }
            }
        }
        return materialQuantityPairs;
    }

    /**
     * Prints the total quantity of materials needed for the production.
     */
    public void printMaterialQuantitiesInAscendingOrder() {
        MaterialsBST materialQuantityBST = new MaterialsBST();
        List<Map.Entry<Material, Double>> materialQuantityPairs = getMaterialQuantityPairs();
        for (Map.Entry<Material, Double> pair : materialQuantityPairs) {
            List<String> materialNames = new ArrayList<>();
            materialNames.add(pair.getKey().getName());
            MaterialsBST.insert(materialNames, pair.getValue());
        }
        materialQuantityBST.inorder();
    }

    /**
     * Prints the total quantity of materials needed for the production in descending order.
     */
    public void printMaterialQuantitiesInDescendingOrder() {
        MaterialsBST materialQuantityBST = new MaterialsBST();
        List<Map.Entry<Material, Double>> materialQuantityPairs = getMaterialQuantityPairs();
        for (Map.Entry<Material, Double> pair : materialQuantityPairs) {
            List<String> materialNames = new ArrayList<>();
            materialNames.add(pair.getKey().getName());
            MaterialsBST.insert(materialNames, pair.getValue());
        }
        materialQuantityBST.reverseInorder();
    }

    /**
     * Updates the quantities of materials in the production tree.
     * @param materialID the ID of the material to update
     * @param newQuantity the new quantity of the material
     */
    public void updateQuantities(String materialID, double newQuantity) {
        TreeNode<String> node = nodesMap.get(materialID);
        if (node == null) {
            System.out.println("Material not found in the production tree.");
            return;
        }

        String value = node.getValue();
        int startIndex = value.indexOf("(Quantity: ");
        int endIndex = value.indexOf(')', startIndex);
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String materialName = value.substring(0, startIndex).trim();
            String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');
            double oldQuantity = Double.parseDouble(quantityStr);
            value = materialName + " (Quantity: " + newQuantity + ")";
            node.setValue(value);
            System.out.println("Updated quantity for " + materialName + " from " + oldQuantity + " to " + newQuantity);
        }
    }
}