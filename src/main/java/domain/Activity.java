package domain;

import java.util.List;

import java.util.List;

public class Activity {
    private String actId;          // Activity ID
    private String description;    // Activity description
    private int duration;          // Duration of the activity
    private String durationUnit;   // Duration unit of the activity
    private int cost;              // Cost of the activity
    private String costUnit;       // Cost unit of the activity
    private List<String> prevActIds; // Predecessor activities

    /**
     * Constructor
     * @param actId Activity ID
     * @param description Activity description
     * @param duration Duration of the activity
     * @param durationUnit Duration unit of the activity
     * @param cost Cost of the activity
     * @param costUnit Cost unit of the activity
     * @param prevActIds List of IDs of predecessor activities
     */
    public Activity(String actId, String description, int duration, String durationUnit, int cost, String costUnit,
                    List<String> prevActIds) {
        this.actId = actId;
        this.description = description;
        this.duration = duration;
        this.durationUnit = durationUnit;
        this.cost = cost;
        this.costUnit = costUnit;
        this.prevActIds = prevActIds;
    }

    /**
     * Get the ID of the activity
     * @return Activity ID
     */
    public String getActId() {
        return actId;
    }

    /**
     * Get the description of the activity
     * @return Activity description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the duration of the activity
     * @return Activity duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Get the duration of the activity with the unit
     * @return Activity duration with the unit
     */
    public String getDurationWithUnit() {
        return duration + " " + durationUnit;
    }

    /**
     * Get the cost of the activity
     * @return Activity cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * Get the cost of the activity with the unit
     * @return Activity cost with the unit
     */
    public String getCostWithUnit() {
        return cost + " " + costUnit;
    }

    /**
     * Get the list of IDs of predecessor activities
     * @return List of IDs of predecessor activities
     */
    public List<String> getPrevActIds() {
        return prevActIds;
    }

    /**
     * Set the ID of the activity
     * @param actId Activity ID
     */
    public void setActId(String actId) {
        this.actId = actId;
    }

    /**
     * Set the description of the activity
     * @param description Activity description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the duration of the activity
     * @param duration Activity duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Set the duration unit of the activity
     * @param durationUnit Duration unit of the activity
     */
    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    /**
     * Set the cost of the activity
     * @param cost Activity cost
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Set the cost unit of the activity
     * @param costUnit Cost unit of the activity
     */
    public void setCostUnit(String costUnit) {
        this.costUnit = costUnit;
    }

    /**
     * Set the list of IDs of predecessor activities
     * @param prevActIds List of IDs of predecessor activities
     */
    public void setPrevActIds(List<String> prevActIds) {
        this.prevActIds = prevActIds;
    }

    // PERT/CPM calculations
    private int earliestStart;
    private int latestFinish;
    private int slack;

    /**
     * Calculate the earliest start time of the activity
     */
    public void calculateEarliestStart() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * Calculate the latest finish time of the activity
     */
    public void calculateLatestFinish() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * Calculate the slack time of the activity
     */
    public void calculateSlack() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * Get the earliest start time of the activity
     * @return Earliest start time of the activity
     */
    public int getEarliestStart() {
        return earliestStart;
    }

    /**
     * Get the latest finish time of the activity
     * @return Latest finish time of the activity
     */
    public int getLatestFinish() {
        return latestFinish;
    }

    /**
     * Get the slack time of the activity
     * @return Slack time of the activity
     */
    public int getSlack() {
        return slack;
    }
}
