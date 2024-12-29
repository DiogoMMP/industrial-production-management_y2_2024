package UI.Domain.USEI.US11;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import domain.Material;
import repository.Instances;
import trees.MaterialsBST.MaterialsBST;
import trees.ProductionTree.ProductionTree;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrackingQuantitiesUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    /**
     * Displays the options to show the material quantities in ascending or descending order.
     */
    @Override
    public void run() {
        String choice;
        List<MenuItem> options = new ArrayList<>();

        options.add(new MenuItem("Show Material Quantities in Ascending Order", new TrackingQuantitiesUI()));
        options.add(new MenuItem("Show Material Quantities in Descending Order", new TrackingQuantitiesUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Choose the Option ------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    if (option == 0) {
                        printMaterialQuantitiesInAscendingOrder();
                    } else if (option == 1) {
                        printMaterialQuantitiesInDescendingOrder();
                    }
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1 && !options.get(option).toString().equals("Back"));
    }

    /**
     * Prints the total quantity of materials needed for the production in ascending order.
     */
    private void printMaterialQuantitiesInAscendingOrder() {
        MaterialsBST materialQuantityBST = new MaterialsBST();
        List<Map.Entry<Material, BigDecimal>> materialQuantityPairs = productionTree.getMaterialQuantityPairs();

        for (Map.Entry<Material, BigDecimal> pair : materialQuantityPairs) {
            List<String> materialNames = new ArrayList<>();
            materialNames.add(pair.getKey().getName());
            MaterialsBST.insert(materialNames, pair.getValue().doubleValue());
        }

        System.out.println("\n" + Utils.BOLD + Utils.CYAN + "--- Material Quantities in Ascending Order ---\n" + Utils.RESET);
        System.out.printf(Utils.BOLD + "%-25s | %-15s%n", "Material", "Quantity");
        System.out.println("--------------------------------------------" + Utils.RESET);
        materialQuantityBST.inorder();
    }

    /**
     * Prints the total quantity of materials needed for the production in descending order.
     */
    private void printMaterialQuantitiesInDescendingOrder() {
        MaterialsBST materialQuantityBST = new MaterialsBST();
        List<Map.Entry<Material, BigDecimal>> materialQuantityPairs = productionTree.getMaterialQuantityPairs();

        for (Map.Entry<Material, BigDecimal> pair : materialQuantityPairs) {
            List<String> materialNames = new ArrayList<>();
            materialNames.add(pair.getKey().getName());
            MaterialsBST.insert(materialNames, pair.getValue().doubleValue());
        }

        System.out.println("\n" + Utils.BOLD + Utils.CYAN + "--- Material Quantities in Descending Order ---\n" + Utils.RESET);
        System.out.printf(Utils.BOLD + "%-25s | %-15s%n", "Material", "Quantity");
        System.out.println("--------------------------------------------" + Utils.RESET);
        materialQuantityBST.reverseInorder();
    }

}
