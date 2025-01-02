package UI.Domain.USEI.US24;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import domain.Activity;
import projectManager.PERT_CPM;
import repository.Instances;

import java.util.*;

public class SimulateProjDelaysUI implements Runnable {

    PERT_CPM pertCpm = Instances.getInstance().getPERT_CPM();
    Map<String, Activity> activities = pertCpm.getActivitiesPERT_CPM();
    Map<String, Double[]> originalTimes = new HashMap<>();

    /**
     * This method is responsible for running the UI.
     */
    @Override
    public void run() {
        // Store original times
        storeOriginalTimes();

        LinkedHashMap<String, Integer> delays = new LinkedHashMap<>();

        String choice;
        List<MenuItem> options = new ArrayList<>();

        if (pertCpm.hasCircularDependencies()) {
            System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Simulate Project Delays ------------\n" + Utils.RESET);
            System.out.println(Utils.RED + "Error: The project has circular dependencies." + Utils.RESET);
            Utils.goBackAndWait();
            return;
        }

        // Create menu options from the sorted map
        for (Map.Entry<String, Activity> entry : activities.entrySet()) {
            options.add(new MenuItem("Activity: " + entry.getKey() + " - " + entry.getValue().getDescription(),
                    new SimulateProjDelaysUI()));
        }

        options.add(new MenuItem("Show New Start and Finish Times", new SimulateProjDelaysUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Choose an Activity to Add a Delay or Show Times------------\n" + Utils.RESET);

            if (option == -2) {
                break;
            }

            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (choice.equals("Show New Start and Finish Times")) {
                    showNewTimes();
                } else if (!choice.equals("Back")) {
                    clearConsole();

                    String actId = options.get(option).toString().split(" ")[1];
                    int delay = Utils.readIntegerFromConsole("Enter delay duration for Activity " + actId + ": ");
                    delays.put(actId, delay);

                    if (!verifyDelay(actId, delay)) {
                        do {
                            delay = Utils.readIntegerFromConsole("Enter delay duration for Activity " + actId + ": ");
                        } while (!verifyDelay(actId, delay));
                    }

                    if (!Utils.confirm(Utils.BOLD + "Do you want to add another delay? (Y/N)" + Utils.RESET)){
                        simulateDelays(delays);
                    }
                }
            }
        } while (option != -1 && !options.get(option).toString().equals("Back"));

        // Restore original times
        restoreOriginalTimes();

    }

    /**
     * This method is responsible for verifying if the delay is valid.
     * @param actId Activity ID
     * @param delay Delay duration
     * @return True if the delay is valid, false otherwise
     */
    private boolean verifyDelay(String actId, int delay){
        if (activities.get(actId).getDuration() + delay < 0){
            System.err.println("Error: Activity duration must be positive or zero.\n");
            return false;
        }
        System.out.println("\n" + Utils.GREEN + "Delay added to activity " + actId + "." + Utils.RESET);
        return true;
    }

    /**
     * This method is responsible for simulating the project delays.
     * @param delays Delays to be simulated
     */
    private void simulateDelays(LinkedHashMap<String, Integer> delays) {
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Simulate Project Delays ------------\n" + Utils.RESET);
        pertCpm.simulateDelaysAndRecalculate(delays);

        System.out.println(Utils.BOLD + "Updated Critical Paths and Project Duration:\n" + Utils.RESET);
        LinkedHashMap<Integer, List<Activity>> criticalPaths = pertCpm.findCriticalPaths();
        for (Map.Entry<Integer, List<Activity>> entry : criticalPaths.entrySet()) {
            System.out.print(Utils.BOLD + "Path " + entry.getKey() + ": "  + Utils.RESET);
            for (Activity activity : entry.getValue()) {
                System.out.print(activity.getActId() + " ");
            }
            System.out.println();
        }
        System.out.println("\n" + Utils.GREEN + "Total Project Duration: " + pertCpm.calculateTotalProjectDuration()
                + Utils.RESET);

        Utils.goBackAndWait();
    }

    private void showNewTimes() {
        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Show New Start and Finish Times ------------\n" + Utils.RESET);

        for (String activity : activities.keySet()) {
            System.out.println("\n\n" + Utils.BOLD + "--- Activity: " + activity + " ------------" + Utils.RESET);
            System.out.println("ES: " + activities.get(activity).getEarliestStart());
            System.out.println("LF: " + activities.get(activity).getLatestFinish());
        }

        Utils.goBackAndWait();
    }

    /**
     * This method is responsible for storing the original times of the activities.
     */
    private void storeOriginalTimes() {
        for (Map.Entry<String, Activity> entry : activities.entrySet()) {
            Activity activity = entry.getValue();
            originalTimes.put(entry.getKey(), new Double[]{
                    (double) activity.getDuration(),
                    activity.getEarliestStart(),
                    activity.getEarliestFinish(),
                    activity.getLatestStart(),
                    activity.getLatestFinish(),
                    activity.getSlack()
            });
        }
    }

    /**
     * This method is responsible for restoring the original times of the activities.
     */
    private void restoreOriginalTimes() {
        for (Map.Entry<String, Double[]> entry : originalTimes.entrySet()) {
            Activity activity = activities.get(entry.getKey());
            Double[] times = entry.getValue();
            activity.setDuration(times[0].intValue());
            activity.setEarliestStart(times[1]);
            activity.setEarliestFinish(times[2]);
            activity.setLatestStart(times[3]);
            activity.setLatestFinish(times[4]);
            activity.setSlack(times[5]);
        }
    }

    /**
     * This method is responsible for clearing the console.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}