package trees.MaterialsBST;

import trees.AVL_BST.BST;
import java.util.List;

public class MaterialsBST extends BST {

    private static class Node {
        List<String> materialNames;
        double quantity;
        Node left, right;

        Node(List<String> materialNames, double quantity) {
            this.materialNames = materialNames;
            this.quantity = quantity;
            left = right = null;
        }
    }

    private static Node root;

    public MaterialsBST() {
        root = null;
    }

    public static void insert(List<String> materialNames, double quantity) {
        root = insertRec(root, materialNames, quantity);
    }

    private static Node insertRec(Node root, List<String> materialNames, double quantity) {
        if (root == null) {
            root = new Node(materialNames, quantity);
            return root;
        }
        if (quantity < root.quantity) {
            root.left = insertRec(root.left, materialNames, quantity);
        } else if (quantity > root.quantity) {
            root.right = insertRec(root.right, materialNames, quantity);
        } else {
            root.materialNames.addAll(materialNames);
        }
        return root;
    }

    public void inorder() {
        inorderRec(root);
    }

    private void inorderRec(Node root) {
        if (root != null) {
            inorderRec(root.left);
            System.out.println(root.materialNames + ": " + root.quantity);
            inorderRec(root.right);
        }
    }

    public void reverseInorder() {
        reverseInorderRec(root);
    }

    private void reverseInorderRec(Node root) {
        if (root != null) {
            reverseInorderRec(root.right);
            System.out.println(root.materialNames + ": " + root.quantity);
            reverseInorderRec(root.left);
        }
    }
}