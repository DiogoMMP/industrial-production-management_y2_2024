package UI.Simulators;

import UI.Menu.SimulationByTimeUI;
import repository.Instances;
import prodPlanSimulator.Simulator;

public class ProcessByTimeUI implements Runnable {
    private Simulator simulator = Instances.getInstance().getSimulator();
    @Override
    public void run() {
        simulator.simulateProcessUS02();
        SimulationByTimeUI mainMenuUITime = new SimulationByTimeUI();
        mainMenuUITime.run();
    }
}
