package UI.Domain.USEI.US09;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import repository.Instances;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShowTreeUI implements Runnable {

    private ProductionTree productionTree = Instances.getInstance().getProductionTree();
    private Map<String, String> items = Instances.getInstance().getItemsRepository().getItemsRepository();

    @Override
    public void run() {
        String choice;
        List<MenuItem> options = new ArrayList<>();

        // Sort the items map by its keys
        Map<String, String> sortedItems = sortItemsByID(items);

        // Create menu options from the sorted map
        for (Map.Entry<String, String> entry : sortedItems.entrySet()) {
            options.add(new MenuItem("Item: " + entry.getKey() + " - " + entry.getValue(), new ShowTreeUI()));
        }

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Choose the Item to be Visualized ------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    Utils.clearConsole();
                    showTree(options.get(option).toString().split(" ")[1]);
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1 && !options.get(option).toString().equals("Back"));
    }

    public void showTree(String mainObjectiveID) {
        productionTree.buildProductionTree(mainObjectiveID);
        if (productionTree.getRoot().getValue().contains("Unknown Operation")) {
            System.err.println("No production tree found for the selected item.");
            System.err.println("Please select a different item.\n");
            return;
        }
        toIndentedStringForObjective();
    }

    /**
     * Returns a string representation of the production tree with the specified main objective.
     * Only includes children of the root.
     */
    public void toIndentedStringForObjective() {
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Production Tree ------------\n" + Utils.RESET);
        StringBuilder builder = new StringBuilder();
        toIndentedStringHelper(productionTree.getRoot(), builder, 0);
        System.out.println(builder);
    }


    /**
     * Generates a string representation of the production tree with a custom indentation.
     * @param node the node to start the string representation from recursively
     * @param builder the string builder to append the string representation to recursively
     * @param level the current level of the tree recursively
     */
    private void toIndentedStringHelper(TreeNode<String> node, StringBuilder builder, int level) {
        if (node == null) {
            return;
        }
        if (level > 0) {
            builder.append("    ".repeat(level - 1)).append("|___");
        }
        String color = getNodeColor(node);
        builder.append(color) // Add color to the node
                .append(node.getValue());
        if (node.getType() != null) {
            builder.append(" (").append(node.getType()).append(")");
        }
        builder.append(Utils.RESET)
                .append("\n"); // Reset color
        for (TreeNode<String> child : node.getChildren()) {
            toIndentedStringHelper(child, builder, level + 1);
        }
    }


    public static Map<String, String> sortItemsByID(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, // Resolves duplicate keys, if any
                        LinkedHashMap::new // Maintains insertion order
                ));
    }

    private String getNodeColor(TreeNode<String> node) {
        return switch (node.getType()) {
            case PRODUCT -> Utils.GREEN; // Green
            case COMPONENT -> Utils.BLUE; // Blue
            case RAW_MATERIAL -> Utils.YELLOW; // Yellow
            case OPERATION -> Utils.RED; // Red
            default -> Utils.RESET;  // Reset
        };
    }

}
