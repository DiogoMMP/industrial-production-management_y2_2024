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
            options.add(new MenuItem("USBD12 - List of parts used in a product", new US12UI()));
            options.add(new MenuItem("USBD13 - List of operations involved in the production of a product", new US13UI()));
            options.add(new MenuItem("USBD14 - Product using all types of machines", new US14UI()));
            options.add(new MenuItem("USBD15 - Register a Workstation", new US15UI()));
            options.add(new MenuItem("USBD16 - Register a product in the system", new US16UI()));
            options.add(new MenuItem("USBD17 - Register an order in the system", new US17UI()));
            options.add(new MenuItem("USBD18 - Deactivate a customer from the system", new US18UI()));

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Sprint 2: Product Registration and Customer Management ---------------------------\n" + Utils.RESET);

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