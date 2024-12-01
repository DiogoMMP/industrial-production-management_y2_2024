package projectManager;

import domain.Activity;
import graph.map.MapGraph;
import repository.ActivitiesMapRepository;
import repository.Instances;
import java.util.Map;

/**
 * Class representing the PERT/CPM.
 */
public class PERT_CPM {

    MapGraph<String, String> pert_CPM;
    Map<String, Activity> activities;
    /**
     * Constructor for the PERT_CPM class.
     */
    public PERT_CPM() {
        pert_CPM = new MapGraph<>(true); // Directed graph

        ActivitiesMapRepository activitiesMapRepository = Instances.getInstance().getActivitiesMapRepository();
        activities = activitiesMapRepository.getActivitiesMapRepository();
        buildPERT_CPM();
    }

    /**
     * Builds the PERT/CPM graph.
     */
    private void buildPERT_CPM() {
        // Add the "START" node
        pert_CPM.addVertex("START");

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
                String dependencyLabel = dependencyId + " (" + activities.get(dependencyId).getDurationWithUnit() + ")";
                pert_CPM.addEdge(dependencyLabel, nodeLabel, "0");
            }
        }

        // Add the "END" node
        pert_CPM.addVertex("END");

        // Connect all activities without successors to the "END" node
        for (String vertex : pert_CPM.vertices()) {
            // A node without successors has an out-degree of 0
            if (pert_CPM.outDegree(vertex) == 0 && !vertex.equals("END") && !vertex.equals("START")) {
                // Connect to "END"
                pert_CPM.addEdge(vertex, "END", "0");
            }
        }
    }

    /**
     * Returns the PERT/CPM graph.
     * @return PERT/CPM graph.
     */
    public MapGraph<String, String> getPert_CPM() {
        return pert_CPM;
    }

    /**
     * Returns the map of activities.
     * @return Map of activities.
     */
    public Map<String, Activity> getActivities() {
        return activities;
    }

    /**
     * Adds an activity to the graph.
     * @param activity Activity to be added.
     */
    public void addActivity(Activity activity) {
        activities.put(activity.getActId(), activity);
        String nodeLabel = activity.getActId() + " (" + activity.getDurationWithUnit() + ")";
        pert_CPM.addVertex(nodeLabel);
    }

    /**
     * Adds a dependency between activities.
     * @param actId ID of the activity.
     * @param prevActId ID of the predecessor activity.
     */
    public void addDependency(String actId, String prevActId) {
        String nodeLabel = actId + " (" + activities.get(actId).getDuration() + ")";
        String dependencyLabel = prevActId + " (" + activities.get(prevActId).getDurationWithUnit() + ")";
        pert_CPM.addEdge(dependencyLabel, nodeLabel, "0");
    }

    /**
     * Removes an activity from the graph.
     * @param actId ID of the activity to be removed.
     */
    public void removeActivity(String actId) {
        String nodeLabel = actId + " (" + activities.get(actId).getDurationWithUnit() + ")";
        activities.remove(actId);
        pert_CPM.removeVertex(nodeLabel);
    }

    /**
     * Removes a dependency between activities.
     * @param actId ID of the activity.
     * @param prevActId ID of the predecessor activity.
     */
    public void removeDependency(String actId, String prevActId) {
        String nodeLabel = actId + " (" + activities.get(actId).getDurationWithUnit() + ")";
        String dependencyLabel = prevActId + " (" + activities.get(prevActId).getDurationWithUnit() + ")";
        pert_CPM.removeEdge(dependencyLabel, nodeLabel);
    }

    /**
     * Checks if an activity is present in the graph.
     * @param actId ID of the activity.
     * @return true if the activity is present, false otherwise.
     */
    public boolean containsActivity(String actId) {
        return activities.containsKey(actId);
    }

    /**
     * Checks if the graph is empty.
     * @return true if the graph is empty, false otherwise.
     */
    public boolean isEmpty() {
        return activities.isEmpty();
    }

    /**
     * Returns the number of activities in the graph.
     * @return Number of activities.
     */
    public int size() {
        return activities.size();
    }

}