package prodPlanSimulator.UI.Menu;

import prodPlanSimulator.UI.*;
import prodPlanSimulator.UI.Domain.US02AndUS08.SimulateProcessTimeAndPriorityUI;
import prodPlanSimulator.UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUIPriority implements Runnable {
    public MainMenuUIPriority() {
    }
    /**
     * Run the main menu
     */
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Show Simulation", new SimulateProcessTimeAndPriorityUI()));
            options.add(new MenuItem("Total Time", new TotalTimeUI()));
            options.add(new MenuItem("Time of Workstation", new WorkstationTimeUI()));
            options.add(new MenuItem("Execution Time", new ListExecutionTimeUI()));
            options.add(new MenuItem("Average and waiting times", new AverageAndWaitingTimesUI()));
            options.add(new MenuItem("Flow Dependency", new FlowDependencyUI()));
            options.add(new MenuItem("Product Structure Graph", new ProductStructureGraphUI()));
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

