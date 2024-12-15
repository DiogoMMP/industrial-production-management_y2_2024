package graphic_representation;

import UI.Utils.Utils;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import repository.Instances;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;
import trees.ProductionTree.NodeType;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static guru.nidi.graphviz.model.Factory.*;

public class ProductStructureGraph {
    private static final String OUTPUT_FILE_PATH = "src/main/java/graphic_representation/output/product_structure_graph.svg";
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    public void generateGraph() {
        MutableGraph graph = mutGraph("Product Structure Graph").setDirected(false);
        TreeNode<String> root = productionTree.getRoot();
        if (root != null) {
            addNodesToGraph(root, graph, null);
        }

        Path outputPath = FileSystems.getDefault().getPath(OUTPUT_FILE_PATH);
        try {
            Graphviz.fromGraph(graph).render(Format.SVG).toFile(outputPath.toFile());
            System.out.println("\n" + Utils.GREEN + "Graph successfully generated in: " + outputPath + Utils.RESET);
        } catch (IOException e) {
            System.err.println("Error generating the graph: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addNodesToGraph(TreeNode<String> node, MutableGraph graph, MutableNode parentMaterialNode) {
        if (node.getType() == NodeType.PRODUCT || node.getType() == NodeType.COMPONENT || node.getType() == NodeType.RAW_MATERIAL) {
            String value = node.getValue();
            int startIndex = value.indexOf("(Quantity: ");
            int endIndex = value.indexOf(')', startIndex);

            if (startIndex != -1 && endIndex != -1) {
                // Extract node name and quantity
                String name = value.substring(0, startIndex).trim();
                String quantityStr = value.substring(startIndex + 11, endIndex).trim().replace(',', '.');

                // Create a node for the graph
                MutableNode graphNode = null;

                // Define the shape and color based on the node type
                switch (node.getType()) {
                    case PRODUCT:
                        graphNode = mutNode(name)
                                .add(Label.lines(name))
                                .add(Shape.ELLIPSE)
                                .add(Style.FILLED)
                                .add(Color.named("lightgreen").fill());
                        break;
                    case COMPONENT:
                        graphNode = mutNode(name)
                                .add(Label.lines(name))
                                .add(Shape.ELLIPSE)
                                .add(Style.FILLED)
                                .add(Color.named("lightblue").fill());
                        break;
                    case RAW_MATERIAL:
                        graphNode = mutNode(name)
                                .add(Label.lines(name))
                                .add(Shape.HEXAGON)
                                .add(Style.FILLED)
                                .add(Color.named("lightyellow").fill());
                        break;
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
