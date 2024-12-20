package UI.Domain.USLP.US08;

import UI.Utils.Utils;
import prodPlanSimulator.Simulator;
import repository.Instances;

import java.io.File;

public class exportToCSVUI implements Runnable {

    private Simulator simulator = Instances.getInstance().getSimulator();

    @Override
    public void run() {

        System.out.println(Utils.BOLD + Utils.CYAN + "\n\n--- Export Simulation to CSV ---------------------------\n" + Utils.RESET);

        File file = new File("machineSupervisor/ARQCP/SPRINT3/UI/Files/simulation.csv");

        try {
            simulator.exportToFile();
            System.out.println(Utils.GREEN + "Simulation exported to file." + Utils.RESET);
            Utils.openInExcel(file);
            Utils.goBackAndWait();
        } catch (Exception e) {
            System.out.println(Utils.RED + "Error: " + e.getMessage() + Utils.RESET);
            Utils.goBackAndWait();
        }
    }
}
