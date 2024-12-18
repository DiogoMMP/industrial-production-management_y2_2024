package UI.Menu;

import UI.Domain.USBD.US12.US12UI;
import UI.Domain.USBD.US13.US13UI;
import UI.Domain.USBD.US14.US14UI;
import UI.Domain.USBD.US15.US15UI;
import UI.Domain.USBD.US16.US16UI;
import UI.Domain.USBD.US17.US17UI;
import UI.Domain.USBD.US18.US18UI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Sprint2MenuUI implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("List of Parts Used in a Product", new US12UI()));
            options.add(new MenuItem("List of Operations Involved in the Production of a Product", new US13UI()));
            options.add(new MenuItem("Product Using All Types of Machines", new US14UI()));
            options.add(new MenuItem("Register a Workstation", new US15UI()));
            options.add(new MenuItem("Register a Product", new US16UI()));
            options.add(new MenuItem("Register an Order", new US17UI()));
            options.add(new MenuItem("Deactivate a Customer", new US18UI()));

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Product Registration and Customer Management ---------------------------\n" + Utils.RESET);

                if (option == -2) {
                    new DatabaseManagementMenuUI().run();
                }

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    System.err.println("Error: Invalid option.");
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}