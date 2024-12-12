package repository;

import prodPlanSimulator.Simulator;
import projectManager.PERT_CPM;
import trees.AVL_BST.BST;
import trees.ProductionTree.ProductionTree;
import trees.heap.HeapPriorityQueue;

public class Instances {

    private HashMap_Items_Machines hashMapItemsWorkstations;
    private HashMap_Items_Machines_Sprint1 hashMapItemsWorkstationsSprint1;
    private static Instances instance;
    private Simulator simulator;
    private BST bst;
    private HeapPriorityQueue heap;
    private OperationsRepository operationsRepository;
    private OperationsMapRepository operationsMapRepository;
    private ItemsRepository itemsRepository;
    private BOORepository booRepository;
    private ProductionTree productionTree;
    private WorkstationRepository workstationRepository;
    private ActivitiesMapRepository activitiesMapRepository;
    private PERT_CPM pert_CPM;
    private OrdersRepository ordersRepository;

    private Instances() {
        hashMapItemsWorkstations = new HashMap_Items_Machines();
        hashMapItemsWorkstationsSprint1 = new HashMap_Items_Machines_Sprint1();
        simulator = new Simulator();
        bst = new BST();
        heap = new HeapPriorityQueue();
        operationsRepository = new OperationsRepository();
        operationsMapRepository = new OperationsMapRepository();
        itemsRepository = new ItemsRepository();
        booRepository = new BOORepository();
        productionTree = new ProductionTree();
        workstationRepository = new WorkstationRepository();
        activitiesMapRepository = new ActivitiesMapRepository();
        pert_CPM = new PERT_CPM();
        ordersRepository = new OrdersRepository();
    }

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

    public HashMap_Items_Machines getHashMapItemsWorkstations() {
        return hashMapItemsWorkstations;
    }

    public Simulator getSimulator() {
        return simulator;
    }

    public BST getBst() {
        return bst;
    }

    public HeapPriorityQueue getHeap() {
        return heap;
    }

    public OperationsRepository getOperationsRepository() {
        return operationsRepository;
    }

    public ProductionTree getProductionTree() {
        return productionTree;
    }

    public ItemsRepository getItemsRepository() {
        return itemsRepository;
    }

    public OperationsMapRepository getOperationsMapRepository() {
        return operationsMapRepository;
    }

    public BOORepository getBOORepository() {
        return booRepository;
    }

    public HashMap_Items_Machines_Sprint1 getHashMapItemsWorkstationsSprint1() {
        return hashMapItemsWorkstationsSprint1;
    }

    public BOORepository getBooRepository() {
        return booRepository;
    }

    public WorkstationRepository getWorkstationRepository() {
        return workstationRepository;
    }

    public ActivitiesMapRepository getActivitiesMapRepository() {
        return activitiesMapRepository;
    }

    public PERT_CPM getPERT_CPM() {
        return pert_CPM;
    }

    public OrdersRepository getOrdersRepository() {
        return ordersRepository;
    }

    public void clear() {
        instance = null;
    }
}