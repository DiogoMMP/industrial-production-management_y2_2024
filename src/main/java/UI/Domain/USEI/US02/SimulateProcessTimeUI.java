package UI.Domain.USEI.US02;

import UI.Menu.MenuItem;
import UI.Simulators.ChooseSimulatorUI;
import UI.Utils.Utils;
import domain.Item;
import repository.HashMap_Items_Machines;
import repository.Instances;
import prodPlanSimulator.Simulator;

import java.util.*;

public class SimulateProcessTimeUI implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();
    private Simulator simulator = Instances.getInstance().getSimulator();
    /**
     * This method is responsible for running the simulation of the process time for each item.
     */
    @Override
    public void run() {
        String choice;
        ArrayList<Item> items = new ArrayList<>(map.getProdPlan().keySet());
        items = sortAndRemoveDuplicates(items);
        List<MenuItem> options = new ArrayList<>();
        for (Item item : items) {
            options.add(new MenuItem("Item: " + item.getId(), new SimulateProcessTimeUI()));
        }
        options.add(new MenuItem("All", new SimulateProcessTimeUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Choose the Item to be Visualized ------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

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

    /**
     * This method is responsible for clearing the console.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * This method is responsible for showing the simulation of the process time for each item.
     * @param choice The choice of the user.
     */
    private void show(String choice) {
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Simulate Process by Time ------------\n" + Utils.RESET);
        int id;
        LinkedHashMap<String, Double> timeOperations;
        if (choice.equals("All")) {
            timeOperations = simulator.getTimeOperations();
            timeOperations = updateOperationKeys(timeOperations);
            String previousId = "";
            int quantity = 0;
            for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
                String currentId = getItemIdFromEntry(entry.getKey());
                int currentQuantity = getQuantityFromEntry(entry.getKey());
                if (!currentId.equals(previousId)) {
                    if (!previousId.isEmpty()) {
                        System.out.println();
                    }
                    System.out.println(Utils.BOLD + "Item: " + currentId + " - Quantity: " + currentQuantity + Utils.RESET);
                    previousId = currentId;
                    quantity = currentQuantity;
                } else if (currentQuantity != quantity) {
                    System.out.println();
                    System.out.println(Utils.BOLD + "Item: " + currentId + " - Quantity: " + currentQuantity + Utils.RESET);
                    quantity = currentQuantity;
                }
                System.out.println(removeQuantityFromEntry(entry.getKey()));
            }
        } else {
            id = Integer.parseInt(choice.split(" ")[1]);
            System.out.println(Utils.BOLD + "Item: " + id + Utils.RESET);
            timeOperations = simulator.getTimeOperations();
            timeOperations = sortOperations(timeOperations, id);
            timeOperations = updateOperationKeys(timeOperations);
            int quantity = 0;
            for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
                int currentQuantity = getQuantityFromEntry(entry.getKey());
                if (currentQuantity != quantity) {
                    if (quantity != 0) {
                        System.out.println();
                    }
                    System.out.println(Utils.BOLD + "Item: " + id + " - Quantity: " + currentQuantity + Utils.RESET);
                    quantity = currentQuantity;
                }
                System.out.println(removeQuantityFromEntry(entry.getKey()));
            }
        }
    }

    /**
     * This method is responsible for getting the item id from the entry.
     * @param entry The entry.
     * @return The item id.
     */
    private String getItemIdFromEntry(String entry) {
        String[] parts = entry.split(" - ");
        for (String part : parts) {
            if (part.startsWith("Item: ")) {
                return part.substring(6);
            }
        }
        return "";
    }

    /**
     * This method is responsible for getting the quantity from the entry.
     * @param entry The entry.
     * @return The quantity.
     */
    private int getQuantityFromEntry(String entry) {
        String[] parts = entry.split(" - ");
        for (String part : parts) {
            if (part.startsWith("Quantity: ")) {
                return Integer.parseInt(part.substring(10));
            }
        }
        return 0;
    }

    /**
     * This method is responsible for removing the quantity from the entry.
     * @param entry The entry.
     * @return The entry without the quantity.
     */
    private String removeQuantityFromEntry(String entry) {
        return entry.replaceAll(" - Quantity: \\d+", "");
    }


    /**
     * This method is responsible for updating the operation keys.
     * @param timeOperations The time operations.
     * @return The updated time operations.
     */
    private LinkedHashMap<String, Double> updateOperationKeys(LinkedHashMap<String, Double> timeOperations) {
        LinkedHashMap<String, Double> updatedTimeOperations = new LinkedHashMap<>();
        int operationNumber = 1;
        for (Map.Entry<String, Double> entry : timeOperations.entrySet()) {
            String newKey = operationNumber + " - " + entry.getKey().replaceFirst("\\d+ - ", "");
            updatedTimeOperations.put(newKey, entry.getValue());
            operationNumber++;
        }
        return updatedTimeOperations;
    }

    /**
     * This method is responsible for sorting the operations.
     * @param timeOperations The time operations.
     * @param id The id.
     * @return The sorted time operations.
     */
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

    /**
     * This method is responsible for sorting and removing duplicates.
     * @param items The items.
     * @return The sorted and unique items.
     */
    private ArrayList<Item> sortAndRemoveDuplicates(ArrayList<Item> items) {
        TreeSet<Item> sortedUniqueItems = new TreeSet<>(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return Integer.compare(Integer.parseInt(o1.getId()), Integer.parseInt(o2.getId()));
            }
        });

        sortedUniqueItems.addAll(items);
        return new ArrayList<>(sortedUniqueItems);
    }

}