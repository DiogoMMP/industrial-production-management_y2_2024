package UI.Domain.USEI.US17;

import graph.map.MapGraph;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import projectManager.CalculateTimes;
import projectManager.PERT_CPM;
import UI.Utils.Utils;
import graph.Edge;
import repository.Instances;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class ShowPERT_CPMUI implements Runnable {

    private static final File OUTPUTPATH = new File("src/main/java/projectManager/output/pert_cpm.svg");

    /**
     * Run the UI
     */
    @Override
    public void run() {
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();  // Get the PERT_CPM instance
        pertCpm.buildPERT_CPM();  // Build the graph
        displayPERT_CPM(pertCpm);  // Display the graph

        MapGraph<String, String> pertCpmGraph = pertCpm.getPert_CPM();

        CalculateTimes calculateTimes = new CalculateTimes();  // Calculate the times
        calculateTimes.calculateTimes();  // Calculate the times

        generateGraph(pertCpmGraph);  // Generate the graph

        // Ask the user if they want to open the generated graph
        if (Utils.confirm(Utils.BOLD + "Do you want to open the generated graph in the default browser? (Y/N)" + Utils.RESET)) {
            Utils.openInBrowser(OUTPUTPATH);  // Open the generated SVG file in the default browser
        }

        Utils.goBackAndWait();  // Wait for the user's action to go back
    }

    /**
     * Displays the PERT/CPM Graph with arrows between the activities.
     *
     * @param pertCpm The PERT_CPM instance containing the activities and dependencies.
     */
    public void displayPERT_CPM(PERT_CPM pertCpm) {
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- PERT/CPM Graph ------------\n" + Utils.RESET);
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

        // Create nodes and edges
        for (String vertex : pert_CPM.vertices()) {
            MutableNode node = mutNode(vertex);
            graph.add(node);

            for (Edge<String, String> edge : pert_CPM.outgoingEdges(vertex)) {
                node.addLink(to(mutNode(edge.getVDest())));
            }
        }

        try {
            // Make sure the directory exists before saving the file
            File outputDirectory = OUTPUTPATH.getParentFile();
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            // Write the temporary DOT file
            File dotFile = new File("graph.dot");
            Graphviz.fromGraph(graph).render(Format.DOT).toFile(dotFile);

            // Now use ProcessBuilder to execute the Graphviz command with timeout
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tsvg", dotFile.getAbsolutePath(), "-o", OUTPUTPATH.getAbsolutePath());
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Set the timeout (in milliseconds)
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(() -> {
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            try {
                future.get(3000000, TimeUnit.MILLISECONDS); // Wait for the process to finish (5 minutes)
            } catch (TimeoutException e) {
                process.destroy(); // End the process if the execution time exceeds the limit
                System.out.println(Utils.RED + "Graph generation timed out!");
            }

            // Close the executor
            executor.shutdown();

            // Check that the graph has been generated correctly
            if (process.exitValue() == 0) {
                System.out.println("\n" + Utils.GREEN + "Graph successfully generated in: " + OUTPUTPATH + Utils.RESET);
            } else {
                System.out.println("Error generating graph!");
            }

            // Delete the temporary DOT file
            dotFile.delete();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
