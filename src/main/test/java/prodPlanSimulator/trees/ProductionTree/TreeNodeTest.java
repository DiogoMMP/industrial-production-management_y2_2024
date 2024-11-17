package prodPlanSimulator.trees.ProductionTree;

import org.junit.Test;
import static org.junit.Assert.*;
import trees.ProductionTree.TreeNode;
import trees.ProductionTree.NodeType;

public class TreeNodeTest{

    @Test
    public void testConstructorWithType() {
        TreeNode<String> node = new TreeNode<>("Test Node", NodeType.OPERATION);
        assertEquals("Test Node", node.getValue());
        assertEquals(NodeType.OPERATION, node.getType());
        assertTrue(node.getChildren().isEmpty());
        assertNull(node.getParent());
        assertNull(node.getOperationParent());
    }

    @Test
    public void testConstructorWithoutType() {
        TreeNode<String> node = new TreeNode<>("Test Node");
        assertEquals("Test Node", node.getValue());
        assertNull(node.getType());
        assertTrue(node.getChildren().isEmpty());
        assertNull(node.getParent());
        assertNull(node.getOperationParent());
    }

    @Test
    public void testAddChild() {
        TreeNode<String> parent = new TreeNode<>("Parent Node", NodeType.OPERATION);
        TreeNode<String> child = new TreeNode<>("Child Node", NodeType.MATERIAL);
        parent.addChild(child);

        assertEquals(1, parent.getChildren().size());
        assertEquals(child, parent.getChildren().get(0));
        assertEquals(parent, child.getParent());
    }

    @Test
    public void testGetChildren() {
        TreeNode<String> parent = new TreeNode<>("Parent Node", NodeType.OPERATION);
        TreeNode<String> child1 = new TreeNode<>("Child Node 1", NodeType.MATERIAL);
        TreeNode<String> child2 = new TreeNode<>("Child Node 2", NodeType.MATERIAL);
        parent.addChild(child1);
        parent.addChild(child2);

        assertEquals(2, parent.getChildren().size());
        assertEquals(child1, parent.getChildren().get(0));
        assertEquals(child2, parent.getChildren().get(1));
    }

    @Test
    public void testGetValue() {
        TreeNode<String> node = new TreeNode<>("Test Node", NodeType.OPERATION);
        assertEquals("Test Node", node.getValue());
    }

    @Test
    public void testGetParent() {
        TreeNode<String> parent = new TreeNode<>("Parent Node", NodeType.OPERATION);
        TreeNode<String> child = new TreeNode<>("Child Node", NodeType.MATERIAL);
        parent.addChild(child);

        assertEquals(parent, child.getParent());
        assertNull(parent.getParent());
    }

    @Test
    public void testGetType() {
        TreeNode<String> node = new TreeNode<>("Test Node", NodeType.OPERATION);
        assertEquals(NodeType.OPERATION, node.getType());
    }

    @Test
    public void testSetType() {
        TreeNode<String> node = new TreeNode<>("Test Node");
        node.setType(NodeType.MATERIAL);
        assertEquals(NodeType.MATERIAL, node.getType());
    }

    @Test
    public void testGetOperationParent() {
        TreeNode<String> node = new TreeNode<>("Test Node", NodeType.OPERATION);
        assertNull(node.getOperationParent());
    }

    @Test
    public void testSetOperationParent() {
        TreeNode<String> node = new TreeNode<>("Test Node", NodeType.OPERATION);
        TreeNode<String> operationParent = new TreeNode<>("Operation Parent", NodeType.OPERATION);
        node.setOperationParent(operationParent);
        assertEquals(operationParent, node.getOperationParent());
    }
}