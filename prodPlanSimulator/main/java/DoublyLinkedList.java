/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;


/**
 * @param <E> Generic list element type
 * @author DEI-ISEP
 */
public class DoublyLinkedList<E> implements Iterable<E>, Cloneable {

    // instance variables of the DoublyLinkedList
    private final Node<E> header;     // header sentinel
    private final Node<E> trailer;    // trailer sentinel
    private int size = 0;       // number of elements in the list
    private int modCount = 0;   // number of modifications to the list (adds or removes)

    /**
     * Creates both elements which act as sentinels
     */
    public DoublyLinkedList() {

        header = new Node<>(null, null, null);      // create header
        trailer = new Node<>(null, header, null);   // trailer is preceded by header
        header.setNext(trailer);                    // header is followed by trailer
    }

    /**
     * Returns the number of elements in the linked list
     *
     * @return the number of elements in the linked list
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the list is empty
     *
     * @return true if the list is empty, and false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns (but does not remove) the first element in the list
     *
     * @return the first element of the list
     */
    public E first() {
        if (isEmpty()) {
            return null;
        }
        return header.getNext().getElement();   // first element is beyond header
    }

    /**
     * Returns (but does not remove) the last element in the list
     *
     * @return the last element of the list
     */
    public E last() {
        if (isEmpty()) {
            return null;
        }
        return trailer.getPrev().getElement();    // last element is before trailer
    }

// public update methods

    /**
     * Adds an element e to the front of the list
     *
     * @param e element to be added to the front of the list
     */
    public void addFirst(E e) {
        // place just after the header

        addBetween(e, header, header.getNext());
    }

    /**
     * Adds an element e to the end of the list
     *
     * @param e element to be added to the end of the list
     */
    public void addLast(E e) {
        Node<E> newNode = new Node<>(e, trailer.getPrev(), trailer);
        trailer.getPrev().setNext(newNode);
        trailer.setPrev(newNode);
        size++;  // Increment size whenever a node is added
        modCount++; // Increment modification count
    }




    /**
     * Removes and returns the first element of the list
     *
     * @return the first element of the list
     */
    public E removeFirst() {
        if (isEmpty()) {
            return null;
        }
        return remove(header.getNext());
    }

    /**
     * Removes and returns the last element of the list
     *
     * @return the last element of the list
     */
    public E removeLast() {
        if (isEmpty()) {
            return null;
        }
        return remove(trailer.getPrev());
    }

// private update methods

    /**
     * Adds an element e to the linked list between the two given nodes.
     */
    private void addBetween(E e, Node<E> predecessor, Node<E> successor) {
        if (predecessor == null || successor == null) {
            throw new IllegalArgumentException("Nodes cannot be null.");
        }
        Node<E> newNode = new Node<>(e, predecessor, successor);
        predecessor.setNext(newNode);
        successor.setPrev(newNode);
        size++;
        modCount++;
    }

