package projectManager;

import domain.Activity;
import repository.Instances;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class CalculateTimes {
    private PERT_CPM pert_cpm;

    public void calculateTimes() {
        pert_cpm = Instances.getInstance().getPERT_CPM();
        LinkedHashMap<String, Activity> activities = pert_cpm.getActivitiesPERT_CPM();
        clearTimes(activities);
        calculateESEF(activities);
        calculateLSLF(activities);
        calculateSlack(activities);
    }

    /**
     * Clear the times of the activities
     *
     * @param activities Activities to clear the times
     */
    private void clearTimes(LinkedHashMap<String, Activity> activities) {
        for (Activity activity : activities.values()) {
            activity.clearTimes();
        }
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
        LinkedHashMap<String, Activity> activitiesCopy = new LinkedHashMap<>(activities);
        LinkedList<Activity> reversedActivities = new LinkedList<>(activities.values());
        Collections.reverse(reversedActivities);
        for (Activity activity : reversedActivities) {
            activity.calculateLSLF(activitiesCopy);
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
