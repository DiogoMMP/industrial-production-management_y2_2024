package UI.Domain.USEI.US18;

import UI.Utils.Utils;
import projectManager.PERT_CPM;
import repository.Instances;

public class ShowCircularDependenciesUI implements Runnable {

    /**
     * Run the UI
     */
    @Override
    public void run() {
        // Retrieve the PERT_CPM instance
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Circular Dependencies ------------\n" + Utils.RESET);

        // Check for circular dependencies
        try {
            // Validate the graph for circular dependencies
            boolean hasCircular= pertCpm.validateGraph();
            if (hasCircular) {
                System.out.println(Utils.RED + "The graph has circular dependencies." + Utils.RESET);
            } else {
                System.out.println(Utils.GREEN + "The graph does not have circular dependencies." + Utils.RESET);
            }
            Utils.goBackAndWait();
        } catch (IllegalStateException e) {
            // Handle the case where circular dependencies are found
            System.err.println("Error: " + e.getMessage());
        }
    }
}
