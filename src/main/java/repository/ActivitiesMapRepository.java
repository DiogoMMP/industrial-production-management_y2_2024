package repository;

import importer_and_exporter.InputFileReader;
import domain.Activity;

import java.util.HashMap;
import java.util.Map;

public class ActivitiesMapRepository {
    // Map to store activities, using the activity ID as the key
    private Map<String, Activity> activities;

    // Constructor with an existing map of activities
    public ActivitiesMapRepository(Map<String, Activity> activities) {
        this.activities = activities;
    }

    // Default constructor that initializes an empty activities map
    public ActivitiesMapRepository() {
        this.activities = new HashMap<>();
    }

    // Getter for the activities map
    public Map<String, Activity> getActivitiesMapRepository() {
        return activities;
    }

    // Setter for the activities map
    public void setActivitiesMapRepository(Map<String, Activity> activities) {
        this.activities = activities;
    }

    // Method to add a new activity to the repository
    public void addActivity(String key, Activity activity) {
        activities.put(key, activity);
    }

    // Method to remove an activity by its key (activity ID)
    public void removeActivity(String key) {
        activities.remove(key);
    }

    // Method to get an activity by its key (activity ID)
    public Activity getActivity(String key) {
        return activities.get(key);
    }

    // Method to get the size of the activities map
    public int size() {
        return activities.size();
    }

    // Method to check if the activities map is empty
    public boolean isEmpty() {
        return activities.isEmpty();
    }

    // Method to clear all activities from the map
    public void clear() {
        activities.clear();
    }

    // Method to check if an activity exists by its key
    public boolean containsKey(String key) {
        return activities.containsKey(key);
    }

    // Method to check if an activity exists by its value (Activity object)
    public boolean containsValue(Activity activity) {
        return activities.containsValue(activity);
    }

    // Method to add activities from a CSV or file
    public void addActivities(String activitiesPath) {
        activities.clear();
        Map<String, Activity> activitiesFromFile = InputFileReader.readActivities(activitiesPath);

        try {
            if (activitiesFromFile.isEmpty()) {
                throw new Exception("Activities not found");
            }
            this.activities.putAll(activitiesFromFile);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
