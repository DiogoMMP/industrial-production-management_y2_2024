package UI.Domain.USEI.US10;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import repository.Instances;
import trees.ProductionTree.ProductionTree;

import java.util.*;
import java.util.stream.Collectors;

public class SearchMaterialUI implements Runnable {

    private ProductionTree productionTree = Instances.getInstance().getProductionTree();
    private Map<String, String> items = Instances.getInstance().getItemsRepository().getItemsRepository();

    /**
     * This method allows the user to choose the search method.
     */
    @Override
    public void run() {
        List<MenuItem> searchOptions = new ArrayList<>();
        searchOptions.add(new MenuItem("Search by ID", this::searchByID));
        searchOptions.add(new MenuItem("Search by Name", this::searchByName));

        int searchOption = Utils.showAndSelectIndex(searchOptions, "\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Choose the Search Method ------------------\n" + Utils.RESET);

        if (searchOption == -2) {
            new SearchUI().run();
        }

        if (searchOption >= 0 && searchOption < searchOptions.size()) {
            searchOptions.get(searchOption).run();
        }
    }

    /**
     * This method allows the user to search for a material by its ID.
     */
    private void searchByID() {
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
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Choose the Material to Search ------------\n" + Utils.RESET);

            if (option == -2) {
                new SearchMaterialUI().run();
            }

            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    Utils.clearConsole();
                    String itemId = choice.split(" ")[1];
                    executeAndPrintSearchByID(itemId);
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1);
    }

    /**
     * This method allows the user to search for a material by its name.
     */
    private void searchByName() {
        Utils.clearConsole();
        String name = Utils.readLineFromConsole("Enter the name of the material (or \"stop\" to go back): ");
        if (name.equals("stop")) {
            return;
        }
        executeAndPrintSearchByName(name);

        String input;
        do {
            input = Utils.readLineFromConsole("Press '0' to go back: ");
        } while (!Objects.equals(input, "0"));

        if (Objects.equals(input, "0")) {
            new SearchMaterialUI().run();
        }

    }

    /**
     * This method executes the search by ID and prints the result.
     * @param id The ID of the material to be searched.
     */
    private void executeAndPrintSearchByID(String id) {
        Map<String, String> result = productionTree.searchNodeByID(id);
        printSearchResult(result);
    }

    /**
     * This method executes the search by name and prints the result.
     * @param name The name of the material to be searched.
     */
    private void executeAndPrintSearchByName(String name) {
        List<Map<String, String>> results = productionTree.searchNodeByName(name); // Recebe múltiplos resultados
        printSearchResultsByName(results);
    }

    /**
     * This method prints the search results.
     * @param results The list of search results to be printed.
     */
    private void printSearchResultsByName(List<Map<String, String>> results) {
        if (results.isEmpty() || (results.size() == 1 && results.get(0).containsKey("Error"))) {
            System.err.println("Error: No operations or materials found with the given name!");
        } else {
            System.out.println("\n" + Utils.BOLD + Utils.CYAN + "--- Operation Search Results ------------\n" + Utils.RESET);
            System.out.printf(Utils.BOLD + "%-20s  %-50s  %-20s%n", "Type", "Description and Quantity", "Parent Operation");
            System.out.println("--------------------------------------------------------------------------------------" +
                    "---------------------------------------" + Utils.RESET);

            for (Map<String, String> result : results) {
                if (!result.containsKey("Error")) {
                    System.out.printf("%-20s  %-50s  %-20s%n",
                            result.get("Type"),
                            result.get("Description"),
                            result.getOrDefault("Parent Operation", "None"));
                }
            }
        }
        System.out.println();
    }

    /**
     * This method prints the search result.
     * @param result The search result to be printed.
     */
    private void printSearchResult(Map<String, String> result) {
        if (result.containsKey("Error")) {
            System.err.println("Error: Material not found!");
        } else {
            System.out.println("\n" + Utils.BOLD + Utils.CYAN + "--- Material Search Result ------------\n" + Utils.RESET);
            System.out.printf(Utils.BOLD + "%-20s  %-50s  %-20s%n", "Type", "Description and Quantity", "Parent Operation");
            System.out.println("--------------------------------------------------------------------------------------" +
                    "---------------------------------------" + Utils.RESET);

            System.out.printf("%-20s  %-50s  %-20s%n", result.get("Type"), result.get("Description"), result.getOrDefault("Parent Operation", "None"));
        }
        System.out.println();
    }

    /**
     * This method sorts the items by ID.
     * @param map The map to be sorted.
     * @return The sorted map.
     */
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