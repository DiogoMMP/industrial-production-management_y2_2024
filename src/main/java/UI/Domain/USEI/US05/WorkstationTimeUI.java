package UI.Domain.USEI.US05;

import UI.Utils.Utils;
import domain.Item;

import java.util.List;
import java.util.Map;

public class WorkstationTimeUI implements Runnable {

    @Override
    public void run() {
        clearConsole();
        show();
        Utils.goBackAndWait();
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void show() {
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Workstations by Ascending Order ------------\n" + Utils.RESET);

        List<Map<String, Object>> workstationStats = Item.listWorkstationsByAscOrder();

        System.out.printf(Utils.BOLD + "%-15s %-15s %-15s%n", "Workstation", "Total Time", "Percentage");
        System.out.println("----------------------------------------------------------" + Utils.RESET);

        for (Map<String, Object> stats : workstationStats) {
            System.out.printf(
                    "%-15s %-15.0f %-15s%n",
                    stats.get("Workstation"),
                    (double) stats.get("TotalTime"),
                    String.format("%.2f %%", (double) stats.get("Percentage")).replace(",", ".")
            );


        }


    }
}