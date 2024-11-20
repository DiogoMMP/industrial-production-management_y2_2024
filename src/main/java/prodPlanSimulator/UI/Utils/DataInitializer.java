package prodPlanSimulator.UI.Utils;
import prodPlanSimulator.UI.Simulators.ChooseSimulatorUI;
import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataInitializer implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();

    /**
     * Initialize the data
     * @param pathArt path to the articles file
     * @param pathWor path to the workstations file
     * @throws FileNotFoundException if the file is not found
     */
    public void init(String pathArt, String pathWor, String pathBOO, String pathItems, String pathOp) throws FileNotFoundException {
        map.addAll(pathArt, pathWor, pathBOO, pathItems, pathOp);
    }

    /**
     * Run the data initializer
     */
    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            String pathOp;
            String pathBOO;
            String pathItems;
            String pathArt;
            String pathWor;
            boolean success = false;

            while (!success) {
                System.out.println("\n\n--- DATA --------------------------");
                System.out.println("1. Use default file paths");
                System.out.println("2. Enter file paths manually");

                System.out.print("\n\nType your option: ");
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
                        pathArt = "articles.csv";
                        pathWor = "workstations.csv";
                        pathBOO = "boo_v2.csv";
                        pathItems = "items.csv";
                        pathOp = "operations.csv";
                        break;
                    case 2:
                        pathArt = Utils.readLineFromConsole("Articles: ");
                        pathWor = Utils.readLineFromConsole("Workstations: ");
                        pathBOO = Utils.readLineFromConsole("Bill of Operations: ");
                        pathItems = Utils.readLineFromConsole("Items: ");
                        pathOp = Utils.readLineFromConsole("Operations: ");
                        break;
                    default:
                        System.err.println("Error: Invalid option. Please enter 1 or 2.");
                        continue;
                }

                try {
                    init(pathArt, pathWor, pathBOO, pathItems, pathOp);
                    success = true;
                } catch (FileNotFoundException e) {
                    System.err.println("Error: File not found. Please check the file path and try again.");
                }
            }
            ChooseSimulatorUI chooseSimulatorUI = new ChooseSimulatorUI();
            chooseSimulatorUI.run();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}