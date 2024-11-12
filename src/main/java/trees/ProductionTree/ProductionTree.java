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

    private static final String FILES_PATH = "src/main/resources/";

    /**
     * Constructs a production tree with the specified main objective.
     * @param mainObjective the main objective of the production tree
     */
    public ProductionTree(String mainObjective) {
        this.root = new TreeNode<>("Build " + mainObjective);
    }

    /**
     * Returns the root of the production tree.
     * @return the root of the production tree
     */
    public TreeNode<String> getRoot() {
        return root;
    }

    /**
     * Builds the production tree from three CSV files: BOO, items, and operations.
     * @param booFileName the path to the CSV file with the Bill of Operations
     * @param itemsFileName the path to the CSV file with the items
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
                        int quantity = Integer.parseInt(booEntry[i + 1]);
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
     * @param mainObjective the main objective of the production tree
     * @return a string representation of the production tree
     */
    public String toIndentedStringForObjective(String mainObjective) {
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
     * @param node the node to start the string representation from recursively
     * @param builder the string builder to append the string representation to recursively
     * @param level the current level of the tree recursively
     */
    private void toIndentedStringHelper(TreeNode<String> node, StringBuilder builder, int level) {
        if (level > 0) {
            builder.append("    ".repeat(level - 1)).append("|___");
        }
        builder.append(node.getValue()).append("\n");
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
            e.printStackTrace();
        }
        return data;
    }

    // main para testar!! Depois apagar
    public static void main(String[] args) {
        ProductionTree tree = new ProductionTree("Bicycle");
        tree.buildProductionTree("boo.csv", "items.csv", "operations.csv");
        System.out.println(tree.toIndentedStringForObjective("Bicycle"));
    }
}

