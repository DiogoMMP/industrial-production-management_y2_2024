package UI.Domain.US19;

import UI.Utils.Utils;
import projectManager.PERT_CPM;
import repository.Instances;

import java.util.List;

public class ShowTopologicalSortUI implements Runnable {

    @Override
    public void run() {
        // Retrieve the PERT/CPM instance
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();

        System.out.println("\n\n\033[1m\033[36m--- Topological Sort ------------\033[0m\n");

        try {
            // Perform topological sort
            List<String> sortedActivities = pertCpm.topologicalSort();

            // Display the sorted activities
            for (String activity : sortedActivities) {
                System.out.println(activity);
            }

            Utils.goBackAndWait();
        } catch (IllegalStateException e) {
            // Handle the case where a cycle exists
            System.err.println("Error: " + e.getMessage());
        }
    }
}
