package trees.ProductionTree;

import prodPlanSimulator.domain.Material;
import prodPlanSimulator.domain.Operation;

import java.util.ArrayList;
import java.util.List;

class TreeNode<T> {
    T value;
    List<TreeNode<T>> children;

    public TreeNode(T value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode<T> child) {
        children.add(child);
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public T getValue() {
        return value;
    }
}
