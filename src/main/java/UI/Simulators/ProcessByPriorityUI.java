package UI.Simulators;

import UI.Menu.SimulationByPriorityUI;
import repository.Instances;
import prodPlanSimulator.Simulator;

public class ProcessByPriorityUI implements Runnable {
    protected Simulator simulator = Instances.getInstance().getSimulator();
    @Override
    public void run() {
        simulator.simulateProcessUS08();
        SimulationByPriorityUI simulationByPriorityUI = new SimulationByPriorityUI();
        simulationByPriorityUI.run();
    }
}
