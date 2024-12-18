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
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- List Total Time -------------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
