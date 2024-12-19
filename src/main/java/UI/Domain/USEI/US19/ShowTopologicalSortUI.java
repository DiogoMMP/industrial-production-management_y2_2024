package UI.Domain.USEI.US19;

import UI.Utils.Utils;
import projectManager.PERT_CPM;
import repository.Instances;

import java.util.List;

public class ShowTopologicalSortUI implements Runnable {

    /**
     * Run the UI
     */
    @Override
    public void run() {
        // Retrieve the PERT/CPM instance
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Topological Sort ------------\n" + Utils.RESET);

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
            System.out.println(Utils.RED + "Error: " + e.getMessage() + Utils.RESET);
            Utils.goBackAndWait();
        }
    }
}
