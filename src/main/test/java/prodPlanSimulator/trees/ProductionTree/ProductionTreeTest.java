package prodPlanSimulator.trees.ProductionTree;

import org.junit.Before;
import org.junit.Test;
import trees.ProductionTree.NodeType;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ProductionTreeTest {
    private ProductionTree productionTree;

    // Files for testing
    private static final String BOO_FILE = "boo_v2.csv";
    private static final String ITEMS_FILE = "items.csv";
    private static final String OPERATIONS_FILE = "operations.csv";

    @Before
    public void setUp() {
        productionTree = new ProductionTree();
        productionTree.setRoot(new TreeNode<>("finished bench"));
    }

    @Test
    public void testBuildProductionTree() {
        TreeNode<String> root = productionTree.buildProductionTree(BOO_FILE, ITEMS_FILE, OPERATIONS_FILE, "1006");
        assertNotNull(root);
        assertEquals("finished bench", root.getValue());
        assertFalse(root.getChildren().isEmpty());
    }

    @Test
    public void testToIndentedStringForObjective() {
        productionTree.buildProductionTree(BOO_FILE, ITEMS_FILE, OPERATIONS_FILE, "1006");
        String treeString = productionTree.toIndentedStringForObjective();
        assertNotNull(treeString);
        assertTrue(treeString.contains("finished bench"));
    }

    @Test
    public void testGetRoot() {
        TreeNode<String> root = productionTree.getRoot();
        assertNotNull(root);
        assertEquals("finished bench", root.getValue());
    }

    @Test
    public void testSetRoot() {
        TreeNode<String> newRoot = new TreeNode<>("New Root");
        productionTree.setRoot(newRoot);
        assertEquals(newRoot, productionTree.getRoot());
    }

    @Test
    public void testSearchNode() {
        productionTree.buildProductionTree(BOO_FILE, ITEMS_FILE, OPERATIONS_FILE, "1006");
        Map<String, String> result = productionTree.searchNode("1006");
        assertNotNull(result);
        assertEquals("Material", result.get("Type"));
        assertTrue(result.get("Description").contains("finished bench"));
    }

    @Test
    public void testCalculateTotalMaterialsAndOperations() {
        productionTree.buildProductionTree(BOO_FILE, ITEMS_FILE, OPERATIONS_FILE, "1006");
        TreeNode<String> root = productionTree.getRoot();
        Map<String, Object> totals = productionTree.calculateTotalMaterialsAndOperations(root);

        assertNotNull(totals);
        assertTrue(totals.containsKey("materialQuantities"));
        assertTrue(totals.containsKey("operationTimes"));

        Map<String, Double> materialQuantities = (Map<String, Double>) totals.get("materialQuantities");
        Map<String, Double> operationTimes = (Map<String, Double>) totals.get("operationTimes");

        assertNotNull(materialQuantities);
        assertNotNull(operationTimes);

        // Add specific assertions based on the expected values in your CSV files
        assertTrue(materialQuantities.containsKey("finished bench"));
        assertTrue(operationTimes.containsKey("assemble bench"));
    }

    @Test
    public void testCalculateTotals() {
        TreeNode<String> root = new TreeNode<>("finished bench (1x)", NodeType.MATERIAL);
        TreeNode<String> operationNode = new TreeNode<>("assemble bench (2x)", NodeType.OPERATION);
        root.addChild(operationNode);

        Map<String, Double> materialQuantities = new HashMap<>();
        Map<String, Double> operationTimes = new HashMap<>();
        productionTree.calculateTotals(materialQuantities, operationTimes, root);

        assertEquals(1.0, materialQuantities.get("finished bench"), 0.001);
        assertEquals(2.0, operationTimes.get("assemble bench"), 0.001);
    }

    @Test
    public void testTraverseTree() {
        TreeNode<String> root = new TreeNode<>("finished bench (1x)", NodeType.MATERIAL);
        TreeNode<String> operationNode = new TreeNode<>("assemble bench (2x)", NodeType.OPERATION);
        root.addChild(operationNode);

        Map<String, Double> materialQuantities = new HashMap<>();
        Map<String, Double> operationTimes = new HashMap<>();
        productionTree.traverseTree(root, materialQuantities, operationTimes);

        assertEquals(1.0, materialQuantities.get("finished bench"), 0.001);
        assertEquals(2.0, operationTimes.get("assemble bench"), 0.001);
    }
}