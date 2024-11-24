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

public class OperationStructureGraph {

    private static final String OUTPUT_FILE_PATH = "src/main/java/projectManager/output/operation_structure_graph.svg";
    private ProductionTree productionTree = Instances.getInstance().getProductionTree();

    private enum NodeTypeGraph {
        OPERATION,
        MATERIAL,
        RAW_MATERIAL;
    }

    public void generateGraph() {
        MutableGraph graph = mutGraph("Operation Structure").setDirected(false);
        TreeNode root = productionTree.getRoot();

        if (root != null) {
            addNodesToGraph(root, graph, null, null);
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

    private void addNodesToGraph(TreeNode node, MutableGraph graph, MutableNode parentNode, MutableNode lastOperationNode) {
        String nodeName = sanitizeNodeName(node.getValue().toString());
        MutableNode graphNode;
        NodeTypeGraph nodeTypeGraph = getNodeTypeGraph(node);

        // Define a forma baseada no tipo de nó
        switch (nodeTypeGraph) {
            case OPERATION:
                graphNode = mutNode(nodeName)
                        .add(Label.lines(nodeName))
                        .add(Shape.RECTANGLE);

                if (lastOperationNode != null) {
                    lastOperationNode.addLink(to(graphNode).with(Label.of(extractQuantity(node.getValue().toString()))));
                }

                lastOperationNode = graphNode; // Atualiza a última operação
                break;
            case MATERIAL:
                graphNode = mutNode(nodeName)
                        .add(Label.lines(nodeName))
                        .add(Shape.HEXAGON);

                // Materiais ligam-se à última operação
                if (lastOperationNode != null) {
                    lastOperationNode.addLink(to(graphNode).with(Label.of(extractQuantity(node.getValue().toString()))));
                }
                break;
            case RAW_MATERIAL:
                graphNode = mutNode(nodeName)
                        .add(Label.lines(nodeName))
                        .add(Shape.ELLIPSE);

                // Materiais brutos ligam-se à última operação
                if (lastOperationNode != null) {
                    lastOperationNode.addLink(to(graphNode).with(Label.of(extractQuantity(node.getValue().toString()))));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown node type: " + nodeTypeGraph);
        }

        graph.add(graphNode);

        // Ligações entre operações e materiais
        if (parentNode != null && nodeTypeGraph != NodeTypeGraph.MATERIAL && nodeTypeGraph != NodeTypeGraph.RAW_MATERIAL) {
            parentNode.addLink(to(graphNode).with(Label.of(extractQuantity(node.getValue().toString()))));
        }

        for (Object child : node.getChildren()) {
            addNodesToGraph((TreeNode) child, graph, graphNode, lastOperationNode);
        }

    }

    private NodeTypeGraph getNodeTypeGraph(TreeNode node) {
        if (node.getType() == NodeType.OPERATION) {
            return NodeTypeGraph.OPERATION;
        } else if (isRawMaterial(node)) {
            return NodeTypeGraph.RAW_MATERIAL;
        } else {
            return NodeTypeGraph.MATERIAL;
        }
    }

    private boolean isRawMaterial(TreeNode node) {
        return node.getChildren().isEmpty(); // Materiais brutos não têm filhos
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
