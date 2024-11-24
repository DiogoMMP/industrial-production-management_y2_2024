package UI.Domain.US10;

import UI.Menu.MenuItem;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SearchUI implements Runnable {

    @Override
    public void run() {
        try {
            // Create options for the user
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Search for an Operation", new SearchOperationUI()));
            options.add(new MenuItem("Search for a Material", new SearchMaterialUI()));

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n--- Choose an option --------------------------");
                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                }
            } while (option != -1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


}
