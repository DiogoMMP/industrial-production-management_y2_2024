package prodPlanSimulator.repository;

public class Instances {

    private HashMap_Items_Machines hashMapItemsWorkstations;
    private static volatile Instances instance;
    private Simulator simulator;

    /**
     * Private constructor to avoid client applications to use constructor
     */
    private Instances() {
        hashMapItemsWorkstations = new HashMap_Items_Machines();
        simulator = new Simulator();
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

    public Simulator getSimulator() {
        return simulator;
    }

}
