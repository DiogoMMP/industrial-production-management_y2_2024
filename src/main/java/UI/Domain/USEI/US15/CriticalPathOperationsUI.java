package UI.Domain.USEI.US15;

import UI.Utils.Utils;
import repository.Instances;
import trees.ProductionTree.ProductionTree;

public class CriticalPathOperationsUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();


    @Override
    public void run() {
        productionTree.prioritizeCriticalPath(productionTree.getRoot());

        Utils.goBackAndWait();
    }
}
