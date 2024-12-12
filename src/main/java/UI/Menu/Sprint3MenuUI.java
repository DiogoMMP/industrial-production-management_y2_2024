package UI.Menu;

import UI.Domain.BDDAD.US23.US23UI;
import UI.Domain.BDDAD.US24.US24UI;
import UI.Domain.BDDAD.US25.US25UI;
import UI.Domain.BDDAD.US26.US26UI;
import UI.Domain.BDDAD.US27.US27UI;
import UI.Domain.BDDAD.US28.US28UI;
import UI.Domain.BDDAD.US29.US29UI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Sprint3MenuUI implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("USBD23 - Ensure operation execution time", new US23UI()));
            options.add(new MenuItem("USBD24 - Avoid circular references in BOO", new US24UI()));
            options.add(new MenuItem("USBD25 - List of product's operations", new US25UI()));
            options.add(new MenuItem("USBD26 - Check stock for order fulfillment", new US26UI()));
            options.add(new MenuItem("USBD27 - Reserve materials and components", new US27UI()));
            options.add(new MenuItem("USBD28 - List of reserved materials and components", new US28UI()));
            options.add(new MenuItem("USBD29 - Workstation types not used in any BOO", new US29UI()));

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n\033[1;36m--- Sprint 3 ---------------------------\033[0m");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    new Sprint3MenuUI().run();
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}