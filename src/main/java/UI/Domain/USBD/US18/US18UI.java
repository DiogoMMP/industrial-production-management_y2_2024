package UI.Domain.USBD.US18;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US18UI implements Runnable {
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String query = "SELECT Customer_ID FROM Customer WHERE Status = 'Activated'";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int customerId = resultSet.getInt("Customer_ID");
                options.add(new MenuItem("Customer ID: " + customerId, new US18UI()));
            }

            int option;
            do {
                option = Utils.showAndSelectIndex(options,
                        "\n\n\033[1m\033[36m--- Choose the Customer to be Visualized ------------\033[0m");
                if ((option >= 0) && (option < options.size())) {
                    String choice = options.get(option).toString();
                    if (!choice.equals("Back")) {
                        clearConsole();
                        int customerId = Integer.parseInt(choice.split(": ")[1]);
                        deactivateCustomer(customerId);
                        Utils.goBackAndWait();
                    }
                }
            } while (option != -1 && !options.get(option).toString().equals("Back"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * This method executes one function to deactivate a customer if the customer is activated and has no orders.
     * @param customerId The ID of the customer to be deactivated
     */
    private void deactivateCustomer(int customerId) {
        String function = "{? = call Deactivate_Customer(?)}";
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(function)) {
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.setInt(2, customerId);
            callableStatement.execute();
            String result = callableStatement.getString(1);
            System.out.println(result);
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