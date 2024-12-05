package UI.Domain.US18;

import projectManager.PERT_CPM;
import repository.Instances;

public class ShowCircularDependenciesUI implements Runnable {

    @Override
    public void run() {
        // Retrieve the PERT_CPM instance
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();

        // Check for circular dependencies
        try {
            // Validate the graph for circular dependencies
            boolean hasCircular= pertCpm.validateGraph();
            if (hasCircular) {
                System.out.println("The graph has circular dependencies.");
            } else {
                System.out.println("The graph does not have circular dependencies.");
            }
        } catch (IllegalStateException e) {
            // Handle the case where circular dependencies are found
            System.err.println("Error: " + e.getMessage());
        }
    }
}
