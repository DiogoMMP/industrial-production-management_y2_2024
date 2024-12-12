package UI.Domain.BDDAD.US5;

import importer_and_exporter.OracleDataExporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class US5UI implements Runnable {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter start date (DD-MON-YYYY): ");
        String startDate = scanner.nextLine();

        System.out.print("Enter end date (DD-MON-YYYY): ");
        String endDate = scanner.nextLine();

        String query = String.format(
                "SELECT C.Name AS Customer_Name, P.Product_Name, COP.Quantity, CO.Delivery_Date " +
                        "FROM Customer_Order CO " +
                        "JOIN Customer_Order_Product COP ON CO.Customer_Order_ID = COP.Customer_Order_ID " +
                        "JOIN Product P ON COP.Product_ID = P.Product_ID " +
                        "JOIN Customer C ON CO.Customer_ID = C.Customer_ID " +
                        "WHERE CO.Delivery_Date BETWEEN TO_DATE('%s', 'DD-MON-YYYY') AND TO_DATE('%s', 'DD-MON-YYYY')",
                startDate, endDate
        );

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String customerName = resultSet.getString("Customer_Name");
                String productName = resultSet.getString("Product_Name");
                int quantity = resultSet.getInt("Quantity");
                String deliveryDate = resultSet.getString("Delivery_Date");

                System.out.printf("Customer: %s, Product: %s, Quantity: %d, Delivery Date: %s%n",
                        customerName, productName, quantity, deliveryDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }
}