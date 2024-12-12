package UI.Menu;

import UI.Domain.USBD.US05.US5UI;
import UI.Domain.USBD.US06.US6UI;
import UI.Domain.USBD.US07.US7UI;
import UI.Domain.USBD.US08.US8UI;
import UI.Domain.USBD.US09.US9UI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Sprint1MenuUI implements Runnable {
    @Override
    public void run() {
        try {
            List<MenuItem> options = new ArrayList<>();
            options.add(new MenuItem("Orders in Time Frame", new US5UI()));
            options.add(new MenuItem("Workstations by Order", new US6UI()));
            options.add(new MenuItem("Materials for an Order", new US7UI()));
            options.add(new MenuItem("Supported Operations", new US8UI()));
            options.add(new MenuItem("Operation Sequence by Product", new US9UI()));

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