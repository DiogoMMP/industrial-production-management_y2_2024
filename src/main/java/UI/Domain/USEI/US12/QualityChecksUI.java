package UI.Domain.USEI.US12;

import UI.Utils.Utils;
import repository.Instances;
import trees.ProductionTree.ProductionTree;
import trees.heap.HeapPriorityQueue;

import java.util.Scanner;

public class QualityChecksUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    @Override
    public void run() {

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "-------- Quality Checks --------" + Utils.RESET);

        boolean interactive = Utils.confirm(Utils.BOLD + "Do You Want to Perform Quality Checks Interactively? (Y/N): " + Utils.RESET);

        if (interactive) {
            productionTree.performQualityChecksInteractively();
            Utils.goBackAndWait();
        } else {
            productionTree.viewQualityChecksInOrder();
            Utils.goBackAndWait();

        }


    }
}
