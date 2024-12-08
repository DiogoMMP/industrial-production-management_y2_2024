package UI.Domain.US04;

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
            option = Utils.showAndSelectIndex(options, "\n\n\033[1m\033[36m--- List Execution Time -------------------\033[0m");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
