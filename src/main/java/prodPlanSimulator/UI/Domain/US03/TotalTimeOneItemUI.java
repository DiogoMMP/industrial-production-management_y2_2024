package prodPlanSimulator.UI.Domain.US03;

import prodPlanSimulator.UI.Domain.US02AndUS08.SimulateProcessTimeAndPriorityUI;
import prodPlanSimulator.UI.Menu.MenuItem;
import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

import java.util.*;

public class TotalTimeOneItemUI implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();

    @Override
    public void run() {
        String choice;
        ArrayList<Item> items = new ArrayList<>(map.getProdPlan().keySet());
        items = sortAndRemoveDuplicates(items);
        List<MenuItem> options = new ArrayList<>();
        for (Item item : items) {
            options.add(new MenuItem("Item: " + item.getId(), new SimulateProcessTimeAndPriorityUI()));
        }
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Choose the Item to be visualized ------------");
            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    clearConsole();
                    show(choice);
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1 && !options.get(option).toString().equals("Back"));
    }

    private ArrayList<Item> sortAndRemoveDuplicates(ArrayList<Item> items) {
        HashSet<Integer> seenIds = new HashSet<>();
        ArrayList<Item> uniqueItems = new ArrayList<>();

        for (Item item : items) {
            if (!seenIds.contains(item.getId())) {
                seenIds.add(item.getId());
                uniqueItems.add(item);
            }
        }

        uniqueItems.sort(Comparator.comparingInt(Item::getId));
        return uniqueItems;
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void show(String choice) {
        System.out.println("\n\n--- Simulate Process by Time ------------");
        int id;
        id = Integer.parseInt(choice.split(" ")[1]);
        System.out.println("Item: " + id);
        HashMap<Item, Double> totalTimes = Item.calculateTotalProductionTimePerItem();
        int index = 1;
        for (Map.Entry<Item, Double> entry : totalTimes.entrySet()) {
            if (entry.getKey().getId() == id) {
                System.out.println(index + " - Total time of the item " + entry.getKey().getId() + " : " + entry.getValue());
                index++;
            }
        }
    }
}
