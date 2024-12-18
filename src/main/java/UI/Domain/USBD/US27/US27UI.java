package UI.Domain.USBD.US27;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US27UI implements Runnable {

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
                    options.add(new MenuItem("Customer Order ID: " + orderId, new US27UI()));
                }

                int option;
                do {
                    option = Utils.showAndSelectIndex(options,
                            "\n\n" + "--- Choose the Customer Order to Reserve Materials ------------\n");

                    if (option == -2) {
                        System.out.println("Returning to the previous menu...");
                        return;
                    }

                    if ((option >= 0) && (option < options.size())) {
                        String choice = options.get(option).toString();
                        if (!choice.equals("Back")) {
                            clearConsole();
                            int orderId = Integer.parseInt(choice.split(": ")[1]);
                            reserveMaterialsForOrder(orderId);
                            Utils.goBackAndWait();
                        }
                    }
                } while (option != -1 && !options.get(option).toString().equals("Back"));
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    /**
     * This method creates a connection to the database.
     *
     * @return The connection to the database.
     * @throws SQLException If an error occurs while connecting to the database.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }


    private void reserveMaterialsForOrder(int customerOrderId) throws SQLException {
        String call = "{ ? = call reserve_materials_for_order(?) }";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(call)) {

            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.setInt(2, customerOrderId);
            callableStatement.execute();
            callableStatement.getString(1);
        }
    }

    /**
     * This method clears the console.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}