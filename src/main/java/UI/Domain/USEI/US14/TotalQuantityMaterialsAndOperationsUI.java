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
    public TotalQuantityMaterialsAndOperationsUI() {
    }

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Show Total Quantity of Materials", this::showTotalQuantityMaterials));
        options.add(new MenuItem("Show Total Quantity of Operations", this::showTotalQuantityOperations));
        options.add(new MenuItem("Show Total Quantity of Materials and Operations", this::showTotalQuantityMaterialsAndOperations));
        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n\033[1m\033[36m--- Total Quantity of Materials and Operations Menu ------------\033[0m");
            if (option >= 0 && option < options.size()) {
                options.get(option).run();
                Utils.goBackAndWait();
            }
        } while (option != -1);
    }

    private void showTotalQuantityMaterialsAndOperations() {
        Map<String, Object> totals = productionTree.calculateTotalMaterialsAndOperations(productionTree.getRoot());
        BigDecimal totalMaterialQuantity = BigDecimal.ZERO;
        BigDecimal totalOperationQuantity = BigDecimal.ZERO;

        for (Map.Entry<String, Object> entry : totals.entrySet()) {
            if (entry.getKey().equals("materialQuantities")) {
                System.out.println("\nQuantidade total por Material:\n");
                Map<String, Double> materialQuantities = (Map<String, Double>) entry.getValue();
                for (Map.Entry<String, Double> materialEntry : materialQuantities.entrySet()) {
                    System.out.println(materialEntry.getKey() + ": " + materialEntry.getValue());
                    totalMaterialQuantity = totalMaterialQuantity.add(BigDecimal.valueOf(materialEntry.getValue()));
                }
                System.out.println("Total Material Quantity: " + totalMaterialQuantity);
            } else if (entry.getKey().equals("operationTimes")) {
                System.out.println("\nQuantidade total por Operação:\n");
                Map<String, Double> operationTimes = (Map<String, Double>) entry.getValue();
                for (Map.Entry<String, Double> operationEntry : operationTimes.entrySet()) {
                    System.out.println(operationEntry.getKey() + ": " + operationEntry.getValue());
                    totalOperationQuantity = totalOperationQuantity.add(BigDecimal.valueOf(operationEntry.getValue()));
                }
                System.out.println("Total Operation Quantity: " + totalOperationQuantity);
            }
        }
    }

    private void showTotalQuantityOperations() {
        Map<String, Object> totals = productionTree.calculateTotalMaterialsAndOperations(productionTree.getRoot());
        BigDecimal totalOperationQuantity = BigDecimal.ZERO;

        for (Map.Entry<String, Object> entry : totals.entrySet()) {
            if (entry.getKey().equals("operationTimes")) {
                System.out.println("\nQuantidade total por Operação:\n");
                Map<String, Double> operationTimes = (Map<String, Double>) entry.getValue();
                for (Map.Entry<String, Double> operationEntry : operationTimes.entrySet()) {
                    System.out.println(operationEntry.getKey() + ": " + operationEntry.getValue());
                    totalOperationQuantity = totalOperationQuantity.add(BigDecimal.valueOf(operationEntry.getValue()));
                }
                System.out.println("Total Operation Quantity: " + totalOperationQuantity);
            }
        }
    }

    private void showTotalQuantityMaterials() {
        Map<String, Object> totals = productionTree.calculateTotalMaterialsAndOperations(productionTree.getRoot());
        BigDecimal totalMaterialQuantity = BigDecimal.ZERO;

        for (Map.Entry<String, Object> entry : totals.entrySet()) {
            if (entry.getKey().equals("materialQuantities")) {
                System.out.println("\nQuantidade total por Material:\n");
                Map<String, Double> materialQuantities = (Map<String, Double>) entry.getValue();
                for (Map.Entry<String, Double> materialEntry : materialQuantities.entrySet()) {
                    System.out.println(materialEntry.getKey() + ": " + materialEntry.getValue());
                    totalMaterialQuantity = totalMaterialQuantity.add(BigDecimal.valueOf(materialEntry.getValue()));
                }
                System.out.println("Total Material Quantity: " + totalMaterialQuantity);
            }
        }
    }
}
