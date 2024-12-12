package UI.Domain.USEI.US03;

import UI.Menu.MenuItem;
import UI.Utils.Utils;

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
            option = Utils.showAndSelectIndex(options, "\n\n\033[1m\033[36m--- List Total Time -------------------\033[0m");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
