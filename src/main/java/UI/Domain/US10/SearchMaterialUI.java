package UI.Domain.US10;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import prodPlanSimulator.repository.Instances;
import trees.ProductionTree.ProductionTree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchMaterialUI implements Runnable {

    private ProductionTree productionTree = Instances.getInstance().getProductionTree();
    private Map<String, String> items = Instances.getInstance().getItemsRepository().getItemsRepository();

    @Override
    public void run() {
        String choice;
        List<MenuItem> options = new ArrayList<>();

        // Sort the items map by its keys
        Map<String, String> sortedItems = sortItemsByID(items);

        // Create menu options from the sorted map
        for (Map.Entry<String, String> entry : sortedItems.entrySet()) {
            options.add(new MenuItem("Item: " + entry.getKey() + " - " + entry.getValue(), new SearchUI()));
        }

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Choose the Material to Search ------------");
            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    clearConsole();
                    String itemId = choice.split(" ")[1];
                    executeAndPrintSearch(itemId);
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1);
    }


    /**
     * This method is responsible for clearing the console.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Helper method to execute and print the search results in a readable format.
     */
    private void executeAndPrintSearch(String id) {
        Map<String, String> result = productionTree.searchNode(id);
        if (result.containsKey("Error")) {
            System.err.println("Error: Material not found!");
        } else {
            System.out.println("\n");
            System.out.println("Type: " + result.get("Type"));
            System.out.println("Description and Quantity: " + result.get("Description"));
            if (result.get("Type").equals("Material")) {
                System.out.println("Parent Operation: " + result.getOrDefault("Parent Operation", "None"));
            } else {
                System.out.println("Parent Operation: " + result.getOrDefault("Parent Operation", "None"));
            }
        }
        System.out.println();
    }

    public static Map<String, String> sortItemsByID(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, // Resolves duplicate keys, if any
                        LinkedHashMap::new // Maintains insertion order
                ));
    }
}
