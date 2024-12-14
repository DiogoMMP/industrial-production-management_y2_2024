package domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Activity {
    private String actId;          // Activity ID
    private String description;    // Activity description
    private int duration;          // Duration of the activity
    private String durationUnit;   // Duration unit of the activity
    private int totalCost;              // Total Cost of the activity
    private List<String> prevActIds; // Predecessor activities
    private double earliestStart;  // Earliest start time of the activity
    private double earliestFinish; // Earliest finish time of the activity
    private double latestStart;    // Latest start time of the activity
    private double latestFinish;   // Latest finish time of the activity
    private double slack;          // Slack time of the activity

    /**
     * Constructor
     *
     * @param actId        Activity ID
     * @param description  Activity description
     * @param duration     Duration of the activity
     * @param durationUnit Duration unit of the activity
     * @param totalCost         Total Cost of the activity
     * @param prevActIds   List of IDs of predecessor activities
     */
    public Activity(String actId, String description, int duration, String durationUnit, int totalCost,
                    List<String> prevActIds) {
        this.actId = actId;
        this.description = description;
        this.duration = duration;
        this.durationUnit = durationUnit;
        this.totalCost = totalCost;
        this.prevActIds = prevActIds;
        this.earliestStart = 0.0;
        this.earliestFinish = 0.0;
        this.latestStart = 0.0;
        this.latestFinish = 0.0;
        this.slack = 0.0;
    }

    /**
     * Constructor for start and finish activities
     *
     * @param actId       Activity ID
     * @param description Activity description
     * @param duration    Duration of the activity
     * @param totalCost        Cost of the activity
     * @param prevActIds  List of IDs of predecessor activities
     */
    public Activity(String actId, String description, int duration, int totalCost, List<String> prevActIds) {
        this.actId = actId;
        this.description = description;
        this.duration = duration;
        this.durationUnit = "";
        this.totalCost = totalCost;
        this.prevActIds = prevActIds;
        this.earliestStart = 0.0;
        this.earliestFinish = 0.0;
        this.latestStart = 0.0;
        this.latestFinish = 0.0;
        this.slack = 0.0;

    }

    /**
     * Get the ID of the activity
     *
     * @return Activity ID
     */
    public String getActId() {
        return actId;
    }

    /**
     * Get the description of the activity
     *
     * @return Activity description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the duration of the activity
     *
     * @return Activity duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Get the duration of the activity with the unit
     *
     * @return Activity duration with the unit
     */
    public String getDurationWithUnit() {
        return duration + " " + durationUnit;
    }

    /**
     * Get the cost of the activity
     *
     * @return Activity cost
     */
    public int getTotalCost() {
        return totalCost;
    }

    /**
     * Get the list of IDs of predecessor activities
     *
     * @return List of IDs of predecessor activities
     */
    public List<String> getPrevActIds() {
        return prevActIds;
    }

    /**
     * Set the ID of the activity
     *
     * @param actId Activity ID
     */
    public void setActId(String actId) {
        this.actId = actId;
    }

    /**
     * Set the description of the activity
     *
     * @param description Activity description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the duration of the activity
     *
     * @param duration Activity duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Set the duration unit of the activity
     *
     * @param durationUnit Duration unit of the activity
     */
    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    /**
     * Set the cost of the activity
     *
     * @param totalCost Activity cost
     */
    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Set the list of IDs of predecessor activities
     *
     * @param prevActIds List of IDs of predecessor activities
     */
    public void setPrevActIds(List<String> prevActIds) {
        this.prevActIds = prevActIds;
    }


    /**
     * Calculate the earliest start time and finish time of the activity
     *
     * @param activities Activities to calculate the times
     */
    public void calculateESEF(LinkedHashMap<String, Activity> activities) {
        if (actId.equalsIgnoreCase("START")) {
            earliestStart = 0.0;
            earliestFinish = 0.0;
            return;
        }
        for (String prevActId : prevActIds) {
            double prevActFinish = activities.get(prevActId).getEarliestFinish();
            if (prevActFinish > earliestStart) {
                earliestStart = prevActFinish;
            }
        }
        earliestFinish = earliestStart + duration;

    }

    /**
     * Calculate the latest start time and finish time of the activity
     *
     * @param activities Activities to calculate the times
     */
    public void calculateLSLF(LinkedHashMap<String, Activity> activities) {
        if (prevActIds.isEmpty()) {
            latestFinish = 0.0;
            latestStart = 0.0;
            return;
        } else if (actId.equalsIgnoreCase("END")) {
            latestFinish = earliestFinish;
            latestStart = earliestStart;
        }
        List<Activity> prevActIds = new ArrayList<>();
        for (String prevActId : this.prevActIds) {
            prevActIds.add(activities.get(prevActId));
        }
        for (Activity activity : prevActIds) {
            double activityStart = latestStart;
            if (activity.getLatestFinish() > activityStart) {
                activity.setLatestFinish(activityStart);
                activity.setLatestStart(activity.getLatestFinish() - activity.getDuration());
            } else if (activity.getLatestFinish() == 0) {
                activity.setLatestStart(activityStart - activity.getDuration());
                activity.setLatestFinish(activityStart);
            }
        }

    }

    /**
     * Calculate the slack time of the activity
     */
    public void calculateSlack() {
        slack = latestStart - earliestStart;
    }

    /**
     * Get the earliest start time of the activity
     *
     * @return Earliest start time of the activity
     */
    public double getEarliestStart() {
        return earliestStart;
    }

    /**
     * Get the latest finish time of the activity
     *
     * @return Latest finish time of the activity
     */
    public double getLatestFinish() {
        return latestFinish;
    }

    /**
     * Get the slack time of the activity
     *
     * @return Slack time of the activity
     */
    public double getSlack() {
        return slack;
    }

    /**
     * Get the earliest finish time of the activity
     *
     * @return Earliest finish time of the activity
     */
    public double getEarliestFinish() {
        return earliestFinish;
    }

    /**
     * Get the latest start time of the activity
     *
     * @return Latest start time of the activity
     */
    public double getLatestStart() {
        return latestStart;
    }

    /**
     * Set the earliest finish time of the activity
     *
     * @param earliestFinish Earliest finish time of the activity
     */
    public void setEarliestFinish(double earliestFinish) {
        this.earliestFinish = earliestFinish;
    }

    /**
     * Set the latest finish time of the activity
     *
     * @param latestFinish Latest finish time of the activity
     */
    public void setLatestFinish(double latestFinish) {
        this.latestFinish = latestFinish;
    }

    /**
     * Set the earliest start time of the activity
     *
     * @param earliestStart Earliest start time of the activity
     */
    public void setEarliestStart(double earliestStart) {
        this.earliestStart = earliestStart;
    }

    /**
     * Set the latest start time of the activity
     *
     * @param latestStart Latest start time of the activity
     */
    public void setLatestStart(double latestStart) {
        this.latestStart = latestStart;
    }

    /**
     * Set the slack time of the activity
     *
     * @param slack Slack time of the activity
     */
    public void setSlack(double slack) {
        this.slack = slack;
    }

    public void clearTimes() {
        earliestStart = 0.0;
        earliestFinish = 0.0;
        latestStart = 0.0;
        latestFinish = 0.0;
        slack = 0.0;
    }
}
