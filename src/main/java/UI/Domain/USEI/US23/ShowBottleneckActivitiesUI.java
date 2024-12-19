package UI.Domain.USEI.US23;

import UI.Utils.Utils;
import domain.Activity;
import projectManager.PERT_CPM;
import repository.Instances;

import java.util.List;

public class ShowBottleneckActivitiesUI implements Runnable{
    @Override
    public void run() {
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();  // Get the PERT_CPM instance

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Bottleneck Activities ------------\n" + Utils.RESET);

        if (pertCpm.hasCircularDependencies()) {
            System.out.println(Utils.RED + "Error: The project has circular dependencies." + Utils.RESET);
            Utils.goBackAndWait();
            return;
        }

        List<Activity> bottleneckActivities = pertCpm.getBottleneckActivities();

        for (Activity activity : bottleneckActivities) {
            System.out.printf(" %s\n", activity.getActId());
        }

        Utils.goBackAndWait();
    }
}
