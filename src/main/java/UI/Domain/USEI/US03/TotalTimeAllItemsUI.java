package UI.Domain.USEI.US03;

import UI.Utils.Utils;
import domain.Item;

import java.util.HashMap;
import java.util.Map;

public class TotalTimeAllItemsUI implements Runnable {
    /**
     * This method calculates the total time of all items in the production plan.
     */
    @Override
    public void run() {
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Total Time for All Items ------------\n" + Utils.RESET);

        HashMap<String, Double> totalTimes = Item.calculateTotalProductionTimePerItem();

        System.out.printf(Utils.BOLD + "%-15s  %-15s%n", "Item", "Total Time");
        System.out.println("-----------------------------------------------" + Utils.RESET);

        int index = 1;
        for (Map.Entry<String, Double> entry : totalTimes.entrySet()) {
            String[] item = entry.getKey().split(" - ");
            System.out.printf("%-15s  %-15.2f%n", item[0], entry.getValue());
            index++;
        }

        Utils.goBackAndWait();
    }
}
