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
            Utils.clearConsole();
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
                        Utils.clearConsole();
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

            // Step 5: Initialize variables for dynamic column widths
            int maxInputLength = "Input Components".length();
            int maxOutputLength = "Output Components".length();

            // Step 6: Process each operation to calculate max column widths
            List<String[]> rows = new ArrayList<>();
            while (resultSet.next()) {
                String operationData = resultSet.getString("OPERATION_DATA");

                // Parse the operationData string into separate fields
                String[] lines = operationData.split("\n");
                String productID = extractField(lines, "Product ID");
                String operationID = extractField(lines, "Manufacturing Operation ID");
                String operationDescription = extractField(lines, "Operation Description");
                String executionTime = extractField(lines, "Execution Time");
                String partType = extractField(lines, "Workstation Type");

                // Handle input and output components
                String inputPart = cleanID(extractField(lines, "Input Components"));
                String outputPart = cleanID(extractField(lines, "Output Components"));

                // Update max column lengths
                maxInputLength = Math.max(maxInputLength, inputPart.length()) + 10;
                maxOutputLength = Math.max(maxOutputLength, outputPart.length()) + 10;

                // Store the row data
                rows.add(new String[]{productID, operationID, operationDescription, executionTime, partType, inputPart, outputPart});
            }

            // Step 7: Print table header with dynamic widths
            Utils.clearConsole();
            System.out.println(Utils.BOLD + Utils.CYAN + "\n--- Operations of Product " + productId + " ------------\n" + Utils.RESET);

            System.out.printf(Utils.BOLD + "%-15s %-15s %-30s %-20s %-20s %-" + maxInputLength + "s %-" + maxOutputLength + "s%n" + Utils.RESET,
                    "Product ID", "Operation ID", "Operation Description", "Execution Time", "Part Type", "Input Components", "Output Components");
            System.out.println("-".repeat(130 + maxInputLength + maxOutputLength) + Utils.RESET); // Table divider

            // Step 8: Print each row with dynamic widths
            for (String[] row : rows) {
                System.out.printf("%-15s %-15s %-30s %-20s %-20s %-" + maxInputLength + "s %-" + maxOutputLength + "s%n",
                        row[0], row[1], row[2], row[3], row[4], row[5], row[6]);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving product operations: " + e.getMessage());
        }
    }

    /**
     * Removes the text inside parentheses and trims the result.
     *
     * @param value The original string containing the ID and additional data in parentheses.
     * @return The cleaned string with only the ID.
     */
    private String cleanID(String value) {
        return value.replaceAll("\\s*\\(.*?\\)", "").trim(); // Removes text inside parentheses
    }

    /**
     * Extracts a specific field value from the operation data.
     *
     * @param lines The array of lines from the operation data.
     * @param fieldName The field name to search for.
     * @return The value of the field, or an empty string if not found.
     */
    private String extractField(String[] lines, String fieldName) {
        for (String line : lines) {
            if (line.startsWith(fieldName + ":")) {
                return line.split(": ", 2)[1].trim();
            }
        }
        return ""; // Return empty string if the field is not found
    }


    /**
     * Establishes a connection to the database.
     * @return The database connection.
     * @throws SQLException If the connection fails.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }
}