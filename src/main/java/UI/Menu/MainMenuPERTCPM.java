package UI.Menu;

import UI.Domain.US17.ShowPERT_CPMUI;
import UI.Domain.US20.ShowESLFUI;
import UI.Simulators.ChooseSimulatorUI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainMenuPERTCPM implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Show PERT/CPM", new ShowPERT_CPMUI()));
            options.add(new MenuItem("Show earliest start and latest finish",new ShowESLFUI()));
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    new ChooseSimulatorUI().run();
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
