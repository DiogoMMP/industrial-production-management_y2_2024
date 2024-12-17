package UI.Domain.USBD.US13;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class US13UI implements Runnable {

    /**
     * This method displays the list of products and allows the user to select a product to visualize its operations.
     */
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String query = "SELECT Product_ID FROM Product";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String productId = resultSet.getString("Product_ID");
                options.add(new MenuItem("Product ID: " + productId, new US13UI()));
            }

            int option;
            do {
                option = Utils.showAndSelectIndex(options,
                        "\n\n" + Utils.BOLD + Utils.CYAN +
                                "--- Choose the Product to be Visualized ------------\n" + Utils.RESET);

                if ((option >= 0) && (option < options.size())) {
                    String choice = options.get(option).toString();
                    if (!choice.equals("Back")) {
                        clearConsole();
                        String productId = choice.split(": ")[1];
                        listOperations(productId);
                        Utils.goBackAndWait();
                    }
                }
            } while (option != -1 && !options.get(option).toString().equals("Back"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method lists the operations involved in the production of a product.
     * @param productId The product ID
     */
    private void listOperations(String productId) {
        String function = "{? = call Get_Product_Operations(?)}";
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(function)) {

            // Register the output parameter (SYS_REFCURSOR)
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);
            // Set the input parameter (Product ID)
            callableStatement.setString(2, productId);

            // Execute the function
            callableStatement.execute();

            // Retrieve the cursor
            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);

            // Check if there are any results
            if (!resultSet.isBeforeFirst()) {
                System.err.println("\nNo operations found for Product ID: " + productId);
            } else {

                // Create a list of rows to store the formatted data
                List<String[]> rows = new ArrayList<>();

                // Iterate over the result set and format the data
                while (resultSet.next()) {
                    String data = resultSet.getString("OPERATION_DATA");
                    String[] parts = data.split("\\n");
                    String[] formattedRow = new String[8]; // Adjusted to 8 columns as expected

                    // Split each part of the data and store in the formatted row
                    formattedRow[0] = parts[1].split(": ")[1]; // Product ID
                    formattedRow[1] = parts[2].split(": ")[1]; // Manufacturing Operation ID
                    formattedRow[2] = parts[3].split(": ")[1]; // Operation Type ID
                    formattedRow[3] = parts[4].split(": ")[1]; // Operation Description
                    formattedRow[4] = parts[5].split(": ")[1]; // Workstation Type
                    formattedRow[5] = parts[6].split(": ")[1]; // Input Components
                    formattedRow[6] = parts[7].split(": ")[1]; // Output Components

                    rows.add(formattedRow);
                }

                System.out.println(Utils.BOLD + Utils.CYAN +
                        "\n\n--- Operations Involved in Producing the Product " + productId + " ------------\n" + Utils.RESET);

                // Print the table header
                System.out.printf(Utils.BOLD + "%-15s %-15s %-20s %-30s %-50s%n",
                        "Product ID", "Operation ID", "Operation Type ID", "Operation Description",
                        "Workstation Type");
                System.out.println("-".repeat(130) + Utils.RESET); // Horizontal line

                // Print the rows of the table
                for (String[] row : rows) {

                    // Print the formatted row with the correct color
                    System.out.printf("%-15s %-15s %-20s %-30s %-50s \033[0m%n",
                            row[0], row[1], row[2], row[3], row[4]);
                }

                // Display the Input and Output components for each operation
                System.out.println(Utils.BOLD + Utils.GREEN +
                        "\n\n--- Input Components Involved in the Operations ------------\n" + Utils.RESET);
                displayComponents("Input", rows);

                System.out.println(Utils.BOLD + Utils.GREEN +
                        "\n\n--- Output Components Involved in the Operations ------------\n" + Utils.RESET);
                displayComponents("Output", rows);
            }

            // Close the result set
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method displays the components for each operation.
     * @param type The type of components (Input or Output)
     * @param rows The list of rows containing the operation data
     */
    private void displayComponents(String type, List<String[]> rows) {
        System.out.printf(Utils.BOLD + "%-20s %-20s %-40s %-20s%n", "Operation Id", "Part ID", "Description", "Quantity");
        System.out.println("-".repeat(100) + Utils.RESET); // Horizontal line

        // Loop over each row and process the components
        for (String[] row : rows) {
            String operationId = row[1];
            String components = type.equals("Input") ? row[5] : row[6]; // Select the correct column based on type
            String[] componentArray = components.split("  "); // Split components by space

            // Process each component
            for (String component : componentArray) {
                // Extract the component product ID, quantity, and description
                String[] parts = component.split(" \\(");
                String componentId = parts[0];
                String[] details = parts[1].replace(")", "").split(" ");
                String quantity = details[0];
                String description = String.join(" ", Arrays.copyOfRange(details, 1, details.length));

                // Print the component details
                System.out.printf("%-20s %-20s %-40s %-20s%n", operationId, componentId, description, quantity);
            }
        }
    }

    /**
     * This method clears the console.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * This method establishes a connection to the Oracle database.
     * @return The connection object
     * @throws SQLException If a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

}
