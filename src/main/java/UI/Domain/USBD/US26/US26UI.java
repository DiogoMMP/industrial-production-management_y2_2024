package UI.Domain.USBD.US26;

import UI.Domain.USBD.US07.US7UI;
import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US26UI implements Runnable {

    /**
     * This method is called when the US26UI object is created (by calling the run method).
     */
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();

        try (Connection connection = getConnection()) {
            // Get all Customer Orders
            String query = "SELECT CUSTOMER_ORDER_ID FROM CUSTOMER_ORDER";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    int orderId = resultSet.getInt("Customer_Order_ID");
                    options.add(new MenuItem("Customer Order ID: " + orderId, new US26UI()));
                }

                int option;
                do {
                    option = Utils.showAndSelectIndex(options,
                            "\n\n\033[1m\033[36m--- Choose the Customer Order to be Visualized ------------\033[0m");
                    if ((option >= 0) && (option < options.size())) {
                        String choice = options.get(option).toString();
                        if (!choice.equals("Back")) {
                            clearConsole();
                            int orderId = Integer.parseInt(choice.split(": ")[1]);
                            getOrderStockStatus(orderId);
                            Utils.goBackAndWait();
                        }
                    }
                } while (option != -1 && !options.get(option).toString().equals("Back"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getOrderStockStatus(int order_id) {
        String call = "{ ? = call get_order_stock_status(?) }";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(call)) {

            // Register the output parameter (cursor)
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);

            // Set the input parameter (order_id)
            callableStatement.setInt(2, order_id);

            // Execute the function
            callableStatement.execute();

            // Get the result set from the output parameter
            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);

            // Print the formatted table
            printFormattedTable(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method prints the formatted table of the order stock status.
     * @param resultSet The result set containing the order stock status data
     * @throws SQLException If an error occurs while accessing the result set
     */
    private void printFormattedTable(ResultSet resultSet) throws SQLException {
        // Create a list of rows to store the formatted data
        List<String[]> rows = new ArrayList<>();

        // Iterate over the result set and format the data
        while (resultSet.next()) {
            String data = resultSet.getString("ORDER_STOCK_DATA");
            String[] parts = data.split("\\n");
            String[] formattedRow = new String[5]; // Array to store the formatted row - 5 columns

            // Split each part of the data and store in the formatted row
            formattedRow[0] = parts[0].split(": ")[1]; // Part ID
            formattedRow[1] = parts[1].split(": ")[1]; // Description
            formattedRow[2] = parts[2].split(": ")[1]; // Total Required
            formattedRow[3] = parts[3].split(": ")[1]; // Stock
            formattedRow[4] = parts[4].split(": ")[1]; // Status

            rows.add(formattedRow);
        }

        // Print the table header
        System.out.printf("\033[1m%n%-20s %-50s %-20s %-20s %-20s%n\033[0m",
                "Part ID", "Description", "Total Required", "Stock", "Status");
        System.out.println("=".repeat(140)); // Horizontal line

        // Print the rows of the table
        for (String[] row : rows) {
            // Set the color based on the status
            String statusColor = row[4].equals("Sufficient") ? "\033[32m" : (row[4].equals("Insufficient") ? "\033[31m" : "\033[0m");

            // Print the formatted row with the correct color
            System.out.printf("%-20s %-50s %-20s %-20s %s%-20s\033[0m%n",
                    row[0], row[1], row[2], row[3], statusColor, row[4]);
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
