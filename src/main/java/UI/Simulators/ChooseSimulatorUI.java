package UI.Simulators;

import UI.Domain.USLP.US06.SimulateOrdersUI;
import UI.Menu.ActivitiesMenu;
import UI.Menu.MenuItem;
import UI.Menu.OrdersMenu;
import UI.Menu.Sprint2MenuUI;
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
            options.add(new MenuItem("Simulation by PERT/CPM", new ActivitiesMenu()));
            options.add(new MenuItem("Simulation by Orders", new OrdersMenu()));
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Choose Simulator -------------------------------\n" + Utils.RESET);

                if (option == -2) {
                    new DataInitializer().run();
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
