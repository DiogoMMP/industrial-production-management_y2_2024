package projectManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import static guru.nidi.graphviz.model.Factory.*;

public class ProductStructureGraph {

    private static final String OUTPUT_FILE_PATH = "src/main/java/projectManager/output/product_structure.svg";

    public void generateGraph(String csvFileName) {
        String line;
        String csvSplitBy = ";";

        try (InputStream inputStream = getClass().getResourceAsStream("/" + csvFileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            MutableGraph graph = mutGraph("Product Structure").setDirected(true);

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                if (data.length < 4) {
                    System.err.println("Insufficient data on the line: " + line);
                    continue;
                }

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
            System.err.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}