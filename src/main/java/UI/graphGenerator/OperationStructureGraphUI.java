package UI.graphGenerator;

import UI.Utils.Utils;
import graphic_representation.OperationStructureGraph;

import java.io.File;
import java.io.FileNotFoundException;

public class OperationStructureGraphUI implements Runnable {
    private OperationStructureGraph operationStructureGraph = new OperationStructureGraph();

    private static final String OUTPUT_FILE_PATH = "src/main/java/graphic_representation/output/operation_structure_graph.svg";

    public void init() throws FileNotFoundException {
        operationStructureGraph.generateGraph();
        Utils.openInBrowser(new File(OUTPUT_FILE_PATH));
    }

    @Override
    public void run() {
        boolean success = false;
        while (!success) {
            try {
                init();
                success = true;
            } catch (FileNotFoundException e) {
                System.err.println("Error: File not found. Please check the file path and try again.");
            }
        }
    }
}
