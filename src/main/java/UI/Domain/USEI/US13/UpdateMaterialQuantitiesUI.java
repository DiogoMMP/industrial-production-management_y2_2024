package UI.Domain.USEI.US13;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import domain.Material;
import repository.Instances;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UpdateMaterialQuantitiesUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Update Material Quantity", this::updateMaterialQuantity));
        options.add(new MenuItem("Show Updated Production Tree", this::showTree));

        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n\033[1m\033[36m--- Update Material Quantities Menu ------------\033[0m");
            if (option >= 0 && option < options.size()) {
                options.get(option).run();
                Utils.goBackAndWait();
            }
        } while (option != -1);
    }

    private void updateMaterialQuantity() {
        Scanner scanner = new Scanner(System.in);

        // Display the list of materials
        List<Map.Entry<Material, Double>> materialQuantityPairs = productionTree.getMaterialQuantityPairs();
        System.out.println("Materials in the production tree:");
        for (int i = 0; i < materialQuantityPairs.size(); i++) {
            Map.Entry<Material, Double> entry = materialQuantityPairs.get(i);
            System.out.println((i + 1) + ". " + entry.getKey().getName() + " (Quantity: " + entry.getValue() + ")");
        }

        // Allow the user to select a material
        System.out.print("Enter the number of the material you want to update: ");
        int materialIndex = scanner.nextInt() - 1;
        if (materialIndex < 0 || materialIndex >= materialQuantityPairs.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Material selectedMaterial = materialQuantityPairs.get(materialIndex).getKey();

        // Prompt the user to enter a new quantity
        System.out.print("Enter the new quantity for " + selectedMaterial.getName() + ": ");
        double newQuantity = scanner.nextDouble();

        // Update the quantities of the children in cascade
        productionTree.updateChildrenQuantities(selectedMaterial.getID(), newQuantity);

        // Update the quantity using the ProductionTree methods
        productionTree.updateQuantities(selectedMaterial.getID(), newQuantity);
        System.out.println("Quantity updated successfully.");
    }

    private void showTree() {
        toIndentedStringForObjective();
    }

    /**
     * Returns a string representation of the production tree with the specified main objective.
     * Only includes children of the root.
     */
    public void toIndentedStringForObjective() {
        System.out.println("\n\n--- Production Tree ------------");
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
        builder.append(node.getValue());
        if (node.getType() != null) {
            builder.append(" (").append(node.getType()).append(")");
        }
        builder.append("\n");
        for (TreeNode<String> child : node.getChildren()) {
            toIndentedStringHelper(child, builder, level + 1);
        }
    }
}
