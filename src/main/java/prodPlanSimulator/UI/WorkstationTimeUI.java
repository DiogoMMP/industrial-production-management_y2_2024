package prodPlanSimulator.UI;

import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.domain.Item;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.Simulator;

import java.util.LinkedHashMap;

public class WorkstationTimeUI implements Runnable {
    HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();
    @Override
    public void run() {
        map.listWorkstationsByAscOrder();
        Utils.goBackAndWait();
    }
}
