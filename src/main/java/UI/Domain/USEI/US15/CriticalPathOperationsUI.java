package UI.Domain.USEI.US15;

import UI.Utils.Utils;
import repository.Instances;
import trees.ProductionTree.ProductionTree;

import java.util.List;

public class CriticalPathOperationsUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    @Override
    public void run() {

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Critical Path Operations ------------\n" + Utils.RESET);

        List<String> criticalPath = productionTree.prioritizeCriticalPath(productionTree.getRoot());

        if (criticalPath == null) {
            System.err.println("Production tree is empty.");
        } else {
            for (String operation : criticalPath) {
                System.out.println(operation);
            }
        }

        Utils.goBackAndWait();
    }
}
