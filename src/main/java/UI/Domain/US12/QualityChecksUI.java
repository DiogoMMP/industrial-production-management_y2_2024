package UI.Domain.US12;

import UI.Utils.Utils;
import repository.Instances;
import trees.ProductionTree.ProductionTree;
import trees.heap.HeapPriorityQueue;

import java.util.Scanner;

public class QualityChecksUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();
    private HeapPriorityQueue<Integer, String> qualityCheckQueue = Instances.getInstance().getHeap();


    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to perform quality checks interactively? (yes/no): ");
        String userInput = scanner.nextLine().trim().toLowerCase();
        boolean interactive = userInput.equals("yes");

        if (interactive) {
            productionTree.performQualityChecksInteractively();
            Utils.goBackAndWait();
        } else {
            productionTree.viewQualityChecksInOrder();
            Utils.goBackAndWait();

        }


    }
}
