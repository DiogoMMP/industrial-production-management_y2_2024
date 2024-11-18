package prodPlanSimulator.UI.Simulators;

import prodPlanSimulator.UI.Menu.MenuItem;
import prodPlanSimulator.UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChooseSimulatorUI implements Runnable{
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Simulation by Time", new ProcessByTimeUI()));
            options.add(new MenuItem("Simulation by Priority", new ProcessByPriorityUI()));
            options.add(new MenuItem("Simulation bt Structural Information", new ProcessByTreeUI()));
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n--- Choose Simulator --------------------------");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                }
            } while (option != -1);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
