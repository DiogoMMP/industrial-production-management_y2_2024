package trees.ProductionTree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    T value;
    List<TreeNode<T>> children;
    TreeNode<T> parent;
    TreeNode<T> operationParent;
    NodeType type;

    /**
     * Constructs a tree node with the specified value and type.
     * @param value the value of the tree node
     * @param type the type of the tree node (e.g., NodeType.MATERIAL or NodeType.OPERATION)
     */
    public TreeNode(T value, NodeType type) {
        this.value = value;
        this.children = new ArrayList<>();
        this.parent = null;
        this.operationParent = null;
        this.type = type;
    }

    /**
     * Constructs a tree node with the specified value.
     * @param value the value of the tree node
     */
    public TreeNode(T value){
        this.value = value;
        this.children = new ArrayList<>();
        this.parent = null;
        this.operationParent = null;
        this.type = null;
    }

    /**
     * Adds a child to the tree node.
     * @param child the child to add to the tree node
     */
    public void addChild(TreeNode<T> child) {
        child.parent = this;
        children.add(child);
    }

    /**
     * Returns the children of the tree node.
     * @return the children of the tree node
     */
    public List<TreeNode<T>> getChildren() {
        return children;
    }

    /**
     * Returns the value of the tree node.
     * @return the value of the tree node
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the parent of the tree node.
     * @return the parent of the tree node
     */
    public TreeNode<T> getParent() {
        return parent;
    }

    /**
     * Returns the type of the tree node.
     * @return the type of the tree node
     */
    public NodeType getType() {
        return type;
    }

    /**
     * Sets the type of the tree node.
     * @param nodeType the type of the tree node
     */
    public void setType(NodeType nodeType) {
        this.type = nodeType;
    }

    /**
     * Returns the operation parent of the tree node.
     * @return the operation parent of the tree node
     */
    public TreeNode<T> getOperationParent() {
        return operationParent;
    }

    /**
     * Sets the operation parent of the tree node.
     * @param operationParent the operation parent of the tree node
     */
    public void setOperationParent(TreeNode<T> operationParent) {
        this.operationParent = operationParent;
    }
}