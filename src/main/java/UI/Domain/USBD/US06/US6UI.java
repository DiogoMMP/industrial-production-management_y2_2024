package UI.Domain.USBD.US06;

import UI.Domain.USBD.US07.US7UI;
import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US6UI implements Runnable {
    /**
     * This method is called when the US7UI object is created (by calling the run method).
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
                    int orderId = resultSet.getInt("CUSTOMER_ORDER_ID");
                    options.add(new MenuItem("Customer Order ID: " + orderId, new US6UI()));
                }

                int option;
                do {
                    option = Utils.showAndSelectIndex(options,
                            "\n\n" + Utils.BOLD + Utils.CYAN +
                                    "--- Choose the Customer Order to be Visualized ------------\n" + Utils.RESET);
                    if ((option >= 0) && (option < options.size())) {
                        String choice = options.get(option).toString();
                        if (!choice.equals("Back")) {
                            clearConsole();
                            int orderId = Integer.parseInt(choice.split(": ")[1]);
                            executeQuery(orderId);
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
     * This method executes the query to get the workstation type for a given order.
     * @param orderId The ID of the order to get the workstation types.
     */
    private void executeQuery(int orderId) {
        String query =
                "SELECT DISTINCT " +
                        "    wt.WORKSTATION_TYPE_ID, " +
                        "    wt.Workstation_Type " +
                        "FROM " +
                        "    Customer_Order co " +
                        "        JOIN " +
                        "    Production_Order po ON co.Customer_Order_ID = po.Customer_Order_ID " +
                        "        JOIN " +
                        "    Product_Production_Order ppo ON po.Order_ID = ppo.Production_Order_ID " +
                        "        JOIN " +
                        "    Product p ON ppo.Product_ID = p.Product_ID " +
                        "        JOIN " +
                        "    BOO boo ON p.Product_ID = boo.Product_ID " +
                        "        JOIN " +
                        "    BOO_Input bii ON boo.Operation_ID = bii.Operation_ID AND boo.Product_ID = bii.Product_ID " +
                        "        JOIN " +
                        "    Operation op ON boo.Operation_ID = op.Operation_ID " +
                        "        JOIN " +
                        "    Workstation_Type_Operation_Type wtot ON op.Operation_Type_ID = wtot.Operation_Type_ID " +
                        "        JOIN " +
                        "    Workstation_Type wt ON wtot.Workstation_Type_ID = wt.Workstation_Type_ID " +
                        "        JOIN " +
                        "    Workstation ws ON ws.Workstation_Type_ID = wt.Workstation_Type_ID " +
                        "WHERE " +
                        "    co.Customer_Order_ID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, orderId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Workstation Types for Order ID " + orderId + " ------------" + Utils.RESET);

                System.out.printf(Utils.BOLD + "%n%-25s %-50s%n", "Workstation Type ID", "Workstation Type");
                System.out.println("-".repeat(75) + Utils.RESET);

                while (resultSet.next()) {
                    String workstationTypeId = resultSet.getString("WORKSTATION_TYPE_ID");
                    String workstationType = resultSet.getString("WORKSTATION_TYPE");

                    System.out.printf("%-25s %-50s%n", workstationTypeId, workstationType);
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
