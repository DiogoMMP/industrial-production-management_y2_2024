package projectManager;

import com.kitfox.svg.A;
import domain.Activity;
import graph.map.MapGraph;
import repository.ActivitiesMapRepository;
import repository.Instances;

import java.util.*;

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
        activitiesPERT_CPM.clear();
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
            String actId = vertex.split(" ")[0];
            boolean hasSuccessors = false;
            // A node without successors has an out-degree of 0
            if (pert_CPM.outDegree(vertex) == 0 && !vertex.equals("END") && !vertex.equals("START")) {
                // Connect to "END"
                pert_CPM.addEdge(vertex, "END", "0");
                if (activitiesPERT_CPM.containsKey(actId)) {
                    activitiesPERT_CPM.get("END").getPrevActIds().add(activitiesPERT_CPM.get(actId).getActId());
                }
            }
            for (String actId2 : activities.keySet()) {
                Activity activity = activities.get(actId2);
                if (activity.getPrevActIds().contains(actId)) {
                    hasSuccessors = true;
                }
            }
            if (!hasSuccessors && !vertex.equals("END") && !vertex.equals("START") && activitiesPERT_CPM.containsKey(actId) && !activitiesPERT_CPM.get("END").getPrevActIds().contains(actId)) {
                // Connect to "END"
                activitiesPERT_CPM.get("END").getPrevActIds().add(activitiesPERT_CPM.get(actId).getActId());
            }
        }
    }

    /**
     * Finds all the critical paths.
     */
    public LinkedHashMap<Integer, List<Activity>> findCriticalPaths() {
        LinkedHashMap<Integer, List<Activity>> criticalPaths = new LinkedHashMap<>();
        List<Activity> path = new ArrayList<>();
        // The method will start from the "END" node
        Activity endActivity = activitiesPERT_CPM.get("END");
        // We will need to get the predecessors of the "END" node
        List<String> endPredecessors = activitiesPERT_CPM.get("END").getPrevActIds();
        for (String endPredecessor : endPredecessors) {
            Activity activity = activitiesPERT_CPM.get(endPredecessor);
            path.add(endActivity);
            path.add(activity);
            findCriticalPathsRec(activity, path, criticalPaths);
            path.clear();
        }
        // The list will be inverted so it goes from start to end
        for (List<Activity> criticalPath : criticalPaths.values()) {
            Collections.reverse(criticalPath);
        }
        return criticalPaths;
    }

    private void findCriticalPathsRec(Activity activity, List<Activity> path, LinkedHashMap<Integer, List<Activity>> criticalPaths) {
        if (activity.getSlack() != 0) {
            return;
        }
        if (activity.getActId().equals("START")) {
            List<Activity> pathCopy = new ArrayList<>(path);
            criticalPaths.put(criticalPaths.size() + 1, pathCopy);
            return;
        }
        List<String> predecessors = activity.getPrevActIds();
        for (String predecessor : predecessors) {
            Activity predecessorActivity = activitiesPERT_CPM.get(predecessor);
            path.add(predecessorActivity);
            findCriticalPathsRec(predecessorActivity, path, criticalPaths);
            path.remove(predecessorActivity);
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
        if (!activitiesPERT_CPM.containsKey(actId) || !activitiesPERT_CPM.containsKey(prevActId)) {
            throw new IllegalArgumentException("One or both activities do not exist in the PERT/CPM graph.");
        }

        String nodeLabel = actId + " (" + activities.get(actId).getDurationWithUnit() + ")";
        String dependencyLabel = prevActId + " (" + activities.get(prevActId).getDurationWithUnit() + ")";
        pert_CPM.addEdge(dependencyLabel, nodeLabel, "0");

        // Ensure the prevActIds list is modifiable
        Activity activity = activitiesPERT_CPM.get(actId);
        if (!(activity.getPrevActIds() instanceof ArrayList)) {
            activity.setPrevActIds(new ArrayList<>(activity.getPrevActIds()));
        }
        activity.getPrevActIds().add(prevActId);

        Activity originalActivity = activities.get(actId);
        if (!(originalActivity.getPrevActIds() instanceof ArrayList)) {
            originalActivity.setPrevActIds(new ArrayList<>(originalActivity.getPrevActIds()));
        }
        originalActivity.getPrevActIds().add(prevActId);
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
        activitiesPERT_CPM.remove(actId);
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
        activitiesPERT_CPM.get(actId).getPrevActIds().remove(prevActId);
        activities.get(actId).getPrevActIds().remove(prevActId);
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