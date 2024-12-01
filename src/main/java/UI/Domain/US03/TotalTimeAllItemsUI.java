package UI.Domain.US03;

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
        System.out.println("\n\n--- Simulate Process by Time ------------");
        HashMap<String, Double> totalTimes = Item.calculateTotalProductionTimePerItem();
        int index = 1;
        for (Map.Entry<String, Double> entry : totalTimes.entrySet()) {
            String[] item = entry.getKey().split(" - ");
            System.out.println(index + " - Total time of the item " +  item[0] + " : " + entry.getValue());
            index++;
        }

        Utils.goBackAndWait();
    }
}
