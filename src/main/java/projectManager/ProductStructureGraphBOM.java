package projectManager;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import prodPlanSimulator.repository.Instances;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;
import trees.ProductionTree.NodeType;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static guru.nidi.graphviz.model.Factory.*;

public class ProductStructureGraphBOM {
    private static final String OUTPUT_FILE_PATH = "src/main/java/projectManager/output/product_structure_BOM.svg";
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    public void generateGraph() {
        MutableGraph graph = mutGraph("Product Structure BOM").setDirected(true);
        TreeNode<String> root = productionTree.getRoot();
        if (root != null) {
            addNodesToGraph(root, graph, null);
        }

        Path outputPath = FileSystems.getDefault().getPath(OUTPUT_FILE_PATH);
        try {
            Graphviz.fromGraph(graph).render(Format.SVG).toFile(outputPath.toFile());
            System.out.println("Graph successfully generated in: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error generating the graph: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addNodesToGraph(TreeNode<String> node, MutableGraph graph, MutableNode parentMaterialNode) {
        if (node.getType() == NodeType.MATERIAL) {
            String value = node.getValue();
            int startIndex = value.indexOf("(Quantity: ");
            int endIndex = value.indexOf(')', startIndex);

            if (startIndex != -1 && endIndex != -1) {
                // Extract node name and quantity
                String name = value.substring(0, startIndex).trim();
                String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');

                // Create a node for the graph
                MutableNode graphNode = mutNode(name).add(Label.lines(name));

                if (node.getChildren().isEmpty()) {
                    // Use a hexagon shape for raw material nodes
                    graphNode.add(Shape.HEXAGON);
                }

                graph.add(graphNode);

                // Add a link with the quantity as a label if there's a parent node
                if (parentMaterialNode != null) {
                    graph.add(parentMaterialNode.addLink(to(graphNode).with(Label.of(quantityStr))));
                }

                // Recursively add children
                for (TreeNode<String> child : node.getChildren()) {
                    addNodesToGraph(child, graph, graphNode);
                }
            }
        } else {
            // Handle other node types (if any)
            for (TreeNode<String> child : node.getChildren()) {
                addNodesToGraph(child, graph, parentMaterialNode);
            }
        }
    }
}
