package UI.Domain.US19;

import projectManager.PERT_CPM;
import repository.Instances;

import java.util.List;

public class ShowTopologicalSortUI implements Runnable {

    @Override
    public void run() {
        // Retrieve the PERT/CPM instance
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();

        try {
            // Perform topological sort
            List<String> sortedActivities = pertCpm.topologicalSort();

            // Display the sorted activities
            System.out.println("Topological Sort of Activities:");
            for (String activity : sortedActivities) {
                System.out.println(activity);
            }
        } catch (IllegalStateException e) {
            // Handle the case where a cycle exists
            System.err.println("Error: " + e.getMessage());
        }
    }
}
