package UI.Simulators;

import UI.Menu.MainMenuUITime;
import repository.Instances;
import prodPlanSimulator.Simulator;

public class ProcessByTimeUI implements Runnable {
    private Simulator simulator = Instances.getInstance().getSimulator();
    @Override
    public void run() {
        simulator.simulateProcessUS02();
        MainMenuUITime mainMenuUITime = new MainMenuUITime();
        mainMenuUITime.run();
    }
}
