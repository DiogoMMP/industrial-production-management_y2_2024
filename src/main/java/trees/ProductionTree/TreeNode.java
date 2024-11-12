package trees.ProductionTree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    T value;
    List<TreeNode<T>> children;

    /**
     * Constructs a tree node with the specified value.
     * @param value the value of the tree node
     */
    public TreeNode(T value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    /**
     * Adds a child to the tree node.
     * @param child the child to add to the tree node
     */
    public void addChild(TreeNode<T> child) {
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
}
