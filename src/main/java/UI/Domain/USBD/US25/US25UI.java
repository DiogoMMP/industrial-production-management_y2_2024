package UI.Domain.USBD.US25;

import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.Scanner;

public class US25UI implements Runnable {
    @Override
    public void run() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Step 1: Query all products
            ResultSet products = statement.executeQuery("SELECT Product_ID, Product_Name FROM Product");

            // Step 2: Display products and let the user choose one
            System.out.println("Available Products:");
            while (products.next()) {
                System.out.printf("Product ID: %s, Product Name: %s%n", products.getString("Product_ID"), products.getString("Product_Name"));
            }

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the Product ID to use: ");
            String productId = scanner.nextLine();

            // Step 3: Call the Get_Product_Operations function
            CallableStatement callableStatement = connection.prepareCall("{? = call Get_Product_Operations(?)}");
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);
            callableStatement.setString(2, productId);
            callableStatement.execute();

            // Step 4: Print the output
            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);
            while (resultSet.next()) {
                String productID = resultSet.getString("Product_ID");
                String inputPart = resultSet.getString("Input_Part");
                String operationDescription = resultSet.getString("Operation_Description");
                int executionTime = resultSet.getInt("Execution_Time");
                String outputPart = resultSet.getString("Output_Part");

                System.out.printf("Product ID: %s, Input Part: %s, Operation: %s, Execution Time: %d, Output Part: %s%n",
                        productID, inputPart, operationDescription, executionTime, outputPart);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }
}