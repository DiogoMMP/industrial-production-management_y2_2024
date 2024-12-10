package UI.Domain.US24;

import UI.Utils.Utils;
import domain.Activity;
import projectManager.PERT_CPM;
import prodPlanSimulator.Simulator;
import repository.Instances;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SimulateProjDelaysUI implements Runnable {
    @Override
    public void run() {
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();
        Scanner scanner = new Scanner(System.in);
        LinkedHashMap<String, Integer> delays = new LinkedHashMap<>();

        System.out.println("\n\n--- Simulate Project Delays ------------");
        while (true) {
            System.out.print("Enter Activity ID (or 'done' to finish): ");
            String actId = scanner.nextLine();
            if (actId.equalsIgnoreCase("done")) {
                break;
            }
            if (!pertCpm.containsActivity(actId)) {
                System.out.println("Activity ID not found. Please try again.");
                continue;
            }
            System.out.print("Enter delay duration for " + actId + ": ");
            int delay = Integer.parseInt(scanner.nextLine());
            delays.put(actId, delay);
        }

        pertCpm.simulateDelaysAndRecalculate(delays);

        System.out.println("\nUpdated Critical Paths and Project Duration:");
        LinkedHashMap<Integer, List<Activity>> criticalPaths = pertCpm.findCriticalPaths();
        for (Map.Entry<Integer, List<Activity>> entry : criticalPaths.entrySet()) {
            System.out.print("Path " + entry.getKey() + ": ");
            for (Activity activity : entry.getValue()) {
                System.out.print(activity.getActId() + " ");
            }
            System.out.println();
        }
        System.out.println("Total Project Duration: " + pertCpm.calculateTotalProjectDuration() + " days");

        Utils.goBackAndWait();
    }
}