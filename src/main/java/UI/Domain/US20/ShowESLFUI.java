package UI.Domain.US20;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import domain.Activity;
import projectManager.PERT_CPM;
import repository.Instances;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ShowESLFUI implements Runnable {
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
            option = Utils.showAndSelectIndex(options, "\n\n--- Choose the Activity to be visualized ------------");
            if ((option >= 0) && (option < options.size())) {
                String choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    show(choice, activitiesPERT_CPM);
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1 && !options.get(option).toString().equals("Back"));
    }

    private void show(String choice, LinkedHashMap<String, Activity> activitiesPERT_CPM) {
        System.out.println("\n\n--- Show Earliest Time and Latest Finish ------------");

        if (choice.equals("All")) {
            for (String activity : activitiesPERT_CPM.keySet()) {
                System.out.println("\n\n--- Activity: " + activity + " ------------");
                System.out.println("ES: " + activitiesPERT_CPM.get(activity).getEarliestStart());
                System.out.println("LF: " + activitiesPERT_CPM.get(activity).getLatestFinish());
            }
        } else {
            System.out.println("\n\n--- Activity: " + choice + " ------------");
            String[] parts = choice.split(" ");
            choice = parts[1];
            System.out.println("ES: " + activitiesPERT_CPM.get(choice).getEarliestStart());
            System.out.println("LF: " + activitiesPERT_CPM.get(choice).getLatestFinish());
        }
    }
}
