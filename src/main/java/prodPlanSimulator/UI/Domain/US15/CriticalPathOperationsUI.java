package prodPlanSimulator.UI.Domain.US15;

import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.repository.Instances;
import trees.ProductionTree.ProductionTree;

public class CriticalPathOperationsUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();


    @Override
    public void run() {
        productionTree.prioritizeCriticalPath(productionTree.getRoot());

        Utils.goBackAndWait();
    }
}
