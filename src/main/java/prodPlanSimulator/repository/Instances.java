package prodPlanSimulator.repository;

import trees.AVL_BST.AVL;
import trees.AVL_BST.BST;
import trees.heap.HeapPriorityQueue;

public class Instances {

    private HashMap_Items_Machines hashMapItemsWorkstations;
    private static volatile Instances instance;
    private Simulator simulator;
    private BST bst;
    private HeapPriorityQueue heap;

    /**
     * Private constructor to avoid client applications to use constructor
     */
    private Instances() {
        hashMapItemsWorkstations = new HashMap_Items_Machines();
        simulator = new Simulator();
        bst = new BST();
        heap = new HeapPriorityQueue();
    }

    /**
     * Static method to get instance.
     */
    public static Instances getInstance() {
        if (instance == null) {
            synchronized (Instances.class) {
                if (instance == null) {
                    instance = new Instances();
                }
            }
        }
        return instance;
    }

    /**
     * Get the HashMap of Items and Workstations
     * @return HashMap of Items and Workstations
     */
    public HashMap_Items_Machines getHashMapItemsWorkstations() {
        return hashMapItemsWorkstations;
    }

    /**
     * Get the Simulator
     * @return Simulator
     */
    public Simulator getSimulator() {
        return simulator;
    }

    /**
     * Get the BST tree which is a binary search tree
     * @return BST tree
     */
    public BST getBst() {
        return bst;
    }

    /**
     * Get the HeapPriorityQueue
     * @return HeapPriorityQueue
     */
    public HeapPriorityQueue getHeap() {
        return heap;
    }


}
