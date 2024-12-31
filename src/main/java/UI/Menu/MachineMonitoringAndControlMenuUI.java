package UI.Menu;

import UI.Utils.Utils;


public class MachineMonitoringAndControlMenuUI implements Runnable {

    private static final String PATH = "../sem3-pi-2024-g094/machineSupervisor/ARQCP/SPRINT3";

    @Override
    public void run() {
        Utils.openCodeInC(PATH);
    }
}
