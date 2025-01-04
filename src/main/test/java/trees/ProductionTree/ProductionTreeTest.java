package trees.ProductionTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import domain.Material;
import repository.BOORepository;
import repository.Instances;
import repository.ItemsRepository;
import repository.OperationsMapRepository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

class ProductionTreeTest {
    private ProductionTree productionTree;
    private ItemsRepository itemsRepository = Instances.getInstance().getItemsRepository();
    private OperationsMapRepository operationsMapRepository = Instances.getInstance().getOperationsMapRepository();
    private BOORepository booRepository = Instances.getInstance().getBOORepository();

    private static final String ITEMS_FILE = "items.csv";
    private static final String OPERATIONS_FILE = "operations.csv";
    private static final String BOO_FILE = "boo_v2.csv";

    @BeforeEach
    public void setUp() {
        productionTree = new ProductionTree();
        productionTree.setRoot(new TreeNode<>("varnish bench", NodeType.OPERATION));

        itemsRepository.addItems(ITEMS_FILE);
        operationsMapRepository.addOperations(OPERATIONS_FILE);
        booRepository.addBOOList(BOO_FILE);

    }

    @Test
    void testBuildProductionTree() {
        String mainObjectiveID = "1006";
        TreeNode<String> root = productionTree.buildProductionTree(mainObjectiveID);

        assertNotNull(root, "Root should not be null");
        assertEquals(NodeType.OPERATION, root.getType(), "Root should be an OPERATION node");
        assertTrue(root.getValue().contains("varnish bench"), "Root should be varnish bench operation");
    }

    @Test
    void testProductionTreeStructure() {
        String mainObjectiveID = "1006";
        TreeNode<String> root = productionTree.buildProductionTree(mainObjectiveID);

        // Verify root node
        assertNotNull(root, "Root should not be null");
        assertEquals(1, root.getChildren().size(), "Root should have one child");

        // First level child
        TreeNode<String> finishedBenchNode = root.getChildren().get(0);
        assertEquals("finished bench", finishedBenchNode.getValue().split(" \\(")[0], "First child should be finished bench");
        assertEquals(NodeType.PRODUCT, finishedBenchNode.getType(), "First child should be a PRODUCT node");

        // Verify nested structure depth
        TreeNode<String> currentNode = finishedBenchNode;
        String[] expectedOperations = {
                "assemble bench",
                "fix nut M16 21",
                "drill bench seat",
                "polish bench seat",
                "cut bench seat"
        };

        int depth = 0;
        while (currentNode.getChildren().size() > 0 && depth < expectedOperations.length) {
            currentNode = currentNode.getChildren().get(0);
            if (currentNode.getType() == NodeType.OPERATION) {
                assertTrue(currentNode.getValue().contains(expectedOperations[depth]),
                        "Operation at depth " + depth + " should match expected");
                depth++;
            }
        }
    }

    @Test
    void testCalculateTotalMaterialsAndOperations() {
        String mainObjectiveID = "1006";
        productionTree.buildProductionTree(mainObjectiveID);

        Map<String, Map<String, BigDecimal>> totals = productionTree.calculateTotalMaterialsAndOperations(productionTree.getRoot());

        Map<String, BigDecimal> materialQuantities = totals.get("materialQuantities");

        // Verify specific raw materials
        assertTrue(materialQuantities.containsKey("wood 3cm"), "Should contain wood 3cm");
        assertTrue(materialQuantities.containsKey("wood pole 4cm"), "Should contain wood pole 4cm");
        assertTrue(materialQuantities.containsKey("varnish"), "Should contain varnish");

        // Verify specific material quantities
        assertEquals(0.0576, materialQuantities.get("wood 3cm").doubleValue(), 0.0001, "Wood 3cm quantity should match");
        assertEquals(0.28, materialQuantities.get("wood pole 4cm").doubleValue(), 0.0001, "Wood pole 4cm quantity should match");
        assertEquals(0.125, materialQuantities.get("varnish").doubleValue(), 0.0001, "Varnish quantity should match");
    }

    @Test
    void testGetMaterialQuantityPairs() {
        String mainObjectiveID = "1006";
        productionTree.buildProductionTree(mainObjectiveID);

        List<Map.Entry<Material, BigDecimal>> materialQuantityPairs = productionTree.getMaterialQuantityPairs();

        // Count specific raw materials
        long rawMaterialCount = materialQuantityPairs.stream()
                .filter(pair -> pair.getValue().compareTo(BigDecimal.ZERO) > 0)
                .count();

        assertTrue(rawMaterialCount > 5, "Should have multiple raw materials");

        // Verify specific materials are present
        boolean hasWood3cm = materialQuantityPairs.stream()
                .anyMatch(pair -> pair.getKey().getName().equals("wood 3cm") &&
                        pair.getValue().subtract(new BigDecimal("0.0576")).abs().compareTo(new BigDecimal("0.0001")) < 0);

        boolean hasWoodPole4cm = materialQuantityPairs.stream()
                .anyMatch(pair -> pair.getKey().getName().equals("wood pole 4cm") &&
                        pair.getValue().subtract(new BigDecimal("0.28")).abs().compareTo(new BigDecimal("0.0001")) < 0);

        assertTrue(hasWood3cm, "Should have wood 3cm material");
        assertTrue(hasWoodPole4cm, "Should have wood pole 4cm material");
    }

    @Test
    void testSearchNode() {
        String mainObjectiveID = "1006";
        productionTree.buildProductionTree(mainObjectiveID);

        // Test searching for specific nodes by ID
        Map<String, String> benchSeatNode = productionTree.searchNodeByID("1006");
        assertNotNull(benchSeatNode, "Should find finished bench node");
        assertEquals("Product", benchSeatNode.get("Type"), "Finished bench should be a product");

        Map<String, String> rawBenchSeatNode = productionTree.searchNodeByID("1012");
        assertNotNull(rawBenchSeatNode, "Should find raw bench seat node");
        assertEquals("Component", rawBenchSeatNode.get("Type"), "Raw bench seat should be a component");

        // Test searching for specific nodes by Name
        List<Map<String, String>> woodMaterialNodes = productionTree.searchNodeByName("cut bench leg");
        assertNotNull(woodMaterialNodes, "Should find cut bench leg node(s)");
        assertFalse(woodMaterialNodes.isEmpty(), "Should return at least one result for 'cut bench leg'");

        // Verify the details of the first node found (if needed)
        Map<String, String> firstResult = woodMaterialNodes.get(0);
        assertEquals("Operation", firstResult.get("Type"), "Cut bench leg should be an operation");
    }

}