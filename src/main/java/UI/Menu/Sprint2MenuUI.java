package UI.Menu;

import UI.Domain.USBD.US12.US12UI;
import UI.Domain.USBD.US13.US13UI;
import UI.Domain.USBD.US14.US14UI;
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
            options.add(new MenuItem("Parts in a Product", new US12UI()));
            options.add(new MenuItem("Operations in Production", new US13UI()));
            options.add(new MenuItem("Products by Machine Types", new US14UI()));
            options.add(new MenuItem("Register Product", new US16UI()));
            options.add(new MenuItem("Register Order", new US17UI()));
            options.add(new MenuItem("Deactivate Customer", new US18UI()));

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n\033[1;36m--- Sprint 2: Product Registration and Customer Management ---------------------------\033[0m");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    new Sprint2MenuUI().run();
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}