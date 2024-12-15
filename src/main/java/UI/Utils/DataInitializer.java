package UI.Utils;
import UI.Menu.MainMenuUI;
import UI.Simulators.ChooseSimulatorUI;
import repository.*;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataInitializer implements Runnable {
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();
    private HashMap_Items_Machines_Sprint1 map2 = Instances.getInstance().getHashMapItemsWorkstationsSprint1();
    private ItemsRepository itemsRepository = Instances.getInstance().getItemsRepository();
    private OperationsMapRepository operationsMapRepository = Instances.getInstance().getOperationsMapRepository();
    private BOORepository booRepository = Instances.getInstance().getBOORepository();
    private OrdersRepository ordersRepository = Instances.getInstance().getOrdersRepository();
    /**
     * Initialize the data
     * @param pathArt path to the articles file
     * @param pathWor path to the workstations file
     * @param pathBoo path to the bill of operations file
     * @param pathItems path to the items file
     * @param pathOp path to the operations file
     * @param pathWor2 path to the workstations file
     * @param pathOrd path to the orders file
     * @throws FileNotFoundException if the file is not found
     */
    public void init(String pathArt, String pathWor, String pathBoo, String pathItems, String pathOp, String pathWor2, String pathOrd) throws FileNotFoundException {
            map2.addAll(pathArt, pathWor);
            map.addAll(pathOp, pathItems, pathWor2);
            itemsRepository.addItems(pathItems);
            operationsMapRepository.addOperations(pathOp);
            booRepository.addBOOList(pathBoo);
            ordersRepository.addOrders(pathOrd);
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
            String pathWor2;
            String pathItems;
            String pathArt;
            String pathWor;
            String pathOrders;
            boolean success = false;

            while (!success) {
                System.out.println("\n\n\033[1;36m--- Choose Your Files --------------------------\033[0m");
                System.out.println("  1. Use Default File Paths");
                System.out.println("  2. Use Exported Files from the Database");
                System.out.println("  3. Enter File Paths Manually");
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
                        pathArt = "articles_sprint1.csv";
                        pathWor = "workstations_sprint1.csv";
                        pathWor2 = "workstations_sprint2.csv";
                        pathBOO = "boo_v2.csv";
                        pathItems = "items.csv";
                        pathOp = "operations.csv";
                        pathOrders = "orders.csv";
                        break;
                    case 2:
                        pathArt = "articles_sprint1.csv";
                        pathWor = "workstations_sprint1.csv";
                        pathWor2 = "workstations_sprint2.csv";
                        pathBOO = "boo_exported.csv";
                        pathItems = "items_exported.csv";
                        pathOp = "operations_exported.csv";
                        pathOrders = "orders_exported.csv";
                        break;
                    case 3:
                        pathArt = Utils.readLineFromConsole("Articles: ");
                        pathWor = Utils.readLineFromConsole("Workstations for the simulator: ");
                        pathWor2 = Utils.readLineFromConsole("Workstations for the database: ");
                        pathBOO = Utils.readLineFromConsole("Bill of Operations: ");
                        pathItems = Utils.readLineFromConsole("Items: ");
                        pathOp = Utils.readLineFromConsole("Operations: ");
                        pathOrders = Utils.readLineFromConsole("Orders: ");
                        break;
                    case 0:
                        MainMenuUI mainMenuUI = new MainMenuUI();
                        mainMenuUI.run();
                        return;
                    default:
                        System.err.println("Error: Invalid option. Please enter 0, 1, 2 or 3.");
                        continue;
                }

                try {
                    init(pathArt, pathWor, pathBOO, pathItems, pathOp, pathWor2, pathOrders);
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