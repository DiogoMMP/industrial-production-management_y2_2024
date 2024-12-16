package UI.Menu;

import UI.Simulators.ChooseSimulatorUI;
import UI.Utils.Utils;
import repository.*;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class ActivitiesMenu implements Runnable {
    private ActivitiesMapRepository activitiesMapRepository = Instances.getInstance().getActivitiesMapRepository();
    /**
     * Initialize the data
     * @param pathAct path to the activities file
     * @throws FileNotFoundException if the file is not found
     */
    public void init(String pathAct) throws FileNotFoundException {
        activitiesMapRepository.addActivities(pathAct);
    }

    /**
     * Run the data initializer
     */
    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            String pathAct;
            boolean success = false;

            while (!success) {
                System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Choose a File for the Activities --------------------------\n" + Utils.RESET);

                System.out.println("  1. Use the Small File");
                System.out.println("  2. Use the Large File");
                System.out.println("  3. Enter the File Path Manually");
                System.out.println("  0. Back");
                System.out.print("\nType your option: ");
                String input = scanner.nextLine();
                int choice;

                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.err.println("Error: Invalid input. Please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        pathAct = "small_project.csv";
                        break;
                    case 2:
                        pathAct = "large_project.csv";
                        break;
                    case 3:
                        pathAct = Utils.readLineFromConsole("Activities: ");
                        break;
                    case 0:
                        ChooseSimulatorUI chooseSimulatorUI = new ChooseSimulatorUI();
                        chooseSimulatorUI.run();
                        return;
                    default:
                        System.err.println("Error: Invalid option. Please enter 0, 1, 2 or 3.");
                        continue;
                }

                try {
                    init(pathAct);
                    success = true;
                } catch (FileNotFoundException e) {
                    System.err.println("Error: File not found. Please check the file path and try again.");
                }
            }
            MainMenuPERTCPM mainMenuPERTCPM = new MainMenuPERTCPM();
            mainMenuPERTCPM.run();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
