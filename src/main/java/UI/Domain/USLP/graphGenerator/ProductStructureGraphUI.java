package UI.Domain.USLP.graphGenerator;


import UI.Utils.Utils;
import graphic_representation.ProductStructureGraph;

import java.io.File;
import java.io.FileNotFoundException;

public class ProductStructureGraphUI implements Runnable{
    private ProductStructureGraph productStructureGraph = new ProductStructureGraph();

    private static final String OUTPUT_FILE_PATH = "src/main/java/graphic_representation/output/product_structure_graph.svg";

    public void init() throws FileNotFoundException {
        productStructureGraph.generateGraph();
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
