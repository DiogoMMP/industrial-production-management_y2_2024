package prodPlanSimulator.UI.Menu;

import prodPlanSimulator.UI.Domain.US15.CriticalPathOperationsUI;
import prodPlanSimulator.UI.Domain.US09.showTreeUI;
import prodPlanSimulator.UI.Domain.US10.SearchUI;
import prodPlanSimulator.UI.Domain.US11.TrackingQuantitiesUI;
import prodPlanSimulator.UI.Domain.US12.QualityChecksUI;
import prodPlanSimulator.UI.Domain.US13.UpdateMaterialQuantitiesUI;
import prodPlanSimulator.UI.Domain.US14.TotalQuantityMaterialsAndOperationsUI;
import prodPlanSimulator.UI.Domain.US16.SimulateProcessTreeUI;
import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.UI.graphGenerator.ProductOperationsGraphUI;
import prodPlanSimulator.UI.graphGenerator.ProductStructureGraphUI;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUITree implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Structural Information of Production", new showTreeUI()));
            options.add(new MenuItem("Search Materials and Operations", new SearchUI()));
            options.add(new MenuItem("Tracking Materials Quantities", new TrackingQuantitiesUI()));
            options.add(new MenuItem("Quality Checks", new QualityChecksUI()));
            options.add(new MenuItem("Update Material Quantities", new UpdateMaterialQuantitiesUI()));
            options.add(new MenuItem("Total Quantity Materials and Operations", new TotalQuantityMaterialsAndOperationsUI()));
            options.add(new MenuItem("Critical Path Operations", new CriticalPathOperationsUI()));
            options.add(new MenuItem("Show Simulation", new SimulateProcessTreeUI()));
            options.add(new MenuItem("Product Structure Graph", new ProductStructureGraphUI()));
            options.add(new MenuItem("Product Operations Graph", new ProductOperationsGraphUI()));
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                }
            } while (option != -1);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
