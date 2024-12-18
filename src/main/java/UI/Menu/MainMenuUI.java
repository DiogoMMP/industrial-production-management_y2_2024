package UI.Menu;

import UI.Domain.USBD.US01.US01UI;
import UI.Utils.DataInitializer;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUI implements Runnable {
    public MainMenuUI() {
    }
    /**
     * Run the main menu
     */
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Data Operations", new DataInitializer()));
            options.add(new MenuItem("Database Management", new DatabaseManagementMenuUI()));
            options.add(new MenuItem("Machine Monitoring and Control", new MachineMonitoringAndControlMenuUI()));
            options.add(new MenuItem("Search Glossary Terms", new US01UI()));
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Main Menu ---------------------------\n" + Utils.RESET);

                if (option == -2) {
                    System.exit(0);
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

