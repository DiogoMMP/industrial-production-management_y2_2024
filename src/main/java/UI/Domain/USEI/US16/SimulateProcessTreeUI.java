package UI.Domain.USEI.US16;

import UI.Utils.Utils;
import repository.Instances;
import prodPlanSimulator.Simulator;

import java.util.LinkedHashMap;

public class SimulateProcessTreeUI implements Runnable {
    private static Simulator simulator = Instances.getInstance().getSimulator();

    /**
     * Run the UI
     */
    @Override
    public void run() {
        LinkedHashMap<String, Double> timeOperations = simulator.simulateBOMBOO();
        if (timeOperations.isEmpty()) {
            System.out.println("No operations to simulate.");
            Utils.goBackAndWait();
            return;
        }
        String operationName = extractOperationName(timeOperations.firstEntry().getKey());
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Simulation Of The Process Tree ------------\n" + Utils.RESET);

        System.out.println(Utils.BOLD + "Operation: " + operationName + Utils.RESET);

        for (String key : timeOperations.keySet()) {
            if (!operationName.equalsIgnoreCase(extractOperationName(key))) {
                operationName = extractOperationName(key);
                System.out.printf("%n");
                System.out.println(Utils.BOLD + "Operation: " + operationName + Utils.RESET);
            }
            String operation = key.replaceAll("^\\d+ - Operation: " + operationName, "");
            System.out.println(operation);
        }
        Utils.goBackAndWait();
    }

    /**
     * Extract the operation name from the key
     * @param key the key to extract the operation name from
     * @return the operation name
     */
    private String extractOperationName(String key) {
        int startIndex = key.indexOf("Operation: ") + 11;
        int endIndex = key.indexOf(" - ", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return key.substring(startIndex, endIndex).trim();
        }
        return "Unknown";
    }

}