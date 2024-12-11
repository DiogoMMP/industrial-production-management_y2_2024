package UI.Domain.US18;

import UI.Utils.Utils;
import projectManager.PERT_CPM;
import repository.Instances;

public class ShowCircularDependenciesUI implements Runnable {

    @Override
    public void run() {
        // Retrieve the PERT_CPM instance
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();

        System.out.println("\n\n\033[1m\033[36m--- Circular Dependencies ------------\033[0m");

        // Check for circular dependencies
        try {
            // Validate the graph for circular dependencies
            boolean hasCircular= pertCpm.validateGraph();
            if (hasCircular) {
                System.out.println("The graph has circular dependencies.");
            } else {
                System.out.println("The graph does not have circular dependencies.");
            }
            Utils.goBackAndWait();
        } catch (IllegalStateException e) {
            // Handle the case where circular dependencies are found
            System.err.println("Error: " + e.getMessage());
        }
    }
}
