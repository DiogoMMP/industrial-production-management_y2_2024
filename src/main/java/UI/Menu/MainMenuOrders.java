package UI.Menu;

import UI.Domain.USLP.US06.SimulateOrdersUI;
import UI.Simulators.ProcessByOrdersUI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainMenuOrders implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Show Orders simulation process", new SimulateOrdersUI()));
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n\033[1;36m--- Simulation by Orders ---------------------------\033[0m");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
