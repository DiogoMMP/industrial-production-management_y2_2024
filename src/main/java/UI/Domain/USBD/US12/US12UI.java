package UI.Domain.USBD.US12;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US12UI implements Runnable {
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()){
            String query = "SELECT Product_ID FROM Product ";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String productId = resultSet.getString("Product_ID");
                options.add(new MenuItem("Product ID: " + productId, new US12UI()));
            }
            int option;
            do {
                option = Utils.showAndSelectIndex(options,
                        "\n\n" + Utils.BOLD + Utils.CYAN +
                                "--- Choose the Product to be Visualized ------------\n" + Utils.RESET);

                if (option == -2) {
                    break;
                }

                if ((option >= 0) && (option < options.size())) {
                    String choice = options.get(option).toString();
                    if (!choice.equals("Back")) {
                        clearConsole();
                        String productId = choice.split(": ")[1];
                        listParts(productId);
                        Utils.goBackAndWait();
                    }
                }
            } while (option != -1 && !options.get(option).toString().equals("Back"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for listing the parts of the selected product.
     */
    private void listParts(String productId) {

        System.out.println(Utils.BOLD + Utils.CYAN + "\n\n--- Parts of Product " + productId +
                " -----------------------------------\n" + Utils.RESET);

        String function = "{? = call get_product_parts(?)}";
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(function)) {

            // Register the output parameter (SYS_REFCURSOR)
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);
            // Set the input parameter (Product ID)
            callableStatement.setString(2, productId);

            // Execute the function
            callableStatement.execute();

            // Retrieve the cursor (ResultSet)
            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);

            // Check if there are no parts found
            if (!resultSet.isBeforeFirst()) {
                System.err.println("No parts found for the product with ID: " + productId);
                return;
            }

            // Output the header
            System.out.printf(Utils.BOLD + "%-20s %-50s %-30s %-20s%n", "Part ID", "Part Description", "Part Type", "Quantity");
            System.out.println("-".repeat(120) + Utils.RESET);

            // Process the result set
            while (resultSet.next()) {
                String partId = resultSet.getString("Part_ID");
                String partDesc = resultSet.getString("Part_Description");
                String partType = resultSet.getString("Part_Type");
                int quantity = resultSet.getInt("Total_Quantity");

                // Print each part in a formatted way
                System.out.printf("%-20s %-50s %-30s %-20d%n", partId, partDesc, partType, quantity);
            }

            // Close the result set
            resultSet.close();

        } catch (SQLException e) {
            // Handling SQLException, and specifically dealing with the known exceptions
            String message = e.getMessage();
            if (message.contains("Error: Product ID cannot be NULL")) {
                System.err.println("Error: The provided Product ID cannot be NULL.");
            } else if (message.contains("Error: Product with ID")) {
                System.err.println("Error: The Product with ID " + productId + " does not exist.");
            } else {
                System.err.println("Error: An unexpected error occurred: " + message);
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