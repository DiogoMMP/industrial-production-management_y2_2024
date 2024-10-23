package prodPlanSimulator.UI.Domain.US04;

import prodPlanSimulator.UI.Menu.MenuItem;
import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListExecutionTimeOneOpUI implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();

    @Override
    public void run() {
        String choice;
        ArrayList<Item> items = new ArrayList<>(map.getProdPlan().keySet());
        List<MenuItem> options = new ArrayList<>();
        for (Item item : items) {
            for (String operation : item.getOperations()) {
                if (options.stream().noneMatch(menuItem -> menuItem.toString().equals(operation))) {
                    options.add(new MenuItem(operation, new ListExecutionTimeOneOpUI()));
                }
            }
        }
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Choose the Operation to be visualized ------------");
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
        System.out.println("\n\n--- Execution Times by Operation  ------------");
        HashMap<String, Double> execTimes = map.calcOpTime();
        for (Map.Entry<String, Double> entry : execTimes.entrySet()) {
            if (entry.getKey().equals(choice)){
                System.out.println("Total time of the operatiom " + entry.getKey() + " : " + entry.getValue());
            }
        }
    }
}
