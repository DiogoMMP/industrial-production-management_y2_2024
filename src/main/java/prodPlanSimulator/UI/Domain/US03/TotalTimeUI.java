package prodPlanSimulator.UI.Domain.US03;

import prodPlanSimulator.UI.Menu.MenuItem;
import prodPlanSimulator.UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TotalTimeUI implements Runnable {
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("List Total Time of All Items", new TotalTimeAllItemsUI()));
        options.add(new MenuItem("List Total Time of Specific Item", new TotalTimeOneItemUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- List Total Time -------------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
