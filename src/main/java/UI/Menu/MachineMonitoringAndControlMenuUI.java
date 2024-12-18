package UI.Menu;

import machineSupervisor.MachineController;

public class MachineMonitoringAndControlMenuUI implements Runnable {
    @Override
    public void run() {
        MachineController.machineController();
    }
}
