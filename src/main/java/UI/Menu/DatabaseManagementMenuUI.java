package UI.Menu;


import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManagementMenuUI implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> sprints = new ArrayList<>();
            sprints.add(new MenuItem("Project Overview and Operations", new Sprint1MenuUI()));
            sprints.add(new MenuItem("Product Registration and Customer Management", new Sprint2MenuUI()));
            sprints.add(new MenuItem("Manage Operations and Materials", new Sprint3MenuUI()));

            int sprintOption = 0;
            do {
                sprintOption = Utils.showAndSelectIndex(sprints, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Database Management ---------------------------\n" + Utils.RESET);

                if (sprintOption == -2) {
                    new MainMenuUI().run();
                }

                if ((sprintOption >= 0) && (sprintOption < sprints.size())) {
                    sprints.get(sprintOption).run();
                } else if (sprintOption == -1) {
                    System.err.println("Error: Invalid option.");
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}