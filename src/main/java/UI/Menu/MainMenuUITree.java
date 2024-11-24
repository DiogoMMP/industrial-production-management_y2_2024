package UI.Menu;

import UI.Domain.US15.CriticalPathOperationsUI;
import UI.Domain.US09.showTreeUI;
import UI.Domain.US10.SearchUI;
import UI.Domain.US11.TrackingQuantitiesUI;
import UI.Domain.US12.QualityChecksUI;
import UI.Domain.US13.UpdateMaterialQuantitiesUI;
import UI.Domain.US14.TotalQuantityMaterialsAndOperationsUI;
import UI.Domain.US16.SimulateProcessTreeUI;
import UI.Simulators.ChooseSimulatorUI;
import UI.Utils.Utils;
import UI.graphGenerator.OperationStructureGraphUI;
import UI.graphGenerator.ProductStructureGraphUI;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUITree implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Show Production Tree", new showTreeUI()));
            options.add(new MenuItem("Search Materials and Operations", new SearchUI()));
            options.add(new MenuItem("Tracking Materials Quantities", new TrackingQuantitiesUI()));
            options.add(new MenuItem("Quality Checks", new QualityChecksUI()));
            options.add(new MenuItem("Update Material Quantities", new UpdateMaterialQuantitiesUI()));
            options.add(new MenuItem("Total Quantity Materials and Operations", new TotalQuantityMaterialsAndOperationsUI()));
            options.add(new MenuItem("Critical Path Operations", new CriticalPathOperationsUI()));
            options.add(new MenuItem("Show Simulation", new SimulateProcessTreeUI()));
            options.add(new MenuItem("Product Structure Graph", new ProductStructureGraphUI()));
            options.add(new MenuItem("Operation Structure Graph ", new OperationStructureGraphUI()));
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    new ChooseSimulatorUI().run();
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
