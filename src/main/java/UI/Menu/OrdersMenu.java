package UI.Menu;

import UI.Domain.USEI.US04.ListExecutionTimeUI;
import UI.Domain.USEI.US03.TotalTimeUI;
import UI.Domain.USEI.US05.WorkstationTimeUI;
import UI.Domain.USEI.US06.AverageAndWaitingTimesUI;
import UI.Domain.USEI.US07.FlowDependencyUI;
import UI.Domain.USEI.US08.SimulateProcessTimeAndPriorityUI;
import UI.Domain.USLP.US06.SimulateOrdersUI;
import UI.Domain.USLP.US07.UpdateAverageTimeUI;
import UI.Domain.USLP.US08.exportToCSVUI;
import UI.Simulators.ChooseSimulatorUI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class OrdersMenu implements Runnable {

    /**
     * Run the main menu
     */
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Show Simulation", new SimulateOrdersUI()));
            options.add(new MenuItem("Average Production Time Management", new UpdateAverageTimeUI()));
            options.add(new MenuItem("Export Simulation to CSV", new exportToCSVUI()));

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Simulation by Order ---------------------------\n" + Utils.RESET);

                if (option == -2) {
                    new ChooseSimulatorUI().run();
                }

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    System.err.println("Error: Invalid option.");
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}

