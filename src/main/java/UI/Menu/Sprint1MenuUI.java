package UI.Menu;

import UI.Domain.BDDAD.US5.US5UI;
import UI.Domain.BDDAD.US6.US6UI;
import UI.Domain.BDDAD.US7.US7UI;
import UI.Domain.BDDAD.US8.US8UI;
import UI.Domain.BDDAD.US9.US9UI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Sprint1MenuUI implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("USBD05 - Orders to be delivered in a time frame", new US5UI()));
            options.add(new MenuItem("USBD06 - Types of workstations in a given Order", new US6UI()));
            options.add(new MenuItem("USBD07 - Materials/Components necessary to fulfill an order", new US7UI()));
            options.add(new MenuItem("USBD08 - Different operations the factory supports", new US8UI()));
            options.add(new MenuItem("USBD09 - Operation sequence and workstation type from a given product", new US9UI()));

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n\033[1;36m--- Sprint 1 ---------------------------\033[0m");

                if ((option >= 0) && (option < options.size())) {
                    options.get(option).run();
                } else if (option == -1) {
                    new Sprint1MenuUI().run();
                }

            } while (true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}