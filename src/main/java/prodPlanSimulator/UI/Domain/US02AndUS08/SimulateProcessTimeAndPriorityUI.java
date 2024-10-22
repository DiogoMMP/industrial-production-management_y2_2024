package prodPlanSimulator.UI.Domain.US02AndUS08;

import prodPlanSimulator.UI.Menu.MenuItem;
import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

import java.util.*;

public class SimulateProcessTimeAndPriorityUI implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsMachines();

    @Override
    public void run() {
        String choice;
        ArrayList<Item> items = new ArrayList<>(map.getProdPlan().keySet());
        items = sortAndRemoveDuplicates(items);
        List<MenuItem> options = new ArrayList<>();
        for (Item item : items) {
            options.add(new MenuItem("Item: " + item.getId(), new SimulateProcessTimeAndPriorityUI()));
        }
        options.add(new MenuItem("All", new SimulateProcessTimeAndPriorityUI()));
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

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void show(String choice) {
        System.out.println("\n\n--- Simulate Process by Time ------------");
        int id;
        LinkedHashMap<String, Double> timeOperations = new LinkedHashMap<>();
        if (choice.equals("All")) {
            timeOperations = Item.simulateProcessUS08();
            timeOperations = updateOperationKeys(timeOperations);
            for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
                System.out.println(entry.getKey() + ", Time: " + entry.getValue());
            }
        } else {
            id = Integer.parseInt(choice.split(" ")[1]);
            System.out.println("Item: " + id);
            timeOperations = Item.simulateProcessUS08();
            timeOperations = sortOperations(timeOperations, id);
            timeOperations = updateOperationKeys(timeOperations);
            for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
                System.out.println(entry.getKey() + ", Time: " + entry.getValue());
            }
        }
    }

    private LinkedHashMap<String, Double> updateOperationKeys(LinkedHashMap<String, Double> timeOperations) {
        LinkedHashMap<String, Double> updatedTimeOperations = new LinkedHashMap<>();
        int operationNumber = 1;
        for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
            String newKey = "Operation: " + operationNumber + " - " + entry.getKey().replaceFirst("\\d+ - ", "");
            updatedTimeOperations.put(newKey, entry.getValue());
            operationNumber++;
        }
        return updatedTimeOperations;
    }

    private LinkedHashMap<String, Double> sortOperations(LinkedHashMap<String, Double> timeOperations, int id) {
        LinkedHashMap<String, Double> sortedTimeOperations = new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
            String[] parts = entry.getKey().split(" - ");
            for (String part : parts) {
                if (part.startsWith("Item: ")) {
                    int itemId = Integer.parseInt(part.substring(6));
                    if (itemId == id) {
                        sortedTimeOperations.put(entry.getKey(), entry.getValue());
                    }
                    break;
                }
            }
        }
        return sortedTimeOperations;
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
}
