package UI.Domain.USBD.US07;

import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US7UI implements Runnable {

    /**
     * This method is called when the US7UI object is created (by calling the run method).
     */
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();

        try (Connection connection = getConnection()) {
            // Get all Production Orders
            String query = "SELECT Order_ID FROM Production_Order";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    int orderId = resultSet.getInt("Order_ID");
                    options.add(new MenuItem("Production Order ID: " + orderId, new US7UI()));
                }

                int option;
                do {
                    option = Utils.showAndSelectIndex(options,
                            "\n\n" + Utils.BOLD + Utils.CYAN +
                                    "--- Choose the Production Order to be Visualized ------------\n" + Utils.RESET);

                    if (option == -2) {
                        break;
                    }

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
     * This method executes the query to get the materials required for a given order.
     * @param orderId The ID of the order to get the materials required.
     */
    private void executeQuery(int orderId) {
        String query =
                "SELECT " +
                        "    p.Product_ID, " +
                        "    p.Product_Name, " +
                        "    pr.Part_ID, " +
                        "    pr.Part_Description, " +
                        "    SUM(bi.Quantity * ppo.Quantity_Ordered) AS Quantity_Required " +
                        "FROM " +
                        "    Production_Order po " +
                        "JOIN " +
                        "    Customer_Order co ON po.Customer_Order_ID = co.Customer_Order_ID " +
                        "JOIN " +
                        "    Customer_Order_Product cop ON co.Customer_Order_ID = cop.Customer_Order_ID " +
                        "JOIN " +
                        "    Product p ON cop.Product_ID = p.Product_ID " +
                        "JOIN " +
                        "    Product_Production_Order ppo ON p.Product_ID = ppo.Product_ID AND po.Order_ID = ppo.Production_Order_ID " +
                        "JOIN " +
                        "    BOO b ON p.Product_ID = b.Product_ID " +
                        "JOIN " +
                        "    BOO_Input bi ON b.Product_ID = bi.Product_ID AND b.Operation_ID = bi.Operation_ID " +
                        "JOIN " +
                        "    Part pr ON bi.Part_ID = pr.Part_ID " +
                        "WHERE " +
                        "    po.Order_ID = ? " +
                        "GROUP BY " +
                        "    p.Product_ID, " +
                        "    p.Product_Name, " +
                        "    pr.Part_ID, " +
                        "    pr.Part_Description " +
                        "ORDER BY " +
                        "    p.Product_Name, " +
                        "    pr.Part_Description";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, orderId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Utils.clearConsole();
                System.out.println(Utils.BOLD + Utils.CYAN + "\n\n--- Materials Required for Production Order ID " + orderId + " ---" + Utils.RESET);
                System.out.printf(Utils.BOLD + "%n%-15s %-30s %-15s %-50s %-20s%n",
                        "Product ID", "Product Name", "Part ID", "Part Description", "Quantity Required");
                System.out.println("-".repeat(140) + Utils.RESET);

                while (resultSet.next()) {
                    String productId = resultSet.getString("Product_ID");
                    String productName = resultSet.getString("Product_Name");
                    String partId = resultSet.getString("Part_ID");
                    String partDescription = resultSet.getString("Part_Description");
                    int quantityRequired = resultSet.getInt("Quantity_Required");

                    System.out.printf("%-15s %-30s %-15s %-50s %-20d%n",
                            productId, productName, partId, partDescription, quantityRequired);
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
