package UI.Domain.USBD.US28;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US28UI implements Runnable {
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT SUPPLIER_ID FROM SUPPLIER";
            try (var statement = connection.createStatement();
                 var resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    int supplierID = resultSet.getInt("Supplier_ID");
                    options.add(new MenuItem("Supplier ID: " + supplierID, new US28UI()));
                }
                options.add(new MenuItem("All", new US28UI()));
                int option;
                do {
                    option = Utils.showAndSelectIndex(options,
                            "\n\n\033[1m\033[36m--- Choose the Customer Order to be Visualized ------------\033[0m");
                    if ((option >= 0) && (option < options.size())) {
                        String choice = options.get(option).toString();
                        if (!choice.equals("Back") && !choice.equals("All")) {
                            clearConsole();
                            String supplierID = choice.split(": ")[1];
                            getReservedMaterials(supplierID);
                            Utils.goBackAndWait();
                        } else if (choice.equals("All")) {
                            clearConsole();
                            getReservedMaterials("All");
                            Utils.goBackAndWait();
                        }
                    }
                } while (option != -1 && !options.get(option).toString().equals("Back"));
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private void getReservedMaterials(String supplierID) {
        String function = "{? = call Get_Reserved_Materials()}"; // Adjust for function call
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(function)) {

            // Register the first parameter as the output for the SYS_REFCURSOR
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);

            // Execute the function
            callableStatement.execute();

            // Retrieve the cursor as a ResultSet
            try (ResultSet resultSet = (ResultSet) callableStatement.getObject(1)) {
                while (resultSet.next()) {
                    String partId = resultSet.getString("Part_ID");
                    int quantity = resultSet.getInt("Quantity");
                    String unit = resultSet.getString("Unit");
                    String description = resultSet.getString("Part_Description");
                    String supplierDetails = resultSet.getString("Supplier_Details");
                    String supplierIDExp = supplierDetails.split(" - ")[0];
                    // Filter by supplierID or display all if supplierID is -1, which means all
                    if (supplierID.equalsIgnoreCase(supplierIDExp) || supplierID.equalsIgnoreCase("All")) {
                        System.out.println("Part ID: " + partId);
                        System.out.println("Description: " + description);
                        System.out.println("Quantity: " + quantity);
                        System.out.println("Unit: " + unit);
                        System.out.println("Supplier Details: " + supplierDetails);
                        System.out.println("-------------------------------");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method creates a connection to the database.
     * @return The connection to the database.
     * @throws SQLException If an error occurs while connecting to the database.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

    /**
     * This method clears the console.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


}
