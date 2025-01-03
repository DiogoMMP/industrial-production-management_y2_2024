package UI.Domain.USEI.US06;


import UI.Utils.Utils;
import domain.Item;

import java.util.HashMap;
import java.util.Map;

public class AverageAndWaitingTimesUI implements Runnable {
    @Override
    public void run() {
        HashMap<String, Double[]> averageTimes = Item.calculateAvgExecutionAndWaitingTimes();

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Average Execution and Waiting Times for Each Operation ------------\n" + Utils.RESET);

        // Print the header of the table
        System.out.printf(Utils.BOLD + "%-20s %-25s %-25s%n", "Operation", "Average Execution Time", "Average Waiting Time");
        System.out.println("--------------------------------------------------------------------" + Utils.RESET);

        // Print the data in table format
        for (Map.Entry<String, Double[]> entry : averageTimes.entrySet()) {
            System.out.printf(
                    "%-20s %-25.2f %-25.2f%n", // Align the values with 2 decimal points
                    entry.getKey(),
                    entry.getValue()[0],        // Average Execution Time
                    entry.getValue()[1]         // Average Waiting Time
            );
        }

        Utils.goBackAndWait();
    }

}

