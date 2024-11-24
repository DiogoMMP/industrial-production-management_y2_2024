/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trees.AVL_BST;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DEI-ESINF
 * @param <E>
 */
public class AVL <E extends Comparable<E>> extends BST<E> {

    private Node<E> latestInsertedNode;
    /**
     * Returns the balance factor of a given node
     * @param node node to calculate the balance factor
     * @return balance factor of the node
     */
    private int balanceFactor(Node<E> node){
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
    private Node<E> rightRotation(Node<E> node){
        if (node == null) {
            return null;
        }
        if (node.getLeft() == null) {
            return node;
        }

        Node<E> newRoot = node.getLeft();
        node.setLeft(newRoot.getRight());
        newRoot.setRight(node);
        return newRoot;
    }

    /**
     * Performs a left rotation of the subtree rooted at node
     * @param node root of the subtree to be rotated
     * @return  the new root of the subtree
     */
    private Node<E> leftRotation(Node<E> node){
        if (node == null) {
            return null;
        }
        if (node.getRight() == null) {
            return node;
        }

        Node<E> newRoot = node.getRight();
        node.setRight(newRoot.getLeft());
        newRoot.setLeft(node);
        return newRoot;
    }

    /**
     * Performs two rotations in the subtree rooted at node
     * @param node root of the subtree to be rotated
     * @return the new root of the subtree
     */
    private Node<E> twoRotations(Node<E> node){
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

    /**
     * Balances the node
     * @param node node to be balanced
     * @return the new root of the subtree
     */
    private Node<E> balanceNode(Node<E> node)
    {
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

    /**
     * Inserts an element in the AVL tree
     * @param element element to be inserted
     */
    @Override
    public void insert(E element){
        root = insert(element, root);
    }

    private Node<E> insert(E element, Node<E> node){
        if (node == null) {
            latestInsertedNode = new Node<>(element, null, null);
            return latestInsertedNode;
        }
        if (element.compareTo(node.getElement()) < 0) {
            node.setLeft(insert(element, node.getLeft()));
        } else {
            node.setRight(insert(element, node.getRight()));
        }
        return balanceNode(node);
    }

    /**
     * Removes an element from the AVL tree
     * @param element element to be removed
     */
    @Override  
    public void remove(E element){
        root = remove(element, root());
    }

    private Node<E> remove(E element, BST.Node<E> node) {
        if (node == null) {
            return null;
        }
        if (element.compareTo(node.getElement()) == 0) {
            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            }
            if (node.getLeft() == null) {
                return node.getRight();
            }
            if (node.getRight() == null) {
                return node.getLeft();
            }
            E min = smallestElement(node.getRight());
            node.setElement(min);
            node.setRight(remove(min, node.getRight()));
        } else if (element.compareTo(node.getElement()) < 0) {
            node.setLeft(remove(element, node.getLeft()));
        } else {
            node.setRight(remove(element, node.getRight()));
        }
        return balanceNode(node);
    }

    public void printInOrder() {
        inOrderTraversal(root);
    }
    
    /**
     * Returns the height of the AVL tree
     * @return height of the AVL tree
     */
    public boolean equals(Object otherObj) {

        if (this == otherObj) 
            return true;

        if (otherObj == null || this.getClass() != otherObj.getClass())
            return false;

        AVL<E> second = (AVL<E>) otherObj;
        return equals(root, second.root);
    }

    public List<E> inOrderTraversal(Node<E> node) {
        List<E> elements = new ArrayList<>();
        inOrderTraversal(node, elements);
        return elements;
    }



    private void inOrderTraversal(Node<E> node, List<E> elements) {
        if (node != null) {
            inOrderTraversal(node.getLeft(), elements);
            elements.add(node.getElement());
            inOrderTraversal(node.getRight(), elements);
        }
    }

    /**
     * Performs a post-order traversal of the AVL tree
     * @return iterable collection of elements in post-order
     */
    public Iterable<E> posOrder() {
        List<E> snapshot = new ArrayList<>();
        if (root != null) {
            posOrderSubtree(root, snapshot);
        }
        return snapshot;
    }

    /**
     * Adds elements of the subtree rooted at Node node to the given
     * snapshot using a post-order traversal
     * @param node Node serving as the root of a subtree
     * @param snapshot a list to which results are appended
     */
    private void posOrderSubtree(Node<E> node, List<E> snapshot) {
        if (node == null) return;
        posOrderSubtree(node.getLeft(), snapshot);
        posOrderSubtree(node.getRight(), snapshot);
        snapshot.add(node.getElement());
    }

    /**
     * Compares two AVL trees for equality
     * @param root1 node of the first AVL tree
     * @param root2 node of the second AVL tree
     * @return true if the AVL trees are equal, false otherwise
     */
    public boolean equals(Node<E> root1, Node<E> root2) {
        if (root1 == null && root2 == null) 
           return true;
        else if (root1 != null && root2 != null) {
            if (root1.getElement().compareTo(root2.getElement()) == 0) {
                return equals(root1.getLeft(), root2.getLeft())
                        && equals(root1.getRight(), root2.getRight());
            } else  
                return false; 
        }
        else return false;
    }

    /**
     * Searches for an element in the AVL tree
     * @param element element to search for
     * @return the element if it is found, null otherwise
     */
    public BOO search(E element) {
        return search(root, element);
    }

    private BOO search(Node<E> node, E element) {
        if (node == null) {
            return null;
        }
        if (element.compareTo(node.getElement()) == 0) {
            return (BOO) node.getElement();
        }
        if (element.compareTo(node.getElement()) < 0) {
            return search(node.getLeft(), element);
        }
        return search(node.getRight(), element);
    }

    public Node<E> getRoot() {
        return root();
    }

    public E getElem(Node<E> node) {
        return node.getElement();
    }

    public Node<E> getLatestInsertedNode() {
        return latestInsertedNode;
    }
}