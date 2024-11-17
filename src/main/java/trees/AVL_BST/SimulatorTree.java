package trees.AVL_BST;

public class SimulatorTree extends BST<BOMBOO>{
    /**
     * Returns the balance factor of a given node, in this case a BOMBOO node,
     * @param node node to calculate the balance factor
     * @return balance factor of the node
     */
    private int balanceFactor(Node<BOMBOO> node){
        if (node == null) {
            return 0;
        }
        return height(node.getLeft()) - height(node.getRight());
    }

    /**
     * Performs a right rotation of the subtree rooted at node
     * @param node root of the subtree to be rotated
     * @return the new root of the subtree
     */
    private Node<BOMBOO> rightRotation(Node<BOMBOO> node){
        if (node == null) {
            return null;
        }
        if (node.getLeft() == null) {
            return node;
        }

        Node<BOMBOO> newRoot = node.getLeft();
        node.setLeft(newRoot.getRight());
        newRoot.setRight(node);
        return newRoot;
    }

    /**
     * Performs a left rotation of the subtree rooted at node
     * @param node root of the subtree to be rotated
     * @return  the new root of the subtree
     */
    private Node<BOMBOO> leftRotation(Node<BOMBOO> node){
        if (node == null) {
            return null;
        }
        if (node.getRight() == null) {
            return node;
        }

        Node<BOMBOO> newRoot = node.getRight();
        node.setRight(newRoot.getLeft());
        newRoot.setLeft(node);
        return newRoot;
    }

    private Node<BOMBOO> twoRotations(Node<BOMBOO> node){
        if (node == null) {
            return null;
        }
        if (balanceFactor(node) > 0) {
            node.setLeft(leftRotation(node.getLeft()));
            return rightRotation(node);
        } else {
            node.setRight(rightRotation(node.getRight()));
            return leftRotation(node);
        }
    }

    private Node<BOMBOO> balanceNode(Node<BOMBOO> node){
        if (balanceFactor(node) > 1) {
            if (balanceFactor(node.getLeft()) > 0) {
                return rightRotation(node);
            } else {
                return twoRotations(node);
            }
        } else if (balanceFactor(node) < -1) {
            if (balanceFactor(node.getRight()) < 0) {
                return leftRotation(node);
            } else {
                return twoRotations(node);
            }
        }
        return node;
    }

    private Node<BOMBOO> insert(BOMBOO element, Node<BOMBOO> node) {
        if (node == null) {
            return new Node<>(element, null, null);
        }
        if (element.compareTo(node.getElement()) == 0) {
            node.getElement().incQuantity();
            return node;
        }
        if (element.compareTo(node.getElement()) < 0) {
            node.setLeft(insert(element, node.getLeft()));
            return balanceNode(node);
        }
        node.setRight(insert(element, node.getRight()));
        return balanceNode(node);
    }

    /**
     * Searches for a BOMBOO in the tree
     * @param bomboo BOMBOO to search
     * @return the BOMBOO if found, null otherwise
     */
    public BOMBOO search(BOMBOO bomboo) {
        return search(bomboo, root);
    }

    private BOMBOO search(BOMBOO bomboo, Node<BOMBOO> node) {
        if (node == null) {
            return null;
        }
        if (bomboo.compareTo(node.getElement()) == 0) {
            return node.getElement();
        }
        if (bomboo.compareTo(node.getElement()) < 0) {
            return search(bomboo, node.getLeft());
        }
        return search(bomboo, node.getRight());
    }
}
