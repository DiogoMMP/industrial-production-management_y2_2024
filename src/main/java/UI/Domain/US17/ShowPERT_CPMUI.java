package UI.Domain.US17;

import graph.map.MapGraph;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import projectManager.CalculateTimes;
import projectManager.PERT_CPM;
import UI.Utils.Utils;
import graph.Edge;
import repository.Instances;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class ShowPERT_CPMUI implements Runnable {

    private static final File OUTPUTPATH = new File("src/main/java/projectManager/output/pert_cpm.svg");

    @Override
    public void run() {
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();  // Get the PERT_CPM instance
        pertCpm.buildPERT_CPM();  // Build the graph
        displayPERT_CPM(pertCpm);  // Display the graph
        MapGraph<String, String> pertCpmGraph = pertCpm.getPert_CPM();
        CalculateTimes calculateTimes = new CalculateTimes();  // Calculate the times
        calculateTimes.calculateTimes();  // Calculate the times
        generateGraph(pertCpmGraph);  // Generate the graph
        Utils.goBackAndWait();  // Wait for the user's action to go back
    }

    /**
     * Displays the PERT/CPM Graph with arrows between the activities.
     *
     * @param pertCpm The PERT_CPM instance containing the activities and dependencies.
     */
    public void displayPERT_CPM(PERT_CPM pertCpm) {
        System.out.println("\n\n--- PERT/CPM Graph ------------");
        StringBuilder builder = new StringBuilder();
        Set<String> visited = new HashSet<>();
        displayPERT_CPMHelper("START", builder, visited, pertCpm);  // Start displaying from the "START" node
        System.out.println(builder);
    }

    /**
     * Recursive method to build a display of the graph with arrows.
     *
     * @param node     The current node.
     * @param builder  StringBuilder to accumulate the result.
     * @param visited  Set to avoid cycles in the graph.
     * @param pertCpm  The PERT_CPM instance containing the graph data.
     */
    private void displayPERT_CPMHelper(String node, StringBuilder builder, Set<String> visited, PERT_CPM pertCpm) {
        if (visited.contains(node)) {
            return; // Avoid cycles in the graph.
        }
        visited.add(node);

        // If the node is "END", we don't need to display outgoing edges, but ensure it's printed correctly
        if (node.equals("END")) {
            return;  // Exit here since END has no outgoing edges.
        }

        // Add the node label
        builder.append(node);

        // Check if the node has outgoing edges (dependencies)
        Collection<Edge<String, String>> outgoingEdges = pertCpm.getPert_CPM().outgoingEdges(node);
        if (!outgoingEdges.isEmpty()) {
            builder.append(" -> ");
            boolean first = true;
            // Add the dependencies (arrows)
            for (Edge<String, String> edge : outgoingEdges) {
                if (!first) builder.append(", "); // Add a comma between multiple destinations
                builder.append(edge.getVDest());
                first = false;
            }
            builder.append("\n");

            // Recursively process the next nodes (dependencies)
            for (Edge<String, String> edge : outgoingEdges) {
                displayPERT_CPMHelper(edge.getVDest(), builder, visited, pertCpm);
            }
        } else {
            builder.append("\n"); // If no outgoing edges, just add a new line.
        }
    }

    /**
     * Generates the graph and saves it to a file.
     *
     * @param pert_CPM The PERT_CPM graph to generate.
     */
    public void generateGraph(MapGraph<String, String> pert_CPM) {
        MutableGraph graph = mutGraph("PERT_CPM").setDirected(true);
        for (String vertex : pert_CPM.vertices()) {
            // Create nodes with durations included in their labels
            MutableNode node = mutNode(vertex);
            graph.add(node);

            for (Edge<String, String> edge : pert_CPM.outgoingEdges(vertex)) {
                // Create edges without weights (duration is already in the node)
                node.addLink(to(mutNode(edge.getVDest())));
            }
        }
        try {
            // Ensure the directory exists before saving the file
            File outputDirectory = OUTPUTPATH.getParentFile();
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            // Render the graph to a file
            Graphviz.fromGraph(graph).render(Format.SVG).toFile(OUTPUTPATH);
            System.out.println("Graph successfully generated in: " + OUTPUTPATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
