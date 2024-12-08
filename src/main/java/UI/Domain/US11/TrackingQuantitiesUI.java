package UI.Domain.US11;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import repository.Instances;
import trees.ProductionTree.ProductionTree;

import java.util.ArrayList;
import java.util.List;

public class TrackingQuantitiesUI implements Runnable {
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    @Override
    public void run() {
        String choice;
        List<MenuItem> options = new ArrayList<>();

        options.add(new MenuItem("Show Material Quantities in Ascending Order", new TrackingQuantitiesUI()));
        options.add(new MenuItem("Show Material Quantities in Descending Order", new TrackingQuantitiesUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n\033[1m\033[36m--- Choose the Option ------------\033[0m");
            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    if (option == 0) {
                        productionTree.printMaterialQuantitiesInAscendingOrder();
                    } else if (option == 1) {
                        productionTree.printMaterialQuantitiesInDescendingOrder();
                    }
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1 && !options.get(option).toString().equals("Back"));
    }
}
