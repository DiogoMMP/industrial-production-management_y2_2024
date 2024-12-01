package prodPlanSimulator.trees.ProductionTree;

import org.junit.Before;
import org.junit.Test;
import domain.Material;
import prodPlanSimulator.repository.BOORepository;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.ItemsRepository;
import prodPlanSimulator.repository.OperationsMapRepository;
import trees.ProductionTree.NodeType;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ProductionTreeTest {
    private ProductionTree productionTree;

    private ItemsRepository itemsRepository = Instances.getInstance().getItemsRepository();
    private OperationsMapRepository operationsMapRepository = Instances.getInstance().getOperationsMapRepository();
    private BOORepository booRepository = Instances.getInstance().getBOORepository();


    // Files for testing
    private static final String BOO_FILE = "boo_v2.csv";
    private static final String ITEMS_FILE = "items.csv";
    private static final String OPERATIONS_FILE = "operations.csv";


    @Before
    public void setUp() {
        productionTree = new ProductionTree();
        productionTree.setRoot(new TreeNode<>("finished bench"));

        itemsRepository.addItems(ITEMS_FILE);
        operationsMapRepository.addOperations(OPERATIONS_FILE);
        booRepository.addBOOList(BOO_FILE);

    }

    @Test
    public void testBuildProductionTree() {
        TreeNode<String> root = productionTree.buildProductionTree("1006");
        assertNotNull(root);
        assertEquals("finished bench (Quantity: 1)", root.getValue());
        assertFalse(root.getChildren().isEmpty());
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
        productionTree.buildProductionTree("1006");
        Map<String, String> result = productionTree.searchNode("1006");
        assertNotNull(result);
        assertEquals("Material", result.get("Type"));
        assertTrue(result.get("Description").contains("finished bench"));
    }

    @Test
    public void testCalculateTotalMaterialsAndOperations() {
        productionTree.buildProductionTree("1006");
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
        TreeNode<String> root = new TreeNode<>("finished bench (Quantity: 1)", NodeType.MATERIAL);
        TreeNode<String> operationNode = new TreeNode<>("assemble bench (Quantity: 2)", NodeType.OPERATION);
        root.addChild(operationNode);

        Map<String, Double> materialQuantities = new HashMap<>();
        Map<String, Double> operationTimes = new HashMap<>();
        productionTree.calculateTotals(materialQuantities, operationTimes, root);

        assertTrue(materialQuantities.containsKey("finished bench"));
        assertTrue(operationTimes.containsKey("assemble bench"));
        assertEquals(1.0, materialQuantities.get("finished bench"), 0.001);
        assertEquals(2.0, operationTimes.get("assemble bench"), 0.001);
    }

    @Test
    public void testTraverseTree() {
        TreeNode<String> root = new TreeNode<>("finished bench (Quantity: 1)", NodeType.MATERIAL);
        TreeNode<String> operationNode = new TreeNode<>("assemble bench (Quantity: 2)", NodeType.OPERATION);
        root.addChild(operationNode);

        Map<String, Double> materialQuantities = new HashMap<>();
        Map<String, Double> operationTimes = new HashMap<>();
        productionTree.traverseTree(root, materialQuantities, operationTimes);

        assertTrue(materialQuantities.containsKey("finished bench"));
        assertTrue(operationTimes.containsKey("assemble bench"));
        assertEquals(1.0, materialQuantities.get("finished bench"), 0.001);
        assertEquals(2.0, operationTimes.get("assemble bench"), 0.001);
    }

    @Test
    public void testUpdateQuantities() {
        productionTree.buildProductionTree("1006");
        productionTree.updateQuantities("1014", 2.0);

        Map<String, String> leafNode = productionTree.searchNode("1014");
        assertEquals("varnish (Quantity: 2.0)", leafNode.get("Description"));

        productionTree.updateQuantities("1014", 1.0);
        leafNode = productionTree.searchNode("1014");
        assertEquals("varnish (Quantity: 1.0)", leafNode.get("Description"));

        productionTree.updateQuantities("1014", 0.0);
        leafNode = productionTree.searchNode("1014");
        assertEquals("varnish (Quantity: 0.0)", leafNode.get("Description"));
    }

    @Test
    public void testGetMaterialQuantityPairs() {
        productionTree.buildProductionTree("1006");
        List<Map.Entry<Material, Double>> materialQuantityPairs = productionTree.getMaterialQuantityPairs();
        assertNotNull(materialQuantityPairs);
        assertFalse(((List<?>) materialQuantityPairs).isEmpty());

        // Add specific assertions based on the expected values in your CSV files
        boolean found = false;
        for (Map.Entry<Material, Double> pair : materialQuantityPairs) {
            if (pair.getKey().getName().equals("finished bench")) {
                assertEquals(1.0, pair.getValue(), 0.001);
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testPrintMaterialQuantitiesInAscendingOrder() {
        productionTree.buildProductionTree("1006");
        // Redirect output to a stream to capture it
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        productionTree.printMaterialQuantitiesInAscendingOrder();

        // Check if the output contains the expected material quantities in ascending order
        String output = outContent.toString();
        assertTrue(output.contains("finished bench"));
        // Add more assertions based on the expected order of materials

        // Reset the standard output
        System.setOut(System.out);
    }

    @Test
    public void testPrintMaterialQuantitiesInDescendingOrder() {
        productionTree.buildProductionTree("1006");
        // Redirect output to a stream to capture it
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        productionTree.printMaterialQuantitiesInDescendingOrder();

        // Check if the output contains the expected material quantities in descending order
        String output = outContent.toString();
        assertTrue(output.contains("finished bench"));
        // Add more assertions based on the expected order of materials

        // Reset the standard output
        System.setOut(System.out);
    }

    @Test
    public void testPrioritizeCriticalPath() {
        // Setup the tree with nodes for testing
        TreeNode<String> root = new TreeNode<>("Start Operation (Quantity: 1)", NodeType.OPERATION);
        TreeNode<String> operation1 = new TreeNode<>("Operation 1 (Quantity: 2)", NodeType.OPERATION);
        TreeNode<String> operation2 = new TreeNode<>("Operation 2 (Quantity: 3)", NodeType.OPERATION);
        TreeNode<String> material1 = new TreeNode<>("Material A (Quantity: 5)", NodeType.MATERIAL);

        root.addChild(operation1);
        operation1.addChild(operation2);
        operation2.addChild(material1);

        productionTree.setRoot(root);

        // Test prioritizing the critical path
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        productionTree.prioritizeCriticalPath(root);

        // Verify the output contains the expected critical path
        String output = outContent.toString();
        assertTrue(output.contains("Start Operation (Quantity: 1)"));
        assertTrue(output.contains("Operation: Operation 1 (Quantity: 2)"));
        assertTrue(output.contains("Operation: Operation 2 (Quantity: 3)"));
        assertFalse(output.contains("Material A (Quantity: 5)")); // Materials shouldn't be included

        // Reset standard output
        System.setOut(System.out);
    }

    @Test
    public void testQualityChecksCreation() {
        // Build the tree which should populate the quality check queue
        productionTree.buildProductionTree("1006");

        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // View the quality checks
        productionTree.viewQualityChecksInOrder();

        // Restore original System.out
        System.setOut(originalOut);

        // Get the output and verify it contains expected content
        String output = outContent.toString();
        assertTrue("Output should indicate quality checks", output.contains("Quality Checks in Order of Priority:"));
        assertTrue("Output should contain quality check entries", output.contains("Quality Check:"));
    }

    @Test
    public void testQualityCheckPriorities() {
        // Build the tree
        productionTree.buildProductionTree("1006");

        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // View the quality checks
        productionTree.viewQualityChecksInOrder();

        // Restore original System.out
        System.setOut(originalOut);

        // Get the output and analyze priority order
        String output = outContent.toString();
        String[] lines = output.split("\n");

        int previousPriority = -1;
        boolean prioritiesAreOrdered = true;

        for (String line : lines) {
            if (line.contains("[Priority:")) {
                int priority = extractPriority(line);
                if (previousPriority != -1 && priority < previousPriority) {
                    prioritiesAreOrdered = false;
                    break;
                }
                previousPriority = priority;
            }
        }

        assertTrue("Quality checks should be ordered by priority", prioritiesAreOrdered);
    }

    // Helper method to extract priority from output line
    private int extractPriority(String line) {
        int start = line.indexOf("[Priority:") + 10;
        int end = line.indexOf("]", start);
        return Integer.parseInt(line.substring(start, end).trim());
    }

    @Test
    public void testQualityCheckDepthCalculation() {
        // Build a simple test tree with known depths
        TreeNode<String> root = new TreeNode<>("Root Operation", NodeType.OPERATION);
        TreeNode<String> child1 = new TreeNode<>("Child Operation 1", NodeType.OPERATION);
        TreeNode<String> child2 = new TreeNode<>("Child Operation 2", NodeType.OPERATION);
        TreeNode<String> grandchild = new TreeNode<>("Grandchild Operation", NodeType.OPERATION);

        root.addChild(child1);
        root.addChild(child2);
        child1.addChild(grandchild);

        productionTree.setRoot(root);

        // Calculate depth for each node
        assertEquals(1, productionTree.calculateDepth(root));
        assertEquals(2, productionTree.calculateDepth(child1));
        assertEquals(2, productionTree.calculateDepth(child2));
        assertEquals(3, productionTree.calculateDepth(grandchild));
    }

    @Test
    public void testEmptyQualityChecks() {
        // Create a new empty tree
        ProductionTree emptyTree = new ProductionTree();

        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // View quality checks for empty tree
        emptyTree.viewQualityChecksInOrder();

        // Restore original System.out
        System.setOut(originalOut);

        // Verify output
        String output = outContent.toString();
        assertTrue("Should show empty quality checks", output.contains("Quality Checks in Order of Priority:"));
    }

    @Test
    public void testTraverseCriticalPath() {
        TreeNode<String> root = new TreeNode<>("Start Operation (Quantity: 1)", NodeType.OPERATION);
        TreeNode<String> operation1 = new TreeNode<>("Operation 1 (Quantity: 2)", NodeType.OPERATION);
        TreeNode<String> operation2 = new TreeNode<>("Operation 2 (Quantity: 3)", NodeType.OPERATION);
        TreeNode<String> material1 = new TreeNode<>("Material A (Quantity: 5)", NodeType.MATERIAL);

        root.addChild(operation1);
        operation1.addChild(operation2);
        operation2.addChild(material1);

        productionTree.setRoot(root);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        productionTree.traverseCriticalPath(root);

        String output = outContent.toString();
        assertTrue(output.contains("Start Operation (Quantity: 1)"));
        assertTrue(output.contains("Operation 1 (Quantity: 2)"));
        assertTrue(output.contains("Operation 2 (Quantity: 3)"));
        assertFalse(output.contains("Material A (Quantity: 5)"));

        System.setOut(System.out);
    }

}