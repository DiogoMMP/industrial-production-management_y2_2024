package UI.Domain.USBD.US05;

import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class US5UI implements Runnable {

    /**
     * This method is called when the US5UI object is created (by calling the run method).
     */
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Date Range Selection ------------\n" + Utils.RESET);

        System.out.print(Utils.BOLD + "Enter start date (DD-MM-YYYY) or 'stop' to return: " + Utils.RESET);
        String startDate = scanner.nextLine();

        if (startDate.equals("stop")) {
            return;
        }

        while (!Utils.isValidDateFormat1(startDate)) {
            System.err.print("Invalid date format. Please enter a valid date (DD-MM-YYYY): ");
            startDate = scanner.nextLine();
        }

        System.out.print(Utils.BOLD + "\nEnter end date (DD-MM-YYYY) or 'stop' to return: " + Utils.RESET);
        String endDate = scanner.nextLine();

        if (endDate.equals("stop")) {
            return;
        }

        while (!Utils.isValidDateFormat1(endDate)) {
            System.err.print("Invalid date format. Please enter a valid date (DD-MM-YYYY): ");
            endDate = scanner.nextLine();
        }

        if (Utils.isBeforeDate(startDate, endDate)) {
            System.err.print("End date must be after start date. Please enter a valid end date (DD-MM-YYYY): ");
            endDate = scanner.nextLine();
        }

        executeQuery(startDate, endDate);
    }

    /**
     * This method executes the query to get the orders in a given time frame.
     * @param startDate The start date of the time frame.
     * @param endDate The end date of the time frame.
     */
    private void executeQuery(String startDate, String endDate) {
        String query = String.format(
                "SELECT C.Name AS Customer_Name, P.Product_Name, COP.Quantity, CO.Delivery_Date " +
                        "FROM Customer_Order CO " +
                        "JOIN Customer_Order_Product COP ON CO.Customer_Order_ID = COP.Customer_Order_ID " +
                        "JOIN Product P ON COP.Product_ID = P.Product_ID " +
                        "JOIN Customer C ON CO.Customer_ID = C.Customer_ID " +
                        "WHERE CO.Delivery_Date BETWEEN TO_DATE('%s', 'DD-MM-YYYY') AND TO_DATE('%s', 'DD-MM-YYYY')",
                startDate, endDate
        );

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (!resultSet.isBeforeFirst()) {
                System.err.println("No orders found in the specified time frame.\n");
                Utils.goBackAndWait();
                return;
            }

            // Print table header
            clearConsole();
            System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Orders from " + startDate + " to " + endDate + " ------------\n" + Utils.RESET);
            System.out.printf(Utils.BOLD + "%-30s %-30s %-10s %-15s%n", "Customer Name", "Product Name", "Quantity", "Delivery Date");
            System.out.println("-------------------------------------------------------------------------------------------" + Utils.RESET);

            // Print table rows
            while (resultSet.next()) {
                String customerName = resultSet.getString("Customer_Name");
                String productName = resultSet.getString("Product_Name");
                int quantity = resultSet.getInt("Quantity");
                String deliveryDate = resultSet.getString("Delivery_Date");

                System.out.printf("%-30s %-30s %-10d %-15s%n", customerName, productName, quantity,
                        deliveryDate.split(" ")[0]);
            }

            Utils.goBackAndWait();

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
     * This method establishes a connection to the database.
     * @return The connection to the database.
     * @throws SQLException If an error occurs while connecting to the database.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }
}