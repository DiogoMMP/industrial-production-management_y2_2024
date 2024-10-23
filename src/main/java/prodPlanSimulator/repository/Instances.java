package prodPlanSimulator.repository;

public class Instances {
    private HashMap_Items_Machines hashMapItemsWorkstations;
    private static volatile Instances instance;

    private Instances() {
        hashMapItemsWorkstations = new HashMap_Items_Machines();
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
}
