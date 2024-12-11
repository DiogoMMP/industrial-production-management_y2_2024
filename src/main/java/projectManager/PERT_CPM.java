package projectManager;

import com.kitfox.svg.A;
import domain.Activity;
import graph.Edge;
import graph.map.MapGraph;
import graph.map.MapVertex;
import repository.ActivitiesMapRepository;
import repository.Instances;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        pert_CPM.clearOldGraph();
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
            if (activity.getPrevActIds().isEmpty() || activity.getPrevActIds().contains("START")) {
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
     * Builds the PERT/CPM graph without duration.
     */
    public MapGraph<String, String> getPert_CPMWithoutDuration() {
        MapGraph<String, String> pert_CPM_without_duration = new MapGraph<>(true);
        for (String vertex : pert_CPM.vertices()) {
            pert_CPM_without_duration.addVertex(vertex.split(" ")[0]);
        }
        for (String vertex : pert_CPM.vertices()) {
            for (Edge<String, String> edge : pert_CPM.outgoingEdges(vertex)) {
                pert_CPM_without_duration.addEdge(vertex.split(" ")[0], edge.getVDest().split(" ")[0], "0");
            }
        }
        return pert_CPM_without_duration;
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

    /**
     * Recursive method to find the critical paths.
     * @param activity Current activity.
     * @param path Current path.
     * @param criticalPaths Map of critical paths.
     */
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
     * Checks if there are circular dependencies in the graph.
     *
     * @return true if a cycle is detected, false otherwise.
     */
    public boolean hasCircularDependencies() {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String vertex : pert_CPM.vertices()) {
            if (detectCycle(vertex, visited, recursionStack)) {
                return true; // Cycle detected
            }
        }
        return false; // No cycle detected
    }

    /**
     * Helper method to detect a cycle using DFS.
     *
     * @param vertex         Current vertex.
     * @param visited        Set of visited vertices.
     * @param recursionStack Set of vertices in the current DFS path.
     * @return true if a cycle is detected, false otherwise.
     */
    private boolean detectCycle(String vertex, Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(vertex)) {
            return true; // Cycle detected
        }

        if (visited.contains(vertex)) {
            return false; // Already processed, no cycle
        }

        visited.add(vertex);
        recursionStack.add(vertex);

        // Check all adjacent vertices
        for (String neighbor : pert_CPM.adjVertices(vertex)) {
            if (detectCycle(neighbor, visited, recursionStack)) {
                return true; // Cycle detected in subgraph
            }
        }

        recursionStack.remove(vertex);
        return false; // No cycle detected
    }

    /**
     * Validates the graph.
     * @return true if the graph is valid, false otherwise.
     */
    public boolean validateGraph() {
        boolean hasCiruclar=false;
        if (hasCircularDependencies()) {
            hasCiruclar=true;
        }
        else {
            hasCiruclar=false;
        }
    return hasCiruclar;
    }

    /**
     * Performs a topological sort of the graph.
     * @return List of vertices in topological order.
     */
    public List<String> topologicalSort() {

        if (hasCircularDependencies()) {
            throw new IllegalStateException("The graph has circular dependencies.");
        }
        Stack<String> stack = new Stack<>(); // Stack to store the sorted order
        Set<String> visited = new HashSet<>(); // Set to track visited nodes

        // Iterate over all vertices in the graph
        for (String vertex : pert_CPM.vertices()) {
            if (!visited.contains(vertex)) {
                topologicalSortUtil(vertex, visited, stack);
            }
        }

        // Convert the stack into the result list
        List<String> sortedOrder = new ArrayList<>();
        while (!stack.isEmpty()) {
            sortedOrder.add(stack.pop());
        }

        return sortedOrder;
    }

    /**
     * Utility function for DFS and topological sorting.
     *
     * @param vertex  The current vertex being visited.
     * @param visited Set of visited vertices.
     * @param stack   Stack to store the result.
     */
    private void topologicalSortUtil(String vertex, Set<String> visited, Stack<String> stack) {
        // Mark the current vertex as visited
        visited.add(vertex);

        // Recur for all adjacent vertices
        for (String neighbor : pert_CPM.adjVertices(vertex)) {
            if (!visited.contains(neighbor)) {
                topologicalSortUtil(neighbor, visited, stack);
            }
        }

        // Push the current vertex to the stack
        stack.push(vertex);
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

    /**
     * Exports the schedule to a CSV file.
     * @param file File to export the schedule to.
     */
    public void exportScheduleToCSV(File file) {

        CalculateTimes calculateTimes = new CalculateTimes();
        calculateTimes.calculateTimes();
        MapGraph<String, String> pert_CPM_without_duration = getPert_CPMWithoutDuration();

        try (FileWriter writer = new FileWriter(file)) {
            // CSV header
            writer.write("act_id;cost;duration;es;ls;ef;lf;prev_act_id1;...;prev_act_idN\n");

            // Write the data for each activity
            for (String actId : activities.keySet()) {
                Activity activity = activities.get(actId);

                // Get the dependencies of the activity
                Collection<Edge<String, String>> dependencies = pert_CPM_without_duration.incomingEdges(actId);

                // Convert the dependencies to a string
                String dependenciesStr = (dependencies == null || dependencies.isEmpty())
                        ? "-"
                        : dependencies.stream()
                        .map(Edge::getVOrig) // Get the IDs of the dependencies
                        .reduce((a, b) -> a + ", " + b) // Concatenate the IDs
                        .orElse("-");


                // Write the activity data to the file
                writer.write(String.format("%s;%d;%d;%.1f;%.1f;%.1f;%.1f;%s\n",
                        activity.getActId(),
                        activity.getCost(),
                        activity.getDuration(),
                        activity.getEarliestStart(),
                        activity.getLatestStart(),
                        activity.getEarliestFinish(),
                        activity.getLatestFinish(),
                        dependenciesStr
                ));
            }

            System.out.println("Schedule exported successfully to: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error while exporting schedule: " + e.getMessage());
        }
    }

    /**
     * Returns a list of bottleneck activities.
     * Bottleneck activities are those with the highest number of dependent activities
     * or that appear on most paths.
     *
     * @return List of bottleneck activities.
     */
    public List<Activity> getBottleneckActivities() {
        Map<Activity, Integer> dependencyCount = new HashMap<>();
        Map<Activity, Integer> pathCount = new HashMap<>();

        // Initialize counts
        for (Activity activity : activities.values()) {
            dependencyCount.put(activity, 0);
            pathCount.put(activity, 0);
        }

        // Count dependencies
        for (Activity activity : activities.values()) {
            for (String prevActId : activity.getPrevActIds()) {
                Activity prevActivity = activities.get(prevActId);
                if (prevActivity != null) {
                    dependencyCount.put(prevActivity, dependencyCount.get(prevActivity) + 1);
                }
            }
        }

        // Count paths
        LinkedHashMap<Integer, List<Activity>> criticalPaths = findCriticalPaths();
        for (List<Activity> path : criticalPaths.values()) {
            for (Activity activity : path) {
                pathCount.put(activity, pathCount.getOrDefault(activity, 0) + 1);
            }
        }

        // Find maximum counts
        int maxDependencies = Collections.max(dependencyCount.values());
        int maxPaths = Collections.max(pathCount.values());


        // Collect bottleneck activities
        List<Activity> bottleneckActivities = new ArrayList<>();
        for (Activity activity : activities.values()) {
            if (!activity.getActId().equals("START") && !activity.getActId().equals("END")) {
                if ((dependencyCount.get(activity) > 1 && dependencyCount.get(activity) == maxDependencies) || (pathCount.get(activity) == maxPaths && dependencyCount.get(activity) > 1)) {
                    bottleneckActivities.add(activity);
                }
            }
        }

        // Identify predecessors of the "END" node
        List<String> endPredecessors = activitiesPERT_CPM.get("END").getPrevActIds();
        Activity mostCriticalPredecessor = null;
        int maxPredecessorPaths = 0;

        for (String endPredecessor : endPredecessors) {
            Activity activity = activitiesPERT_CPM.get(endPredecessor);
            int pathsThroughPredecessor = pathCount.getOrDefault(activity, 0);
            if (pathsThroughPredecessor > maxPredecessorPaths) {
                maxPredecessorPaths = pathsThroughPredecessor;
                mostCriticalPredecessor = activity;
            }
        }

        // Add the most critical predecessor to the bottleneck activities list
        if (mostCriticalPredecessor != null && !bottleneckActivities.contains(mostCriticalPredecessor)) {
            bottleneckActivities.add(mostCriticalPredecessor);
        }

        return bottleneckActivities;
    }

    /**
     * Simulates delays in specific activities by increasing their durations,
     * and automatically recalculates the critical path, total project duration, and slack times.
     *
     * @param delays A map of activity IDs and the corresponding delay durations to be added.
     */
    public void simulateDelaysAndRecalculate(LinkedHashMap<String, Integer> delays) {
        ActivitiesMapRepository activitiesMapRepository = Instances.getInstance().getActivitiesMapRepository();
        // Apply delays to specified activities
        for (String actId : delays.keySet()) {
            Activity activity_PERT_CPM = activitiesPERT_CPM.get(actId);
            Activity activity = activities.get(actId);
            if (activity != null) {
                int newDuration = activity.getDuration() + delays.get(actId);
                if (newDuration > 0) {
                    activity.setDuration(newDuration);
                    activity_PERT_CPM.setDuration(newDuration);
                }else if(newDuration < 0){
                    throw new IllegalArgumentException("Activity duration must be positive or zero.\n" +
                            "Activity ID: " + actId + "\n" + "Current Duration: " + activity.getDuration() + "\n");
                }else {
                    removeActivityAndRecalculate(actId);
                }
            }
        }

        // Recalculate times
        CalculateTimes calculateTimes = new CalculateTimes();
        calculateTimes.calculateTimes();

        // Recalculate Slack times
        for (Activity activity : activities.values()) {
            activity.calculateSlack();
        }
        activitiesMapRepository.setActivitiesMapRepository(activities);
    }

    // Method to calculate total project duration
    public double calculateTotalProjectDuration() {
        double maxFinishTime = 0.0;
        for (Activity activity : activities.values()) {
            if (activity.getEarliestFinish() > maxFinishTime) {
                maxFinishTime = activity.getEarliestFinish();
            }
        }
        return maxFinishTime;
    }

    public void removeActivityAndRecalculate(String actId) {
        // Remove the activity and its connections
        Activity activity = activities.get(actId);
        if (activity != null) {
            String nodeLabel = actId + " (" + activity.getDurationWithUnit() + ")";
            activities.remove(actId);
            pert_CPM.removeVertex(nodeLabel);
            activitiesPERT_CPM.remove(actId);

            // Remove connections from other activities
            for (Activity otherActivity : activities.values()) {
                otherActivity.getPrevActIds().remove(actId);
                String otherNodeLabel = otherActivity.getActId() + " (" + otherActivity.getDurationWithUnit() + ")";
                pert_CPM.removeEdge(nodeLabel, otherNodeLabel);
            }

            // Recalculate times
            CalculateTimes calculateTimes = new CalculateTimes();
            calculateTimes.calculateTimes();

            // Recalculate Slack times
            for (Activity remainingActivity : activities.values()) {
                remainingActivity.calculateSlack();
            }

            // Update the repository
            ActivitiesMapRepository activitiesMapRepository = Instances.getInstance().getActivitiesMapRepository();
            activitiesMapRepository.setActivitiesMapRepository(activities);
        }
    }
}