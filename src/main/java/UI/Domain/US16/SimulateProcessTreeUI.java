package UI.Domain.US16;

import UI.Utils.Utils;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.Simulator;

import java.util.LinkedHashMap;

public class SimulateProcessTreeUI implements Runnable {
    private static Simulator simulator = Instances.getInstance().getSimulator();

    public SimulateProcessTreeUI() {
    }

    @Override
    public void run() {
        LinkedHashMap<String, Double> timeOperations = simulator.simulateBOMBOO();
        String operationName = extractOperationName(timeOperations.firstEntry().getKey());
        System.out.println("Simulation of the process tree:");
        System.out.printf("%n");
        System.out.println("Operation: " + operationName);
        for (String key : timeOperations.keySet()) {
            if (!operationName.equalsIgnoreCase(extractOperationName(key))) {
                operationName = extractOperationName(key);
                System.out.printf("%n");
                System.out.println("Operation: " + operationName);
            }
            String operation = key.replaceAll("^\\d+ - Operation: " + operationName, "");
            System.out.println(operation);
        }
        Utils.goBackAndWait();
    }

    private String extractOperationName(String key) {
        // Assuming the operation name is the part after "Operation: " and before the next " - "
        int startIndex = key.indexOf("Operation: ") + 11;
        int endIndex = key.indexOf(" - ", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return key.substring(startIndex, endIndex).trim();
        }
        return "Unknown";
    }
}