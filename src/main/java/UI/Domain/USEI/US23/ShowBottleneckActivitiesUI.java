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
        List<Activity> bottleneckActivities = pertCpm.getBottleneckActivities();
        System.out.println("\n\n\033[1m\033[36m--- Bottleneck Activities ------------\033[0m");
        for (Activity activity : bottleneckActivities) {
            System.out.printf(" %s\n", activity.getActId());
        }
        Utils.goBackAndWait();
    }
}
