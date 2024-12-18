package UI.Menu;

import UI.Domain.USBD.US01.US01UI;
import UI.Domain.USBD.US05.US5UI;
import UI.Domain.USBD.US06.US6UI;
import UI.Domain.USBD.US07.US7UI;
import UI.Domain.USBD.US08.US8UI;
import UI.Utils.Utils;

import javax.xml.crypto.Data;
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

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Project Overview and Operations ---------------------------\n" + Utils.RESET);

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