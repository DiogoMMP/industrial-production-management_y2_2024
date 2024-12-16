package UI.Menu;

import UI.Domain.USBD.US23.US23UI;
import UI.Domain.USBD.US24.US24UI;
import UI.Domain.USBD.US25.US25UI;
import UI.Domain.USBD.US26.US26UI;
import UI.Domain.USBD.US27.US27UI;
import UI.Domain.USBD.US28.US28UI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Sprint3MenuUI implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Ensure Operation Time", new US23UI()));
            options.add(new MenuItem("Avoid Circular References", new US24UI()));
            options.add(new MenuItem("List Product Operations", new US25UI()));
            options.add(new MenuItem("Check Stock for Orders", new US26UI()));
            options.add(new MenuItem("Reserve Materials", new US27UI()));
            options.add(new MenuItem("List Reserved Materials", new US28UI()));

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Sprint 3: Manage Operations and Materials ---------------------------\n" + Utils.RESET);

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    new DatabaseManagementMenuUI().run();
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}