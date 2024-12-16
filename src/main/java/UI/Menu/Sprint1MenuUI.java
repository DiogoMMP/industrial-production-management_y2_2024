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
            options.add(new MenuItem("USBD01: Glossary", new US01UI()));
            options.add(new MenuItem("USBD05: Orders in Time Frame", new US5UI()));
            options.add(new MenuItem("USBD06: Workstations by Order", new US6UI()));
            options.add(new MenuItem("USBD07: Materials for an Order", new US7UI()));
            options.add(new MenuItem("USBD08: Supported Operations", new US8UI()));

            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Sprint 1: Project Overview and Operations ---------------------------\n" + Utils.RESET);

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