package UI.Menu;

import UI.Domain.USEI.US04.ListExecutionTimeUI;
import UI.Domain.USEI.US03.TotalTimeUI;
import UI.Domain.USEI.US05.WorkstationTimeUI;
import UI.Domain.USEI.US06.AverageAndWaitingTimesUI;
import UI.Domain.USEI.US07.FlowDependencyUI;
import UI.Domain.USEI.US08.SimulateProcessTimeAndPriorityUI;
import UI.Simulators.ChooseSimulatorUI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SimulationByPriorityUI implements Runnable {
    public SimulationByPriorityUI() {
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
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n\033[1;36m--- Simulation by Priority ---------------------------\033[0m");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    new ChooseSimulatorUI().run();
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}

