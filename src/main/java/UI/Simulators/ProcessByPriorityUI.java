package UI.Simulators;

import UI.Menu.MainMenuUIPriority;
import repository.Instances;
import prodPlanSimulator.Simulator;

public class ProcessByPriorityUI implements Runnable {
    protected Simulator simulator = Instances.getInstance().getSimulator();
    @Override
    public void run() {
        simulator.simulateProcessUS08();
        MainMenuUIPriority mainMenuUIPriority = new MainMenuUIPriority();
        mainMenuUIPriority.run();
    }
}
