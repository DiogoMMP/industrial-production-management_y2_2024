package prodPlanSimulator.UI.Domain.US16;

import prodPlanSimulator.UI.Menu.MenuItem;
import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.Simulator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SimulateProcessTreeUI implements Runnable {
    private static Simulator simulator = Instances.getInstance().getSimulator();
    public SimulateProcessTreeUI() {
    }

    @Override
    public void run() {
        LinkedHashMap<String,Double> timeOperations = simulator.simulateBOMBOO();
        for (String key : timeOperations.keySet()) {
            System.out.println(key);
        }
        Utils.goBackAndWait();
    }
}
