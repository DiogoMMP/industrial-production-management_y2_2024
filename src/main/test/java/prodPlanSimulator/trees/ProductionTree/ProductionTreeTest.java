package prodPlanSimulator.trees.ProductionTree;

import org.junit.Before;
import org.junit.Test;
import prodPlanSimulator.domain.Material;
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
        assertEquals("varnish bench(Quantity: 1)", root.getValue());
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
        productionTree.buildProductionTree( "1006");
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
        productionTree.updateQuantities("1006", 2.0);
        TreeNode<String> root = productionTree.getRoot();
        assertEquals("finished bench (Quantity: 2.0)", root.getChildren().get(0).getValue());

        productionTree.updateQuantities("1006", 1.0);
        assertEquals("finished bench (Quantity: 1.0)", root.getChildren().get(0).getValue());

        productionTree.updateQuantities("1006", 0.0);
        assertEquals("finished bench (Quantity: 0.0)", root.getChildren().get(0).getValue());
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
}