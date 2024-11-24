package prodPlanSimulator.UI.graphGenerator;


import projectManager.ProductStructureGraphBOM;

import java.io.FileNotFoundException;

public class ProductStructureGraphUI implements Runnable{
    private ProductStructureGraphBOM productStructureGraph = new ProductStructureGraphBOM();
    public void init() throws FileNotFoundException {
        productStructureGraph.generateGraph();
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
