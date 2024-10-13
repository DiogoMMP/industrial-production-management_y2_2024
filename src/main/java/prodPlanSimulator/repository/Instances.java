package prodPlanSimulator.repository;

public class Instances {
    private HashMap_Items_Machines HashMap_Items_Machines;
    private static Instances instance;

    public Instances() {
        HashMap_Items_Machines = new HashMap_Items_Machines();
        HashMap_Items_Machines.addAll();
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


    public HashMap_Items_Machines getHashMap_Items_Machines() {
        return HashMap_Items_Machines;
    }


}
