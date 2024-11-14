import org.junit.Before;
import org.junit.Test;
import trees.ProductionTree.ProductionTree;
import trees.ProductionTree.TreeNode;

import static org.junit.Assert.*;

public class ProductionTreeTest {
    private ProductionTree productionTree;

    @Before
    public void setUp() {
        productionTree = new ProductionTree("Bicycle");
    }

    @Test
    public void testBuildProductionTree() {
        TreeNode<String> root = productionTree.buildProductionTree("boo1.csv", "items1.csv", "operations1.csv");
        assertNotNull(root);
        assertEquals("Build Bicycle", root.getValue());
    }

    @Test
    public void testToIndentedStringForObjective() {
        productionTree.buildProductionTree("boo1.csv", "items1.csv", "operations1.csv");
        String treeString = productionTree.toIndentedStringForObjective();
        assertNotNull(treeString);
        assertTrue(treeString.contains("Build Bicycle"));
    }

    @Test
    public void testGetRoot() {
        TreeNode<String> root = productionTree.getRoot();
        assertNotNull(root);
        assertEquals("Build Bicycle", root.getValue());
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