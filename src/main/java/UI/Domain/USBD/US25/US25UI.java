package UI.Domain.USBD.US25;

import UI.Domain.USBD.US17.US17UI;
import UI.Menu.MenuItem;
import UI.Menu.Sprint2MenuUI;
import UI.Menu.Sprint3MenuUI;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class US25UI implements Runnable {

    /**
     * The run method is the main method of the class and is called when the class is executed
     */
    @Override
    public void run() {
        try (Connection connection = getConnection()) {
            System.out.println(Utils.BOLD + Utils.CYAN + "\n\n--- Choose the Product to be Visualized ------------\n" + Utils.RESET);

            // Step 1: Allow the user to choose a product
            String productId = chooseProductID();

            // Step 2: Retrieve and print the product operations
            printProductOperations(connection, productId);
            Utils.goBackAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows the user to choose a product ID from the database.
     * @return The selected Product ID.
     */
    private String chooseProductID() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT DISTINCT p.Product_ID FROM Product p JOIN BOO b ON p.Product_ID = b.Product_ID";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String customerId = resultSet.getString("Product_ID");
                options.add(new MenuItem("Product ID: " + customerId, new US25UI()));
            }

            int option;

            while (true){
                option = Utils.showAndSelectIndex(options,
                        Utils.BOLD + "Choose Product ID:\n" + Utils.RESET);

                if (option == -2) {
                    new Sprint3MenuUI().run();
                }

                if ((option >= 0) && (option < options.size())) {
                    String choice = options.get(option).toString();

                    if (!choice.equals("Back")) {
                        clearConsole();
                        return choice.split(": ")[1];
                    }

                } else {
                    System.out.println(Utils.RED + "\nInvalid option. Please choose a valid Product ID.\n" + Utils.RESET);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving products: " + e.getMessage());
        }
        return null;
    }

    /**
     * Executes the Get_Product_Operations function and prints the operations for the selected product in a table format.
     *
     * @param connection The database connection.
     * @param productId The selected Product ID.
     */
    private void printProductOperations(Connection connection, String productId) {
        try {
            // Step 3: Call the Get_Product_Operations function
            CallableStatement callableStatement = connection.prepareCall("{? = call Get_Product_Operations(?)}");
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);
            callableStatement.setString(2, productId);
            callableStatement.execute();

            // Step 4: Get the result set
            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);

            if (!resultSet.isBeforeFirst()) {
                System.err.println("No operations found for the selected product.\n");
                return;
            }

            // Step 5: Print table header
            System.out.println(Utils.BOLD + Utils.CYAN + "\n--- Operations of Product " + productId + " ------------\n" + Utils.RESET);
            System.out.printf(Utils.BOLD + "%-15s %-15s %-30s %-20s %-15s %-15s %-15s%n" + Utils.RESET,
                    "Product ID", "Operation ID", "Operation Description", "Execution Time", "Part Type", "Input Part", "Output Part");
            System.out.println("-".repeat(130) + Utils.RESET); // Table divider

            // Step 6: Print table rows
            while (resultSet.next()) {
                String productID = resultSet.getString("Product_ID");
                int operationID = resultSet.getInt("Operation_ID");
                String operationDescription = resultSet.getString("Operation_Description");
                int executionTime = resultSet.getInt("Execution_Time");
                String partType = resultSet.getString("Part_Type");
                String inputPart = resultSet.getString("Input_Part");
                String outputPart = resultSet.getString("Output_Part");

                System.out.printf("%-15s %-15d %-30s %-20d %-15s %-15s %-15s%n",
                        productID, operationID, operationDescription, executionTime, partType, inputPart, outputPart);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving product operations: " + e.getMessage());
        }
    }

    /**
     * Establishes a connection to the database.
     * @return The database connection.
     * @throws SQLException If the connection fails.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

    /**
     * Clears the console.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}