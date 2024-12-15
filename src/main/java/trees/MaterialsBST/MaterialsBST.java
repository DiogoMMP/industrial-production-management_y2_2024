package trees.MaterialsBST;

import trees.AVL_BST.BST;
import java.util.List;

public class MaterialsBST extends BST {

    /**
     * Node class for MaterialsBST
     */
    private static class Node {
        List<String> materialNames;
        double quantity;
        Node left, right;

        /**
         * Constructor
         * @param materialNames List of material names
         * @param quantity Quantity of material
         */
        Node(List<String> materialNames, double quantity) {
            this.materialNames = materialNames;
            this.quantity = quantity;
            left = right = null;
        }
    }

    private static Node root;

    /**
     * Constructor
     */
    public MaterialsBST() {
        root = null;
    }

    /**
     * Insert a new node in the BST
     * @param materialNames List of material names
     * @param quantity Quantity of material
     */
    public static void insert(List<String> materialNames, double quantity) {
        root = insertRec(root, materialNames, quantity);
    }

    /**
     * Insert a new node in the BST
     * @param root Root node
     * @param materialNames List of material names
     * @param quantity Quantity of material
     * @return Root node
     */
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

    /**
     * Inorder traversal of the BST
     */
    public void inorder() {
        inorderRec(root);
    }

    /**
     * Inorder traversal of the BST
     * @param root Root node
     */
    private void inorderRec(Node root) {
        if (root != null) {
            inorderRec(root.left);

            String materialNames = String.join("\n", root.materialNames)
                    .replace("[", "").replace("]", "");

            String[] materialLines = materialNames.split("\n");
            for (String materialLine : materialLines) {
                System.out.printf("%-25s | %.4f%n", materialLine, root.quantity);
            }

            inorderRec(root.right);
        }
    }

    /**
     * Reverse inorder traversal of the BST
     */
    public void reverseInorder() {
        reverseInorderRec(root);
    }

    /**
     * Reverse inorder traversal of the BST
     * @param root Root node
     */
    private void reverseInorderRec(Node root) {
        if (root != null) {
            reverseInorderRec(root.right);

            String materialNames = String.join("\n", root.materialNames)
                    .replace("[", "").replace("]", "");

            String[] materialLines = materialNames.split("\n");
            for (String materialLine : materialLines) {
                System.out.printf("%-25s | %.4f%n", materialLine, root.quantity);
            }

            reverseInorderRec(root.left);
        }
    }
}