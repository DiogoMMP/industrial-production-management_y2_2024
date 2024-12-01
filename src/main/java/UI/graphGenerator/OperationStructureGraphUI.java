package UI.graphGenerator;

import graphic_representation.OperationStructureGraph;

import java.io.FileNotFoundException;

public class OperationStructureGraphUI implements Runnable {
    private OperationStructureGraph operationStructureGraph = new OperationStructureGraph();
    public void init() throws FileNotFoundException {
        operationStructureGraph.generateGraph();
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
