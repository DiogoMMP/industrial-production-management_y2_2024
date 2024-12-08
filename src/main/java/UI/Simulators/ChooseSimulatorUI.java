package UI.Simulators;

import UI.Menu.MenuItem;
import UI.Utils.DataInitializer;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChooseSimulatorUI implements Runnable{
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Simulation by Time", new ProcessByTimeUI()));
            options.add(new MenuItem("Simulation by Priority", new ProcessByPriorityUI()));
            options.add(new MenuItem("Simulation by Structural Information", new ProcessByTreeUI()));
            options.add(new MenuItem("Simulation by PERT/CPM", new ProcessByPERTCPMUI()));
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n\033[1;36m--- Choose Simulator -------------------------------\033[0m");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    new DataInitializer().run();
                }

            } while (true);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