    /**
     * Removes a given node from the list and returns its content.
     */
    private E remove(Node<E> node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null.");
        }
        Node<E> predecessor = node.getPrev();
        Node<E> successor = node.getNext();
        predecessor.setNext(successor);
        successor.setPrev(predecessor);
        size--;
        modCount++;
        return node.getElement();
    }

    // Overriden methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        DoublyLinkedList<?> other = (DoublyLinkedList<?>) obj;
        if (this.size != other.size) {
            return false;
        }

        Iterator<E> it1 = this.iterator();
        Iterator<?> it2 = other.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            E e1 = it1.next();
            Object e2 = it2.next();
            if (!e1.equals(e2)) {
                return false;
            }
        }
        return !(it1.hasNext() || it2.hasNext());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // Step 1: Create a new instance of DoublyLinkedList using super.clone()
        DoublyLinkedList<E> other = (DoublyLinkedList<E>) super.clone();

        // Step 2: Initialize size and modification count for the new list
        other.size = 0;  // Reset size of the cloned list
        other.modCount = 0;  // Reset modification count of the cloned list

        // Step 3: Iterate through the original list until the last node
        Node<E> current = this.header.getNext();  // Start from the first actual node
        Node<E> lastNode = this.trailer.getPrev();  // Get the last actual node

        while (current != lastNode.getNext()) {  // Iterate until we reach the node after the last node
            other.addLast(current.getElement());  // Add each element to the cloned list
            current = current.getNext();  // Move to the next node
        }

        // Step 4: Return the cloned list
        return other;
    }










    //---------------- nested DoublyLinkedListIterator class ----------------
    private class DoublyLinkedListIterator implements ListIterator<E> {

        private DoublyLinkedList.Node<E> nextNode, prevNode, lastReturnedNode; // node that will be returned using next and prev respectively
        private int nextIndex;  // Index of the next element
        private int expectedModCount;  // Expected number of modifications = modCount;

        public DoublyLinkedListIterator() {
            this.prevNode = header;
            this.nextNode = header.getNext();
            lastReturnedNode = null;
            nextIndex = 0;
            expectedModCount = modCount;
        }

        final void checkForComodification() {  // invalidate iterator on list modification outside the iterator
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }

        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        public E next() throws NoSuchElementException {
            checkForComodification();

            if (!hasNext()) {
                throw new NoSuchElementException("End of list reached.");
            }

            lastReturnedNode = nextNode;
            prevNode = nextNode;
            nextNode = nextNode.getNext();
            nextIndex++;

            return lastReturnedNode.getElement();
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        @Override
        public E previous() throws NoSuchElementException {
            checkForComodification();

            if (!hasPrevious()) {
                throw new NoSuchElementException("Beginning of list reached.");
            }

            nextNode = prevNode;
            lastReturnedNode = prevNode;
            prevNode = prevNode.getPrev();
            nextIndex--;

            return lastReturnedNode.getElement();
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() throws NoSuchElementException {
            checkForComodification();
            if (lastReturnedNode == null) {
                throw new NoSuchElementException("No element to remove.");
            }
            DoublyLinkedList.this.remove(lastReturnedNode);
            if (nextNode == lastReturnedNode) {
                nextNode = nextNode.getNext();
            } else {
                prevNode = prevNode.getPrev();
                nextIndex--;
            }
            lastReturnedNode = null;
            expectedModCount++;
        }

        @Override
        public void set(E e) throws NoSuchElementException {
            if (lastReturnedNode == null) throw new NoSuchElementException();
            checkForComodification();

            lastReturnedNode.setElement(e);
        }

        @Override
        public void add(E e) {
            checkForComodification();
            DoublyLinkedList.this.addBetween(e, prevNode, nextNode);
            prevNode = prevNode.getNext();
            nextIndex++;
            expectedModCount++;
        }
    }    //----------- end of inner DoublyLinkedListIterator class ----------

    //---------------- Iterable implementation ----------------
    @Override
    public Iterator<E> iterator() {
        return new DoublyLinkedListIterator();
    }

    public ListIterator<E> listIterator() {
        return new DoublyLinkedListIterator();
    }

    //---------------- nested Node class ----------------
    private static class Node<E> {

        private E element;      // reference to the element stored at this node
        private Node<E> prev;   // reference to the previous node in the list
        private Node<E> next;   // reference to the subsequent node in the list

        public Node(E element, Node<E> prev, Node<E> next) {
            this.element = element;
            this.prev = prev;
            this.next = next;
        }

        public E getElement() {
            return element;
        }

        public Node<E> getPrev() {
            return prev;
        }

        public Node<E> getNext() {
            return next;
        }

        public void setElement(E element) { // Not on the original interface. Added due to list iterator implementation
            this.element = element;
        }

        public void setPrev(Node<E> prev) {
            this.prev = prev;
        }

        public void setNext(Node<E> next) {
            this.next = next;
        }
    } //----------- end of nested Node class ----------

}
