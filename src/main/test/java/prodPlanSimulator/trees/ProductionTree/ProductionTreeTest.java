package prodPlanSimulator.trees.ProductionTree;

import org.junit.Before;
import org.junit.Test;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;

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
        productionTree.setRoot(new TreeNode<>("Build finished bench"));
    }

    @Test
    public void testBuildProductionTree() {
        TreeNode<String> root = productionTree.buildProductionTree(BOO_FILE, ITEMS_FILE, OPERATIONS_FILE, "1006");
        assertNotNull(root);
        assertEquals("Build finished bench", root.getValue());
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
        assertEquals("Build finished bench", root.getValue());
    }

    @Test
    public void testTreeNodeAddChild() {
        TreeNode<String> root = new TreeNode<>("Root");
        TreeNode<String> child = new TreeNode<>("Child");
        root.addChild(child);
        assertEquals(1, root.getChildren().size());
        assertEquals("Child", root.getChildren().get(0).getValue());
    }

    @Test
    public void testTreeNodeGetChildren() {
        TreeNode<String> root = new TreeNode<>("Root");
        TreeNode<String> child1 = new TreeNode<>("Child1");
        TreeNode<String> child2 = new TreeNode<>("Child2");
        root.addChild(child1);
        root.addChild(child2);
        assertEquals(2, root.getChildren().size());
    }

    @Test
    public void testTreeNodeGetValue() {
        TreeNode<String> node = new TreeNode<>("Value");
        assertEquals("Value", node.getValue());
    }
}