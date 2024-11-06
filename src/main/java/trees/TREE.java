
package trees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * @author DEI-ESINF
 * @param <E>
 */

public class TREE<E extends Comparable<E>> extends BST<E> {

    /*
     * @param element A valid element within the tree
     * @return the path to a given element in the tree
     */
    public List<E> path(E elem) {
        if (elem == null) {
            return null;
        }
        List<E> path = new ArrayList<>();
        Node<E> current = root;
        while (current != null) {
            path.add(current.getElement());
            if (elem.compareTo(current.getElement()) == 0) {
                return path;
            } else if (elem.compareTo(current.getElement()) < 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }
        return path;
    }

    /*
     * @return the set of the leaf node elements of the tree
     */
    public Set<E> leafs() {
        Set<E> leafs = new HashSet<>();
        leafs(root, leafs);
        return leafs;
    }

    private void leafs(Node<E> node, Set<E> leafs) {
        if (node == null) {
            return;
        }
        if (node.getLeft() == null && node.getRight() == null) {
            leafs.add(node.getElement());
        }
        leafs(node.getLeft(), leafs);
        leafs(node.getRight(), leafs);
    }

    /*
     * @return an array with the minimum and the maximum values of the tree
     */
    public E[] range() {
        if (root == null) {
            return null;
        }
        E min = root.getElement();
        E max = root.getElement();
        Node<E> current = root;
        while (current.getLeft() != null) {
            current = current.getLeft();
            min = current.getElement();
        }
        current = root;
        while (current.getRight() != null) {
            current = current.getRight();
            max = current.getElement();
        }
        return (E[]) new Comparable[]{min, max};
    }

    /*
     *  @return the set of elements belonging to the diameter of the BST
     */
    public Set<E> diameter() {
        Set<E> diameter = new HashSet<>();
        diameter(this.root, diameter);
        return diameter;
    }

    private void diameter(Node<E> node, Set<E> diameter) {
        if (node == null) {
            return;
        }
        diameter.add(node.getElement());
        diameter(node.getLeft(), diameter);
        diameter(node.getRight(), diameter);
    }

    /*
     *  @return the previous element of the tree for a given element
     */
    public E findPredecessor(E element) {
        if (element == null) {
            return null;
        }
        Node<E> predecessor = findPredecessor(element, root);
        return predecessor == null ? null : predecessor.getElement();
    }

    private Node<E> findPredecessor(E element, Node<E> node) {
        if (node == null) {
            return null;
        }
        if (element.compareTo(node.getElement()) == 0) {
            if (node.getLeft() != null) {
                Node<E> current = node.getLeft();
                while (current.getRight() != null) {
                    current = current.getRight();
                }
                return current;
            }
            return null;
        }
        if (element.compareTo(node.getElement()) < 0) {
            return findPredecessor(element, node.getLeft());
        }
        Node<E> predecessor = findPredecessor(element, node.getRight());
        return predecessor == null ? node : predecessor;
    }

    /*
     * – verify if the current and tree BST are identical.
     */
    public boolean identical(BST<E> tree) {
        return identical(this.root, tree.root);
    }

    private boolean identical(Node<E> node1, Node<E> node2) {
        if (node1 == null && node2 == null) {
            return true;
        }
        if (node1 == null || node2 == null) {
            return false;
        }
        return node1.getElement().equals(node2.getElement()) && identical(node1.getLeft(), node2.getLeft()) && identical(node1.getRight(), node2.getRight());
    }

    /*
     * – remove all elements in the current BST that are outside the range [low, high]
     */
    public void truncate(E low, E high) {
        root = truncate(root, low, high);
    }

    private Node<E> truncate(Node<E> node, E low, E high) {
        if (node == null) {
            return null;
        }
        if (node.getElement().compareTo(low) < 0) {
            return truncate(node.getRight(), low, high);
        }
        if (node.getElement().compareTo(high) > 0) {
            return truncate(node.getLeft(), low, high);
        }
        node.setLeft(truncate(node.getLeft(), low, high));
        node.setRight(truncate(node.getRight(), low, high));
        return node;
    }

    /*
     *– return true if BST<E> tree is a sub tree of the BST<E>.
     */
    public boolean isSubTree(BST<E> tree) {
        return isSubTree(this.root, tree.root);
    }

    private boolean isSubTree(Node<E> node1, Node<E> node2) {
        if (node1 == null) {
            return false;
        }
        if (node1.getElement().equals(node2.getElement()) && identical(node1, node2)) {
            return true;
        }
        return isSubTree(node1.getLeft(), node2) || isSubTree(node1.getRight(), node2);
    }


    public boolean isSymmetric() {
        if (root == null) {
            return true;
        }
        return isSymmetric((Node<Integer>) root.getLeft(), (Node<Integer>) root.getRight());
    }

    private boolean isSymmetric(Node<Integer> left, Node<Integer> right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        if (!left.getElement().equals(-right.getElement())) {
            return false;
        }
        return isSymmetric(left.getLeft(), right.getRight()) && isSymmetric(left.getRight(), right.getLeft());
    }


    public TREE<E> minimumSubtree(Set<E> elems) {
        Node<E> lca = findLCA(root, elems);
        if (lca == null) {
            return null;
        }
        TREE<E> subtree = new TREE<>();
        subtree.root = lca;
        return subtree;
    }

    private Node<E> findLCA(Node<E> node, Set<E> elems) {
        if (node == null) {
            return null;
        }
        if (elems.contains(node.getElement())) {
            return node;
        }

        Node<E> leftLCA = findLCA(node.getLeft(), elems);
        Node<E> rightLCA = findLCA(node.getRight(), elems);

        if (leftLCA != null && rightLCA != null) {
            return node;
        }

        return leftLCA != null ? leftLCA : rightLCA;
    }


}
