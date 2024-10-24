package projectManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import static guru.nidi.graphviz.model.Factory.*;

import guru.nidi.graphviz.engine.GraphvizV8Engine;

public class ProductStructureGraph {

    private static final String INPUT_FILE_PATH = "src/main/java/projectManager/input/";
    private static final String OUTPUT_FILE_PATH = "src/main/java/projectManager/output/product_structure.svg";

    public void generateGraph(String csvFileName) {
        String line;
        String csvSplitBy = ";";

        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE_PATH + csvFileName))) {
            MutableGraph graph = mutGraph("Product Structure").setDirected(true);

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                String productId = data[0];
                String partNumber = data[1];
                String description = data[2];
                String quantity = data[3];

                MutableNode productNode = mutNode(productId);
                MutableNode partNode = mutNode(partNumber).add(Label.lines(partNumber, description));

                graph.add(productNode.addLink(to(partNode).with(Label.of(quantity))));
            }

            Path outputPath = FileSystems.getDefault().getPath(OUTPUT_FILE_PATH);
            Graphviz.fromGraph(graph).render(Format.SVG).toFile(outputPath.toFile());

            System.out.println("Graph successfully generated in: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

