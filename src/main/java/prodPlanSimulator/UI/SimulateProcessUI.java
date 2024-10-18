package prodPlanSimulator.UI;

import prodPlanSimulator.UI.Domain.SimulateProcessTimeAndPriorityUI;
import prodPlanSimulator.UI.Domain.SimulateProcessTimeUI;
import prodPlanSimulator.UI.Menu.MenuItem;
import prodPlanSimulator.UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SimulateProcessUI implements Runnable {

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Sort by Time", new SimulateProcessTimeUI()));
        options.add(new MenuItem("Sort by Time and Priority", new SimulateProcessTimeAndPriorityUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Simulate Process -------------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);

    }
}
