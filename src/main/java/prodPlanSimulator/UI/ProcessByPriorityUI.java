package prodPlanSimulator.UI;

import prodPlanSimulator.UI.Menu.MainMenuUIPriority;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.Simulator;

public class ProcessByPriorityUI implements Runnable {
    private Simulator simulator = Instances.getInstance().getSimulator();
    @Override
    public void run() {
        simulator.simulateProcessUS08();
        MainMenuUIPriority mainMenuUIPriority = new MainMenuUIPriority();
        mainMenuUIPriority.run();
    }
}
