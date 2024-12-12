package UI.Domain.USEI.US10;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import repository.Instances;
import trees.ProductionTree.ProductionTree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchOperationUI implements Runnable {

    private ProductionTree productionTree = Instances.getInstance().getProductionTree();
    private Map<String, String> operations = Instances.getInstance().getOperationsMapRepository().getOperationsMapRepository();

    @Override
    public void run() {
        List<MenuItem> searchOptions = new ArrayList<>();
        searchOptions.add(new MenuItem("Search by ID", this::searchByID));
        searchOptions.add(new MenuItem("Search by Name", this::searchByName));

        int searchOption = Utils.showAndSelectIndex(searchOptions, "\n\n\033[1m\033[36m--- Choose the Search Method ------------------\033[0m");
        if (searchOption >= 0 && searchOption < searchOptions.size()) {
            searchOptions.get(searchOption).run();
        }
    }

    private void searchByID() {
        String choice;
        List<MenuItem> options = new ArrayList<>();

        // Sort the operations map by its keys
        Map<String, String> sortedOperations = sortOperationsByID(operations);

        // Create menu options from the sorted map
        for (Map.Entry<String, String> entry : sortedOperations.entrySet()) {
            options.add(new MenuItem("Operation: " + entry.getKey() + " - " + entry.getValue(), new SearchOperationUI()));
        }

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n\033[1m\033[36m--- Choose the Operation to Search ------------\033[0m");
            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    clearConsole();
                    String operationId = choice.split(" ")[1];
                    executeAndPrintSearchByID(operationId);
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1);
    }

    private void searchByName() {
        String name = Utils.readLineFromConsole("Enter the name of the operation: ");
        executeAndPrintSearchByName(name);
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void executeAndPrintSearchByID(String id) {
        Map<String, String> result = productionTree.searchNodeByID(id);
        printSearchResult(result);
    }

    private void executeAndPrintSearchByName(String name) {
        Map<String, String> result = productionTree.searchNodeByName(name);
        printSearchResult(result);
    }

    private void printSearchResult(Map<String, String> result) {
        if (result.containsKey("Error")) {
            System.err.println("Error: Operation not found!");
        } else {
            System.out.println("\n");
            System.out.println("Type: " + result.get("Type"));
            System.out.println("Description and Quantity: " + result.get("Description"));
            System.out.println("Parent Operation: " + result.getOrDefault("Parent Operation", "None"));
        }
        System.out.println();
    }

    public static Map<String, String> sortOperationsByID(Map<String, String> operations) {
        return operations.entrySet()
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