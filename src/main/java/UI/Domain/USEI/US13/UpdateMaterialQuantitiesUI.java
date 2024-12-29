package UI.Domain.USEI.US13;

import UI.Domain.USEI.US09.ShowTreeUI;
import UI.Menu.MenuItem;
import UI.Utils.Utils;
import domain.Material;
import repository.Instances;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UpdateMaterialQuantitiesUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();
    private ShowTreeUI showTreeUI = new ShowTreeUI();

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Update Material Quantity", this::updateMaterialQuantity));
        options.add(new MenuItem("Show Updated Production Tree", this::showTree));

        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Update Material Quantities Menu ------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
                Utils.goBackAndWait();
            }
        } while (option != -1);
    }

    private void updateMaterialQuantity() {
        String choice;
        List<MenuItem> options = new ArrayList<>();

        List<Map.Entry<Material, BigDecimal>> materialQuantityPairs = productionTree.getMaterialQuantityPairs();
        for (int i = 0; i < materialQuantityPairs.size(); i++) {
            Map.Entry<Material, BigDecimal> entry = materialQuantityPairs.get(i);
            options.add(new MenuItem(entry.getKey().getName() + " (Quantity: " + entry.getValue().doubleValue() + ")", new UpdateMaterialQuantitiesUI()));
        }

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Choose the Material to be Updated ------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    clearConsole();
                    double newQuantity = Utils.readDoubleFromConsole(Utils.BOLD + "Enter the new quantity for " +
                            materialQuantityPairs.get(option).getKey().getName() + ": " + Utils.RESET);

                    while (newQuantity < 0) {
                        System.err.println("Quantity must be a positive number.\n");
                        newQuantity = Utils.readDoubleFromConsole(Utils.BOLD + "Enter the new quantity for " +
                                materialQuantityPairs.get(option).getKey().getName() + ": " + Utils.RESET);
                    }
                    updateQuantities(materialQuantityPairs.get(option).getKey().getID(), newQuantity);
                }
            }
        } while (option != -1 && !options.get(option).toString().equals("Back"));
    }

    private void updateQuantities(String materialID, double newQuantity){
        productionTree.updateQuantities(materialID, newQuantity);
        System.out.println(Utils.GREEN + "Quantity updated successfully." + Utils.RESET);
        Utils.goBackAndWait();
    }

    private void showTree() {
        showTreeUI.toIndentedStringForObjective();
        Utils.goBackAndWait();
    }

    /**
     * This method is responsible for clearing the console.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
