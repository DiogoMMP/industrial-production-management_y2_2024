package UI.Domain.US22;

import UI.Utils.Utils;
import domain.Activity;
import projectManager.PERT_CPM;
import repository.Instances;

import java.util.LinkedHashMap;
import java.util.List;

public class ShowCriticalPathsUI implements Runnable {
    @Override
    public void run() {
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();  // Get the PERT_CPM instance
        LinkedHashMap<Integer, List<Activity>> criticalPaths = pertCpm.findCriticalPaths();
        System.out.println("\n\n--- Show Critical Paths ------------");
        for (Integer i : criticalPaths.keySet()) {
            System.out.println("\n\n--- Critical Path " + (i) + " ------------");
            List<Activity> path = criticalPaths.get(i);
            for (Activity activity : path) {
                if (activity.getActId().equals("END")) {
                    System.out.printf(" %s", activity.getActId());
                } else {
                    System.out.printf(" %s ->", activity.getActId());
                }
            }
        }
        Utils.goBackAndWait();
    }
}
