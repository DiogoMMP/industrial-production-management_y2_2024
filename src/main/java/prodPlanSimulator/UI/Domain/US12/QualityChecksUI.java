package prodPlanSimulator.UI.Domain.US12;

import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.repository.Instances;
import trees.ProductionTree.ProductionTree;
import trees.heap.HeapPriorityQueue;

public class QualityChecksUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();
    private HeapPriorityQueue<Integer, String> qualityCheckQueue = Instances.getInstance().getHeap();


    @Override
    public void run() {

        System.out.println("Quality ");
        productionTree.viewQualityChecksInOrder();
       // productionTree.performQualityChecksInteractively();

        Utils.goBackAndWait();
    }
}
