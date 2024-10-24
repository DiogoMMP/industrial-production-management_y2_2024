package prodPlanSimulator.UI;

import prodPlanSimulator.UI.Utils.Utils;
import projectManager.ProductStructureGraph;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ProductStructureGraphUI implements Runnable{
    private ProductStructureGraph productStructureGraph = new ProductStructureGraph();

    public void init(String pathArt) throws FileNotFoundException {
        productStructureGraph.generateGraph(pathArt);
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String path;
        boolean success = false;

        while (!success) {
            System.out.println("\n\n--- DATA --------------------------");
            System.out.println("1. Use default file path");
            System.out.println("2. Enter file path manually");

            System.out.print("\n\nType your option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                path = "BOM.csv";
            } else {
                path = Utils.readLineFromConsole("File: ");
            }

            try {
                init(path);
                success = true;
            } catch (FileNotFoundException e) {
                System.err.println("Error: File not found. Please check the file path and try again.");
            }
        }
    }
}
