package trees.ProductionTree;

import trees.AVL_BST.AVL;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import trees.heap.HeapPriorityQueue;


public class ProductionTree {
    private TreeNode<String> root;
    private Map<String, TreeNode<String>> nodesMap = new HashMap<>();
    private HeapPriorityQueue<Integer, String> qualityCheckQueue; // Priority Queue for Quality Checks
    private Map<Integer, Integer> depthPriorityMap; // Maps depth to priority
    private int nextPriority; // Counter for the next available priority

    private static final String FILES_PATH = "src/main/resources/";

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
     * Builds the production tree from three CSV files: BOO, items, and operations.
     * @param booFileName the path to the CSV file with the Bill of Operations
     * @param itemsFileName the path to the CSV file with the items
     * @param operationFileName the path to the CSV file with the operations
     * @return the root of the production tree
     */
    public TreeNode<String> buildProductionTree(
            String booFileName,
            String itemsFileName,
            String operationFileName,
            String mainObjectiveID
    ) {

        // Read the data from the files
        List<String[]> booData = readCsvFile(booFileName);
        Map<String, String> itemNames = readMaterials(itemsFileName);
        Map<String, String> operationDescriptions = readOperations(operationFileName);

        // Get the name of the main objective
        String mainObjectiveName = itemNames.getOrDefault(mainObjectiveID, "Unknown Product");

        // Create the root node
        root = new TreeNode<>(mainObjectiveName);

        // Identifies the operation associated with mainObjectiveID in BOO
        String initialOperationID = null;
        for (String[] entry : booData) {
            if (entry.length >= 2 && entry[1].equals(mainObjectiveID)) {
                initialOperationID = entry[0];
                break;
            }
        }

        if (initialOperationID == null) {
            System.out.println("Main objective not found in the Bill of Operations.");
            return root; // Returns the incomplete tree
        }

        // Build the production tree recursively
        buildSubTree(initialOperationID, root, booData, itemNames, operationDescriptions);

        return root; // Returns the complete tree
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
        for (String[] booEntry : booData) {
            if (booEntry.length >= 2 && booEntry[0].equals(currentOperationID)) {
                String productID = booEntry[1];
                String productQuantity = booEntry[2];

                String productName = itemNames.getOrDefault(productID, "Unknown Product");
                TreeNode<String> productNode = new TreeNode<>(productName + " (" + productQuantity + "x)");
                productNode.setType(NodeType.MATERIAL);
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
                    TreeNode<String> subOperationNode = new TreeNode<>(subOperationDescription + " (" + subOperationQuantity + "x)");
                    subOperationNode.setType(NodeType.OPERATION);
                    subOperationNode.setOperationParent(parent); // Define the parent
                    productNode.addChild(subOperationNode);

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

                    TreeNode<String> materialNode = new TreeNode<>(materialName + " (" + quantity + "x)");
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
     * Reads the materials from a CSV file and returns a map of item IDs to item names.
     * @param itemsFileName the path to the CSV file with the items
     * @return a map of item IDs to item names
     */
    private Map<String, String> readMaterials(String itemsFileName) {
        List<String[]> itemsData = readCsvFile(itemsFileName);
        Map<String, String> itemNames = new HashMap<>();
        for (String[] item : itemsData) {
            if (item.length >= 2) {
                itemNames.put(item[0], item[1]);
            }
        }
        return itemNames;
    }

    /**
     * Reads the operations from a CSV file and returns a map of operation IDs to operation descriptions.
     * @param operationFileName the path to the CSV file with the operations
     * @return a map of operation IDs to operation descriptions
     */
    private Map<String, String> readOperations(String operationFileName) {
        List<String[]> operationsData = readCsvFile(operationFileName);
        Map<String, String> operationDescriptions = new HashMap<>();
        for (String[] operation : operationsData) {
            if (operation.length >= 2) {
                operationDescriptions.put(operation[0], operation[1]);
            }
        }
        return operationDescriptions;
    }

    /**
     * Returns a string representation of the production tree with the specified main objective.
     * Only includes children of the root.
     * @return a string representation of the production tree
     */
    public String toIndentedStringForObjective() {
        StringBuilder builder = new StringBuilder();

        for (TreeNode<String> child : root.getChildren()) {
            toIndentedStringHelper(child, builder, 1);
        }

        return builder.toString();
    }


    /**
     * Generates a string representation of the production tree with a custom indentation.
     * @param node the node to start the string representation from recursively
     * @param builder the string builder to append the string representation to recursively
     * @param level the current level of the tree recursively
     */
    private void toIndentedStringHelper(TreeNode<String> node, StringBuilder builder, int level) {
        if (level > 1) {
            builder.append("    ".repeat(level - 1)).append("|___");
        }
        builder.append(node.getValue());
        if (node.getType() != null) {
            builder.append(" (").append(node.getType()).append(")");
        }
        builder.append("\n");
        for (TreeNode<String> child : node.getChildren()) {
            toIndentedStringHelper(child, builder, level + 1);
        }
    }

    /**
     * Reads a CSV file and returns its data as a list of string arrays.
     * @param filePath the path to the CSV file
     * @return a list of string arrays representing the data in the CSV file
     */
    private List<String[]> readCsvFile(String filePath) {
        List<String[]> data = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILES_PATH + filePath));
            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i).split(";");
                data.add(values);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the CSV file: " + e.getMessage());
        }
        return data;
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
            result.put("Error", "Material or Operation not found.");
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
            if (parentOperation != null && !Objects.equals(parentOperation.getValue(), root.getValue())) {
                result.put("Parent Operation", parentOperation.getValue());
            }else {
                assert parentOperation != null;
                if (Objects.equals(parentOperation.getValue(), root.getValue())) {
                    result.put("Parent Operation", "None");
                }
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

    private int calculatePriorityLevel(TreeNode<String> node) {
        int depth = 0;
        while (node != null) {
            depth++;
            node = node.getParent();
        }
        return depth; // Use depth as priority (lower depth = higher priority)
    }

    private int calculateDepth(TreeNode<String> node) {
        int depth = 0;
        while (node != null) {
            depth++;
            node = node.getParent();
        }
        return depth;
    }

    private int getPriorityForDepth(int depth) {
        // Assign a unique priority to each depth level
        if (!depthPriorityMap.containsKey(depth)) {
            depthPriorityMap.put(depth, nextPriority++);
        }
        return depthPriorityMap.get(depth);
    }

    public void viewQualityChecksInOrder() {
        System.out.println("Quality Checks in Order of Priority:");
        HeapPriorityQueue<Integer, String> tempQueue = qualityCheckQueue.clone();
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

        scanner.close();
    }


    public void simulateProduction(TreeNode<String> productionTreeRoot) {
        AVL<String> avl = new AVL<>();

        // Populate AVL Tree with operations from the production tree
        populateAVL(productionTreeRoot, avl);

        // Traverse AVL Tree and simulate operations
        System.out.println("Simulating Production Process (In-Order):");
        avl.printInOrder();
    }

    private void populateAVL(TreeNode<String> node, AVL<String> avlTree) {
        if (node.getType() == NodeType.OPERATION) {
            avlTree.insert(node.getValue());
        }
        for (TreeNode<String> child : node.getChildren()) {
            populateAVL(child, avlTree);
        }
    }


    // main para testar!! Depois apagar
    public static void main(String[] args) {
        // Criar uma árvore de produção a partir dos ficheiros CSV
        ProductionTree productionTree = new ProductionTree();
        productionTree.buildProductionTree("boo_v2.csv", "items.csv", "operations.csv", "1006");

        // Exibir a árvore de produção de forma indentada
        System.out.println("Árvore de Produção:\n" + productionTree.toIndentedStringForObjective());

        // Secção de testes de pesquisa
        System.out.println("\nResultados da Pesquisa:");

        // Teste 1: Procurar uma operação existente pelo ID
        String operationId = "11";
        System.out.println("Teste 1 - Pesquisa por Operação com ID " + operationId + ":");
        executeAndPrintSearch(productionTree, operationId);

        // Teste 2: Procurar um material existente pelo ID
        String materialId = "1004";
        System.out.println("Teste 2 - Pesquisa por Material com ID " + materialId + ":");
        executeAndPrintSearch(productionTree, materialId);

        // Teste 3: Procurar por um ID ou nome inexistente
        String nonExistentId = "500";
        System.out.println("Teste 3 - Pesquisa por ID ou Nome " + nonExistentId + " (não existe):");
        executeAndPrintSearch(productionTree, nonExistentId);

        // Realizar verificações de qualidade
        System.out.println("\nRealizando Verificações de Qualidade:");
        productionTree.viewQualityChecksInOrder();
        productionTree.performQualityChecksInteractively();

        // Simular a produção completa
        System.out.println("\nSimulando Produção:");
        productionTree.simulateProduction(productionTree.getRoot());
    }

    /**
     * Helper method to execute and print the search results in a readable format.
     */
    private static void executeAndPrintSearch(ProductionTree productionTree, String id) {
        Map<String, String> result = productionTree.searchNode(id);
        if (result.containsKey("Error")) {
            System.out.println("Error: Material or operation not found!");
        } else {
            System.out.println("Type: " + result.get("Type"));
            System.out.println("Description and Quantity: " + result.get("Description"));
            if (result.get("Type").equals("Material")) {
                System.out.println("Parent Operation: " + result.getOrDefault("Parent Operation", "None"));
            } else {
                System.out.println("Parent Operation: " + result.getOrDefault("Parent Operation", "None"));
            }
        }
        System.out.println();
    }

}