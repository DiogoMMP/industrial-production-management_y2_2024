package trees.ProductionTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prodPlanSimulator.domain.Material;
import prodPlanSimulator.domain.Operation;

public class ProductionTree {
    private TreeNode<String> root;
    private Map<String, TreeNode<String>> nodesMap = new HashMap<>();
    private String mainObjective;

    private static final String FILES_PATH = "src/main/resources/";

    /**
     * Constructs a production tree with the specified main objective.
     *
     * @param mainObjective the main objective of the production tree
     */
    public ProductionTree(String mainObjective) {
        this.mainObjective = mainObjective;
        this.root = new TreeNode<>("Build " + mainObjective);
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
     * Builds the production tree from three CSV files: BOO, items, and operations.
     *
     * @param booFileName       the path to the CSV file with the Bill of Operations
     * @param itemsFileName     the path to the CSV file with the items
     * @param operationFileName the path to the CSV file with the operations
     * @return the root of the production tree
     */
    public TreeNode<String> buildProductionTree(String booFileName, String itemsFileName, String operationFileName) {
        List<String[]> booData = readCsvFile(booFileName);
        List<String[]> itemsData = readCsvFile(itemsFileName);
        List<String[]> operationsData = readCsvFile(operationFileName);

        Map<String, String> itemNames = new HashMap<>();
        for (String[] item : itemsData) {
            if (item.length >= 2) {
                itemNames.put(item[0], item[1]);
            }
        }

        Map<String, String> operationDescriptions = new HashMap<>();
        for (String[] operation : operationsData) {
            if (operation.length >= 2) {
                operationDescriptions.put(operation[0], operation[1]);
            }
        }

        Map<String, TreeNode<String>> productSubTrees = new HashMap<>();

        for (String[] booEntry : booData) {
            if (booEntry.length >= 2) {
                String itemId = booEntry[0];
                String operationId = booEntry[1];

                TreeNode<String> subTreeRoot = productSubTrees.get(itemId);
                if (subTreeRoot == null) {
                    subTreeRoot = new TreeNode<>("Build " + itemNames.getOrDefault(itemId, "Unknown Item"));
                    root.addChild(subTreeRoot);
                    productSubTrees.put(itemId, subTreeRoot);
                }

                String operationDescription = operationDescriptions.getOrDefault(operationId, "Unknown Operation");
                Operation operation = new Operation(operationId, operationDescription, 1);
                TreeNode<String> operationNode = new TreeNode<>(operation.toString());
                subTreeRoot.addChild(operationNode);
                nodesMap.put(operationId, operationNode);

                for (int i = 2; i < booEntry.length; i += 2) {
                    if (i + 1 < booEntry.length) {
                        String subitemId = booEntry[i];
                        String quantity = booEntry[i + 1];
                        String subitemName = itemNames.getOrDefault(subitemId, "Unknown Subitem");

                        Material material = new Material(subitemId, subitemName, "Unknown description", "Type", quantity);
                        TreeNode<String> subitemNode = new TreeNode<>(material.toString());
                        operationNode.addChild(subitemNode);

                        nodesMap.put(subitemId, subitemNode);
                    }
                }
            }
        }
        return root;
    }

    /**
     * Returns a string representation of the production tree with the specified main objective.
     *
     * @return a string representation of the production tree
     */
    public String toIndentedStringForObjective() {
        StringBuilder builder = new StringBuilder();
        for (TreeNode<String> child : root.getChildren()) {
            if (child.getValue().equals("Build " + mainObjective)) {
                toIndentedStringHelper(child, builder, 0);
                break;
            }
        }
        return builder.toString();
    }

    /**
     * Generates a string representation of the production tree with a custom indentation.
     *
     * @param node    the node to start the string representation from recursively
     * @param builder the string builder to append the string representation to recursively
     * @param level   the current level of the tree recursively
     */
    private void toIndentedStringHelper(TreeNode<String> node, StringBuilder builder, int level) {
        if (level > 0) {
            builder.append("    ".repeat(level - 1)).append("|___");
        }
        builder.append(node.getValue());

        // Check if the node represents a material and extract the quantity
        if (node.getValue().contains("(Material)")) {
            String[] parts = node.getValue().split("x");
            if (parts.length > 1) {
                String quantity = parts[0].trim();
                builder.append(" [Quantity: ").append(quantity).append("]");
            }
        }

        builder.append("\n");
        for (TreeNode<String> child : node.getChildren()) {
            toIndentedStringHelper(child, builder, level + 1);
        }
    }

    /**
     * Reads a CSV file and returns its data as a list of string arrays.
     *
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
            e.printStackTrace();
        }
        return data;
    }


    /**
     * Updates the quantity of a specific material in the production tree.
     *
     * @param materialId  The ID of the material to update.
     * @param newQuantity The new quantity to set.
     * @return True if the material was updated successfully, false otherwise.
     */
    public boolean updateMaterialQuantity(String materialId, String newQuantity) {
        // Locate the node using the nodesMap
        TreeNode<String> node = nodesMap.get(materialId);
        if (node == null) {
            System.out.println("Material with ID " + materialId + " not found.");
            return false;
        }

        // Check if the node represents a material (leaf node)
        String nodeValue = node.getValue();
        if (nodeValue.contains("(Material)")) { // Materials contain "(Material)" in their description
            // Update the quantity in the string representation
            String updatedNodeValue = nodeValue.replaceFirst("\\d+x", newQuantity + "x");
            node.setValue(updatedNodeValue);
            System.out.println("Updated material: " + materialId + " to quantity: " + newQuantity);
            return true;
        }

        System.out.println("Node with ID " + materialId + " is not a material.");
        return false;
    }


    // main para testar!! Depois apagar
    public static void main(String[] args) {
        ProductionTree tree1 = new ProductionTree("raw bench seat");
        tree1.buildProductionTree("boo.csv", "items.csv", "operations.csv");
        System.out.println(tree1.toIndentedStringForObjective());
        // Print the tree before update
        System.out.println("Before Update:");
        System.out.println(tree1.toIndentedStringForObjective());

        // Update a material's quantity
        String materialId = "1015"; // Replace with a valid material ID from your CSV
        String newQuantity = "60";
        boolean result = tree1.updateMaterialQuantity(materialId, newQuantity);

        if (result) {
            System.out.println("Material updated successfully.");
        } else {
            System.out.println("Failed to update material.");
        }

        // Print the tree after update
        System.out.println("After Update:");
        System.out.println(tree1.toIndentedStringForObjective());
        System.out.println("\n");

        ProductionTree tree2 = new ProductionTree("Bicycle");
        tree2.buildProductionTree("boo1.csv", "items1.csv", "operations1.csv");
        System.out.println(tree2.toIndentedStringForObjective());
    }
}

