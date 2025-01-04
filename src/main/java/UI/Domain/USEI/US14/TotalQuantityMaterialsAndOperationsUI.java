package UI.Domain.USEI.US14;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import repository.Instances;
import trees.ProductionTree.ProductionTree;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TotalQuantityMaterialsAndOperationsUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    /**
     * Run method of the TotalQuantityMaterialsAndOperationsUI class.
     */
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Show Total Quantity of Materials", this::showTotalQuantityMaterials));
        options.add(new MenuItem("Show Total Quantity of Operations", this::showTotalQuantityOperations));
        options.add(new MenuItem("Show Total Quantity of Materials and Operations", this::showTotalQuantityMaterialsAndOperations));
        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Total Quantity of Materials and Operations Menu ------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if (option >= 0 && option < options.size()) {
                Utils.clearConsole();
                options.get(option).run();
                Utils.goBackAndWait();
            }
        } while (option != -1);
    }

    /**
     * Method to show the total quantity of materials and operations.
     */
    private void showTotalQuantityMaterialsAndOperations() {
        Map<String, Map<String,BigDecimal>> totals = productionTree.calculateTotalMaterialsAndOperations(productionTree.getRoot());
        BigDecimal totalMaterialQuantity = BigDecimal.ZERO;
        BigDecimal totalOperationQuantity = BigDecimal.ZERO;

        for (Map.Entry<String, Map<String,BigDecimal>> entry : totals.entrySet()) {
            if (entry.getKey().equals("materialQuantities")) {

                System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Total Quantity of Materials ------------\n" + Utils.RESET);

                System.out.printf(Utils.BOLD + "%-30s | %-10s%n", "Material", "Quantity");
                System.out.println("--------------------------------------------" + Utils.RESET);

                Map<String, BigDecimal> materialQuantities =  entry.getValue();

                for (Map.Entry<String, BigDecimal> materialEntry : materialQuantities.entrySet()) {
                    System.out.printf("%-30s  %.4f%n", materialEntry.getKey(), materialEntry.getValue());
                    totalMaterialQuantity = totalMaterialQuantity.add(materialEntry.getValue());
                }

                System.out.println("--------------------------------------------");
                System.out.println(Utils.BOLD + "Total Material Quantity" + Utils.RESET + " = " + totalMaterialQuantity);

            } else if (entry.getKey().equals("operationTimes")) {

                System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Total Quantity of Operations ------------\n" + Utils.RESET);

                System.out.printf(Utils.BOLD + "%-30s | %-10s%n", "Operation", "Quantity");
                System.out.println("--------------------------------------------" + Utils.RESET);

                Map<String, BigDecimal> operationTimes = entry.getValue();

                for (Map.Entry<String, BigDecimal> operationEntry : operationTimes.entrySet()) {
                    System.out.printf("%-30s  %.4f%n", operationEntry.getKey(), operationEntry.getValue());
                    totalOperationQuantity = totalOperationQuantity.add(operationEntry.getValue());
                }

                System.out.println("--------------------------------------------");
                System.out.println(Utils.BOLD + "Total Operation Quantity" + Utils.RESET + " = " + totalOperationQuantity);
            }
        }
    }

    /**
     * Method to show the total quantity of operations.
     */
    private void showTotalQuantityOperations() {
        Map<String, Map<String,BigDecimal>> totals = productionTree.calculateTotalMaterialsAndOperations(productionTree.getRoot());
        BigDecimal totalOperationQuantity = BigDecimal.ZERO;

        // Cabeçalho da tabela
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Total Quantity of Operations ------------\n" + Utils.RESET);

        System.out.printf(Utils.BOLD + "%-30s | %-10s%n", "Operation", "Quantity");
        System.out.println("--------------------------------------------" + Utils.RESET);

        for (Map.Entry<String, Map<String, BigDecimal>> entry : totals.entrySet()) {
            if (entry.getKey().equals("operationTimes")) {
                Map<String, BigDecimal> operationTimes = entry.getValue();
                for (Map.Entry<String, BigDecimal> operationEntry : operationTimes.entrySet()) {
                    System.out.printf("%-30s  %.4f%n", operationEntry.getKey(), operationEntry.getValue());
                    totalOperationQuantity = totalOperationQuantity.add(operationEntry.getValue());
                }

                System.out.println("--------------------------------------------");
                System.out.println(Utils.BOLD + "Total Operation Quantity" + Utils.RESET + " = " + totalOperationQuantity);
            }
        }
    }


    /**
     * Method to show the total quantity of materials.
     */
    private void showTotalQuantityMaterials() {
        Map<String, Map<String,BigDecimal>> totals = productionTree.calculateTotalMaterialsAndOperations(productionTree.getRoot());
        BigDecimal totalMaterialQuantity = BigDecimal.ZERO;

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Total Quantity of Materials ------------\n" + Utils.RESET);

        System.out.printf(Utils.BOLD + "%-30s | %-10s%n", "Material", "Quantity");
        System.out.println("--------------------------------------------" + Utils.RESET);

        for (Map.Entry<String, Map<String,BigDecimal>> entry : totals.entrySet()) {
            if (entry.getKey().equals("materialQuantities")) {
                Map<String, BigDecimal> materialQuantities = entry.getValue();
                for (Map.Entry<String, BigDecimal> materialEntry : materialQuantities.entrySet()) {
                    System.out.printf("%-30s  %.4f%n", materialEntry.getKey(), materialEntry.getValue());
                    totalMaterialQuantity = totalMaterialQuantity.add(materialEntry.getValue());
                }

                System.out.println("--------------------------------------------");
                System.out.println(Utils.BOLD + "Total Material Quantity" + Utils.RESET + " = " + totalMaterialQuantity);
            }
        }
    }

}
