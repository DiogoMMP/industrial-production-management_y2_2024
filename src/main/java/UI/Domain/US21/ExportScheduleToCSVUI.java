package UI.Domain.US21;

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

        // Export the schedule to a CSV file
        pertCpm.exportScheduleToCSV(OUTPUTPATH);

        // Ask the user if they want to open the generated CSV file
        if (Utils.confirm("Do you want to open the generated CSV file in the default application? (Y/N)")) {
            Utils.openInEditor(OUTPUTPATH);  // Open the generated CSV file in the default application
        }

        Utils.goBackAndWait();

    }

}

