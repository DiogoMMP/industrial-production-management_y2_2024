package UI.Menu;

import UI.Domain.USEI.US17.ShowPERT_CPMUI;
import UI.Domain.USEI.US18.ShowCircularDependenciesUI;
import UI.Domain.USEI.US19.ShowTopologicalSortUI;
import UI.Domain.USEI.US20.ShowESLFUI;
import UI.Domain.USEI.US21.ExportScheduleToCSVUI;
import UI.Domain.USEI.US22.ShowCriticalPathsUI;
import UI.Domain.USEI.US23.ShowBottleneckActivitiesUI;
import UI.Domain.USEI.US24.SimulateProjDelaysUI;
import UI.Simulators.ChooseSimulatorUI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainMenuPERTCPM implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Show PERT/CPM", new ShowPERT_CPMUI()));
            options.add(new MenuItem("Detect Circular Dependencies",new ShowCircularDependenciesUI()));
            options.add(new MenuItem("Topological Sort of Project Activities",new ShowTopologicalSortUI()));
            options.add(new MenuItem("Show Earliest and Latest Start and Finish Times",new ShowESLFUI()));
            options.add(new MenuItem("Export Project Schedule to CSV", new ExportScheduleToCSVUI()));
            options.add(new MenuItem("Identify the Critical Path", new ShowCriticalPathsUI()));
            options.add(new MenuItem("Identify Bottlenecks Activities in the Project Graph", new ShowBottleneckActivitiesUI()));
            options.add(new MenuItem("Simulate Project Delays and Their Impact", new SimulateProjDelaysUI()));


            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Simulation by PERT/CPM ---------------------------\n" + Utils.RESET);

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    new ActivitiesMenu().run();
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
