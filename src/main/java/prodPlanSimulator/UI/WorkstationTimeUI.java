package prodPlanSimulator.UI;

import prodPlanSimulator.UI.Menu.MenuItem;
import prodPlanSimulator.UI.Utils.Utils;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkstationTimeUI implements Runnable {
    HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();
    @Override
    public void run() {
        map.listWorkstationsByAscOrder();
        Utils.goBackAndWait();
    }
}
