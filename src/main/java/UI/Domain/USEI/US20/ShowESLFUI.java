package UI.Domain.USEI.US20;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import domain.Activity;
import jdk.jshell.execution.Util;
import projectManager.PERT_CPM;
import repository.Instances;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ShowESLFUI implements Runnable {

    /**
     * Run UI
     */
    @Override
    public void run() {
        PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();  // Get the PERT_CPM instance
        LinkedHashMap<String, Activity> activitiesPERT_CPM = pertCpm.getActivitiesPERT_CPM();
        List<MenuItem> options = new ArrayList<>();

        for (String activity : activitiesPERT_CPM.keySet()) {
            options.add(new MenuItem("Activity: " + activity, new ShowESLFUI()));
        }

        options.add(new MenuItem("All", new ShowESLFUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Choose the Activity to be Visualized ------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if ((option >= 0) && (option < options.size())) {
                String choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    show(choice, activitiesPERT_CPM);
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1 && !options.get(option).toString().equals("Back"));
    }

    /**
     * This method is used to show the Earliest Start and Latest Finish of an activity
     * @param choice The activity to be visualized
     * @param activitiesPERT_CPM The activities of the PERT_CPM
     */
    private void show(String choice, LinkedHashMap<String, Activity> activitiesPERT_CPM) {
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Show Earliest Time and Latest Finish ------------" + Utils.RESET);

        if (choice.equals("All")) {
            for (String activity : activitiesPERT_CPM.keySet()) {
                System.out.println("\n\n" + Utils.BOLD + "--- Activity: " + activity + " ------------" + Utils.RESET);
                System.out.println("ES: " + activitiesPERT_CPM.get(activity).getEarliestStart());
                System.out.println("LF: " + activitiesPERT_CPM.get(activity).getLatestFinish());
            }
        } else {
            System.out.println("\n\n" + Utils.BOLD + "--- Activity: " + choice + " ------------" + Utils.RESET);
            String[] parts = choice.split(" ");
            choice = parts[1];
            System.out.println("ES: " + activitiesPERT_CPM.get(choice).getEarliestStart());
            System.out.println("LF: " + activitiesPERT_CPM.get(choice).getLatestFinish());
        }
    }
}
