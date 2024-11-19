package prodPlanSimulator.UI.Simulators;

import prodPlanSimulator.UI.Menu.MainMenuUITree;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.Simulator;

public class ProcessByTreeUI implements Runnable {
    protected Simulator simulator = Instances.getInstance().getSimulator();
    @Override
    public void run() {
        simulator.simulateBOMBOO();
        MainMenuUITree mainMenuUITree = new MainMenuUITree();
        mainMenuUITree.run();
    }
}
