package projectManager;

import domain.Activity;
import repository.Instances;

import java.util.LinkedHashMap;

public class CalculateTimes {
    private PERT_CPM pert_cpm;

    public void calculateTimes() {
        pert_cpm = Instances.getInstance().getPERT_CPM();
        LinkedHashMap<String, Activity> activities = pert_cpm.getActivitiesPERT_CPM();
        calculateESEF(activities);
        calculateLSLF(activities);
        calculateSlack(activities);
    }

    /**
     * Calculate the slack time of the activities
     *
     * @param activities Activities to calculate the slack
     */
    private static void calculateSlack(LinkedHashMap<String, Activity> activities) {
        for (Activity activity : activities.values()) {
            activity.calculateSlack();
        }
    }

    /**
     * Calculate the latest start and finish times of the activities
     *
     * @param activities Activities to calculate the times
     */
    private static void calculateLSLF(LinkedHashMap<String, Activity> activities) {
        for (Activity activity : activities.values()) {
            activity.calculateLSLF(activities);
        }
    }

    /**
     * Calculate the earliest start and finish times of the activities
     *
     * @param activities Activities to calculate the times
     */
    private static void calculateESEF(LinkedHashMap<String, Activity> activities) {
        for (Activity activity : activities.values()) {
            activity.calculateESEF(activities);
        }
    }

}
