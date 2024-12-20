package UI.Domain.USBD.US27;

import UI.Menu.MenuItem;
import UI.Menu.Sprint3MenuUI;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US27UI implements Runnable {

    /**
     * This method displays a list of customer orders and allows the user to select one to reserve materials for.
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
                    options.add(new MenuItem("Customer Order ID: " + orderId, new US27UI()));
                }

                int option;
                do {
                    option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                            "--- Choose the Customer Order to Reserve Materials ------------\n" + Utils.RESET);

                    if (option == -2) {
                        new Sprint3MenuUI().run();
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

    /**
     * This method reserves materials for a customer order.
     * @param customerOrderId The ID of the customer order.
     * @throws SQLException If an error occurs while reserving materials.
     */
    private void reserveMaterialsForOrder(int customerOrderId) throws SQLException {
        String call = "{ ? = call reserve_materials_for_order(?) }";
        String result;

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(call)) {

            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.setInt(2, customerOrderId);

            callableStatement.execute();

            callableStatement.getString(1);

            System.out.println(Utils.GREEN + "\nMaterials reserved successfully for Customer Order ID: " + customerOrderId + Utils.RESET);

        } catch (SQLException e) {
            System.out.println(Utils.RED + "\nFailed to reserve materials for Customer Order ID: " + customerOrderId +
                    " - " + e.getMessage() + Utils.RESET);
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