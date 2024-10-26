package prodPlanSimulator.UI.Utils;
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
    public void init(String pathArt, String pathWor) throws FileNotFoundException {
        map.addAll(pathArt, pathWor);
    }

    /**
     * Run the data initializer
     */
    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            String pathArt;
            String pathWor;
            boolean success = false;

            while (!success) {
                System.out.println("\n\n--- DATA --------------------------");
                System.out.println("1. Use default file paths");
                System.out.println("2. Enter file paths manually");

                System.out.print("\n\nType your option: ");
                int choice = Integer.parseInt(scanner.nextLine());

                if (choice == 1) {
                    pathArt = "articles.csv";
                    pathWor = "workstations.csv";
                } else {
                    pathArt = Utils.readLineFromConsole("Articles: ");
                    pathWor = Utils.readLineFromConsole("Workstations: ");
                }

                try {
                    init(pathArt, pathWor);
                    success = true;
                } catch (FileNotFoundException e) {
                    System.err.println("Error: File not found. Please check the file path and try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}