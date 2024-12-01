package UI.Simulators;

import UI.Menu.MainMenuUITree;
import repository.Instances;
import prodPlanSimulator.Simulator;

public class ProcessByTreeUI implements Runnable {
    protected Simulator simulator = Instances.getInstance().getSimulator();
    @Override
    public void run() {
        simulator.simulateBOMBOO();
        MainMenuUITree mainMenuUITree = new MainMenuUITree();
        mainMenuUITree.run();
    }
}
