package UI.Domain.USBD.US13;

import UI.Domain.USBD.US12.US12UI;
import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US13UI implements Runnable {
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()){
            String query = "SELECT Product_ID FROM Product ";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String productId = resultSet.getString("Product_ID");
                options.add(new MenuItem("Product ID: " + productId, new US13UI()));
            }
            int option;
            do {
                option = Utils.showAndSelectIndex(options,
                        "\n\n\033[1m\033[36m--- Choose the Product to be Visualized ------------\033[0m");
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
            if (!resultSet.next()) {
                System.out.println("\nNo operations found for Product ID: " + productId);
            } else {
                // Process the result set
                do {
                    String operationData = resultSet.getString("operation_data");

                    // Output the result
                    System.out.println("\n" + operationData);
                } while (resultSet.next());
            }

            // Close the result set
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
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
