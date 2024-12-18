package UI.Domain.USEI.US04;

import UI.Menu.MenuItem;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ListExecutionTimeUI implements Runnable {
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("List Execution Time of one Operation", new ListExecutionTimeOneOpUI()));
        options.add(new MenuItem("List Execution Time of all Operations", new ListExecutionTimeAllOpUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- List Execution Time -------------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
