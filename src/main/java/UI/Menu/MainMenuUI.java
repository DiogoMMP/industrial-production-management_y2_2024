package UI.Menu;

import UI.Domain.US04.ListExecutionTimeUI;
import UI.Domain.US03.TotalTimeUI;
import UI.Domain.US05.WorkstationTimeUI;
import UI.Domain.US06.AverageAndWaitingTimesUI;
import UI.Domain.US07.FlowDependencyUI;
import UI.Domain.US08.SimulateProcessTimeAndPriorityUI;
import UI.Simulators.ChooseSimulatorUI;
import UI.Utils.DataInitializer;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUI implements Runnable {
    public MainMenuUI() {
    }
    /**
     * Run the main menu
     */
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Data Operations", new DataInitializer()));
            options.add(new MenuItem("Database Management", new DatabaseManagementMenuUI()));
            options.add(new MenuItem("Machine Monitoring and Control", new MachineMonitoringAndControlMenuUI()));
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n\033[1;36m--- Main Menu ---------------------------\033[0m");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    System.exit(0);
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}

