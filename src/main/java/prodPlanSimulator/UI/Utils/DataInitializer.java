package prodPlanSimulator.UI.Utils;
import prodPlanSimulator.UI.Simulators.ChooseSimulatorUI;
import prodPlanSimulator.repository.*;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataInitializer implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();
    private HashMap_Items_Machines_Sprint1 map2 = Instances.getInstance().getHashMapItemsWorkstationsSprint1();
    private ItemsRepository itemsRepository = Instances.getInstance().getItemsRepository();
    private OperationsMapRepository operationsMapRepository = Instances.getInstance().getOperationsMapRepository();
    private BOORepository booRepository = Instances.getInstance().getBOORepository();

    /**
     * Initialize the data
     * @param pathArt path to the articles file
     * @param pathWor path to the workstations file
     * @throws FileNotFoundException if the file is not found
     */
    public void init(String pathArt, String pathWor, String pathBoo, String pathItems, String pathOp, Integer option) throws FileNotFoundException {
        if (option == 1){
            map2.addAll(pathArt, pathWor);
            map.addAll(pathOp, pathItems);
            itemsRepository.addItems(pathItems);
            operationsMapRepository.addOperations(pathOp);
            booRepository.addBOOList(pathBoo);
        } else {
            map.addAll(pathArt, pathWor, pathItems, pathOp);
            itemsRepository.addItems(pathItems);
            operationsMapRepository.addOperations(pathOp);
            booRepository.addBOOList(pathBoo);
        }


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
            String pathArt2;
            String pathWor2;
            boolean success = false;

            while (!success) {
                System.out.println("\n\n--- DATA --------------------------");
                System.out.println("1. Use default file paths");
                System.out.println("2. Use exported files from the database");
                System.out.println("3. Enter file paths manually");

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
                        pathArt = "articles_exported.csv";
                        pathWor = "workstations_exported.csv";
                        pathBOO = "boo_exported.csv";
                        pathItems = "items_exported.csv";
                        pathOp = "operations_exported.csv";
                        break;
                    case 3:
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
                    init(pathArt, pathWor, pathBOO, pathItems, pathOp, choice);
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