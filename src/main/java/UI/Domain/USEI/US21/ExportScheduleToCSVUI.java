package UI.Domain.USEI.US21;

import UI.Utils.Utils;
import projectManager.PERT_CPM;
import repository.Instances;

import java.io.File;

public class ExportScheduleToCSVUI implements Runnable {

    private static final File OUTPUTPATH = new File("src/main/java/projectManager/output/schedule.csv");

    @Override
    public void run() {

        // Retrieve the PERT_CPM instance
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();
        Utils.clearConsole();
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Export Project Schedule to CSV ------------\n" + Utils.RESET);

        if (pertCpm.hasCircularDependencies()) {
            System.out.println(Utils.RED + "Error: The project has circular dependencies." + Utils.RESET);
            Utils.goBackAndWait();
            return;
        }

        // Export the schedule to a CSV file
        if (pertCpm.exportScheduleToCSV(OUTPUTPATH)){
            System.out.println("\n" + Utils.GREEN + "Schedule exported successfully to: " + OUTPUTPATH + Utils.RESET);
        }

        // Ask the user if they want to open the generated CSV file
        if (Utils.confirm(Utils.BOLD + "Do you want to open the generated CSV file in the default application? (Y/N)" + Utils.RESET)) {
            Utils.openInExcel(OUTPUTPATH);  // Open the generated CSV file in the default application
        }

        Utils.goBackAndWait();

    }

}

