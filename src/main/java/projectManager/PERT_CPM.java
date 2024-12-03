package projectManager;

import domain.Activity;
import graph.map.MapGraph;
import repository.ActivitiesMapRepository;
import repository.Instances;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing the PERT/CPM.
 */
public class PERT_CPM {

    MapGraph<String, String> pert_CPM;
    Map<String, Activity> activities;
    LinkedHashMap<String, Activity> activitiesPERT_CPM;

    /**
     * Constructor for the PERT_CPM class.
     */
    public PERT_CPM() {
        pert_CPM = new MapGraph<>(true); // Directed graph
        activitiesPERT_CPM = new LinkedHashMap<>();
    }

    /**
     * Builds the PERT/CPM graph.
     */
    public void buildPERT_CPM() {
        // Add the "START" node
        ActivitiesMapRepository activitiesMapRepository = Instances.getInstance().getActivitiesMapRepository();
        activities = activitiesMapRepository.getActivitiesMapRepository();
        pert_CPM.addVertex("START");
        activitiesPERT_CPM.put("START", new Activity("START", "START", 0, 0, new ArrayList<>()));

        for (Activity activity : activities.values()) {
            // Create the node label with duration
            String nodeLabel = activity.getActId() + " (" + activity.getDurationWithUnit() + ")";
            pert_CPM.addVertex(nodeLabel);

            // Connect "START" to activities without predecessors
            if (activity.getPrevActIds().isEmpty()) {
                pert_CPM.addEdge("START", nodeLabel, "0");
            }

            // Connect activities to their predecessors
            for (String dependencyId : activity.getPrevActIds()) {
                Activity dependencyActivity = activities.get(dependencyId);
                if (dependencyActivity != null) {
                    String dependencyLabel = dependencyId + " (" + dependencyActivity.getDurationWithUnit() + ")";
                    pert_CPM.addEdge(dependencyLabel, nodeLabel, "0");
                }
            }

            activitiesPERT_CPM.put(activity.getActId(), activity);

            if (activity.getPrevActIds().isEmpty()) {
                List<String> modifiablePrevActIds = new ArrayList<>(activity.getPrevActIds());
                modifiablePrevActIds.add("START");
                activity.setPrevActIds(modifiablePrevActIds);
            }
        }

        // Add the "END" node
        pert_CPM.addVertex("END");
        activitiesPERT_CPM.put("END", new Activity("END", "END", 0, 0, new ArrayList<>()));

        // Connect all activities without successors to the "END" node
        for (String vertex : pert_CPM.vertices()) {
            // A node without successors has an out-degree of 0
            if (pert_CPM.outDegree(vertex) == 0 && !vertex.equals("END") && !vertex.equals("START")) {
                // Connect to "END"
                pert_CPM.addEdge(vertex, "END", "0");
                String actId = vertex.split(" ")[0];
                activitiesPERT_CPM.get("END").getPrevActIds().add(activitiesPERT_CPM.get(actId).getActId());
            }
        }
    }

    /**
     * Returns the PERT/CPM graph.
     *
     * @return PERT/CPM graph.
     */
    public MapGraph<String, String> getPert_CPM() {
        return pert_CPM;
    }

    /**
     * Returns the map of activities.
     *
     * @return Map of activities.
     */
    public Map<String, Activity> getActivities() {
        return activities;
    }

    /**
     * Returns the map of activities.
     *
     * @return Map of activities.
     */
    public LinkedHashMap<String, Activity> getActivitiesPERT_CPM() {
        return activitiesPERT_CPM;
    }

    /**
     * Adds an activity to the graph.
     *
     * @param activity Activity to be added.
     */
    public void addActivity(Activity activity) {
        activities.put(activity.getActId(), activity);
        activitiesPERT_CPM.put(activity.getActId(), activity);
        String nodeLabel = activity.getActId() + " (" + activity.getDurationWithUnit() + ")";
        pert_CPM.addVertex(nodeLabel);
    }

    /**
     * Adds a dependency between activities.
     *
     * @param actId     ID of the activity.
     * @param prevActId ID of the predecessor activity.
     */
    public void addDependency(String actId, String prevActId) {
        String nodeLabel = actId + " (" + activities.get(actId).getDuration() + ")";
        String dependencyLabel = prevActId + " (" + activities.get(prevActId).getDurationWithUnit() + ")";
        pert_CPM.addEdge(dependencyLabel, nodeLabel, "0");
    }

    /**
     * Removes an activity from the graph.
     *
     * @param actId ID of the activity to be removed.
     */
    public void removeActivity(String actId) {
        String nodeLabel = actId + " (" + activities.get(actId).getDurationWithUnit() + ")";
        activities.remove(actId);
        pert_CPM.removeVertex(nodeLabel);
    }

    /**
     * Removes a dependency between activities.
     *
     * @param actId     ID of the activity.
     * @param prevActId ID of the predecessor activity.
     */
    public void removeDependency(String actId, String prevActId) {
        String nodeLabel = actId + " (" + activities.get(actId).getDurationWithUnit() + ")";
        String dependencyLabel = prevActId + " (" + activities.get(prevActId).getDurationWithUnit() + ")";
        pert_CPM.removeEdge(dependencyLabel, nodeLabel);
    }

    /**
     * Checks if an activity is present in the graph.
     *
     * @param actId ID of the activity.
     * @return true if the activity is present, false otherwise.
     */
    public boolean containsActivity(String actId) {
        return activities.containsKey(actId);
    }

    /**
     * Checks if the graph is empty.
     *
     * @return true if the graph is empty, false otherwise.
     */
    public boolean isEmpty() {
        return activities.isEmpty();
    }

    /**
     * Returns the number of activities in the graph.
     *
     * @return Number of activities.
     */
    public int size() {
        return activities.size();
    }

}