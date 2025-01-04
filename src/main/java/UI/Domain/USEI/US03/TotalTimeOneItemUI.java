package UI.Domain.USEI.US03;

import UI.Domain.USEI.US08.SimulateProcessTimeAndPriorityUI;
import UI.Menu.MenuItem;
import UI.Utils.Utils;
import domain.Item;
import jdk.jshell.execution.Util;
import repository.HashMap_Items_Machines;
import repository.Instances;

import java.util.*;

public class TotalTimeOneItemUI implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();

    /**
     * This method calculates the total time of one item in the production plan.
     */
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
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Choose the Item to be Visualized ------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    Utils.clearConsole();
                    show(choice);
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1);
    }

    /**
     * This method sorts the items by id and removes duplicates.
     *
     * @param items the list of items to be sorted and have duplicates removed
     * @return the sorted list of items without duplicates
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

    /**
     * This method shows the total time of one item in the production plan.
     *
     * @param choice the item to be visualized
     */
    private void show(String choice) {
        int id;
        id = Integer.parseInt(choice.split(" ")[1]);
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Total Time for Item " + id + " ------------\n" + Utils.RESET);
        HashMap<String, Double> totalTimes = Item.calculateTotalProductionTimePerItem();

        System.out.printf(Utils.BOLD + "%-15s  %-15s%n", "Item", "Total Time");
        System.out.println("-----------------------------------------------" + Utils.RESET);

        for (Map.Entry<String, Double> entry : totalTimes.entrySet()) {
            String[] item = entry.getKey().split(" - ");
            int itemID = Integer.parseInt(item[0]);
            if (itemID == id) {
                System.out.printf("%-15s  %-15.2f%n", item[0], entry.getValue());
            }
        }
    }
}
