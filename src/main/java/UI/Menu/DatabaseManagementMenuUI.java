package UI.Menu;


import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManagementMenuUI implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> sprints = new ArrayList<>();
            sprints.add(new MenuItem("Sprint 1: Project Overview and Operations", new Sprint1MenuUI()));
            sprints.add(new MenuItem("Sprint 2: Product Registration and Customer Management", new Sprint2MenuUI()));
            sprints.add(new MenuItem("Sprint 3: Manage Operations and Materials", new Sprint3MenuUI()));

            int sprintOption = 0;
            do {
                sprintOption = Utils.showAndSelectIndex(sprints, "\n\n\033[1;36m--- Database Management ---------------------------\033[0m");

                if ((sprintOption >= 0) && (sprintOption < sprints.size())) {
                    sprints.get(sprintOption).run();
                } else if (sprintOption == -1) {
                    new MainMenuUI().run();
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}