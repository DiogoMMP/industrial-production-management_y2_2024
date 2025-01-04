package UI.Domain.USLP.US07;

import UI.Menu.MenuItem;
import UI.Menu.OrdersMenu;
import UI.Utils.Utils;
import domain.Order;
import importer_and_exporter.OracleDataExporter;
import prodPlanSimulator.Simulator;
import repository.Instances;

import java.io.IOException;
import java.sql.*;
import java.util.*;

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
            List<MenuItem> options = new ArrayList<>();
            while (products.next()) {
                options.add(new MenuItem(products.getString("Product_ID"), new UpdateAverageTimeUI()));
            }
            options.add(new MenuItem("All", new UpdateAverageTimeUI()));
            // Step 2: Allow the user to select a product
            String choice;
            int option = 0;
            do {
                option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                        "--- Choose the Product to be Visualized ------------\n" + Utils.RESET);

                if (option == -2) {
                    new OrdersMenu().run();
                }

                if ((option >= 0) && (option < options.size())) {
                    choice = options.get(option).toString();
                    if (!choice.equals("Back")) {
                        show(choice, connection);
                        Utils.goBackAndWait();
                    }
                }
            } while (option != -1 && !options.get(option).toString().equals("Back"));


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

    private void updateAverageTimesInDB(Connection connection, String productId, double averageTime) throws SQLException {
        String updateQuery = "UPDATE Product SET Production_Average_Time = ? WHERE Product_ID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setDouble(1, averageTime);
            preparedStatement.setString(2, productId);
            preparedStatement.executeUpdate();
        }
    }

    private void show(String choice, Connection connection) throws SQLException {

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                "--- Average Production Time Management ------------" + Utils.RESET);

        if (choice.equals("All")) {
            // Retrieve all products
            String selectAllQuery = "SELECT Product_ID FROM Product";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectAllQuery)) {
                while (resultSet.next()) {
                    String productId = resultSet.getString("Product_ID");
                    updateProductAverageTime(productId, connection);
                }
            }
        } else {
            // Update a single product
            updateProductAverageTime(choice, connection);
        }
    }

    private void updateProductAverageTime(String productId, Connection connection) throws SQLException {
        // Retrieve the old average time from the database
        String selectQuery = "SELECT Production_Average_Time FROM Product WHERE Product_ID = ?";
        double oldAverageTime = 0.0;
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setString(1, productId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                oldAverageTime = resultSet.getDouble("Production_Average_Time");
            }
        }

        // Calculate the new average time using the simulator
        Simulator simulator = Instances.getInstance().getSimulator();
        double newAverageTime = simulator.CalculateAverageProductionTimeConsideringWaitingTime(productId);

        // Display the old and new average times
        System.out.println(Utils.BOLD + "\nProduct Average Time Update for Product: " + productId + Utils.RESET);
        System.out.printf("Old Average Time: %.2f\nNew Average Time: %.2f\n", oldAverageTime, newAverageTime);

        // Update the average times in the database
        ProductionDataLoader loader = new ProductionDataLoader(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
        loader.initializeProductionSystem(productId);
        updateAverageTimesInDB(connection, productId, newAverageTime);
    }


}