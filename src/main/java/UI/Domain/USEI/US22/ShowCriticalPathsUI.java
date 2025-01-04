package UI.Domain.USEI.US22;

import UI.Utils.Utils;
import domain.Activity;
import jdk.jshell.execution.Util;
import projectManager.PERT_CPM;
import repository.Instances;

import java.util.LinkedHashMap;
import java.util.List;

public class ShowCriticalPathsUI implements Runnable {
    @Override
    public void run() {
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();  // Get the PERT_CPM instance

        if (pertCpm.hasCircularDependencies()) {
            System.out.println(Utils.RED + "\nError: The project has circular dependencies." + Utils.RESET);
            Utils.goBackAndWait();
            return;
        }

        LinkedHashMap<Integer, List<Activity>> criticalPaths = pertCpm.findCriticalPaths();
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Show Critical Paths ------------" + Utils.RESET);

        if (criticalPaths.isEmpty()) {
            System.out.println(Utils.RED + "No critical paths found!" + Utils.RESET);
            Utils.goBackAndWait();
            return;
        }

        for (Integer i : criticalPaths.keySet()) {
            System.out.println("\n\n" + Utils.BOLD + "--- Critical Path " + i + " ------------" + Utils.RESET);
            List<Activity> path = criticalPaths.get(i);
            for (Activity activity : path) {
                if (activity.getActId().equals("END")) {
                    System.out.printf(" %s", activity.getActId());
                } else {
                    System.out.printf(" %s ->", activity.getActId());
                }
            }
        }
        System.out.println("\n");
        Utils.goBackAndWait();
    }
}
