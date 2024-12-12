package UI.Domain.USEI.US24;

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

        System.out.println("\n\n\033[1m\033[36m--- Simulate Project Delays ------------\033[0m");
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

        System.out.println("\n\033[1mUpdated Critical Paths and Project Duration:\033[0m");
        LinkedHashMap<Integer, List<Activity>> criticalPaths = pertCpm.findCriticalPaths();
        for (Map.Entry<Integer, List<Activity>> entry : criticalPaths.entrySet()) {
            System.out.print("\033[1mPath " + entry.getKey() + ": \033[0m");
            for (Activity activity : entry.getValue()) {
                System.out.print(activity.getActId() + " ");
            }
            System.out.println();
        }
        System.out.println("\033[1mTotal Project Duration: " + pertCpm.calculateTotalProjectDuration() + " days\033[0m");

        Utils.goBackAndWait();
    }
}