package UI.Domain.USEI.US04;

import UI.Utils.Utils;
import repository.HashMap_Items_Machines;
import repository.Instances;
import domain.Item;

import java.util.HashMap;
import java.util.Map;
public class ListExecutionTimeAllOpUI implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();

    /**
     * This method calculates the total time of each operation and prints it on the screen.
     */
    @Override
    public void run() {
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Execution Times by Operation  ------------\n" + Utils.RESET);
        HashMap<String, Double> execTimes = Item.calcOpTime();
        for (Map.Entry<String, Double> entry : execTimes.entrySet()) {
            System.out.println(Utils.BOLD + "Total time of the operation " + entry.getKey() + " : " + Utils.RESET + entry.getValue());
        }
        Utils.goBackAndWait();
    }
}
