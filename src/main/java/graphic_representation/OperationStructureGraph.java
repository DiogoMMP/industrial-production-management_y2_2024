package graphic_representation;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import importer_and_exporter.InputFileReader;
import repository.Instances;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;
import trees.ProductionTree.NodeType;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static guru.nidi.graphviz.model.Factory.*;
import static trees.ProductionTree.NodeType.PRODUCT;

public class OperationStructureGraph {

    private static final String OUTPUT_FILE_PATH = "src/main/java/graphic_representation/output/operation_structure_graph.svg";
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    public void generateGraph() {
        MutableGraph graph = mutGraph("Operation Structure");  // No longer directed
        TreeNode<String> root = productionTree.getRoot();

        if (root != null) {
            addNodesToGraph(root, graph, null, null, null, null);
        } else {
            System.out.println("Could not generate diagram - invalid main objective ID");
            return;
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

    private void addNodesToGraph(TreeNode<String> node, MutableGraph graph, MutableNode parentNode,
                                 MutableNode lastOperationNode, TreeNode<String> parentTreeNode, NodeType parentNodeType) {
        String nodeName = sanitizeNodeName(node.getValue());
        MutableNode graphNode;

        // Define the shape based on the node's NodeType
        switch (node.getType()) {
            case OPERATION:
                graphNode = mutNode(nodeName)
                        .add(Label.lines(nodeName))
                        .add(Shape.RECTANGLE)
                        .add(Style.FILLED)
                        .add(Color.named("lightcoral").fill());
                if (lastOperationNode != null) {
                    lastOperationNode.addLink(to(graphNode).with(Label.of(extractQuantity(node.getValue()))));
                }
                lastOperationNode = graphNode; // Update the last operation
                break;
            case PRODUCT:
            case COMPONENT:

                if (node.getType() == PRODUCT) {
                    graphNode = mutNode(nodeName)
                            .add(Label.lines(nodeName))
                            .add(Shape.ELLIPSE)
                            .add(Style.FILLED)
                            .add(Color.named("lightgreen").fill());
                } else {
                    graphNode = mutNode(nodeName)
                            .add(Label.lines(nodeName))
                            .add(Shape.ELLIPSE)
                            .add(Style.FILLED)
                            .add(Color.named("lightblue").fill());
                }

                // Always link products and components to the right of the operation
                if (lastOperationNode != null) {
                    lastOperationNode.addLink(to(graphNode).with(Label.of(extractQuantity(node.getValue()))));
                }
                break;
            case RAW_MATERIAL:
                graphNode = mutNode(nodeName)
                        .add(Label.lines(nodeName))
                        .add(Shape.HEXAGON)
                        .add(Style.FILLED)
                        .add(Color.named("lightyellow").fill());

                if (lastOperationNode != null) {
                    lastOperationNode.addLink(to(graphNode).with(Label.of(extractQuantity(node.getValue())))); // Connect to last operation
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown node type: " + node.getType());
        }

        graph.add(graphNode);

        // Recursively add children to the graph
        for (Object child : node.getChildren()) {
            addNodesToGraph((TreeNode<String>) child, graph, graphNode, lastOperationNode, node, node.getType());
        }
    }

    private String sanitizeNodeName(String name) {
        int quantityIndex = name.indexOf("(Quantity:");
        if (quantityIndex != -1) {
            name = name.substring(0, quantityIndex).trim();
        }
        return name.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
    }

    private String extractQuantity(String value) {
        int start = value.indexOf("(Quantity: ");
        if (start != -1) {
            int end = value.indexOf(")", start);
            if (end != -1) {
                return value.substring(start + 10, end).trim();
            }
        }
        return "1"; // Default quantity if not specified
    }
}
