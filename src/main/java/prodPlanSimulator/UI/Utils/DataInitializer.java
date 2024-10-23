package prodPlanSimulator.UI.Utils;

import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

import java.util.Scanner;

public class DataInitializer implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();

    public void init(String pathArt, String pathWor) {
        map.addAll(pathArt, pathWor);
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n\n--- DATA --------------------------");
        System.out.println("1. Use default file paths");
        System.out.println("2. Enter file paths manually");

        System.out.print("\n\nType your option: ");
        int choice = Integer.parseInt(scanner.nextLine());

        String pathArt;
        String pathWor;

        if (choice == 1) {
            pathArt = "articles.csv";
            pathWor = "workstations.csv";
        } else {
            pathArt = Utils.readLineFromConsole("Articles: ");
            pathWor = Utils.readLineFromConsole("Workstations: ");
        }

        init(pathArt, pathWor);
    }

}
