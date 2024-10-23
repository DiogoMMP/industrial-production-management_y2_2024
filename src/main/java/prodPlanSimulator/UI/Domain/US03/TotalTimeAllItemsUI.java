package prodPlanSimulator.UI.Domain.US03;

import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.domain.Item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class TotalTimeAllItemsUI implements Runnable {
    @Override
    public void run() {
        System.out.println("\n\n--- Simulate Process by Time ------------");
        TreeMap<Item, Double> totalTimes = Item.calculateTotalProductionTimePerItem();
        int index = 1;
        for (Map.Entry<Item, Double> entry : totalTimes.entrySet()) {
            System.out.println(index + " - Total time of the item " + entry.getKey().getId() + " : " + entry.getValue());
            index++;
        }

        Utils.goBackAndWait();
    }
}
