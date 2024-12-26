package UI.Domain.USLP.US07;

import importer_and_exporter.OracleDataExporter;
import prodPlanSimulator.Simulator;
import repository.Instances;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class UpdateAverageTimeUI implements Runnable {
    private Simulator simulator;

    public UpdateAverageTimeUI() throws IOException {
        this.simulator = Instances.getInstance().getSimulator();
    }

    @Override
    public void run() {
        try (Connection connection = getConnection()) {
            // Step 1: Retrieve and display the list of products from the database
            Statement statement = connection.createStatement();
            ResultSet products = statement.executeQuery("SELECT Product_ID, Product_Name FROM Product");

            System.out.println("Available Products:");
            while (products.next()) {
                System.out.printf("Product ID: %s, Product Name: %s%n", products.getString("Product_ID"), products.getString("Product_Name"));
            }

            // Step 2: Allow the user to select a product
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the Product ID to use: ");
            String productId = scanner.nextLine();

            // Step 3: Use the selected product's information in the simulator
            ProductionDataLoader loader = new ProductionDataLoader(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);

            loader.initializeProductionSystem(productId);

// Your simulator can now use the loaded data
            Simulator simulator = new Simulator();
            LinkedHashMap<String, Double> results = simulator.simulateProcessUS02();

            System.out.println("Average waiting times updated successfully."+results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

    private void updateAverageTimesInDB(Connection connection, String productId, LinkedHashMap<String, Double> averageTimes) throws SQLException {
        String updateQuery = "UPDATE Product SET Production_Average_Time = ? WHERE Product_ID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            for (Map.Entry<String, Double> entry : averageTimes.entrySet()) {
                double averageTime = entry.getValue();

                preparedStatement.setDouble(1, averageTime);
                preparedStatement.setString(2, productId);
                preparedStatement.executeUpdate();
            }
        }
    }
}